package com.jkky.blog.api.post.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminPostStatusUpdateRequest(
	@NotBlank(message = "상태를 입력해 주세요.")
	String status
) {
}
