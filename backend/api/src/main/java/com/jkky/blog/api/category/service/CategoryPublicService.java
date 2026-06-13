package com.jkky.blog.api.category.service;

import com.jkky.blog.api.category.dto.CategorySummaryResponse;
import com.jkky.blog.api.category.support.CategoryPublicReader;
import com.jkky.blog.api.category.support.CategoryResponseAssembler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryPublicService {

	private final CategoryPublicReader categoryPublicReader;
	private final CategoryResponseAssembler categoryResponseAssembler;

	public List<CategorySummaryResponse> getCategories() {
		return categoryResponseAssembler.toSummaryResponses(
			categoryPublicReader.readPublicCategoryPostCounts()
		);
	}
}
