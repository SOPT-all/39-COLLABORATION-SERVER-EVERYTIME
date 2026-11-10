package org.sopt.everytime.review.dto.response;

import java.math.BigDecimal;

import org.sopt.everytime.review.entity.Review;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewResponse(
	@Schema(description = "강의평 ID", example = "103")
	Long reviewId,

	@Schema(description = "수강 연도", example = "2026")
	Long year,

	@Schema(description = "수강 학기", example = "2학기")
	String semester,

	@Schema(description = "평점 (0.00~5.00)", example = "5.00")
	BigDecimal gradePoint,

	@Schema(description = "강의평 본문", example = "교수님께서 좋으셔서 듣기 좋았어요!")
	String content,

	@Schema(description = "선택한 키워드의 본문 강조 구간. 전체 조회이거나 구간이 없으면 null", nullable = true)
	HighlightResponse highlight
) {

	public static ReviewResponse of(Review review, HighlightResponse highlight) {
		return new ReviewResponse(review.getId(), review.getYear(), review.getSemester(), review.getGradePoint(),
			review.getContent(), highlight);
	}
}
