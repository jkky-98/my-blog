package com.jkky.blog.api.tag.support;

import com.jkky.blog.api.tag.dto.TagSummaryResponse;
import com.jkky.blog.domain.tag.repository.projection.TagPostCount;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TagResponseAssembler {

	public List<TagSummaryResponse> toSummaryResponses(List<TagPostCount> tagPostCounts) {
		return tagPostCounts.stream()
			.map(tagPostCount -> TagSummaryResponse.builder()
				.name(tagPostCount.name())
				.key(tagPostCount.filterKey())
				.count(tagPostCount.count())
				.build())
			.toList();
	}
}
