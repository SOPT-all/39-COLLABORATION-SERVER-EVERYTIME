package org.sopt.everytime.lecture.controller;

import java.util.List;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.lecture.code.LectureSuccessCode;
import org.sopt.everytime.lecture.dto.request.CourseSort;
import org.sopt.everytime.lecture.dto.request.CourseTimeFilter;
import org.sopt.everytime.lecture.dto.response.CoursePageResponse;
import org.sopt.everytime.lecture.dto.response.CourseResponse;
import org.sopt.everytime.lecture.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CourseController implements CourseApi {
	private final CourseService courseService;

	@Override
	public ResponseEntity<BaseResponse<CoursePageResponse>> getCourses(Long userId, Long categoryId,
		Long departmentId, String semester, boolean hideTaken, boolean hideRestricted, List<String> times,
		CourseSort sort, int page, int size) {
		return BaseResponse.success(LectureSuccessCode.COURSES_FOUND,
			courseService.getCourses(userId, categoryId, departmentId, semester, hideTaken, hideRestricted,
				CourseTimeFilter.from(times), sort, page, size));
	}
}
