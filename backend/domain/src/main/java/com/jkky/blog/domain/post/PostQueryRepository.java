package com.jkky.blog.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

	Page<Post> findPublicPosts(PublicPostSearchCondition condition, Pageable pageable);
}
