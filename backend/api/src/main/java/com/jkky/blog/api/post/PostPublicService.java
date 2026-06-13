package com.jkky.blog.api.post;

import com.jkky.blog.api.post.support.PostPageData;
import com.jkky.blog.api.post.support.PostPublicReader;
import com.jkky.blog.api.post.support.PostSummaryAssembler;
import com.jkky.blog.domain.post.PublicPostSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostPublicService {

	private final PostPublicReader postPublicReader;
	private final PostSummaryAssembler postSummaryAssembler;

	public PostListResponse getPosts(String categoryKey, String tagKey, Pageable pageable) {
		PublicPostSearchCondition condition = PublicPostSearchCondition.builder()
			.categoryKey(categoryKey)
			.tagKey(tagKey)
			.build();
		PostPageData pageData = postPublicReader.readPublicPosts(condition, pageable);

		return postSummaryAssembler.toListResponse(pageData, pageable);
	}
}
