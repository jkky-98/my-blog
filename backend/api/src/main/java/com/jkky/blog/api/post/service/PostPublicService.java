package com.jkky.blog.api.post.service;

import com.jkky.blog.api.common.error.BlogException;
import com.jkky.blog.api.common.error.ErrorCode;
import com.jkky.blog.api.post.dto.PostDetailResponse;
import com.jkky.blog.api.post.dto.PostListResponse;
import com.jkky.blog.api.post.support.PostPageData;
import com.jkky.blog.api.post.support.PostPublicReader;
import com.jkky.blog.api.post.support.PostResponseAssembler;
import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostStatus;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.post.repository.condition.PublicPostSearchCondition;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostPublicService {

	private final PostPublicReader postPublicReader;
	private final PostResponseAssembler postResponseAssembler;

	public PostListResponse getPosts(String categoryKey, String tagKey, Pageable pageable) {
		PublicPostSearchCondition condition = PublicPostSearchCondition.builder()
			.categoryKey(categoryKey)
			.tagKey(tagKey)
			.build();
		PostPageData pageData = postPublicReader.readPublicPosts(condition, pageable);

		return postResponseAssembler.toListResponse(pageData, pageable);
	}

	public PostDetailResponse getPost(String slug) {
		Post post = postPublicReader.readPostBySlug(slug)
			.orElseThrow(() -> new BlogException(ErrorCode.POST_NOT_FOUND));

		if (post.getStatus() != PostStatus.PUBLISHED) {
			throw new BlogException(ErrorCode.POST_NOT_PUBLIC);
		}

		List<PostTag> postTags = postPublicReader.readPostTags(post);

		return postResponseAssembler.toDetailResponse(post, postTags);
	}
}
