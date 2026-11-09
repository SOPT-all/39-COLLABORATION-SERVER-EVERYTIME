package org.sopt.everytime.lecture.dto.response;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.sopt.everytime.lecture.entity.Course;
import org.sopt.everytime.lecture.entity.LectureSection;
import org.sopt.everytime.lecture.entity.SectionTime;

public record CourseResponse(Long courseId, String courseCode, String courseName, String semester, Integer credit,
	Long departmentId, String departmentName, Long categoryId, String categoryName, Integer recommendedGrade,
	Integer targetGrade, String targetText, List<SectionResponse> sections) {

	public static CourseResponse from(Course course, List<LectureSection> sections, Map<Long, List<SectionTime>> timesBySection) {
		return new CourseResponse(course.getId(), course.getCourseCode(), course.getName(), course.getSemester(),
			course.getCredit(), course.getDepartment().getId(), course.getDepartment().getName(),
			course.getCategory().getId(), course.getCategory().getName(), course.getRecommendedGrade(),
			course.getTargetGrade(), course.getTargetText(), sections.stream().map(section -> SectionResponse.from(section,
				timesBySection.getOrDefault(section.getId(), List.of()))).toList());
	}

	public record SectionResponse(Long sectionId, String sectionNo, String professor, Integer capacity,
		Integer savedCount, List<SectionTimeResponse> times) {

		public static SectionResponse from(LectureSection section, List<SectionTime> times) {
			return new SectionResponse(section.getId(), section.getSectionNo(), section.getProfessor(),
				section.getCapacity(), section.getSavedCount(), times.stream().map(SectionTimeResponse::from).toList());
		}
	}

	public record SectionTimeResponse(String dayOfWeek, LocalTime startTime, LocalTime endTime, String classroom) {

		public static SectionTimeResponse from(SectionTime time) {
			return new SectionTimeResponse(time.getDayOfWeek().name(), time.getStartTime(), time.getEndTime(),
				time.getClassroom());
		}
	}
}
