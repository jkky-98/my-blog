package com.jkky.blog.api.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jkky.blog.api.BlogApiApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(classes = BlogApiApplication.class)
@AutoConfigureMockMvc
@DisplayName("관리자 인증 API")
@TestPropertySource(properties = {
	"spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
	"spring.datasource.url=jdbc:mysql://${DB_TEST_HOST:localhost}:${DB_TEST_PORT:3307}/${DB_TEST_NAME:jkky_blog_test}?createDatabaseIfNotExist=true&serverTimezone=UTC&connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true&useUnicode=true&characterEncoding=utf8",
	"spring.datasource.username=${DB_TEST_USERNAME:root}",
	"spring.datasource.password=${DB_TEST_PASSWORD:root}",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
	"blog.admin.username=admin",
	"blog.admin.password=password",
	"blog.admin.display-name=Jin"
})
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("CSRF 토큰을 조회한다")
	void getCsrfToken() throws Exception {
		mockMvc.perform(get("/api/auth/csrf"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"))
			.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	@DisplayName("CSRF 토큰과 올바른 계정으로 로그인한다")
	void loginWithCsrfToken() throws Exception {
		CsrfSession csrfSession = getCsrfSession();

		mockMvc.perform(post("/api/auth/login")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "admin",
					  "password": "password"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.username").value("admin"))
			.andExpect(jsonPath("$.displayName").value("Jin"));
	}

	@Test
	@DisplayName("로그인한 관리자는 내 정보를 조회한다")
	void getCurrentAdminAfterLogin() throws Exception {
		CsrfSession csrfSession = login();

		mockMvc.perform(get("/api/auth/me")
				.with(session(csrfSession.session())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("admin"))
			.andExpect(jsonPath("$.displayName").value("Jin"));
	}

	@Test
	@DisplayName("로그인하지 않은 사용자의 내 정보 조회는 UNAUTHENTICATED를 반환한다")
	void getCurrentAdminWithoutLoginReturnsUnauthenticated() throws Exception {
		mockMvc.perform(get("/api/auth/me"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("UNAUTHENTICATED"))
			.andExpect(jsonPath("$.message").value("로그인이 필요합니다."));
	}

	@Test
	@DisplayName("인증 없이 관리자 API에 접근하면 UNAUTHENTICATED를 반환한다")
	void accessAdminApiWithoutLoginReturnsUnauthenticated() throws Exception {
		mockMvc.perform(get("/api/admin/posts"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("UNAUTHENTICATED"))
			.andExpect(jsonPath("$.message").value("로그인이 필요합니다."));
	}

	@Test
	@DisplayName("로그인 입력값 검증 실패는 필드당 하나의 메시지를 반환한다")
	void loginValidationErrorReturnsOneMessagePerField() throws Exception {
		CsrfSession csrfSession = getCsrfSession();

		mockMvc.perform(post("/api/auth/login")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "",
					  "password": ""
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.fieldErrors.username").value("아이디를 입력해 주세요."))
			.andExpect(jsonPath("$.fieldErrors.password").value("비밀번호를 입력해 주세요."));
	}

	@Test
	@DisplayName("틀린 계정으로 로그인하면 INVALID_CREDENTIALS를 반환한다")
	void loginWithInvalidCredentialsReturnsInvalidCredentials() throws Exception {
		CsrfSession csrfSession = getCsrfSession();

		mockMvc.perform(post("/api/auth/login")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "admin",
					  "password": "wrong"
					}
					"""))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
			.andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
	}

	@Test
	@DisplayName("CSRF 토큰 없이 로그인하면 CSRF_TOKEN_INVALID를 반환한다")
	void loginWithoutCsrfTokenReturnsCsrfError() throws Exception {
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "admin",
					  "password": "password"
					}
					"""))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("CSRF_TOKEN_INVALID"))
			.andExpect(jsonPath("$.message").value("요청 보안 토큰이 유효하지 않습니다."));
	}

	@Test
	@DisplayName("로그아웃하면 세션 인증이 제거된다")
	void logoutClearsSessionAuthentication() throws Exception {
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/auth/logout")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token()))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/auth/me")
				.with(session(csrfSession.session())))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
	}

	private CsrfSession login() throws Exception {
		CsrfSession csrfSession = getCsrfSession();

		mockMvc.perform(post("/api/auth/login")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "admin",
					  "password": "password"
					}
					"""))
			.andExpect(status().isOk());

		return csrfSession;
	}

	private CsrfSession getCsrfSession() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
			.andExpect(status().isOk())
			.andReturn();

		JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
		return new CsrfSession(
			(MockHttpSession) result.getRequest().getSession(false),
			response.get("headerName").asText(),
			response.get("token").asText()
		);
	}

	private record CsrfSession(
		MockHttpSession session,
		String headerName,
		String token
	) {
	}

	private RequestPostProcessor session(MockHttpSession session) {
		return request -> {
			request.setSession(session);
			return request;
		};
	}
}
