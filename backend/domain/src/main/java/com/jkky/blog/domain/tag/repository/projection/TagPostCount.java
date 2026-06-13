package com.jkky.blog.domain.tag.repository.projection;

import lombok.Builder;

@Builder
public record TagPostCount(
	String name,
	String filterKey,
	long count
) {
}
