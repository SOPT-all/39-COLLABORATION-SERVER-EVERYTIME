package org.sopt.everytime.timetable.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.timetable.code.TimetableSuccessCode;
import org.sopt.everytime.timetable.dto.request.TimetableRequest;
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
	public ResponseEntity<BaseResponse<TimetableResponse>> getTimetable(TimetableRequest request) {
		return BaseResponse.success(TimetableSuccessCode.TIMETABLE_FETCHED,
			timetableService.getTimetable(request.userId()));
	}
}
