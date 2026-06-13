package com.jkky.blog.domain.post.policy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("글 description 생성 정책")
class PostDescriptionGeneratorTest {

	private final PostDescriptionGenerator generator = new PostDescriptionGenerator();

	@Test
	@DisplayName("Markdown 문법을 제거한 일반 텍스트를 description으로 사용한다")
	void descriptionUsesPlainText() {
		String markdown = "# 제목\n\n본문 **강조** 텍스트입니다.";

		assertThat(generator.generate(markdown)).isEqualTo("제목 본문 강조 텍스트입니다.");
	}

	@Test
	@DisplayName("description은 최대 300자로 자른다")
	void descriptionIsLimitedTo300Characters() {
		String markdown = "가".repeat(301);

		assertThat(generator.generate(markdown))
			.hasSize(300)
			.isEqualTo("가".repeat(300));
	}
}
