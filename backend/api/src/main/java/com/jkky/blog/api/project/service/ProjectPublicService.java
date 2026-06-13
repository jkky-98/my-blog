package com.jkky.blog.api.project.service;

import com.jkky.blog.api.project.dto.ProjectSummaryResponse;
import com.jkky.blog.api.project.support.ProjectPublicReader;
import com.jkky.blog.api.project.support.ProjectResponseAssembler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectPublicService {

	private final ProjectPublicReader projectPublicReader;
	private final ProjectResponseAssembler projectResponseAssembler;

	public List<ProjectSummaryResponse> getProjects() {
		return projectResponseAssembler.toSummaryResponses(
			projectPublicReader.readProjects()
		);
	}
}
