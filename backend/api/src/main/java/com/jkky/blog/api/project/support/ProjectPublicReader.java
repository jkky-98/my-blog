package com.jkky.blog.api.project.support;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectPublicReader {

	private static final List<ProjectData> PROJECTS = List.of(
		ProjectData.builder()
			.id(1)
			.name("Private Blog Platform")
			.description("Vue 3 프론트와 Spring Boot API를 연결하는 개인 블로그 플랫폼.")
			.stack(List.of("Vue", "Vuetify", "Spring Boot"))
			.status("Mockup")
			.build(),
		ProjectData.builder()
			.id(2)
			.name("Developer Portfolio Kit")
			.description("프로젝트, 글, 실험 기록을 한 화면에서 보여주는 포트폴리오 구성.")
			.stack(List.of("TypeScript", "Design System", "SEO"))
			.status("Design")
			.build(),
		ProjectData.builder()
			.id(3)
			.name("Backend Lab Notes")
			.description("캐시, DB, 배포 기록을 글과 코드 조각으로 정리하는 실험 노트.")
			.stack(List.of("Redis", "MySQL", "Docker"))
			.status("Planning")
			.build()
	);

	public List<ProjectData> readProjects() {
		return PROJECTS;
	}
}
