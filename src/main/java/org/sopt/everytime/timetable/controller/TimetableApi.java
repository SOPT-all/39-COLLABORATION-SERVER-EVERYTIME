package org.sopt.everytime.timetable.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.timetable.dto.response.TimetableResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

@Tag(name = "Timetable", description = "시간표 API")
@RequestMapping("/timetable")
public interface TimetableApi {

	@Operation(summary = "시간표 조회", description = "단일 연도·학기 데모의 담긴 강의 전체를 페이징 없이 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "시간표 조회 성공. 담긴 강의가 없으면 entries는 빈 배열입니다."),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다. data는 null입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 사용자입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.",
			content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping
	ResponseEntity<BaseResponse<TimetableResponse>> getTimetable(
		@Parameter(description = "시간표를 조회할 사용자 ID", example = "1", required = true)
		@RequestHeader("X-Member-Id") @Min(value = 1, message = "사용자 ID는 1 이상이어야 합니다.") Long userId);
}
