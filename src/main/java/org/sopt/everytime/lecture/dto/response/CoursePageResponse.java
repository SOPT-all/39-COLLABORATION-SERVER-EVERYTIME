package org.sopt.everytime.lecture.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record CoursePageResponse(List<CourseResponse> courses, int page, int size, long totalElements, int totalPages,
	boolean hasNext) {

	public static CoursePageResponse from(Page<CourseResponse> page) {
		return new CoursePageResponse(page.getContent(), page.getNumber() + 1, page.getSize(), page.getTotalElements(),
			page.getTotalPages(), page.hasNext());
	}
}
