package org.sopt.everytime.timetable.dto.response;

import java.util.List;

import org.sopt.everytime.timetable.entity.TimetableEntry;

import io.swagger.v3.oas.annotations.media.Schema;

public record TimetableEntryResponse(
	@Schema(description = "삭제 요청에 사용하는 담기 항목 ID", example = "1")
	Long entryId,

	@Schema(description = "과목명", example = "운영체제")
	String courseName,

	@Schema(description = "해당 강의의 수업 시간 전체")
	List<TimetableTimeResponse> times
) {

	public static TimetableEntryResponse of(TimetableEntry entry, List<TimetableTimeResponse> times) {
		return new TimetableEntryResponse(entry.getId(), entry.getSection().getCourse().getName(), times);
	}
}
