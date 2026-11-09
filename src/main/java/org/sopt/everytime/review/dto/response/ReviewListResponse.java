package org.sopt.everytime.review.dto.response;

import java.util.List;

import org.sopt.everytime.global.response.PageResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewListResponse(
	@Schema(description = "화면 상단에 표시할 강의 정보")
	LectureInfoResponse lecture,

	@Schema(description = "고정 키워드 칩 4개와 칩별 강의평 수 (분반 전체 기준)")
	List<KeywordCountResponse> keywords,

	@Schema(description = "강의평 페이지 (등록순)")
	PageResponse<ReviewResponse> reviews
) {
}
