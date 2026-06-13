package com.jkky.blog.api.common.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
	GlobalExceptionHandler.class,
	GlobalExceptionHandlerTest.TestController.class
})
@DisplayName("공통 에러 응답")
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("검증 실패는 VALIDATION_ERROR와 fieldErrors를 반환한다")
	void validationErrorIncludesFieldErrors() throws Exception {
		mockMvc.perform(post("/test/validation")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": ""
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.message").value("입력값을 확인해 주세요."))
			.andExpect(jsonPath("$.fieldErrors.title").value("제목을 입력해 주세요."));
	}

	@Test
	@DisplayName("잘못된 JSON 요청은 INVALID_REQUEST를 반환하고 fieldErrors를 포함하지 않는다")
	void invalidJsonReturnsInvalidRequest() throws Exception {
		mockMvc.perform(post("/test/validation")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
			.andExpect(jsonPath("$.message").value("올바르지 않은 요청입니다."))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
	}

	@Test
	@DisplayName("query parameter 타입 오류는 INVALID_REQUEST를 반환한다")
	void queryTypeMismatchReturnsInvalidRequest() throws Exception {
		mockMvc.perform(get("/test/type-mismatch")
				.param("page", "not-number"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
	}

	@Test
	@DisplayName("BlogException은 ErrorCode에 정의된 상태와 응답을 반환한다")
	void blogExceptionUsesErrorCode() throws Exception {
		mockMvc.perform(get("/test/blog-exception"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("글을 찾을 수 없습니다."))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
	}

	@Test
	@DisplayName("존재하지 않는 리소스는 RESOURCE_NOT_FOUND를 반환한다")
	void missingResourceReturnsResourceNotFound() throws Exception {
		mockMvc.perform(get("/not-found"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("요청한 리소스를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
	}

	@Test
	@DisplayName("예상하지 못한 예외는 INTERNAL_SERVER_ERROR를 반환한다")
	void unknownExceptionReturnsInternalServerError() throws Exception {
		mockMvc.perform(get("/test/unknown-exception"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
			.andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
	}

	@SpringBootConfiguration
	static class TestApplication {
	}

	@RestController
	public static class TestController {

		@PostMapping("/test/validation")
		void validation(@Valid @RequestBody TestRequest request) {
		}

		@GetMapping("/test/type-mismatch")
		void typeMismatch(@RequestParam int page) {
		}

		@GetMapping("/test/blog-exception")
		void blogException() {
			throw new BlogException(ErrorCode.POST_NOT_FOUND);
		}

		@GetMapping("/test/unknown-exception")
		void unknownException() {
			throw new IllegalStateException("unexpected");
		}
	}

	record TestRequest(
		@NotBlank(message = "제목을 입력해 주세요.")
		String title
	) {
	}
}
