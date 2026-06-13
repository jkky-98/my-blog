package com.jkky.blog.domain.post.repository.condition;

import lombok.Builder;

@Builder
public record PublicPostSearchCondition(
	String categoryKey,
	String tagKey
) {
}
