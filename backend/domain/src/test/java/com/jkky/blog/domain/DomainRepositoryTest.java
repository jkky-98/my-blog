package com.jkky.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jkky.blog.domain.auth.entity.AdminUser;
import com.jkky.blog.domain.auth.repository.AdminUserRepository;
import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.category.repository.CategoryRepository;
import com.jkky.blog.domain.post.entity.Post;
import com.jkky.blog.domain.post.entity.PostStatus;
import com.jkky.blog.domain.post.entity.PostTag;
import com.jkky.blog.domain.post.repository.PostRepository;
import com.jkky.blog.domain.post.repository.PostTagRepository;
import com.jkky.blog.domain.tag.entity.Tag;
import com.jkky.blog.domain.tag.repository.TagRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@DisplayName("도메인 엔티티와 리포지토리 매핑")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
	"spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
	"spring.datasource.url=jdbc:mysql://${DB_TEST_HOST:localhost}:${DB_TEST_PORT:3307}/${DB_TEST_NAME:jkky_blog_test}?createDatabaseIfNotExist=true&serverTimezone=UTC&connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true&useUnicode=true&characterEncoding=utf8",
	"spring.datasource.username=${DB_TEST_USERNAME:root}",
	"spring.datasource.password=${DB_TEST_PASSWORD:root}",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.properties.hibernate.jdbc.time_zone=UTC"
})
class DomainRepositoryTest {

	@Autowired
	private AdminUserRepository adminUserRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostTagRepository postTagRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("관리자 계정은 저장 후 username으로 조회할 수 있다")
	void adminUserIsPersistedAndFoundByUsername() {
		AdminUser adminUser = AdminUser.builder()
			.username("admin")
			.passwordHash("bcrypt-hash")
			.displayName("Jin")
			.build();

		adminUserRepository.save(adminUser);
		flushAndClear();

		assertThat(adminUserRepository.findByUsername("admin"))
			.hasValueSatisfying(found -> {
				assertThat(found.getPasswordHash()).isEqualTo("bcrypt-hash");
				assertThat(found.getDisplayName()).isEqualTo("Jin");
				assertThat(found.getCreatedAt()).isNotNull();
				assertThat(found.getUpdatedAt()).isNotNull();
			});
	}

	@Test
	@DisplayName("카테고리는 정규화 이름과 필터 key가 중복되면 저장할 수 없다")
	void categoryNormalizedNameAndFilterKeyAreUnique() {
		categoryRepository.saveAndFlush(Category.builder()
			.name("Backend")
			.normalizedName("backend")
			.filterKey("backend")
			.build());

		assertThatThrownBy(() -> categoryRepository.saveAndFlush(Category.builder()
			.name("backend")
			.normalizedName("backend")
			.filterKey("backend-api")
			.build()))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("태그는 정규화 이름과 필터 key가 중복되면 저장할 수 없다")
	void tagNormalizedNameAndFilterKeyAreUnique() {
		tagRepository.saveAndFlush(Tag.builder()
			.name("Redis")
			.normalizedName("redis")
			.filterKey("redis")
			.build());

		assertThatThrownBy(() -> tagRepository.saveAndFlush(Tag.builder()
			.name("redis")
			.normalizedName("redis")
			.filterKey("redis-cache")
			.build()))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("글 slug는 중복될 수 없고 공개 상태는 DB 값으로 저장된다")
	void postSlugIsUniqueAndStatusIsStoredAsDatabaseValue() {
		Category category = categoryRepository.save(Category.builder()
			.name("Backend")
			.normalizedName("backend")
			.filterKey("backend")
			.build());
		postRepository.save(Post.builder()
			.category(category)
			.title("Published Post")
			.slug("published-post")
			.description("Post description")
			.content("# Content")
			.readingTime(1)
			.author("Jin")
			.featured(true)
			.status(PostStatus.PUBLISHED)
			.build());
		flushAndClear();

		String status = jdbcTemplate.queryForObject(
			"select status from post where slug = ?",
			String.class,
			"published-post"
		);

		assertThat(status).isEqualTo("published");
		assertThatThrownBy(() -> postRepository.saveAndFlush(Post.builder()
			.category(category)
			.title("Duplicate Slug")
			.slug("published-post")
			.description("Duplicate post description")
			.content("# Duplicate")
			.readingTime(1)
			.author("Jin")
			.featured(false)
			.status(PostStatus.DRAFT)
			.build()))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("글과 태그 연결은 저장할 수 있고 같은 연결은 중복 저장할 수 없다")
	void postTagStoresPostAndTagRelationshipAndRejectsDuplicateLink() {
		Category category = categoryRepository.save(Category.builder()
			.name("Backend")
			.normalizedName("backend")
			.filterKey("backend")
			.build());
		Tag tag = tagRepository.save(Tag.builder()
			.name("Spring Boot")
			.normalizedName("spring boot")
			.filterKey("spring-boot")
			.build());
		Post post = postRepository.save(Post.builder()
			.category(category)
			.title("Spring Boot Post")
			.slug("spring-boot-post")
			.description("Spring Boot post description")
			.content("# Spring Boot")
			.readingTime(1)
			.author("Jin")
			.featured(false)
			.status(PostStatus.PUBLISHED)
			.build());
		postTagRepository.save(newPostTag(post, tag));
		flushAndClear();

		Post savedPost = postRepository.findBySlug("spring-boot-post").orElseThrow();
		List<PostTag> links = postTagRepository.findByPost(savedPost);

		assertThat(links).hasSize(1);
		assertThat(links.getFirst().getTag().getName()).isEqualTo("Spring Boot");
		assertThat(links.getFirst().getCreatedAt()).isNotNull();
		assertThatThrownBy(() -> postTagRepository.saveAndFlush(newPostTag(savedPost, links.getFirst().getTag())))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	private PostTag newPostTag(Post post, Tag tag) {
		return PostTag.builder()
			.post(post)
			.tag(tag)
			.build();
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@EnableJpaAuditing
	static class TestApplication {
	}
}
