package com.jkky.blog.api.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값을 확인해 주세요."),
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
	UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	CSRF_TOKEN_INVALID(HttpStatus.FORBIDDEN, "요청 보안 토큰이 유효하지 않습니다."),
	POST_NOT_PUBLIC(HttpStatus.FORBIDDEN, "비공개 글이거나 삭제된 글입니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "글을 찾을 수 없습니다."),
	CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
