package com.jkky.blog.api.auth.dto;

import lombok.Builder;

@Builder
public record CsrfTokenResponse(
	String headerName,
	String token
) {
}
