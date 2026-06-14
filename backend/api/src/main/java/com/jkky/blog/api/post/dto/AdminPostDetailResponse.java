package com.jkky.blog.api.post.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record AdminPostDetailResponse(
	Long id,
	String title,
	String slug,
	String description,
	String category,
	String categoryKey,
	List<String> tags,
	List<String> tagKeys,
	String createdAt,
	String updatedAt,
	int readingTime,
	long viewCount,
	String author,
	boolean featured,
	String status,
	String content
) {
}
