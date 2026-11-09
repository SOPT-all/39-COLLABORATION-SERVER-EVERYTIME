package org.sopt.everytime.timetable.dto.response;

import java.time.LocalTime;

import org.sopt.everytime.lecture.entity.SectionTime;
import org.sopt.everytime.lecture.entity.Weekday;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record TimetableTimeResponse(
	@Schema(description = "수업 요일", example = "MON")
	Weekday dayOfWeek,

	@Schema(description = "수업 시작 시각 (HH:mm:ss)", example = "13:30:00", type = "string")
	@JsonFormat(pattern = "HH:mm:ss")
	LocalTime startTime,

	@Schema(description = "수업 종료 시각 (HH:mm:ss)", example = "14:45:00", type = "string")
	@JsonFormat(pattern = "HH:mm:ss")
	LocalTime endTime
) {

	public static TimetableTimeResponse from(SectionTime time) {
		return new TimetableTimeResponse(time.getDayOfWeek(), time.getStartTime(), time.getEndTime());
	}
}
