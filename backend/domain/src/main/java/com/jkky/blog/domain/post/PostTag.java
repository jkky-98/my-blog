package com.jkky.blog.domain.post;

import com.jkky.blog.domain.tag.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
	name = "post_tag",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_post_tag_post_id_tag_id", columnNames = {"post_id", "tag_id"})
	},
	indexes = {
		@Index(name = "idx_post_tag_post_id", columnList = "post_id"),
		@Index(name = "idx_post_tag_tag_id", columnList = "tag_id")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
		name = "post_id",
		nullable = false,
		foreignKey = @ForeignKey(name = "fk_post_tag_post")
	)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
		name = "tag_id",
		nullable = false,
		foreignKey = @ForeignKey(name = "fk_post_tag_tag")
	)
	private Tag tag;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	private PostTag(Post post, Tag tag) {
		this.post = post;
		this.tag = tag;
	}
}
