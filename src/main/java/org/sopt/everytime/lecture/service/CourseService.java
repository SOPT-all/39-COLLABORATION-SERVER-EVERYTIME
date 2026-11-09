package org.sopt.everytime.lecture.service;

import org.sopt.everytime.global.exception.BusinessException;
import org.sopt.everytime.global.response.PageResponse;
import org.sopt.everytime.lecture.dto.request.CourseSort;
import org.sopt.everytime.lecture.dto.response.CourseResponse;
import org.sopt.everytime.lecture.repository.CourseRepository;
import org.sopt.everytime.user.code.UserErrorCode;
import org.sopt.everytime.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
	private final CourseRepository courseRepository;
	private final UserRepository userRepository;

	public PageResponse<CourseResponse> getCourses(Long userId, String category, Long departmentId, String semester,
		boolean hideTaken, boolean hideRestricted, CourseSort sort, int page, int size) {
		if (!userRepository.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		Pageable pageable = PageRequest.of(page - 1, size, sort.getSort());
		Page<CourseResponse> courses = courseRepository
			.findCourses(userId, category, departmentId, semester, hideTaken, hideRestricted, pageable)
			.map(CourseResponse::from);
		return PageResponse.from(courses);
	}
}
