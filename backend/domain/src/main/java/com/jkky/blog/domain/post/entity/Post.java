package com.jkky.blog.domain.post.entity;

import com.jkky.blog.domain.category.entity.Category;
import com.jkky.blog.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	name = "post",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_post_slug", columnNames = "slug")
	},
	indexes = {
		@Index(name = "idx_post_category_id", columnList = "category_id"),
		@Index(name = "idx_post_status_created_at", columnList = "status, created_at"),
		@Index(name = "idx_post_status_view_count", columnList = "status, view_count")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
		name = "category_id",
		nullable = false,
		foreignKey = @ForeignKey(name = "fk_post_category")
	)
	private Category category;

	@Column(name = "title", nullable = false, length = 120)
	private String title;

	@Column(name = "slug", nullable = false, length = 160)
	private String slug;

	@Column(name = "description", nullable = false, length = 300)
	private String description;

	@Lob
	@Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
	private String content;

	@Column(name = "reading_time", nullable = false)
	private int readingTime;

	@Column(name = "view_count", nullable = false)
	private long viewCount;

	@Column(name = "author", nullable = false, length = 100)
	private String author;

	@Column(name = "featured", nullable = false)
	private boolean featured;

	@Convert(converter = PostStatusConverter.class)
	@Column(name = "status", nullable = false, length = 20)
	private PostStatus status;

	@Builder
	private Post(
		Category category,
		String title,
		String slug,
		String description,
		String content,
		int readingTime,
		String author,
		boolean featured,
		PostStatus status
	) {
		this.category = category;
		this.title = title;
		this.slug = slug;
		this.description = description;
		this.content = content;
		this.readingTime = readingTime;
		this.viewCount = 0L;
		this.author = author;
		this.featured = featured;
		this.status = status;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void update(
		Category category,
		String title,
		String description,
		String content,
		int readingTime,
		boolean featured,
		PostStatus status
	) {
		this.category = category;
		this.title = title;
		this.description = description;
		this.content = content;
		this.readingTime = readingTime;
		this.featured = featured;
		this.status = status;
	}

	public void updateStatus(PostStatus status) {
		this.status = status;
	}
}
