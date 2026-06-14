package com.jkky.blog.api.post.support;

import com.jkky.blog.api.common.error.RequestValidationException;
import com.jkky.blog.api.post.dto.AdminPostDetailResponse;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.category.repository.CategoryRepository;
import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.post.repository.PostRepository;
import com.jkky.blog.domain.post.repository.PostTagRepository;
import com.jkky.blog.domain.tag.entity.Tag;
import com.jkky.blog.domain.tag.policy.TagNamePolicy;
import com.jkky.blog.domain.tag.repository.TagRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostAdminWriter {

	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;
	private final PostRepository postRepository;
	private final PostTagRepository postTagRepository;
	private final PostAdminResponseAssembler responseAssembler;

	private final TagNamePolicy tagNamePolicy = new TagNamePolicy();

	public AdminPostDetailResponse create(PostAdminCreateCommand command) {
		Category category = findCategory(command.categoryNormalizedName());
		List<Tag> tags = findOrCreateTags(command.tagNames());
		String slug = uniqueSlug(command.slug());

		Post post = Post.builder()
			.category(category)
			.title(command.title())
			.slug(slug)
			.description(command.description())
			.content(command.content())
			.readingTime(command.readingTime())
			.author(command.author())
			.featured(command.featured())
			.status(command.status())
			.build();

		Post savedPost = postRepository.saveAndFlush(post);
		List<PostTag> postTags = savePostTags(savedPost, tags);

		return responseAssembler.toDetailResponse(savedPost, postTags);
	}

	private Category findCategory(String normalizedName) {
		return categoryRepository.findByNormalizedName(normalizedName)
			.orElseThrow(() -> RequestValidationException.of("category", "존재하지 않는 카테고리입니다."));
	}

	private List<Tag> findOrCreateTags(List<String> tagNames) {
		List<String> normalizedNames = tagNames.stream()
			.map(tagNamePolicy::toNormalizedName)
			.toList();

		Map<String, Tag> existingTags = tagRepository.findByNormalizedNameIn(normalizedNames).stream()
			.collect(Collectors.toMap(
				Tag::getNormalizedName,
				Function.identity(),
				(left, right) -> left,
				LinkedHashMap::new
			));

		List<Tag> tags = new ArrayList<>();
		for (String tagName : tagNames) {
			String normalizedName = tagNamePolicy.toNormalizedName(tagName);
			Tag tag = existingTags.computeIfAbsent(normalizedName, key -> createTag(tagName));
			tags.add(tag);
		}

		return tags;
	}

	private Tag createTag(String tagName) {
		Tag tag = Tag.builder()
			.name(tagName)
			.normalizedName(tagNamePolicy.toNormalizedName(tagName))
			.filterKey(tagNamePolicy.toFilterKey(tagName))
			.build();

		return tagRepository.save(tag);
	}

	private String uniqueSlug(String slug) {
		String candidate = slug;
		int suffix = 2;
		while (postRepository.existsBySlug(candidate)) {
			candidate = slug + "-" + suffix;
			suffix++;
		}

		return candidate;
	}

	private List<PostTag> savePostTags(Post post, List<Tag> tags) {
		List<PostTag> postTags = tags.stream()
			.map(tag -> PostTag.builder()
				.post(post)
				.tag(tag)
				.build())
			.toList();

		return postTagRepository.saveAll(postTags);
	}
}
