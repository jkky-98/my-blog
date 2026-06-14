package com.jkky.blog.api.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AdminPostSaveRequest(
	@NotBlank(message = "제목을 입력해 주세요.")
	@Size(max = 120, message = "제목은 최대 120자까지 입력할 수 있습니다.")
	String title,

	@NotBlank(message = "카테고리를 입력해 주세요.")
	@Size(max = 40, message = "카테고리는 최대 40자까지 입력할 수 있습니다.")
	String category,

	@NotNull(message = "태그는 최소 1개 이상 입력해야 합니다.")
	List<String> tags,

	boolean featured,

	@NotBlank(message = "상태를 입력해 주세요.")
	String status,

	@NotBlank(message = "본문을 입력해 주세요.")
	@Size(max = 100_000, message = "본문은 최대 100000자까지 입력할 수 있습니다.")
	String content
) {
}
