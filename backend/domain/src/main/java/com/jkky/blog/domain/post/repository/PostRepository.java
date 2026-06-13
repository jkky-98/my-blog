package com.jkky.blog.domain.post.repository;

import com.jkky.blog.domain.post.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

	Optional<Post> findBySlug(String slug);

	boolean existsBySlug(String slug);
}
