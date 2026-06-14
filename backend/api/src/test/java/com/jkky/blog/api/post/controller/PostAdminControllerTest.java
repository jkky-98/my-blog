package com.jkky.blog.api.post.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jkky.blog.api.BlogApiApplication;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.category.policy.CategoryNamePolicy;
import com.jkky.blog.domain.category.repository.CategoryRepository;
import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.post.entity.PostStatus;
import com.jkky.blog.domain.post.repository.PostRepository;
import com.jkky.blog.domain.post.repository.PostTagRepository;
import com.jkky.blog.domain.tag.entity.Tag;
import com.jkky.blog.domain.tag.repository.TagRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(classes = BlogApiApplication.class)
@AutoConfigureMockMvc
@DisplayName("관리자 글 API")
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
class PostAdminControllerTest {

	private final CategoryNamePolicy categoryNamePolicy = new CategoryNamePolicy();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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
	@DisplayName("로그인한 관리자는 숨김 글 상세도 id로 조회한다")
	void getPostDetailReturnsHiddenPost() throws Exception {
		Category category = saveCategory("Backend");
		Tag redis = saveTag("Redis", "redis");
		Tag spring = saveTag("Spring Boot", "spring-boot");
		Post post = postRepository.saveAndFlush(Post.builder()
			.category(category)
			.title("Hidden Post")
			.slug("hidden-post")
			.description("Hidden Post description")
			.content("# Hidden Post")
			.readingTime(2)
			.author("Jin")
			.featured(true)
			.status(PostStatus.HIDDEN)
			.build());
		savePostTags(post, redis, spring);
		CsrfSession csrfSession = login();

		mockMvc.perform(get("/api/admin/posts/{id}", post.getId())
				.with(session(csrfSession.session())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(post.getId()))
			.andExpect(jsonPath("$.title").value("Hidden Post"))
			.andExpect(jsonPath("$.slug").value("hidden-post"))
			.andExpect(jsonPath("$.description").value("Hidden Post description"))
			.andExpect(jsonPath("$.category").value("Backend"))
			.andExpect(jsonPath("$.categoryKey").value("backend"))
			.andExpect(jsonPath("$.tags[0]").value("Redis"))
			.andExpect(jsonPath("$.tags[1]").value("Spring Boot"))
			.andExpect(jsonPath("$.tagKeys[0]").value("redis"))
			.andExpect(jsonPath("$.tagKeys[1]").value("spring-boot"))
			.andExpect(jsonPath("$.createdAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.updatedAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.readingTime").value(2))
			.andExpect(jsonPath("$.viewCount").value(0))
			.andExpect(jsonPath("$.author").value("Jin"))
			.andExpect(jsonPath("$.featured").value(true))
			.andExpect(jsonPath("$.status").value("hidden"))
			.andExpect(jsonPath("$.content").value("# Hidden Post"));
	}

	@Test
	@DisplayName("관리자 글 목록은 기본으로 초안과 공개 글만 최신순으로 조회한다")
	void getPostsReturnsDraftAndPublishedByDefault() throws Exception {
		Category category = saveCategory("Backend");
		Tag redis = saveTag("Redis", "redis");
		Post draft = savePost(category, "Draft Post", "draft-post", PostStatus.DRAFT);
		savePostTags(draft, redis);
		Post published = savePost(category, "Published Post", "published-post", PostStatus.PUBLISHED);
		savePostTags(published, redis);
		Post hidden = savePost(category, "Hidden Post", "hidden-post", PostStatus.HIDDEN);
		savePostTags(hidden, redis);
		CsrfSession csrfSession = login();

		mockMvc.perform(get("/api/admin/posts")
				.with(session(csrfSession.session())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(2)))
			.andExpect(jsonPath("$.items[0].id").value(published.getId()))
			.andExpect(jsonPath("$.items[0].status").value("published"))
			.andExpect(jsonPath("$.items[1].id").value(draft.getId()))
			.andExpect(jsonPath("$.items[1].status").value("draft"))
			.andExpect(jsonPath("$.page").value(1))
			.andExpect(jsonPath("$.size").value(10))
			.andExpect(jsonPath("$.totalCount").value(2))
			.andExpect(jsonPath("$.totalPages").value(1));
	}

	@Test
	@DisplayName("관리자 글 목록은 hidden 상태를 명시했을 때 숨김 글만 조회한다")
	void getPostsFiltersHiddenStatus() throws Exception {
		Category category = saveCategory("Backend");
		Tag redis = saveTag("Redis", "redis");
		savePostTags(savePost(category, "Draft Post", "draft-post", PostStatus.DRAFT), redis);
		Post hidden = savePost(category, "Hidden Post", "hidden-post", PostStatus.HIDDEN);
		savePostTags(hidden, redis);
		CsrfSession csrfSession = login();

		mockMvc.perform(get("/api/admin/posts")
				.with(session(csrfSession.session()))
				.param("status", "hidden"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(1)))
			.andExpect(jsonPath("$.items[0].id").value(hidden.getId()))
			.andExpect(jsonPath("$.items[0].status").value("hidden"))
			.andExpect(jsonPath("$.totalCount").value(1));
	}

	@Test
	@DisplayName("관리자 글 목록은 status, categoryKey, tagKey, keyword로 필터링한다")
	void getPostsFiltersByStatusCategoryTagAndKeyword() throws Exception {
		Category backend = saveCategory("Backend");
		Category frontend = saveCategory("Frontend");
		Tag redis = saveTag("Redis", "redis");
		Tag spring = saveTag("Spring Boot", "spring-boot");
		Post target = savePost(backend, "Redis Cache Notes", "redis-cache-notes", PostStatus.PUBLISHED);
		savePostTags(target, redis);
		Post otherTag = savePost(backend, "Spring Cache Notes", "spring-cache-notes", PostStatus.PUBLISHED);
		savePostTags(otherTag, spring);
		Post otherCategory = savePost(frontend, "Redis Cache UI", "redis-cache-ui", PostStatus.PUBLISHED);
		savePostTags(otherCategory, redis);
		Post draft = savePost(backend, "Redis Cache Draft", "redis-cache-draft", PostStatus.DRAFT);
		savePostTags(draft, redis);
		CsrfSession csrfSession = login();

		mockMvc.perform(get("/api/admin/posts")
				.with(session(csrfSession.session()))
				.param("status", "published")
				.param("categoryKey", "backend")
				.param("tagKey", "redis")
				.param("keyword", "Cache"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.items", hasSize(1)))
			.andExpect(jsonPath("$.items[0].id").value(target.getId()))
			.andExpect(jsonPath("$.items[0].title").value("Redis Cache Notes"))
			.andExpect(jsonPath("$.items[0].categoryKey").value("backend"))
			.andExpect(jsonPath("$.items[0].tagKeys[0]").value("redis"))
			.andExpect(jsonPath("$.items[0].status").value("published"))
			.andExpect(jsonPath("$.totalCount").value(1));
	}

	@Test
	@DisplayName("존재하지 않는 관리자 글 상세 조회는 POST_NOT_FOUND를 반환한다")
	void getPostDetailWithUnknownIdReturnsPostNotFound() throws Exception {
		CsrfSession csrfSession = login();

		mockMvc.perform(get("/api/admin/posts/{id}", 999_999L)
				.with(session(csrfSession.session())))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("글을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("로그인한 관리자는 기존 글을 수정하고 slug와 author는 유지한다")
	void updatePost() throws Exception {
		Category backend = saveCategory("Backend");
		saveCategory("Frontend");
		Tag redis = saveTag("Redis", "redis");
		Tag spring = saveTag("Spring Boot", "spring-boot");
		Post post = postRepository.saveAndFlush(Post.builder()
			.category(backend)
			.title("Original Title")
			.slug("original-title")
			.description("Original description")
			.content("# Original Title")
			.readingTime(3)
			.author("Original Author")
			.featured(false)
			.status(PostStatus.DRAFT)
			.build());
		savePostTags(post, redis);
		CsrfSession csrfSession = login();

		mockMvc.perform(put("/api/admin/posts/{id}", post.getId())
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Updated Title",
					  "category": "Frontend",
					  "tags": ["Spring Boot"],
					  "featured": true,
					  "status": "published",
					  "content": "# Updated Title\\n\\n수정된 본문입니다."
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(post.getId()))
			.andExpect(jsonPath("$.title").value("Updated Title"))
			.andExpect(jsonPath("$.slug").value("original-title"))
			.andExpect(jsonPath("$.description").value("Updated Title 수정된 본문입니다."))
			.andExpect(jsonPath("$.category").value("Frontend"))
			.andExpect(jsonPath("$.categoryKey").value("frontend"))
			.andExpect(jsonPath("$.tags", hasSize(1)))
			.andExpect(jsonPath("$.tags[0]").value("Spring Boot"))
			.andExpect(jsonPath("$.tagKeys[0]").value("spring-boot"))
			.andExpect(jsonPath("$.readingTime").value(1))
			.andExpect(jsonPath("$.author").value("Original Author"))
			.andExpect(jsonPath("$.featured").value(true))
			.andExpect(jsonPath("$.status").value("published"))
			.andExpect(jsonPath("$.content").value("# Updated Title\n\n수정된 본문입니다."));

		Post savedPost = postRepository.findById(post.getId()).orElseThrow();
		assertThat(savedPost.getSlug()).isEqualTo("original-title");
		assertThat(savedPost.getAuthor()).isEqualTo("Original Author");
		assertThat(postTagRepository.findByPostOrderByIdAsc(savedPost))
			.extracting(postTag -> postTag.getTag().getName())
			.containsExactly("Spring Boot");
	}

	@Test
	@DisplayName("존재하지 않는 글 수정은 POST_NOT_FOUND를 반환한다")
	void updateUnknownPostReturnsPostNotFound() throws Exception {
		saveCategory("Backend");
		CsrfSession csrfSession = login();

		mockMvc.perform(put("/api/admin/posts/{id}", 999_999L)
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Unknown",
					  "category": "Backend",
					  "tags": ["Redis"],
					  "featured": false,
					  "status": "draft",
					  "content": "# Unknown"
					}
					"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("글을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("로그인한 관리자는 새 글을 생성하고 서버 생성값을 응답받는다")
	void createPost() throws Exception {
		saveCategory("Backend");
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/admin/posts")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Spring Boot Redis",
					  "category": "Backend",
					  "tags": [" Spring Boot ", "spring boot", "Redis"],
					  "featured": true,
					  "status": "published",
					  "content": "# Spring Boot Redis\\n\\n본문 **강조** 내용입니다."
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.title").value("Spring Boot Redis"))
			.andExpect(jsonPath("$.slug").value("spring-boot-redis"))
			.andExpect(jsonPath("$.description").value("Spring Boot Redis 본문 강조 내용입니다."))
			.andExpect(jsonPath("$.category").value("Backend"))
			.andExpect(jsonPath("$.categoryKey").value("backend"))
			.andExpect(jsonPath("$.tags", hasSize(2)))
			.andExpect(jsonPath("$.tags[0]").value("Spring Boot"))
			.andExpect(jsonPath("$.tags[1]").value("Redis"))
			.andExpect(jsonPath("$.tagKeys[0]").value("spring-boot"))
			.andExpect(jsonPath("$.tagKeys[1]").value("redis"))
			.andExpect(jsonPath("$.createdAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.updatedAt", endsWith("+09:00")))
			.andExpect(jsonPath("$.readingTime").value(1))
			.andExpect(jsonPath("$.viewCount").value(0))
			.andExpect(jsonPath("$.author").value("Jin"))
			.andExpect(jsonPath("$.featured").value(true))
			.andExpect(jsonPath("$.status").value("published"))
			.andExpect(jsonPath("$.content").value("# Spring Boot Redis\n\n본문 **강조** 내용입니다."));

		Post savedPost = postRepository.findBySlug("spring-boot-redis").orElseThrow();
		assertThat(savedPost.getStatus()).isEqualTo(PostStatus.PUBLISHED);
		assertThat(tagRepository.findByNormalizedNameIn(List.of("spring boot", "redis"))).hasSize(2);
	}

	@Test
	@DisplayName("이미 같은 slug가 있으면 숫자 suffix를 붙여 생성한다")
	void createPostUsesSlugSuffixWhenSlugExists() throws Exception {
		Category category = saveCategory("Backend");
		postRepository.saveAndFlush(Post.builder()
			.category(category)
			.title("Spring Boot Redis")
			.slug("spring-boot-redis")
			.description("기존 글")
			.content("# 기존 글")
			.readingTime(1)
			.author("Jin")
			.featured(false)
			.status(PostStatus.PUBLISHED)
			.build());
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/admin/posts")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Spring Boot Redis",
					  "category": "Backend",
					  "tags": ["Redis"],
					  "featured": false,
					  "status": "draft",
					  "content": "# 새 글"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.slug").value("spring-boot-redis-2"))
			.andExpect(jsonPath("$.status").value("draft"));
	}

	@Test
	@DisplayName("태그가 정리 후 비어 있으면 VALIDATION_ERROR를 반환한다")
	void createPostWithEmptyTagsReturnsValidationError() throws Exception {
		saveCategory("Backend");
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/admin/posts")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Empty Tags",
					  "category": "Backend",
					  "tags": [" ", ""],
					  "featured": false,
					  "status": "published",
					  "content": "# Empty Tags"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.fieldErrors.tags").value("태그는 최소 1개 이상 입력해야 합니다."));
	}

	@Test
	@DisplayName("존재하지 않는 카테고리로 글을 생성하면 VALIDATION_ERROR를 반환한다")
	void createPostWithUnknownCategoryReturnsValidationError() throws Exception {
		CsrfSession csrfSession = login();

		mockMvc.perform(post("/api/admin/posts")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Unknown Category",
					  "category": "Backend",
					  "tags": ["Redis"],
					  "featured": false,
					  "status": "published",
					  "content": "# Unknown Category"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.fieldErrors.category").value("존재하지 않는 카테고리입니다."));
	}

	private Category saveCategory(String name) {
		return categoryRepository.save(Category.builder()
			.name(categoryNamePolicy.toDisplayName(name))
			.normalizedName(categoryNamePolicy.toNormalizedName(name))
			.filterKey(categoryNamePolicy.toFilterKey(name))
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
			.readingTime(1)
			.author("Jin")
			.featured(false)
			.status(status)
			.build());
	}

	private void savePostTags(Post post, Tag... tags) {
		List<PostTag> postTags = Arrays.stream(tags)
			.map(tag -> PostTag.builder()
				.post(post)
				.tag(tag)
				.build())
			.toList();

		postTagRepository.saveAll(postTags);
	}

	private CsrfSession login() throws Exception {
		CsrfSession csrfSession = getCsrfSession();

		mockMvc.perform(post("/api/auth/login")
				.with(session(csrfSession.session()))
				.header(csrfSession.headerName(), csrfSession.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "admin",
					  "password": "password"
					}
					"""))
			.andExpect(status().isOk());

		return csrfSession;
	}

	private CsrfSession getCsrfSession() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
			.andExpect(status().isOk())
			.andReturn();

		JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
		return new CsrfSession(
			(MockHttpSession) result.getRequest().getSession(false),
			response.get("headerName").asText(),
			response.get("token").asText()
		);
	}

	private RequestPostProcessor session(MockHttpSession session) {
		return request -> {
			request.setSession(session);
			return request;
		};
	}

	private record CsrfSession(
		MockHttpSession session,
		String headerName,
		String token
	) {
	}
}
