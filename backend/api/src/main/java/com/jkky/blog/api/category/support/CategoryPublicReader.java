package com.jkky.blog.api.category.support;

import com.jkky.blog.domain.category.repository.CategoryRepository;
import com.jkky.blog.domain.category.repository.projection.CategoryPostCount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryPublicReader {

	private final CategoryRepository categoryRepository;

	public List<CategoryPostCount> readPublicCategoryPostCounts() {
		return categoryRepository.findPublicCategoryPostCounts();
	}
}
