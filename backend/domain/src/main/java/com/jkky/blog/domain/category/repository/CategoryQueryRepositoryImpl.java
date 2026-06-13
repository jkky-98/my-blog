package com.jkky.blog.domain.category.repository;

import static com.jkky.blog.domain.category.entity.QCategory.category;
import static com.jkky.blog.domain.post.entity.QPost.post;

import com.jkky.blog.domain.category.repository.projection.CategoryPostCount;
import com.jkky.blog.domain.post.entity.PostStatus;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

	private final JPAQueryFactory queryFactory;

	public CategoryQueryRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public List<CategoryPostCount> findPublicCategoryPostCounts() {
		List<Tuple> rows = queryFactory
			.select(category.name, category.filterKey, post.id.count())
			.from(post)
			.join(post.category, category)
			.where(post.status.eq(PostStatus.PUBLISHED))
			.groupBy(category.id, category.name, category.filterKey)
			.orderBy(post.id.count().desc(), category.name.asc())
			.fetch();

		return rows.stream()
			.map(row -> CategoryPostCount.builder()
				.name(row.get(category.name))
				.filterKey(row.get(category.filterKey))
				.count(row.get(post.id.count()))
				.build())
			.toList();
	}
}
