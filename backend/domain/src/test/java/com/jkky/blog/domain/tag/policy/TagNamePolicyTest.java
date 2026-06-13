package com.jkky.blog.domain.tag.policy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("태그 이름 정책")
class TagNamePolicyTest {

	private final TagNamePolicy policy = new TagNamePolicy();

	@Test
	@DisplayName("표시 이름은 앞뒤 공백을 제거하고 연속 공백을 하나로 줄인다")
	void displayNameNormalizesSpaces() {
		assertThat(policy.toDisplayName("  Spring   Boot  ")).isEqualTo("Spring Boot");
	}

	@Test
	@DisplayName("정규화 이름은 표시 이름 기준으로 소문자로 만든다")
	void normalizedNameIsLowerCaseDisplayName() {
		assertThat(policy.toNormalizedName("  Spring   Boot  ")).isEqualTo("spring boot");
		assertThat(policy.toNormalizedName("REDIS")).isEqualTo("redis");
	}

	@Test
	@DisplayName("필터 key는 소문자와 하이픈 중심의 query 값으로 만든다")
	void filterKeyUsesHyphenSeparators() {
		assertThat(policy.toFilterKey("  Spring   Boot  ")).isEqualTo("spring-boot");
		assertThat(policy.toFilterKey("Spring_Boot")).isEqualTo("spring-boot");
		assertThat(policy.toFilterKey("Spring.Boot")).isEqualTo("spring-boot");
	}

	@Test
	@DisplayName("필터 key는 한글을 제거하지 않는다")
	void filterKeyKeepsKoreanLetters() {
		assertThat(policy.toFilterKey("스프링 부트")).isEqualTo("스프링-부트");
	}
}
