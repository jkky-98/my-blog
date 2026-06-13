package com.jkky.blog.api.tag.controller;

import com.jkky.blog.api.tag.dto.TagSummaryResponse;
import com.jkky.blog.api.tag.service.TagPublicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagPublicController {

	private final TagPublicService tagPublicService;

	@GetMapping
	public List<TagSummaryResponse> getTags() {
		return tagPublicService.getTags();
	}
}
