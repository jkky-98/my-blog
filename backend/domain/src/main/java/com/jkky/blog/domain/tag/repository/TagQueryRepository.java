package com.jkky.blog.domain.tag.repository;

import com.jkky.blog.domain.tag.repository.projection.TagPostCount;
import java.util.List;

public interface TagQueryRepository {

	List<TagPostCount> findPublicTagPostCounts();
}
