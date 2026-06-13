package com.jkky.blog.api.project.support;

import com.jkky.blog.api.project.dto.ProjectSummaryResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectResponseAssembler {

	public List<ProjectSummaryResponse> toSummaryResponses(List<ProjectData> projects) {
		return projects.stream()
			.map(project -> ProjectSummaryResponse.builder()
				.id(project.id())
				.name(project.name())
				.description(project.description())
				.stack(project.stack())
				.status(project.status())
				.build())
			.toList();
	}
}
