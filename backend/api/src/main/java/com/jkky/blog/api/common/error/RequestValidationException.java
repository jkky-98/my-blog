package com.jkky.blog.api.common.error;

import java.util.Map;

public class RequestValidationException extends RuntimeException {

	private final Map<String, String> fieldErrors;

	public RequestValidationException(Map<String, String> fieldErrors) {
		this.fieldErrors = Map.copyOf(fieldErrors);
	}

	public static RequestValidationException of(String field, String message) {
		return new RequestValidationException(Map.of(field, message));
	}

	public Map<String, String> getFieldErrors() {
		return fieldErrors;
	}
}
