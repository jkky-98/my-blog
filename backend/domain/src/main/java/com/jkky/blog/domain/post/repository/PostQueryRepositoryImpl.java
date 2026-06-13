package com.jkky.blog.domain.post.repository;

import static com.jkky.blog.domain.category.entity.QCategory.category;
import static com.jkky.blog.domain.post.entity.QPost.post;

import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostStatus;
import com.jkky.blog.domain.post.entity.QPostTag;
import com.jkky.blog.domain.post.repository.condition.PublicPostSearchCondition;
import com.jkky.blog.domain.tag.entity.QTag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public class PostQueryRepositoryImpl implements PostQueryRepository {

	private final JPAQueryFactory queryFactory;

	public PostQueryRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public Page<Post> findPublicPosts(PublicPostSearchCondition condition, Pageable pageable) {
		List<Post> content = queryFactory
			.selectFrom(post)
			.join(post.category, category).fetchJoin()
			.where(
				post.status.eq(PostStatus.PUBLISHED),
				categoryKeyEq(condition.categoryKey()),
				tagKeyExists(condition.tagKey())
			)
			.orderBy(post.createdAt.desc(), post.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(post.count())
			.from(post)
			.join(post.category, category)
			.where(
				post.status.eq(PostStatus.PUBLISHED),
				categoryKeyEq(condition.categoryKey()),
				tagKeyExists(condition.tagKey())
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, total == null ? 0L : total);
	}

	@Override
	public List<Post> findPopularPosts(int limit) {
		return queryFactory
			.selectFrom(post)
			.join(post.category, category).fetchJoin()
			.where(post.status.eq(PostStatus.PUBLISHED))
			.orderBy(post.viewCount.desc(), post.createdAt.desc(), post.id.desc())
			.limit(limit)
			.fetch();
	}

	@Override
	public List<Post> findFeaturedPosts() {
		return queryFactory
			.selectFrom(post)
			.join(post.category, category).fetchJoin()
			.where(
				post.status.eq(PostStatus.PUBLISHED),
				post.featured.isTrue()
			)
			.orderBy(post.id.asc())
			.fetch();
	}

	@Override
	public Optional<Post> findBySlugWithCategory(String slug) {
		Post foundPost = queryFactory
			.selectFrom(post)
			.join(post.category, category).fetchJoin()
			.where(post.slug.eq(slug))
			.fetchOne();

		return Optional.ofNullable(foundPost);
	}

	private BooleanExpression categoryKeyEq(String categoryKey) {
		if (!StringUtils.hasText(categoryKey)) {
			return null;
		}

		return category.filterKey.eq(categoryKey);
	}

	private BooleanExpression tagKeyExists(String tagKey) {
		if (!StringUtils.hasText(tagKey)) {
			return null;
		}

		QPostTag postTag = QPostTag.postTag;
		QTag tag = QTag.tag;

		return JPAExpressions
			.selectOne()
			.from(postTag)
			.join(postTag.tag, tag)
			.where(
				postTag.post.eq(post),
				tag.filterKey.eq(tagKey)
			)
			.exists();
	}
}
