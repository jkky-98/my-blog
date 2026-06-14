package com.jkky.blog.api.category.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jkky.blog.api.BlogApiApplication;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.category.policy.CategoryNamePolicy;
import com.jkky.blog.domain.category.repository.CategoryRepository;
import com.jkky.blog.domain.post.repository.PostRepository;
import com.jkky.blog.domain.post.repository.PostTagRepository;
import com.jkky.blog.domain.tag.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("관리자 카테고리 API")
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
class CategoryAdminControllerTest {

	private final CategoryNamePolicy categoryNamePolicy = new CategoryNamePolicy();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostTagRepository postTagRepository;

	@BeforeEach
	void setUp() {
		postTagRepository.deleteAllInBatch();
		postRepository.deleteAllInBatch();
		tagRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("로그인한 관리자는 새 카테고리를 생성한다")
	void createCategory() throws Exception {
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/admin/categories")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": " Spring   Boot "
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Spring Boot"))
			.andExpect(jsonPath("$.key").value("spring-boot"))
			.andExpect(jsonPath("$.count").value(0));

		Category category = categoryRepository.findByNormalizedName("spring boot").orElseThrow();
		assertThat(category.getName()).isEqualTo("Spring Boot");
		assertThat(category.getFilterKey()).isEqualTo("spring-boot");
	}

	@Test
	@DisplayName("이미 같은 카테고리 이름이 있으면 CATEGORY_ALREADY_EXISTS를 반환한다")
	void createDuplicateCategoryReturnsConflict() throws Exception {
		saveCategory("Backend");
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/admin/categories")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": " backend "
					}
					"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("CATEGORY_ALREADY_EXISTS"))
			.andExpect(jsonPath("$.message").value("이미 존재하는 카테고리입니다."));
	}

	@Test
	@DisplayName("카테고리 이름이 비어 있으면 VALIDATION_ERROR를 반환한다")
	void createBlankCategoryReturnsValidationError() throws Exception {
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/admin/categories")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": ""
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.fieldErrors.name").value("카테고리 이름을 입력해 주세요."));
	}

	private Category saveCategory(String name) {
		return categoryRepository.save(Category.builder()
			.name(categoryNamePolicy.toDisplayName(name))
			.normalizedName(categoryNamePolicy.toNormalizedName(name))
			.filterKey(categoryNamePolicy.toFilterKey(name))
			.build());
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

	private RequestPostProcessor session(MockHttpSession session) {
		return request -> {
			request.setSession(session);
			return request;
		};
	}

	private record CsrfSession(
		MockHttpSession session,
		String headerName,
		String token
	) {
	}
}
