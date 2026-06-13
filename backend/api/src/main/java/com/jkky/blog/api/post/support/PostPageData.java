package com.jkky.blog.api.post.support;

import com.jkky.blog.domain.post.Post;
import com.jkky.blog.domain.post.PostTag;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public record PostPageData(
	Page<Post> posts,
	Map<Long, List<PostTag>> tagsByPostId
) {
	public List<PostTag> tagsOf(Post post) {
		return tagsByPostId.getOrDefault(post.getId(), List.of());
	}
}
