package com.jkky.blog.api.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
	@NotBlank(message = "카테고리 이름을 입력해 주세요.")
	@Size(max = 40, message = "카테고리는 최대 40자까지 입력할 수 있습니다.")
	String name
) {
}
