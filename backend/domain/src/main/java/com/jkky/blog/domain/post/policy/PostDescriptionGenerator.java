package com.jkky.blog.domain.post.policy;

/**
 * Markdown 본문에서 글 목록과 상세 상단에 사용할 요약을 생성한다.
 *
 * <p>초기 구현은 규칙 기반으로 일반 텍스트 앞부분을 사용한다.
 * 추후 AI 요약 서버를 도입하면 더 자연스러운 description을 자동 생성하도록 교체할 수 있다.
 * AI 요약 실패 시에는 이 규칙 기반 요약을 fallback으로 사용할 수 있게 유지한다.</p>
 */
public class PostDescriptionGenerator {

	private static final int MAX_DESCRIPTION_LENGTH = 300;

	private final PostContentTextExtractor textExtractor;

	public PostDescriptionGenerator() {
		this(new PostContentTextExtractor());
	}

	PostDescriptionGenerator(PostContentTextExtractor textExtractor) {
		this.textExtractor = textExtractor;
	}

	public String generate(String markdown) {
		String text = textExtractor.extract(markdown);
		if (text.length() <= MAX_DESCRIPTION_LENGTH) {
			return text;
		}

		return text.substring(0, MAX_DESCRIPTION_LENGTH);
	}
}
