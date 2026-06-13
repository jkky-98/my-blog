package com.jkky.blog.domain.tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 요청으로 들어온 태그 이름 목록에 정리 정책을 적용한다.
 *
 * <p>태그 이름 하나의 정리는 {@link TagNamePolicy}에 위임한다.
 * 이 클래스는 빈 값 제거, 중복 제거, 입력 순서 유지처럼 목록 단위 정책만 담당한다.
 * 정리 후 태그 개수 제한과 실패 응답은 관리자 글 요청 validation에서 처리한다.</p>
 *
 * <p>예: {@code [" Spring Boot ", "spring boot", " Redis "]}
 * -> {@code ["Spring Boot", "Redis"]}</p>
 */
public class TagInputCleaner {

	private final TagNamePolicy tagNamePolicy;

	public TagInputCleaner() {
		this(new TagNamePolicy());
	}

	TagInputCleaner(TagNamePolicy tagNamePolicy) {
		this.tagNamePolicy = tagNamePolicy;
	}

	public List<String> clean(List<String> tagNames) {
		List<String> cleaned = new ArrayList<>();
		Set<String> seenNormalizedNames = new LinkedHashSet<>();

		for (String tagName : tagNames) {
			if (tagName == null || tagName.isBlank()) {
				continue;
			}

			String displayName = tagNamePolicy.toDisplayName(tagName);
			String normalizedName = tagNamePolicy.toNormalizedName(displayName);
			if (seenNormalizedNames.add(normalizedName)) {
				cleaned.add(displayName);
			}
		}

		return cleaned;
	}
}
