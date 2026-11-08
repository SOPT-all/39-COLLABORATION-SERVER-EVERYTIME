package org.sopt.everytime.review.dto.response;

import org.sopt.everytime.review.entity.ReviewKeyword;

import io.swagger.v3.oas.annotations.media.Schema;

public record HighlightResponse(
	@Schema(description = "강조 시작 위치 (0부터, 포함)", example = "0")
	int start,

	@Schema(description = "강조 끝 위치 (미포함, content.substring(start, end))", example = "3")
	int end
) {

	public static HighlightResponse from(ReviewKeyword reviewKeyword) {
		if (reviewKeyword.getHighlightStart() == null || reviewKeyword.getHighlightEnd() == null) {
			return null;
		}
		return new HighlightResponse(reviewKeyword.getHighlightStart(), reviewKeyword.getHighlightEnd());
	}
}
