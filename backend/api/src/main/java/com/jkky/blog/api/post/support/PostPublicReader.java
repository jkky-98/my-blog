package com.jkky.blog.api.post.support;

import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.post.repository.PostRepository;
import com.jkky.blog.domain.post.repository.PostTagRepository;
import com.jkky.blog.domain.post.repository.condition.PublicPostSearchCondition;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostPublicReader {

	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;

	public PostPageData readPublicPosts(PublicPostSearchCondition condition, Pageable pageable) {
		Page<Post> posts = postRepository.findPublicPosts(condition, pageable);
		if (posts.isEmpty()) {
			return new PostPageData(posts, Map.of());
		}

		return new PostPageData(posts, readTagsByPostId(posts.getContent()));
	}

	public PostSummaryData readPopularPosts(int limit) {
		List<Post> posts = postRepository.findPopularPosts(limit);
		if (posts.isEmpty()) {
			return new PostSummaryData(posts, Map.of());
		}

		return new PostSummaryData(posts, readTagsByPostId(posts));
	}

	public Optional<Post> readPostBySlug(String slug) {
		return postRepository.findBySlugWithCategory(slug);
	}

	public List<PostTag> readPostTags(Post post) {
		return postTagRepository.findByPostOrderByIdAsc(post);
	}

	private Map<Long, List<PostTag>> readTagsByPostId(List<Post> posts) {
		List<PostTag> postTags = postTagRepository.findByPostInOrderByIdAsc(posts);

		return postTags.stream()
			.collect(Collectors.groupingBy(
				postTag -> postTag.getPost().getId(),
				LinkedHashMap::new,
				Collectors.toList()
			));
	}
}
