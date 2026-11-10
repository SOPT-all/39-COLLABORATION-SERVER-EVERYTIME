package org.sopt.everytime.timetable.code;

import org.sopt.everytime.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimetableErrorCode implements ErrorCode {

	SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 강의 분반입니다."),
	SECTION_ID_INVALID(HttpStatus.BAD_REQUEST, "분반 ID는 1 이상의 숫자 값입니다."),
	TIMETABLE_ENTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 시간표 항목입니다."),
	TIMETABLE_ENTRY_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 시간표 항목을 삭제할 권한이 없습니다."),
	TIMETABLE_ENTRY_DUPLICATED(HttpStatus.CONFLICT, "이미 시간표에 담긴 강의입니다."),
	TIMETABLE_TIME_CONFLICT(HttpStatus.BAD_REQUEST, "시간이 겹치는 강의가 있습니다."),
	COURSE_NOT_AVAILABLE_FOR_GRADE(HttpStatus.BAD_REQUEST, "수강 대상 학년이 아닙니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
