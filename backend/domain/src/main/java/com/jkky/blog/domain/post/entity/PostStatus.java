package com.jkky.blog.domain.post.entity;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum PostStatus {
	DRAFT("draft"),
	PUBLISHED("published"),
	HIDDEN("hidden");

	private final String value;

	PostStatus(String value) {
		this.value = value;
	}

	public static PostStatus fromValue(String value) {
		return Arrays.stream(values())
			.filter(status -> status.value.equals(value))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown post status: " + value));
	}
}
