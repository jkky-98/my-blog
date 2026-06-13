package com.jkky.blog.api.category.controller;

import com.jkky.blog.api.category.dto.CategorySummaryResponse;
import com.jkky.blog.api.category.service.CategoryPublicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryPublicController {

	private final CategoryPublicService categoryPublicService;

	@GetMapping
	public List<CategorySummaryResponse> getCategories() {
		return categoryPublicService.getCategories();
	}
}
