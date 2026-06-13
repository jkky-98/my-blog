package com.jkky.blog.domain.post;

import java.util.regex.Pattern;

/**
 * Markdown 본문 기준 예상 읽는 시간을 계산한다.
 *
 * <p>일반 텍스트에서 공백을 제외한 글자 수 500자당 1분으로 계산하고, 최소값은 1분이다.</p>
 */
public class ReadingTimeCalculator {

	private static final int CHARACTERS_PER_MINUTE = 500;
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");

	private final PostContentTextExtractor textExtractor;

	public ReadingTimeCalculator() {
		this(new PostContentTextExtractor());
	}

	ReadingTimeCalculator(PostContentTextExtractor textExtractor) {
		this.textExtractor = textExtractor;
	}

	public int calculate(String markdown) {
		String text = textExtractor.extract(markdown);
		int characterCount = WHITESPACE.matcher(text).replaceAll("").length();
		return Math.max(1, (int) Math.ceil((double) characterCount / CHARACTERS_PER_MINUTE));
	}
}
