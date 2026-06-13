package com.jkky.blog.api.project.controller;

import com.jkky.blog.api.project.dto.ProjectSummaryResponse;
import com.jkky.blog.api.project.service.ProjectPublicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectPublicController {

	private final ProjectPublicService projectPublicService;

	@GetMapping
	public List<ProjectSummaryResponse> getProjects() {
		return projectPublicService.getProjects();
	}
}
