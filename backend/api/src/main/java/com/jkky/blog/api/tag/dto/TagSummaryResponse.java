package com.jkky.blog.api.tag.dto;

import lombok.Builder;

@Builder
public record TagSummaryResponse(
	String name,
	String key,
	long count
) {
}
