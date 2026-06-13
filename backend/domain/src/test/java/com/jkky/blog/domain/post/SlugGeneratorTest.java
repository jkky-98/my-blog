package com.jkky.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("글 slug 생성 정책")
class SlugGeneratorTest {

	private final SlugGenerator generator = new SlugGenerator(
		Clock.fixed(Instant.parse("2026-06-13T00:00:00Z"), ZoneOffset.UTC),
		() -> "a1b2c3"
	);

	@Test
	@DisplayName("영문과 숫자 제목은 소문자와 하이픈으로 된 slug가 된다")
	void asciiTitleBecomesReadableSlug() {
		assertThat(generator.generate(" Spring Boot Redis Cache ")).isEqualTo("spring-boot-redis-cache");
		assertThat(generator.generate("Spring Boot 101")).isEqualTo("spring-boot-101");
	}

	@Test
	@DisplayName("기호와 연속 공백은 하나의 하이픈으로 정리한다")
	void separatorsBecomeSingleHyphen() {
		assertThat(generator.generate("Spring_Boot.Redis")).isEqualTo("spring-boot-redis");
		assertThat(generator.generate("---Spring   Boot---")).isEqualTo("spring-boot");
	}

	@Test
	@DisplayName("한글 제목은 날짜와 짧은 식별자를 사용한 fallback slug가 된다")
	void koreanTitleUsesFallbackSlug() {
		assertThat(generator.generate("스프링 부트")).isEqualTo("post-20260613-a1b2c3");
	}

	@Test
	@DisplayName("ASCII slug 후보가 비는 제목은 fallback slug가 된다")
	void emptyAsciiCandidateUsesFallbackSlug() {
		assertThat(generator.generate("!!!")).isEqualTo("post-20260613-a1b2c3");
	}
}
