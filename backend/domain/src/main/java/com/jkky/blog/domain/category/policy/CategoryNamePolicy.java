package com.jkky.blog.domain.category.policy;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 카테고리 이름을 저장과 조회에 필요한 값으로 변환한다.
 *
 * <p>예: {@code " Spring   Boot "}는 표시 이름 {@code "Spring Boot"},
 * 정규화 이름 {@code "spring boot"}, 필터 key {@code "spring-boot"}가 된다.
 * 한글은 query parameter 값으로 사용할 수 있으므로 제거하지 않는다.</p>
 */
public class CategoryNamePolicy {

	private static final Pattern SPACES = Pattern.compile("\\s+");
	private static final Pattern FILTER_KEY_SEPARATORS = Pattern.compile("[^\\p{L}\\p{N}]+");

	public String toDisplayName(String name) {
		return normalizeSpaces(name);
	}

	/**
	 * 대소문자와 앞뒤 공백 차이를 무시해 중복 카테고리를 판단하는 값이다.
	 */
	public String toNormalizedName(String name) {
		return normalizeSpaces(name).toLowerCase(Locale.ROOT);
	}

	/**
	 * 공개 글 목록 필터 query parameter에 사용하는 고정 key다.
	 *
	 * <p>예: {@code "Spring Boot"} -> {@code "spring-boot"},
	 * {@code "스프링 부트"} -> {@code "스프링-부트"}</p>
	 */
	public String toFilterKey(String name) {
		String key = FILTER_KEY_SEPARATORS.matcher(toNormalizedName(name)).replaceAll("-");
		return trimHyphen(key);
	}

	private String normalizeSpaces(String value) {
		return SPACES.matcher(value.trim()).replaceAll(" ");
	}

	private String trimHyphen(String value) {
		int start = 0;
		int end = value.length();

		while (start < end && value.charAt(start) == '-') {
			start++;
		}
		while (end > start && value.charAt(end - 1) == '-') {
			end--;
		}

		return value.substring(start, end);
	}
}
