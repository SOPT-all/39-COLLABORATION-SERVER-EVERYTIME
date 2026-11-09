package org.sopt.everytime.lecture.dto.response;

import org.sopt.everytime.lecture.entity.Course;

public record CourseResponse(Long id, String courseCode, String name, String semester, Integer credit,
	Long departmentId, String departmentName, Long categoryId, String categoryName, Integer recommendedGrade,
	Integer targetGrade, String targetText) {

	public static CourseResponse from(Course course) {
		return new CourseResponse(course.getId(), course.getCourseCode(), course.getName(), course.getSemester(),
			course.getCredit(), course.getDepartment().getId(), course.getDepartment().getName(),
			course.getCategory().getId(), course.getCategory().getName(), course.getRecommendedGrade(),
			course.getTargetGrade(), course.getTargetText());
	}
}
