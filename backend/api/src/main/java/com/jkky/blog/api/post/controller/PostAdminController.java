package com.jkky.blog.api.post.controller;

import com.jkky.blog.api.post.dto.AdminPostDetailResponse;
import com.jkky.blog.api.post.dto.AdminPostListResponse;
import com.jkky.blog.api.post.dto.AdminPostSaveRequest;
import com.jkky.blog.api.post.dto.AdminPostStatusUpdateRequest;
import com.jkky.blog.api.post.service.PostAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class PostAdminController {

	private final PostAdminService postAdminService;

	@GetMapping
	public AdminPostListResponse getPosts(
		@RequestParam(required = false) String status,
		@RequestParam(required = false) String categoryKey,
		@RequestParam(required = false) String tagKey,
		@RequestParam(required = false) String keyword,
		@PageableDefault(size = 10) Pageable pageable
	) {
		return postAdminService.getPosts(status, categoryKey, tagKey, keyword, pageable);
	}

	@GetMapping("/{id}")
	public AdminPostDetailResponse getDetail(@PathVariable Long id) {
		return postAdminService.getDetail(id);
	}

	@PostMapping
	public ResponseEntity<AdminPostDetailResponse> create(@Valid @RequestBody AdminPostSaveRequest request) {
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(postAdminService.create(request));
	}

	@PutMapping("/{id}")
	public AdminPostDetailResponse update(
		@PathVariable Long id,
		@Valid @RequestBody AdminPostSaveRequest request
	) {
		return postAdminService.update(id, request);
	}

	@PatchMapping("/{id}/status")
	public AdminPostDetailResponse updateStatus(
		@PathVariable Long id,
		@Valid @RequestBody AdminPostStatusUpdateRequest request
	) {
		return postAdminService.updateStatus(id, request);
	}
}
