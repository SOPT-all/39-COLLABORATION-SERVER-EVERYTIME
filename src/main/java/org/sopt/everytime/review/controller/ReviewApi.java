package org.sopt.everytime.review.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.review.dto.request.ReviewSort;
import org.sopt.everytime.review.dto.response.ReviewListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Tag(name = "Review", description = "강의평 API")
public interface ReviewApi {

	@Operation(
		summary = "전체 강의평 조회",
		description = "분반의 강의 정보, 고정 키워드 칩 4개와 칩별 강의평 수, 전체 강의평 목록을 조회합니다. highlight는 항상 null입니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "강의평 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 강의입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/sections/{sectionId}/reviews")
	ResponseEntity<BaseResponse<ReviewListResponse>> getReviews(
		@Parameter(description = "분반 ID", example = "1")
		@PathVariable Long sectionId,

		@Parameter(description = "정렬 기준 (LATEST: 등록순)")
		@RequestParam(defaultValue = "LATEST") ReviewSort sort,

		@Parameter(description = "페이지 번호 (1부터)", example = "1")
		@RequestParam(defaultValue = "1") @Min(1) int page,

		@Parameter(description = "페이지 크기 (1~50)", example = "10")
		@RequestParam(defaultValue = "10") @Min(1) @Max(50) int size
	);

	@Operation(
		summary = "칩별 강의평 조회",
		description = """
			선택한 키워드 칩이 달린 강의평만 조회하고, 본문 강조 구간(highlight)을 함께 응답합니다.
			lecture와 keywords는 칩 선택과 무관하게 분반 전체 기준입니다.
			"""
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "강의평 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 강의입니다. / 존재하지 않는 키워드입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/sections/{sectionId}/reviews/keywords/{keywordId}")
	ResponseEntity<BaseResponse<ReviewListResponse>> getReviewsByKeyword(
		@Parameter(description = "분반 ID", example = "1")
		@PathVariable Long sectionId,

		@Parameter(description = "키워드 칩 ID (1 교수님, 2 과제, 3 시험, 4 팀 프로젝트)", example = "1")
		@PathVariable Long keywordId,

		@Parameter(description = "정렬 기준 (LATEST: 등록순)")
		@RequestParam(defaultValue = "LATEST") ReviewSort sort,

		@Parameter(description = "페이지 번호 (1부터)", example = "1")
		@RequestParam(defaultValue = "1") @Min(1) int page,

		@Parameter(description = "페이지 크기 (1~50)", example = "10")
		@RequestParam(defaultValue = "10") @Min(1) @Max(50) int size
	);
}
