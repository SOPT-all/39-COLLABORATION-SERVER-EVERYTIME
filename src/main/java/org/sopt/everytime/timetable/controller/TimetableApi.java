package org.sopt.everytime.timetable.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.timetable.dto.request.TimetableEntryCreateRequest;
import org.sopt.everytime.timetable.dto.response.TimetableResponse;
import org.sopt.everytime.timetable.dto.response.TimetableEntryCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Tag(name = "Timetable", description = "시간표 API")
@RequestMapping("/api/v1/timetable")
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
		@RequestHeader("X-Member-Id") Long userId);

	@Operation(summary = "시간표 항목 추가", description = "선택한 강의 분반을 사용자의 시간표에 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "시간표 추가 성공. 생성된 entryId를 반환합니다."),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다. 강의 시간이 겹치면 시간표에 담을 수 없습니다."),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 또는 강의 분반입니다."),
		@ApiResponse(responseCode = "409", description = "이미 시간표에 담긴 강의입니다."),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
	})
	@PostMapping("/entries")
	ResponseEntity<BaseResponse<TimetableEntryCreateResponse>> addTimetableEntry(
		@Parameter(description = "시간표를 추가할 사용자 ID", example = "1", required = true)
		@RequestHeader("X-Member-Id") Long userId,
		@Valid @RequestBody TimetableEntryCreateRequest request);

	@Operation(summary = "시간표 항목 삭제", description = "담기 항목만 삭제하며 강의 원본은 유지합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "시간표 항목 삭제 성공. data는 null입니다."),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다. data는 null입니다."),
		@ApiResponse(responseCode = "403", description = "해당 시간표 항목을 삭제할 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 사용자입니다. / 존재하지 않는 시간표 항목입니다."),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
	})
	@DeleteMapping("/entries/{entryId}")
	ResponseEntity<BaseResponse<Void>> deleteTimetableEntry(
		@Parameter(description = "삭제할 시간표 담기 항목 ID", example = "1")
		@PathVariable("entryId") @Min(value = 1, message = "시간표 항목 ID는 1 이상이어야 합니다.") Long entryId,
		@Parameter(description = "삭제를 요청하는 사용자 ID", example = "1", required = true)
		@RequestHeader("X-Member-Id") Long userId);
}
