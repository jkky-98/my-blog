package com.jkky.blog.api.category.support;

import com.jkky.blog.api.category.dto.CategorySummaryResponse;
import com.jkky.blog.domain.category.repository.projection.CategoryPostCount;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategoryResponseAssembler {

	public List<CategorySummaryResponse> toSummaryResponses(List<CategoryPostCount> categoryPostCounts) {
		return categoryPostCounts.stream()
			.map(categoryPostCount -> CategorySummaryResponse.builder()
				.name(categoryPostCount.name())
				.key(categoryPostCount.filterKey())
				.count(categoryPostCount.count())
				.build())
			.toList();
	}
}
