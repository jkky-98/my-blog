package com.jkky.blog.api.project.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jkky.blog.api.BlogApiApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = BlogApiApplication.class)
@AutoConfigureMockMvc
@DisplayName("공개 프로젝트 API")
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
class ProjectPublicControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("서버 고정 프로젝트 목록을 노출 순서대로 조회한다")
	void getProjectsReturnsFixedProjects() throws Exception {
		mockMvc.perform(get("/api/projects"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)))
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("Private Blog Platform"))
			.andExpect(jsonPath("$[0].description").value("Vue 3 프론트와 Spring Boot API를 연결하는 개인 블로그 플랫폼."))
			.andExpect(jsonPath("$[0].stack[0]").value("Vue"))
			.andExpect(jsonPath("$[0].stack[1]").value("Vuetify"))
			.andExpect(jsonPath("$[0].stack[2]").value("Spring Boot"))
			.andExpect(jsonPath("$[0].status").value("Mockup"))
			.andExpect(jsonPath("$[1].name").value("Developer Portfolio Kit"))
			.andExpect(jsonPath("$[1].status").value("Design"))
			.andExpect(jsonPath("$[2].name").value("Backend Lab Notes"))
			.andExpect(jsonPath("$[2].status").value("Planning"));
	}
}
