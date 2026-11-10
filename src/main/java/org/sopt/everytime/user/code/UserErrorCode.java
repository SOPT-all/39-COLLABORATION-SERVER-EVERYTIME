package org.sopt.everytime.user.code;

import org.sopt.everytime.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
