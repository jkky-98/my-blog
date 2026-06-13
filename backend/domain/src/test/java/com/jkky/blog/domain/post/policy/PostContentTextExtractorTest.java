package com.jkky.blog.domain.post.policy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("글 본문 텍스트 추출 정책")
class PostContentTextExtractorTest {

	private final PostContentTextExtractor extractor = new PostContentTextExtractor();

	@Test
	@DisplayName("Markdown 제목과 강조 문법을 제거하고 일반 텍스트를 남긴다")
	void markdownSyntaxIsRemoved() {
		String markdown = "# 제목\n\n본문 **강조** 텍스트입니다.";

		assertThat(extractor.extract(markdown)).isEqualTo("제목 본문 강조 텍스트입니다.");
	}

	@Test
	@DisplayName("코드블럭은 요약과 읽는 시간 계산 대상에서 제외한다")
	void fencedCodeBlocksAreRemoved() {
		String markdown = """
			본문 앞

			```java
			System.out.println("hello");
			```

			본문 뒤
			""";

		assertThat(extractor.extract(markdown)).isEqualTo("본문 앞 본문 뒤");
	}

	@Test
	@DisplayName("링크는 표시 텍스트만 남기고 이미지는 제거한다")
	void linksKeepTextAndImagesAreRemoved() {
		String markdown = "[블로그](https://example.com) ![이미지](image.png)";

		assertThat(extractor.extract(markdown)).isEqualTo("블로그");
	}

	@Test
	@DisplayName("목록과 인용문 기호를 제거하고 공백을 하나로 정리한다")
	void lineMarkersAndWhitespaceAreNormalized() {
		String markdown = """
			> 인용문
			- 첫 번째
			1. 두 번째
			""";

		assertThat(extractor.extract(markdown)).isEqualTo("인용문 첫 번째 두 번째");
	}
}
