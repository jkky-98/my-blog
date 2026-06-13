package com.jkky.blog.domain.post.repository;

import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.repository.condition.PublicPostSearchCondition;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

	Page<Post> findPublicPosts(PublicPostSearchCondition condition, Pageable pageable);

	List<Post> findPopularPosts(int limit);

	List<Post> findFeaturedPosts();

	Optional<Post> findBySlugWithCategory(String slug);
}
