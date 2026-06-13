package com.jkky.blog.domain.category.repository;

import com.jkky.blog.domain.category.repository.projection.CategoryPostCount;
import java.util.List;

public interface CategoryQueryRepository {

	List<CategoryPostCount> findPublicCategoryPostCounts();
}
