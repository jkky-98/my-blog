package com.jkky.blog.domain.post;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PostStatusConverter implements AttributeConverter<PostStatus, String> {

	@Override
	public String convertToDatabaseColumn(PostStatus attribute) {
		return attribute == null ? null : attribute.getValue();
	}

	@Override
	public PostStatus convertToEntityAttribute(String dbData) {
		return dbData == null ? null : PostStatus.fromValue(dbData);
	}
}
