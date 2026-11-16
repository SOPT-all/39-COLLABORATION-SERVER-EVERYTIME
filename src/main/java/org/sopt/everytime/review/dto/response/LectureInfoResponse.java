package org.sopt.everytime.review.dto.response;

import org.sopt.everytime.lecture.entity.LectureSection;

import io.swagger.v3.oas.annotations.media.Schema;

public record LectureInfoResponse(
	@Schema(description = "분반 ID", example = "1")
	Long sectionId,

	@Schema(description = "과목명", example = "AI와창의적문제해결")
	String courseName,

	@Schema(description = "강의평 평균 평점 (소수 첫째 자리에서 반올림한 정수, 강의평이 없으면 0)", example = "4")
	int averageGradePoint
) {

	public static LectureInfoResponse of(LectureSection section, Double averageGradePoint) {
		int rounded = averageGradePoint == null ? 0 : (int)Math.round(averageGradePoint);
		return new LectureInfoResponse(section.getId(), section.getCourse().getName(), rounded);
	}
}
