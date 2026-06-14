package com.jkky.blog.api.post.service;

import com.jkky.blog.api.common.error.BlogException;
import com.jkky.blog.api.common.error.ErrorCode;
import com.jkky.blog.api.common.error.RequestValidationException;
import com.jkky.blog.api.post.dto.AdminPostCreateRequest;
import com.jkky.blog.api.post.dto.AdminPostDetailResponse;
import com.jkky.blog.api.post.support.PostAdminCreateCommand;
import com.jkky.blog.api.post.support.PostAdminWriter;
import com.jkky.blog.domain.auth.entity.AdminUser;
import com.jkky.blog.domain.auth.repository.AdminUserRepository;
import com.jkky.blog.domain.category.policy.CategoryNamePolicy;
import com.jkky.blog.domain.post.entity.PostStatus;
import com.jkky.blog.domain.post.policy.PostDescriptionGenerator;
import com.jkky.blog.domain.post.policy.ReadingTimeCalculator;
import com.jkky.blog.domain.post.policy.SlugGenerator;
import com.jkky.blog.domain.tag.policy.TagInputCleaner;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostAdminService {

	private static final int MAX_TAG_COUNT = 10;
	private static final int MAX_TAG_NAME_LENGTH = 30;

	private final AdminUserRepository adminUserRepository;
	private final PostAdminWriter postAdminWriter;

	private final CategoryNamePolicy categoryNamePolicy = new CategoryNamePolicy();
	private final TagInputCleaner tagInputCleaner = new TagInputCleaner();
	private final SlugGenerator slugGenerator = new SlugGenerator();
	private final PostDescriptionGenerator descriptionGenerator = new PostDescriptionGenerator();
	private final ReadingTimeCalculator readingTimeCalculator = new ReadingTimeCalculator();

	@Transactional
	public AdminPostDetailResponse create(AdminPostCreateRequest request) {
		List<String> tagNames = cleanTagNames(request.tags());
		PostStatus status = parseStatus(request.status().trim());
		AdminUser adminUser = getCurrentAdmin();
		String title = request.title().trim();
		String content = request.content().trim();

		PostAdminCreateCommand command = PostAdminCreateCommand.builder()
			.title(title)
			.categoryNormalizedName(
				categoryNamePolicy.toNormalizedName(request.category())
			)
			.tagNames(tagNames)
			.slug(slugGenerator.generate(title))
			.description(descriptionGenerator.generate(content))
			.content(content)
			.readingTime(readingTimeCalculator.calculate(content))
			.author(adminUser.getDisplayName())
			.featured(request.featured())
			.status(status)
			.build();

		return postAdminWriter.create(command);
	}

	private List<String> cleanTagNames(List<String> tagNames) {
		List<String> cleanedTagNames = tagInputCleaner.clean(tagNames);
		if (cleanedTagNames.isEmpty()) {
			throw RequestValidationException.of("tags", "태그는 최소 1개 이상 입력해야 합니다.");
		}
		if (cleanedTagNames.size() > MAX_TAG_COUNT) {
			throw RequestValidationException.of("tags", "태그는 최대 10개까지 입력할 수 있습니다.");
		}
		boolean hasTooLongTagName = cleanedTagNames.stream()
			.anyMatch(tagName -> tagName.length() > MAX_TAG_NAME_LENGTH);
		if (hasTooLongTagName) {
			throw RequestValidationException.of("tags", "태그 이름은 최대 30자까지 입력할 수 있습니다.");
		}

		return cleanedTagNames;
	}

	private PostStatus parseStatus(String status) {
		try {
			return PostStatus.fromValue(status);
		} catch (IllegalArgumentException exception) {
			throw RequestValidationException.of("status", "상태는 draft, published, hidden 중 하나여야 합니다.");
		}
	}

	private AdminUser getCurrentAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BlogException(ErrorCode.UNAUTHENTICATED);
		}

		return adminUserRepository.findByUsername(authentication.getName())
			.orElseThrow(() -> new BlogException(ErrorCode.UNAUTHENTICATED));
	}
}
