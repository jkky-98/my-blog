package com.jkky.blog.api.post.support;

import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.post.repository.PostRepository;
import com.jkky.blog.domain.post.repository.PostTagRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostAdminReader {

	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;

	public Optional<Post> readPost(Long id) {
		return postRepository.findById(id);
	}

	public List<PostTag> readPostTags(Post post) {
		return postTagRepository.findByPostOrderByIdAsc(post);
	}
}
