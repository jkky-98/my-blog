package com.jkky.blog.api.common.error;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BlogException.class)
	public ResponseEntity<ErrorResponse> handleBlogException(BlogException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(fieldError ->
			fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage())
		);

		return ResponseEntity
			.badRequest()
			.body(ErrorResponse.validation(fieldErrors));
	}

	@ExceptionHandler({
		HttpMessageNotReadableException.class,
		MethodArgumentTypeMismatchException.class,
		MissingServletRequestParameterException.class
	})
	public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
		return ResponseEntity
			.badRequest()
			.body(ErrorResponse.of(ErrorCode.INVALID_REQUEST));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
		return ResponseEntity
			.status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
			.body(ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception exception) {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
