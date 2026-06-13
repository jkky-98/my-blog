package com.jkky.blog.api.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
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
}
