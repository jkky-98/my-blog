package com.jkky.blog.domain.tag.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("태그 입력 정리 정책")
class TagInputCleanerTest {

	private final TagInputCleaner cleaner = new TagInputCleaner();

	@Test
	@DisplayName("각 태그 표시 이름은 앞뒤 공백을 제거하고 연속 공백을 하나로 줄인다")
	void cleanNormalizesDisplayNames() {
		List<String> tagNames = List.of("  Spring   Boot  ", " Redis ");

		assertThat(cleaner.clean(tagNames)).containsExactly("Spring Boot", "Redis");
	}

	@Test
	@DisplayName("빈 태그 입력은 제거한다")
	void cleanRemovesBlankTags() {
		List<String> tagNames = List.of("", " ", "\t", "Redis");

		assertThat(cleaner.clean(tagNames)).containsExactly("Redis");
	}

	@Test
	@DisplayName("같은 요청 안의 중복 태그는 정규화 이름 기준으로 제거한다")
	void cleanRemovesDuplicateTagsByNormalizedName() {
		List<String> tagNames = List.of(" Spring Boot ", "spring boot", "Redis", " redis ");

		assertThat(cleaner.clean(tagNames)).containsExactly("Spring Boot", "Redis");
	}

	@Test
	@DisplayName("중복 제거 후 먼저 나온 표시 이름과 입력 순서를 유지한다")
	void cleanKeepsFirstDisplayNameAndInputOrder() {
		List<String> tagNames = List.of("Vue", "spring boot", "Spring Boot", "Redis");

		assertThat(cleaner.clean(tagNames)).containsExactly("Vue", "spring boot", "Redis");
	}

	@Test
	@DisplayName("정리 결과가 비어도 검증 예외를 던지지 않는다")
	void cleanDoesNotValidateRequiredTags() {
		List<String> tagNames = List.of("", " ");

		assertThat(cleaner.clean(tagNames)).isEmpty();
	}
}
