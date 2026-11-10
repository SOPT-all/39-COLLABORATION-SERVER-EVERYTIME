package org.sopt.everytime.timetable.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record TimetableResponse(
	@Schema(description = "담긴 강의 전체. 없으면 빈 배열")
	List<TimetableEntryResponse> entries
) {
}
