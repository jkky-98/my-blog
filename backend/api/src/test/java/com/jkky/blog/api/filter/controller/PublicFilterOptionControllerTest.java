package com.jkky.blog.api.filter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jkky.blog.api.BlogApiApplication;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.category.repository.CategoryRepository;
import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostStatus;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.post.repository.PostRepository;
import com.jkky.blog.domain.post.repository.PostTagRepository;
import com.jkky.blog.domain.tag.entity.Tag;
import com.jkky.blog.domain.tag.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = BlogApiApplication.class)
@AutoConfigureMockMvc
@DisplayName("공개 필터 옵션 API")
@TestPropertySource(properties = {
	"spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
	"spring.datasource.url=jdbc:mysql://${DB_TEST_HOST:localhost}:${DB_TEST_PORT:3307}/${DB_TEST_NAME:jkky_blog_test}?createDatabaseIfNotExist=true&serverTimezone=UTC&connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true&useUnicode=true&characterEncoding=utf8",
	"spring.datasource.username=${DB_TEST_USERNAME:root}",
	"spring.datasource.password=${DB_TEST_PASSWORD:root}",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
	"blog.admin.username=admin",
	"blog.admin.password=password",
	"blog.admin.display-name=Jin"
})
class PublicFilterOptionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostTagRepository postTagRepository;

	@BeforeEach
	void setUp() {
		postTagRepository.deleteAllInBatch();
		postRepository.deleteAllInBatch();
		tagRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("카테고리는 공개 글 개수와 함께 개수 내림차순, 이름 오름차순으로 조회한다")
	void getCategoriesReturnsPublishedPostCounts() throws Exception {
		Category backend = saveCategory("Backend", "backend");
		Category frontend = saveCategory("Frontend", "frontend");
		Category design = saveCategory("Design", "design");
		Category hiddenOnly = saveCategory("Hidden", "hidden");
		savePost(backend, "Backend 1", "backend-1", PostStatus.PUBLISHED);
		savePost(backend, "Backend 2", "backend-2", PostStatus.PUBLISHED);
		savePost(frontend, "Frontend 1", "frontend-1", PostStatus.PUBLISHED);
		savePost(design, "Design 1", "design-1", PostStatus.PUBLISHED);
		savePost(hiddenOnly, "Draft Only", "draft-only", PostStatus.DRAFT);
		savePost(hiddenOnly, "Hidden Only", "hidden-only", PostStatus.HIDDEN);

		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value("Backend"))
			.andExpect(jsonPath("$[0].key").value("backend"))
			.andExpect(jsonPath("$[0].count").value(2))
			.andExpect(jsonPath("$[1].name").value("Design"))
			.andExpect(jsonPath("$[1].key").value("design"))
			.andExpect(jsonPath("$[1].count").value(1))
			.andExpect(jsonPath("$[2].name").value("Frontend"))
			.andExpect(jsonPath("$[2].key").value("frontend"))
			.andExpect(jsonPath("$[2].count").value(1))
			.andExpect(jsonPath("$[3]").doesNotExist());
	}

	@Test
	@DisplayName("태그는 공개 글 개수와 함께 개수 내림차순, 이름 오름차순으로 조회한다")
	void getTagsReturnsPublishedPostCounts() throws Exception {
		Category category = saveCategory("Backend", "backend");
		Tag redis = saveTag("Redis", "redis");
		Tag spring = saveTag("Spring Boot", "spring-boot");
		Tag jpa = saveTag("JPA", "jpa");
		Tag hiddenOnly = saveTag("Hidden", "hidden");
		Post redisPost = savePost(category, "Redis Post", "redis-post", PostStatus.PUBLISHED);
		savePostTags(redisPost, redis, spring);
		Post anotherRedisPost = savePost(category, "Another Redis Post", "another-redis-post", PostStatus.PUBLISHED);
		savePostTags(anotherRedisPost, redis, jpa);
		Post draftPost = savePost(category, "Draft Hidden Tag", "draft-hidden-tag", PostStatus.DRAFT);
		savePostTags(draftPost, hiddenOnly);

		mockMvc.perform(get("/api/tags"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value("Redis"))
			.andExpect(jsonPath("$[0].key").value("redis"))
			.andExpect(jsonPath("$[0].count").value(2))
			.andExpect(jsonPath("$[1].name").value("JPA"))
			.andExpect(jsonPath("$[1].key").value("jpa"))
			.andExpect(jsonPath("$[1].count").value(1))
			.andExpect(jsonPath("$[2].name").value("Spring Boot"))
			.andExpect(jsonPath("$[2].key").value("spring-boot"))
			.andExpect(jsonPath("$[2].count").value(1))
			.andExpect(jsonPath("$[3]").doesNotExist());
	}

	@Test
	@DisplayName("공개 글이 없으면 카테고리와 태그는 빈 목록을 반환한다")
	void getFilterOptionsReturnsEmptyLists() throws Exception {
		Category category = saveCategory("Backend", "backend");
		Tag tag = saveTag("Redis", "redis");
		Post draftPost = savePost(category, "Draft Post", "draft-post", PostStatus.DRAFT);
		savePostTags(draftPost, tag);

		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]").doesNotExist());

		mockMvc.perform(get("/api/tags"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]").doesNotExist());
	}

	private Category saveCategory(String name, String filterKey) {
		return categoryRepository.save(Category.builder()
			.name(name)
			.normalizedName(name.toLowerCase())
			.filterKey(filterKey)
			.build());
	}

	private Tag saveTag(String name, String filterKey) {
		return tagRepository.save(Tag.builder()
			.name(name)
			.normalizedName(name.toLowerCase())
			.filterKey(filterKey)
			.build());
	}

	private Post savePost(Category category, String title, String slug, PostStatus status) {
		return postRepository.saveAndFlush(Post.builder()
			.category(category)
			.title(title)
			.slug(slug)
			.description(title + " description")
			.content("# " + title)
			.readingTime(3)
			.author("Jin")
			.featured(false)
			.status(status)
			.build());
	}

	private void savePostTags(Post post, Tag... tags) {
		for (Tag tag : tags) {
			postTagRepository.save(PostTag.builder()
				.post(post)
				.tag(tag)
				.build());
		}
	}
}
