package com.jkky.blog.api.post.support;

import com.jkky.blog.api.post.dto.PostDetailResponse;
import com.jkky.blog.api.post.dto.PostListResponse;
import com.jkky.blog.api.post.dto.PostSummaryResponse;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.tag.entity.Tag;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PostResponseAssembler {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	public PostListResponse toListResponse(PostPageData pageData, Pageable pageable) {
		List<PostSummaryResponse> items = pageData.posts().getContent().stream()
			.map(post -> toSummaryResponse(post, pageData.tagsOf(post)))
			.toList();

		return PostListResponse.builder()
			.items(items)
			.page(pageable.getPageNumber() + 1)
			.size(pageable.getPageSize())
			.totalCount(pageData.posts().getTotalElements())
			.totalPages(pageData.posts().getTotalPages())
			.build();
	}

	public PostDetailResponse toDetailResponse(Post post, List<PostTag> postTags) {
		Category category = post.getCategory();
		List<Tag> tags = tagsOf(postTags);

		return PostDetailResponse.builder()
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
			.content(post.getContent())
			.build();
	}

	private PostSummaryResponse toSummaryResponse(Post post, List<PostTag> postTags) {
		Category category = post.getCategory();
		List<Tag> tags = tagsOf(postTags);

		return PostSummaryResponse.builder()
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
			.build();
	}

	private List<Tag> tagsOf(List<PostTag> postTags) {
		return postTags.stream()
			.map(PostTag::getTag)
			.toList();
	}

	private String toKstIsoDateTime(LocalDateTime dateTime) {
		return dateTime.atOffset(ZoneOffset.UTC)
			.atZoneSameInstant(KST)
			.toOffsetDateTime()
			.toString();
	}
}
