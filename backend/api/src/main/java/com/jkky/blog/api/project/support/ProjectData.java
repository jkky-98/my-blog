package com.jkky.blog.api.project.support;

import java.util.List;
import lombok.Builder;

@Builder
public record ProjectData(
	long id,
	String name,
	String description,
	List<String> stack,
	String status
) {
}
