package com.jkky.blog.api.tag.support;

import com.jkky.blog.domain.tag.repository.TagRepository;
import com.jkky.blog.domain.tag.repository.projection.TagPostCount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagPublicReader {

	private final TagRepository tagRepository;

	public List<TagPostCount> readPublicTagPostCounts() {
		return tagRepository.findPublicTagPostCounts();
	}
}
