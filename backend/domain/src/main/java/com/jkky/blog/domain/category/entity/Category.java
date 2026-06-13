package com.jkky.blog.domain.category.entity;

import com.jkky.blog.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	name = "category",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_category_normalized_name", columnNames = "normalized_name"),
		@UniqueConstraint(name = "uk_category_filter_key", columnNames = "filter_key")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 40)
	private String name;

	// 중복 판단에 사용하는 정규화 이름이다. 예: " Backend "와 "backend"는 모두 "backend"로 저장한다.
	@Column(name = "normalized_name", nullable = false, length = 40)
	private String normalizedName;

	// API 필터 요청에 사용하는 고정 key다. 예: 카테고리 "Spring Boot"는 /api/posts?categoryKey=spring-boot 로 조회한다.
	@Column(name = "filter_key", nullable = false, length = 60)
	private String filterKey;

	@Builder
	private Category(String name, String normalizedName, String filterKey) {
		this.name = name;
		this.normalizedName = normalizedName;
		this.filterKey = filterKey;
	}
}
