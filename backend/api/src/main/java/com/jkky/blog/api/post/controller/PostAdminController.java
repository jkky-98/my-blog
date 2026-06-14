package com.jkky.blog.api.post.controller;

import com.jkky.blog.api.post.dto.AdminPostCreateRequest;
import com.jkky.blog.api.post.dto.AdminPostDetailResponse;
import com.jkky.blog.api.post.service.PostAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class PostAdminController {

	private final PostAdminService postAdminService;

	@PostMapping
	public ResponseEntity<AdminPostDetailResponse> create(@Valid @RequestBody AdminPostCreateRequest request) {
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(postAdminService.create(request));
	}
}
