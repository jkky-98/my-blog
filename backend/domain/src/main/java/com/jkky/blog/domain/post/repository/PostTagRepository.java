package com.jkky.blog.domain.post.repository;

import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.tag.entity.Tag;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

	boolean existsByPostAndTag(Post post, Tag tag);

	List<PostTag> findByPost(Post post);

	@EntityGraph(attributePaths = "tag")
	List<PostTag> findByPostOrderByIdAsc(Post post);

	@EntityGraph(attributePaths = "tag")
	List<PostTag> findByPostInOrderByIdAsc(Collection<Post> posts);
}
