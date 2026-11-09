package org.sopt.everytime.lecture.code;

import org.sopt.everytime.global.code.SuccessCode;
import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LectureSuccessCode implements SuccessCode {
	COURSES_FOUND(HttpStatus.OK, "강의 목록 조회 성공");

	private final HttpStatus httpStatus;
	private final String message;
}
