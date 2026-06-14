package com.jkky.blog.api.category.service;

import com.jkky.blog.api.category.dto.CategoryCreateRequest;
import com.jkky.blog.api.category.dto.CategorySummaryResponse;
import com.jkky.blog.api.common.error.BlogException;
import com.jkky.blog.api.common.error.ErrorCode;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.category.policy.CategoryNamePolicy;
import com.jkky.blog.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryAdminService {

	private final CategoryRepository categoryRepository;
	private final CategoryNamePolicy categoryNamePolicy = new CategoryNamePolicy();

	@Transactional
	public CategorySummaryResponse create(CategoryCreateRequest request) {
		String displayName = categoryNamePolicy.toDisplayName(request.name());
		String normalizedName = categoryNamePolicy.toNormalizedName(displayName);

		if (categoryRepository.existsByNormalizedName(normalizedName)) {
			throw new BlogException(ErrorCode.CATEGORY_ALREADY_EXISTS);
		}

		Category category = categoryRepository.save(Category.builder()
			.name(displayName)
			.normalizedName(normalizedName)
			.filterKey(categoryNamePolicy.toFilterKey(displayName))
			.build());

		return CategorySummaryResponse.builder()
			.name(category.getName())
			.key(category.getFilterKey())
			.count(0)
			.build();
	}
}
