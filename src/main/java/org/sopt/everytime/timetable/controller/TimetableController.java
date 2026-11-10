package org.sopt.everytime.timetable.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.timetable.code.TimetableSuccessCode;
import org.sopt.everytime.timetable.dto.request.TimetableEntryCreateRequest;
import org.sopt.everytime.timetable.dto.response.TimetableResponse;
import org.sopt.everytime.timetable.service.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TimetableController implements TimetableApi {

	private final TimetableService timetableService;

	@Override
	public ResponseEntity<BaseResponse<Void>> addTimetableEntry(Long userId,
		TimetableEntryCreateRequest request) {
		timetableService.addTimetableEntry(userId, request);
		return BaseResponse.success(TimetableSuccessCode.TIMETABLE_ENTRY_CREATED);
	}

	@Override
	public ResponseEntity<BaseResponse<TimetableResponse>> getTimetable(Long userId) {
		return BaseResponse.success(TimetableSuccessCode.TIMETABLE_FETCHED,
			timetableService.getTimetable(userId));
	}

	@Override
	public ResponseEntity<BaseResponse<Void>> deleteTimetableEntry(Long entryId, Long userId) {
		timetableService.deleteTimetableEntry(entryId, userId);
		return BaseResponse.success(TimetableSuccessCode.TIMETABLE_DELETED);
	}
}
