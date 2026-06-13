package com.jkky.blog.api.post.support;

import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import java.util.List;
import java.util.Map;

public record PostSummaryData(
	List<Post> posts,
	Map<Long, List<PostTag>> tagsByPostId
) {
	public List<PostTag> tagsOf(Post post) {
		return tagsByPostId.getOrDefault(post.getId(), List.of());
	}
}
