package com.jkky.blog.api.post.controller;

import com.jkky.blog.api.post.dto.PostDetailResponse;
import com.jkky.blog.api.post.dto.PostListResponse;
import com.jkky.blog.api.post.dto.PostSummaryResponse;
import com.jkky.blog.api.post.service.PostPublicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostPublicController {

	private final PostPublicService postPublicService;

	@GetMapping
	public PostListResponse getPosts(
		@RequestParam(required = false) String categoryKey,
		@RequestParam(required = false) String tagKey,
		Pageable pageable
	) {
		return postPublicService.getPosts(categoryKey, tagKey, pageable);
	}

	@GetMapping("/popular")
	public List<PostSummaryResponse> getPopularPosts(@RequestParam(required = false) Integer limit) {
		return postPublicService.getPopularPosts(limit);
	}

	@GetMapping("/{slug}")
	public PostDetailResponse getPost(@PathVariable String slug) {
		return postPublicService.getPost(slug);
	}
}
