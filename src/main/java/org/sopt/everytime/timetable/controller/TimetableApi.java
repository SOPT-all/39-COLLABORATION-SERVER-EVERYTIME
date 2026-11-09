package org.sopt.everytime.timetable.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

@Tag(name = "Timetable", description = "시간표 API")
@RequestMapping("/timetable")
public interface TimetableApi {

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
		@RequestHeader("X-Member-Id") @Min(value = 1, message = "사용자 ID는 1 이상이어야 합니다.") Long userId);
}
