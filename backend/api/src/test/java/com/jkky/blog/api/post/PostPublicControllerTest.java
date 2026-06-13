package com.jkky.blog.api.post;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jkky.blog.api.BlogApiApplication;
import com.jkky.blog.domain.category.Category;
import com.jkky.blog.domain.category.CategoryRepository;
import com.jkky.blog.domain.post.Post;
import com.jkky.blog.domain.post.PostRepository;
import com.jkky.blog.domain.post.PostStatus;
import com.jkky.blog.domain.post.PostTag;
import com.jkky.blog.domain.post.PostTagRepository;
import com.jkky.blog.domain.tag.Tag;
import com.jkky.blog.domain.tag.TagRepository;
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
@DisplayName("공개 글 목록 API")
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
