package org.sopt.everytime.lecture.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.global.response.PageResponse;
import org.sopt.everytime.lecture.dto.request.CourseSort;
import org.sopt.everytime.lecture.dto.response.CourseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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

@Tag(name = "Course", description = "강의 API")
@RequestMapping("/api/v1/courses")
public interface CourseApi {
	@Operation(summary = "전체 강의 목록 조회",
		description = "조건에 맞는 강의 목록을 페이지네이션하여 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "강의 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 사용자입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping
	ResponseEntity<BaseResponse<PageResponse<CourseResponse>>> getCourses(
		@Parameter(description = "사용자 ID", example = "1", required = true) @RequestHeader("X-Member-Id") Long userId,
		@Parameter(description = "카테고리명", example = "전공필수")
		@RequestParam(value = "category", required = false) String category,
		@Parameter(description = "학과 ID", example = "1")
		@RequestParam(value = "departmentId", required = false) Long departmentId,
		@Parameter(description = "개설 학기", example = "2학기")
		@RequestParam(value = "semester", required = false) String semester,
		@RequestParam(value = "hideTaken", defaultValue = "false") boolean hideTaken,
		@RequestParam(value = "hideRestricted", defaultValue = "false") boolean hideRestricted,
		@RequestParam(value = "sort", defaultValue = "NAME_ASC") CourseSort sort,
		@RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
		@RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(50) int size);
}
