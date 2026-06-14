package com.jkky.blog.api.post.support;

import com.jkky.blog.api.post.dto.AdminPostDetailResponse;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.tag.entity.Tag;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PostAdminResponseAssembler {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	public AdminPostDetailResponse toDetailResponse(Post post, List<PostTag> postTags) {
		Category category = post.getCategory();
		List<Tag> tags = postTags.stream()
			.map(PostTag::getTag)
			.toList();

		return AdminPostDetailResponse.builder()
			.id(post.getId())
			.title(post.getTitle())
			.slug(post.getSlug())
			.description(post.getDescription())
			.category(category.getName())
			.categoryKey(category.getFilterKey())
			.tags(
				tags.stream()
					.map(Tag::getName)
					.toList()
			)
			.tagKeys(
				tags.stream()
					.map(Tag::getFilterKey)
					.toList()
			)
			.createdAt(
				toKstIsoDateTime(post.getCreatedAt())
			)
			.updatedAt(
				toKstIsoDateTime(post.getUpdatedAt())
			)
			.readingTime(post.getReadingTime())
			.viewCount(post.getViewCount())
			.author(post.getAuthor())
			.featured(post.isFeatured())
			.status(post.getStatus().getValue())
			.content(post.getContent())
			.build();
	}

	private String toKstIsoDateTime(LocalDateTime dateTime) {
		return dateTime.atOffset(ZoneOffset.UTC)
			.atZoneSameInstant(KST)
			.toOffsetDateTime()
			.toString();
	}
}
