package org.sopt.everytime.timetable.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TimetableRequest(
	@Schema(description = "시간표를 조회할 사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "사용자 ID는 필수입니다.")
	@Min(value = 1, message = "사용자 ID는 1 이상이어야 합니다.")
	Long userId
) {
}
