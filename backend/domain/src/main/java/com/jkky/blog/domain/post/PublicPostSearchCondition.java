package com.jkky.blog.domain.post;

import lombok.Builder;

@Builder
public record PublicPostSearchCondition(
	String categoryKey,
	String tagKey
) {
}
