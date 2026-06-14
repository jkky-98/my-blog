package com.jkky.blog.domain.post.repository.condition;

import com.jkky.blog.domain.post.entity.PostStatus;
import lombok.Builder;

@Builder
public record AdminPostSearchCondition(
	PostStatus status,
	String categoryKey,
	String tagKey,
	String keyword
) {
}
