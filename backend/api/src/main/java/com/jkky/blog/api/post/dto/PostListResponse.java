package com.jkky.blog.api.post.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PostListResponse(
	List<PostSummaryResponse> items,
	int page,
	int size,
	long totalCount,
	int totalPages
) {
}
