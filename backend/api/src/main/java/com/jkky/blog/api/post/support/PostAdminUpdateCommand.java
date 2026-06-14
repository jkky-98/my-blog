package com.jkky.blog.api.post.support;

import com.jkky.blog.domain.post.entity.PostStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record PostAdminUpdateCommand(
	Long id,
	String title,
	String categoryNormalizedName,
	List<String> tagNames,
	String description,
	String content,
	int readingTime,
	boolean featured,
	PostStatus status
) {
}
