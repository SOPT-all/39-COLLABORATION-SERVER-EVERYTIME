package org.sopt.everytime.timetable.code;

import org.sopt.everytime.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimetableErrorCode implements ErrorCode {

	INVALID_USER_ID(HttpStatus.BAD_REQUEST, "사용자 ID는 숫자여야 합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
