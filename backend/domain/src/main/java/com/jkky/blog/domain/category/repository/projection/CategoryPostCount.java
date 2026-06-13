package com.jkky.blog.domain.category.repository.projection;

import lombok.Builder;

@Builder
public record CategoryPostCount(
	String name,
	String filterKey,
	long count
) {
}
