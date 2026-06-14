package com.jkky.blog.api.auth.dto;

import lombok.Builder;

@Builder
public record AdminUserResponse(
	Long id,
	String username,
	String displayName
) {
}
