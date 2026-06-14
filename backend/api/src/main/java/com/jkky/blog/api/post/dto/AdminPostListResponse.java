package com.jkky.blog.api.post.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record AdminPostListResponse(
	List<AdminPostSummaryResponse> items,
	int page,
	int size,
	long totalCount,
	int totalPages
) {
}
