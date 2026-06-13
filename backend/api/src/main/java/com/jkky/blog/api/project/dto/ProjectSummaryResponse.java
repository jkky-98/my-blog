package com.jkky.blog.api.project.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ProjectSummaryResponse(
	long id,
	String name,
	String description,
	List<String> stack,
	String status
) {
}
