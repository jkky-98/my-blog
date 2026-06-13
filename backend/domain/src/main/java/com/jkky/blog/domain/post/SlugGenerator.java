package com.jkky.blog.domain.post;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 글 제목을 공개 URL에 사용할 slug 후보로 변환한다.
 *
 * <p>예: {@code " Spring Boot Redis "} -> {@code "spring-boot-redis"}.
 * 한글 제목처럼 ASCII slug 후보가 비는 경우에는 {@code "post-20260613-a1b2c3"} 형태의 fallback을 만든다.</p>
 *
 * <p>DB 중복으로 인한 숫자 suffix 부여는 repository 조회가 필요한 저장 정책이므로 여기서 처리하지 않는다.</p>
 */
public class SlugGenerator {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
	private static final Pattern SEPARATORS = Pattern.compile("[^a-z0-9]+");

	private final Clock clock;
	private final Supplier<String> suffixSupplier;

	public SlugGenerator() {
		this(Clock.systemUTC(), () -> UUID.randomUUID().toString().replace("-", "").substring(0, 6));
	}

	SlugGenerator(Clock clock, Supplier<String> suffixSupplier) {
		this.clock = clock;
		this.suffixSupplier = suffixSupplier;
	}

	public String generate(String title) {
		String slug = toAsciiSlug(title);
		if (!slug.isBlank()) {
			return slug;
		}

		return fallbackSlug();
	}

	private String toAsciiSlug(String title) {
		String slug = SEPARATORS.matcher(title.trim().toLowerCase(Locale.ROOT)).replaceAll("-");
		return trimHyphen(slug);
	}

	private String fallbackSlug() {
		String date = LocalDate.now(clock).format(DATE_FORMATTER);
		return "post-" + date + "-" + suffixSupplier.get();
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
