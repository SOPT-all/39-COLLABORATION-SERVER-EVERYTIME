package org.sopt.everytime.lecture.code;

import org.sopt.everytime.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LectureErrorCode implements ErrorCode {

	LECTURE_SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
