package com.jkky.blog.api.category.controller;

import com.jkky.blog.api.category.dto.CategoryCreateRequest;
import com.jkky.blog.api.category.dto.CategorySummaryResponse;
import com.jkky.blog.api.category.service.CategoryAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

	private final CategoryAdminService categoryAdminService;

	@PostMapping
	public ResponseEntity<CategorySummaryResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(categoryAdminService.create(request));
	}
}
