package com.jkky.blog.domain.category;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findByNormalizedName(String normalizedName);

	boolean existsByNormalizedName(String normalizedName);

	boolean existsByFilterKey(String filterKey);
}
