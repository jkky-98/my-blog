package com.jkky.blog.api.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
	String code,
	String message,
	Map<String, String> fieldErrors
) {

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.name(), errorCode.getMessage(), Map.of());
	}

	public static ErrorResponse validation(Map<String, String> fieldErrors) {
		return new ErrorResponse(
			ErrorCode.VALIDATION_ERROR.name(),
			ErrorCode.VALIDATION_ERROR.getMessage(),
			fieldErrors
		);
	}
}
