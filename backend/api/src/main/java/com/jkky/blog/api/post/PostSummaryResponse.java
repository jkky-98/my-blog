package com.jkky.blog.api.post;

import java.util.List;
import lombok.Builder;

@Builder
public record PostSummaryResponse(
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
	boolean featured
) {
}
