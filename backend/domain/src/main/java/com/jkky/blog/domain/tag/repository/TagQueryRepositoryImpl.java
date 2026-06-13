package com.jkky.blog.domain.tag.repository;

import static com.jkky.blog.domain.post.entity.QPost.post;
import static com.jkky.blog.domain.post.entity.QPostTag.postTag;
import static com.jkky.blog.domain.tag.entity.QTag.tag;

import com.jkky.blog.domain.post.entity.PostStatus;
import com.jkky.blog.domain.tag.repository.projection.TagPostCount;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

public class TagQueryRepositoryImpl implements TagQueryRepository {

	private final JPAQueryFactory queryFactory;

	public TagQueryRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public List<TagPostCount> findPublicTagPostCounts() {
		List<Tuple> rows = queryFactory
			.select(tag.name, tag.filterKey, post.id.count())
			.from(postTag)
			.join(postTag.post, post)
			.join(postTag.tag, tag)
			.where(post.status.eq(PostStatus.PUBLISHED))
			.groupBy(tag.id, tag.name, tag.filterKey)
			.orderBy(post.id.count().desc(), tag.name.asc())
			.fetch();

		return rows.stream()
			.map(row -> TagPostCount.builder()
				.name(row.get(tag.name))
				.filterKey(row.get(tag.filterKey))
				.count(row.get(post.id.count()))
				.build())
			.toList();
	}
}
