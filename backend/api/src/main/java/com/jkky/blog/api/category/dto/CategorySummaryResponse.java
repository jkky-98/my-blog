package com.jkky.blog.api.category.dto;

import lombok.Builder;

@Builder
public record CategorySummaryResponse(
	String name,
	String key,
	long count
) {
}
