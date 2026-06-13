package com.jkky.blog.api.tag.service;

import com.jkky.blog.api.tag.dto.TagSummaryResponse;
import com.jkky.blog.api.tag.support.TagPublicReader;
import com.jkky.blog.api.tag.support.TagResponseAssembler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagPublicService {

	private final TagPublicReader tagPublicReader;
	private final TagResponseAssembler tagResponseAssembler;

	public List<TagSummaryResponse> getTags() {
		return tagResponseAssembler.toSummaryResponses(
			tagPublicReader.readPublicTagPostCounts()
		);
	}
}
