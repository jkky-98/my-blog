package com.jkky.blog.api.post.support;

import com.jkky.blog.domain.post.entity.PostStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record PostAdminCreateCommand(
	String title,
	String categoryNormalizedName,
	List<String> tagNames,
	String slug,
	String description,
	String content,
	int readingTime,
	String author,
	boolean featured,
	PostStatus status
) {
}
