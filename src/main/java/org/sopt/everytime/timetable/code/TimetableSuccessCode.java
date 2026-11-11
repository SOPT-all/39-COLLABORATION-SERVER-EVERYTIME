package org.sopt.everytime.timetable.code;

import org.sopt.everytime.global.code.SuccessCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimetableSuccessCode implements SuccessCode {

	TIMETABLE_FETCHED(HttpStatus.OK, "시간표 조회 성공"),
	TIMETABLE_ENTRY_CREATED(HttpStatus.CREATED, "시간표 추가 성공"),
	TIMETABLE_DELETED(HttpStatus.OK, "시간표 항목 삭제 성공");

	private final HttpStatus httpStatus;
	private final String message;
}
