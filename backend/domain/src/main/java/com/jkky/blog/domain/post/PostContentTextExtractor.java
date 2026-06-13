package com.jkky.blog.domain.post;

import java.util.regex.Pattern;

/**
 * Markdown 본문에서 description과 readingTime 계산에 사용할 일반 텍스트를 추출한다.
 *
 * <p>코드블럭은 요약과 읽는 시간 계산에서 제외한다.
 * 예: {@code "# 제목\n\n본문 **강조**"} -> {@code "제목 본문 강조"}</p>
 */
public class PostContentTextExtractor {

	private static final Pattern FENCED_CODE_BLOCKS = Pattern.compile("(?s)```.*?```|~~~.*?~~~");
	private static final Pattern IMAGES = Pattern.compile("!\\[[^\\]]*]\\([^)]*\\)");
	private static final Pattern LINKS = Pattern.compile("\\[([^\\]]+)]\\([^)]*\\)");
	private static final Pattern INLINE_CODE = Pattern.compile("`([^`]*)`");
	private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>");
	private static final Pattern LINE_PREFIX_MARKERS = Pattern.compile("(?m)^\\s{0,3}(#{1,6}|>|[-*+]\\s+|\\d+\\.\\s+)");
	private static final Pattern MARKDOWN_SYMBOLS = Pattern.compile("[*_~|`]");
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");

	public String extract(String markdown) {
		String text = FENCED_CODE_BLOCKS.matcher(markdown).replaceAll(" ");
		text = IMAGES.matcher(text).replaceAll(" ");
		text = LINKS.matcher(text).replaceAll("$1");
		text = INLINE_CODE.matcher(text).replaceAll("$1");
		text = HTML_TAGS.matcher(text).replaceAll(" ");
		text = LINE_PREFIX_MARKERS.matcher(text).replaceAll(" ");
		text = MARKDOWN_SYMBOLS.matcher(text).replaceAll("");
		return WHITESPACE.matcher(text.trim()).replaceAll(" ");
	}
}
