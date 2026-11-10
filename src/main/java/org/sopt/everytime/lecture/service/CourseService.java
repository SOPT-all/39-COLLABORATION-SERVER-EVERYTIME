package org.sopt.everytime.lecture.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sopt.everytime.global.code.GlobalErrorCode;
import org.sopt.everytime.global.exception.BusinessException;
import org.sopt.everytime.lecture.code.LectureErrorCode;
import org.sopt.everytime.lecture.dto.response.CoursePageResponse;
import org.sopt.everytime.lecture.dto.request.CourseSort;
import org.sopt.everytime.lecture.dto.response.CourseResponse;
import org.sopt.everytime.lecture.entity.Course;
import org.sopt.everytime.lecture.entity.LectureSection;
import org.sopt.everytime.lecture.entity.SectionTime;
import org.sopt.everytime.lecture.repository.CourseRepository;
import org.sopt.everytime.lecture.repository.LectureSectionRepository;
import org.sopt.everytime.lecture.repository.SectionTimeRepository;
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
	private final LectureSectionRepository lectureSectionRepository;
	private final SectionTimeRepository sectionTimeRepository;

	public CoursePageResponse getCourses(Long userId, Long categoryId, Long departmentId, String semester,
		boolean hideTaken, boolean hideRestricted, CourseSort sort, int page, int size) {
		if (userId <= 0) {
			throw new BusinessException(GlobalErrorCode.USER_ID_INVALID);
		}
		if (categoryId != null && (categoryId < 1 || categoryId > 5)) {
			throw new BusinessException(LectureErrorCode.CATEGORY_ID_INVALID);
		}
		if (!userRepository.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		Pageable pageable = PageRequest.of(page - 1, size, sort.getSort());
		Page<Course> courses = courseRepository.findCourses(userId, categoryId, departmentId, semester, hideTaken,
			hideRestricted, pageable);
		List<Long> courseIds = courses.map(course -> course.getId()).toList();
		List<LectureSection> sections = lectureSectionRepository.findAllByCourseIdIn(courseIds);
		Map<Long, List<LectureSection>> sectionsByCourse = sections.stream()
			.collect(Collectors.groupingBy(section -> section.getCourse().getId()));
		List<Long> sectionIds = sections.stream().map(LectureSection::getId).toList();
		Map<Long, List<SectionTime>> timesBySection = sectionTimeRepository.findAllBySectionIdIn(sectionIds).stream()
			.collect(Collectors.groupingBy(time -> time.getSection().getId()));
		Page<CourseResponse> responses = courses.map(course -> {
			return CourseResponse.from(course,
				sectionsByCourse.getOrDefault(course.getId(), List.of()), timesBySection);
		});
		return CoursePageResponse.from(responses);
	}
}
