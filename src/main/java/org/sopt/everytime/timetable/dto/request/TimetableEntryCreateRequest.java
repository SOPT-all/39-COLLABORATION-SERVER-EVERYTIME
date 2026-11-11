package org.sopt.everytime.timetable.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TimetableEntryCreateRequest(
	@NotNull(message = "분반 ID는 필수입니다.")
	@Min(value = 1, message = "분반 ID는 1 이상의 숫자 값입니다.")
	Long sectionId
) {
}
