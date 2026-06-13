package com.jkky.blog.domain.post.policy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("글 읽는 시간 계산 정책")
class ReadingTimeCalculatorTest {

	private final ReadingTimeCalculator calculator = new ReadingTimeCalculator();

	@Test
	@DisplayName("읽는 시간은 최소 1분이다")
	void readingTimeIsAtLeastOneMinute() {
		assertThat(calculator.calculate("짧은 본문")).isEqualTo(1);
	}

	@Test
	@DisplayName("공백을 제외한 500자당 1분으로 계산한다")
	void readingTimeUsesNonWhitespaceCharacters() {
		String markdown = "가".repeat(500) + " " + "나";

		assertThat(calculator.calculate(markdown)).isEqualTo(2);
	}

	@Test
	@DisplayName("Markdown 코드블럭은 읽는 시간 계산에서 제외한다")
	void fencedCodeBlocksAreExcluded() {
		String markdown = """
			짧은 본문

			```
			%s
			```
			""".formatted("가".repeat(1000));

		assertThat(calculator.calculate(markdown)).isEqualTo(1);
	}
}
