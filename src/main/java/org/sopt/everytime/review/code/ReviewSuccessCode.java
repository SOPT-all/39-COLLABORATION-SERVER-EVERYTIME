package org.sopt.everytime.review.code;

import org.sopt.everytime.global.code.SuccessCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccessCode implements SuccessCode {

	REVIEWS_FOUND(HttpStatus.OK, "강의평 목록 조회 성공");

	private final HttpStatus httpStatus;
	private final String message;
}
