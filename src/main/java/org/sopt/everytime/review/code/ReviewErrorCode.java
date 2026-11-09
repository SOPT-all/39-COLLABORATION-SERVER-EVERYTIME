package org.sopt.everytime.review.code;

import org.sopt.everytime.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

	KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 키워드입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
