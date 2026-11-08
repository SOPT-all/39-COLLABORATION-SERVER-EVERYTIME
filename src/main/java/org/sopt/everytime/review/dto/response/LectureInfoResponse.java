package org.sopt.everytime.review.dto.response;

import org.sopt.everytime.lecture.entity.LectureSection;

import io.swagger.v3.oas.annotations.media.Schema;

public record LectureInfoResponse(
	@Schema(description = "분반 ID", example = "1")
	Long sectionId,

	@Schema(description = "과목명", example = "AI와창의적문제해결")
	String courseName,

	@Schema(description = "강의평 평균 평점 (소수 셋째 자리에서 반올림, 강의평이 없으면 0)", example = "3.67")
	double averageGradePoint
) {

	public static LectureInfoResponse of(LectureSection section, Double averageGradePoint) {
		double rounded = averageGradePoint == null ? 0 : Math.round(averageGradePoint * 100) / 100.0;
		return new LectureInfoResponse(section.getId(), section.getCourse().getName(), rounded);
	}
}
