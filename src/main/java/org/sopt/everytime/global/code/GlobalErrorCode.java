package org.sopt.everytime.global.code;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "사용자 ID는 필수입니다."),
	USER_ID_INVALID(HttpStatus.BAD_REQUEST, "사용자 ID는 숫자여야 합니다."),
	USER_ID_MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "사용자 ID는 1 이상이어야 합니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

	private final HttpStatus httpStatus;
	private final String message;

	public static GlobalErrorCode from(HttpStatusCode statusCode) {
		return switch (statusCode.value()) {
			case 404 -> RESOURCE_NOT_FOUND;
			case 405 -> METHOD_NOT_ALLOWED;
			case 415 -> UNSUPPORTED_MEDIA_TYPE;
			default -> statusCode.is4xxClientError() ? INVALID_REQUEST : INTERNAL_SERVER_ERROR;
		};
	}
}
