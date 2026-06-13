package com.jkky.blog.api.post.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
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
@DisplayName("공개 글 조회 API")
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
class PostPublicControllerTest {

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
	@DisplayName("공개 글만 최신순으로 조회하고 요약 정보를 반환한다")
	void getPostsReturnsPublishedSummaries() throws Exception {
		Category category = saveCategory("Backend", "backend");
		Tag spring = saveTag("Spring Boot", "spring-boot");
		Tag redis = saveTag("Redis", "redis");
		Post oldPost = savePost(category, "Old Published", "old-published", PostStatus.PUBLISHED);
		savePostTags(oldPost, spring, redis);
		Thread.sleep(10L);
		Post newPost = savePost(category, "New Published", "new-published", PostStatus.PUBLISHED);
		savePostTags(newPost, redis);
		savePost(category, "Draft Post", "draft-post", PostStatus.DRAFT);

		mockMvc.perform(get("/api/posts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(2)))
			.andExpect(jsonPath("$.items[0].id").value(newPost.getId()))
			.andExpect(jsonPath("$.items[0].title").value("New Published"))
			.andExpect(jsonPath("$.items[0].slug").value("new-published"))
			.andExpect(jsonPath("$.items[0].description").value("New Published description"))
			.andExpect(jsonPath("$.items[0].category").value("Backend"))
			.andExpect(jsonPath("$.items[0].categoryKey").value("backend"))
			.andExpect(jsonPath("$.items[0].tags[0]").value("Redis"))
			.andExpect(jsonPath("$.items[0].tagKeys[0]").value("redis"))
			.andExpect(jsonPath("$.items[0].createdAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.items[0].updatedAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.items[0].readingTime").value(3))
			.andExpect(jsonPath("$.items[0].viewCount").value(0))
			.andExpect(jsonPath("$.items[0].author").value("Jin"))
			.andExpect(jsonPath("$.items[0].featured").value(false))
			.andExpect(jsonPath("$.items[1].id").value(oldPost.getId()))
			.andExpect(jsonPath("$.items[1].tags[0]").value("Spring Boot"))
			.andExpect(jsonPath("$.items[1].tags[1]").value("Redis"))
			.andExpect(jsonPath("$.page").value(1))
			.andExpect(jsonPath("$.size").value(6))
			.andExpect(jsonPath("$.totalCount").value(2))
			.andExpect(jsonPath("$.totalPages").value(1));
	}

	@Test
	@DisplayName("기본 페이지 크기는 6개이고 페이지 메타 정보를 반환한다")
	void getPostsUsesDefaultPagination() throws Exception {
		Category category = saveCategory("Backend", "backend");
		for (int index = 1; index <= 7; index++) {
			savePost(category, "Post " + index, "post-" + index, PostStatus.PUBLISHED);
		}

		mockMvc.perform(get("/api/posts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(6)))
			.andExpect(jsonPath("$.page").value(1))
			.andExpect(jsonPath("$.size").value(6))
			.andExpect(jsonPath("$.totalCount").value(7))
			.andExpect(jsonPath("$.totalPages").value(2));
	}

	@Test
	@DisplayName("categoryKey와 tagKey로 공개 글 목록을 필터링한다")
	void getPostsFiltersByCategoryKeyAndTagKey() throws Exception {
		Category backend = saveCategory("Backend", "backend");
		Category frontend = saveCategory("Frontend", "frontend");
		Tag redis = saveTag("Redis", "redis");
		Tag spring = saveTag("Spring Boot", "spring-boot");
		Post backendRedis = savePost(backend, "Backend Redis", "backend-redis", PostStatus.PUBLISHED);
		savePostTags(backendRedis, redis);
		Post backendSpring = savePost(backend, "Backend Spring", "backend-spring", PostStatus.PUBLISHED);
		savePostTags(backendSpring, spring);
		Post frontendRedis = savePost(frontend, "Frontend Redis", "frontend-redis", PostStatus.PUBLISHED);
		savePostTags(frontendRedis, redis);
		Post draftBackendRedis = savePost(backend, "Draft Backend Redis", "draft-backend-redis", PostStatus.DRAFT);
		savePostTags(draftBackendRedis, redis);

		mockMvc.perform(get("/api/posts")
				.param("categoryKey", "backend")
				.param("tagKey", "redis"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(1)))
			.andExpect(jsonPath("$.items[0].id").value(backendRedis.getId()))
			.andExpect(jsonPath("$.items[0].categoryKey").value("backend"))
			.andExpect(jsonPath("$.items[0].tagKeys[0]").value("redis"))
			.andExpect(jsonPath("$.page").value(1))
			.andExpect(jsonPath("$.size").value(6))
			.andExpect(jsonPath("$.totalCount").value(1))
			.andExpect(jsonPath("$.totalPages").value(1));
	}

	@Test
	@DisplayName("요청 page는 1부터 시작하는 페이지 번호로 해석한다")
	void getPostsUsesOneIndexedPageParameter() throws Exception {
		Category category = saveCategory("Backend", "backend");
		for (int index = 1; index <= 7; index++) {
			savePost(category, "Post " + index, "one-indexed-post-" + index, PostStatus.PUBLISHED);
		}

		mockMvc.perform(get("/api/posts")
				.param("page", "2")
				.param("size", "3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(3)))
			.andExpect(jsonPath("$.page").value(2))
			.andExpect(jsonPath("$.size").value(3))
			.andExpect(jsonPath("$.totalCount").value(7))
			.andExpect(jsonPath("$.totalPages").value(3));
	}

	@Test
	@DisplayName("조회 결과가 없으면 빈 items와 0건 메타 정보를 반환한다")
	void getPostsReturnsEmptyPage() throws Exception {
		mockMvc.perform(get("/api/posts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(0)))
			.andExpect(jsonPath("$.page").value(1))
			.andExpect(jsonPath("$.size").value(6))
			.andExpect(jsonPath("$.totalCount").value(0))
			.andExpect(jsonPath("$.totalPages").value(0));
	}

	@Test
	@DisplayName("slug로 공개 글 상세를 조회한다")
	void getPostReturnsPublicDetail() throws Exception {
		Category category = saveCategory("Backend", "backend");
		Tag spring = saveTag("Spring Boot", "spring-boot");
		Tag redis = saveTag("Redis", "redis");
		Post post = savePost(category, "Detail Post", "detail-post", PostStatus.PUBLISHED);
		savePostTags(post, spring, redis);

		mockMvc.perform(get("/api/posts/detail-post"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(post.getId()))
			.andExpect(jsonPath("$.title").value("Detail Post"))
			.andExpect(jsonPath("$.slug").value("detail-post"))
			.andExpect(jsonPath("$.description").value("Detail Post description"))
			.andExpect(jsonPath("$.category").value("Backend"))
			.andExpect(jsonPath("$.categoryKey").value("backend"))
			.andExpect(jsonPath("$.tags[0]").value("Spring Boot"))
			.andExpect(jsonPath("$.tags[1]").value("Redis"))
			.andExpect(jsonPath("$.tagKeys[0]").value("spring-boot"))
			.andExpect(jsonPath("$.tagKeys[1]").value("redis"))
			.andExpect(jsonPath("$.createdAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.updatedAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.readingTime").value(3))
			.andExpect(jsonPath("$.viewCount").value(0))
			.andExpect(jsonPath("$.author").value("Jin"))
			.andExpect(jsonPath("$.featured").value(false))
			.andExpect(jsonPath("$.content").value("# Detail Post"));
	}

	@Test
	@DisplayName("인기 글은 공개 글만 조회수 내림차순으로 기본 3개를 조회한다")
	void getPopularPostsReturnsDefaultLimit() throws Exception {
		Category category = saveCategory("Backend", "backend");
		Tag redis = saveTag("Redis", "redis");
		Post mostViewed = savePost(category, "Most Viewed", "most-viewed", PostStatus.PUBLISHED, 7);
		savePostTags(mostViewed, redis);
		Post secondViewed = savePost(category, "Second Viewed", "second-viewed", PostStatus.PUBLISHED, 5);
		savePostTags(secondViewed, redis);
		Post thirdViewed = savePost(category, "Third Viewed", "third-viewed", PostStatus.PUBLISHED, 3);
		savePostTags(thirdViewed, redis);
		savePost(category, "Fourth Viewed", "fourth-viewed", PostStatus.PUBLISHED, 1);
		savePost(category, "Draft Popular", "draft-popular", PostStatus.DRAFT, 100);

		mockMvc.perform(get("/api/posts/popular"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)))
			.andExpect(jsonPath("$[0].id").value(mostViewed.getId()))
			.andExpect(jsonPath("$[0].viewCount").value(7))
			.andExpect(jsonPath("$[0].tags[0]").value("Redis"))
			.andExpect(jsonPath("$[1].id").value(secondViewed.getId()))
			.andExpect(jsonPath("$[1].viewCount").value(5))
			.andExpect(jsonPath("$[2].id").value(thirdViewed.getId()))
			.andExpect(jsonPath("$[2].viewCount").value(3));
	}

	@Test
	@DisplayName("인기 글 limit query로 조회 개수를 조절한다")
	void getPopularPostsUsesLimitParameter() throws Exception {
		Category category = saveCategory("Backend", "backend");
		savePost(category, "First Popular", "first-popular", PostStatus.PUBLISHED, 3);
		savePost(category, "Second Popular", "second-popular", PostStatus.PUBLISHED, 2);

		mockMvc.perform(get("/api/posts/popular")
				.param("limit", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].title").value("First Popular"));
	}

	@Test
	@DisplayName("대표 글은 공개된 featured 글 중 하나를 요약 응답으로 조회한다")
	void getFeaturedPostReturnsPublishedFeaturedPost() throws Exception {
		Category category = saveCategory("Backend", "backend");
		Tag redis = saveTag("Redis", "redis");
		Post featuredPost = savePost(category, "Featured Post", "featured-post", PostStatus.PUBLISHED, 0, true);
		savePostTags(featuredPost, redis);
		savePost(category, "Normal Post", "normal-post", PostStatus.PUBLISHED, 0, false);
		savePost(category, "Draft Featured", "draft-featured", PostStatus.DRAFT, 0, true);

		mockMvc.perform(get("/api/posts/featured"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(featuredPost.getId()))
			.andExpect(jsonPath("$.title").value("Featured Post"))
			.andExpect(jsonPath("$.slug").value("featured-post"))
			.andExpect(jsonPath("$.featured").value(true))
			.andExpect(jsonPath("$.tags[0]").value("Redis"))
			.andExpect(jsonPath("$.content").doesNotExist());
	}

	@Test
	@DisplayName("대표 글 후보가 없으면 204 No Content를 반환한다")
	void getFeaturedPostReturnsNoContent() throws Exception {
		Category category = saveCategory("Backend", "backend");
		savePost(category, "Normal Post", "normal-post", PostStatus.PUBLISHED, 0, false);
		savePost(category, "Draft Featured", "draft-featured", PostStatus.DRAFT, 0, true);

		mockMvc.perform(get("/api/posts/featured"))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("존재하지 않는 slug는 POST_NOT_FOUND를 반환한다")
	void getPostReturnsPostNotFound() throws Exception {
		mockMvc.perform(get("/api/posts/missing-post"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("글을 찾을 수 없습니다."))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
	}

	@Test
	@DisplayName("초안 글 상세 공개 조회는 POST_NOT_PUBLIC을 반환한다")
	void getPostRejectsDraftPost() throws Exception {
		Category category = saveCategory("Backend", "backend");
		savePost(category, "Draft Detail", "draft-detail", PostStatus.DRAFT);

		mockMvc.perform(get("/api/posts/draft-detail"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("POST_NOT_PUBLIC"))
			.andExpect(jsonPath("$.message").value("비공개 글이거나 삭제된 글입니다."))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
	}

	@Test
	@DisplayName("숨김 글 상세 공개 조회는 POST_NOT_PUBLIC을 반환한다")
	void getPostRejectsHiddenPost() throws Exception {
		Category category = saveCategory("Backend", "backend");
		savePost(category, "Hidden Detail", "hidden-detail", PostStatus.HIDDEN);

		mockMvc.perform(get("/api/posts/hidden-detail"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("POST_NOT_PUBLIC"))
			.andExpect(jsonPath("$.message").value("비공개 글이거나 삭제된 글입니다."))
			.andExpect(jsonPath("$.fieldErrors").doesNotExist());
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
		return savePost(category, title, slug, status, 0);
	}

	private Post savePost(Category category, String title, String slug, PostStatus status, int viewCount) {
		return savePost(category, title, slug, status, viewCount, false);
	}

	private Post savePost(
		Category category,
		String title,
		String slug,
		PostStatus status,
		int viewCount,
		boolean featured
	) {
		Post post = Post.builder()
			.category(category)
			.title(title)
			.slug(slug)
			.description(title + " description")
			.content("# " + title)
			.readingTime(3)
			.author("Jin")
			.featured(featured)
			.status(status)
			.build();
		for (int count = 0; count < viewCount; count++) {
			post.increaseViewCount();
		}

		return postRepository.saveAndFlush(post);
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
