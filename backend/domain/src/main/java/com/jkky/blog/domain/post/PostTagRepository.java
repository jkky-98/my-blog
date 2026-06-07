package com.jkky.blog.domain.post;

import com.jkky.blog.domain.tag.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

	boolean existsByPostAndTag(Post post, Tag tag);

	List<PostTag> findByPost(Post post);
}
