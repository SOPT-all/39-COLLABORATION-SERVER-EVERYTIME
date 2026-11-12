package org.sopt.everytime.lecture.dto.response;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.sopt.everytime.lecture.entity.Course;
import org.sopt.everytime.lecture.entity.LectureSection;
import org.sopt.everytime.lecture.entity.SectionTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record CourseResponse(Long courseId, String courseCode, String courseName, String semester, Integer credit,
	Long departmentId, String departmentName, Long categoryId, String categoryName, Integer recommendedGrade,
	Integer targetGrade, String targetText,
	@Schema(description = "강의평 평균 평점 (전체 분반 기준, 소수 셋째 자리에서 반올림, 강의평이 없으면 0)",
		example = "3.67")
	double averageGradePoint,
	List<SectionResponse> sections) {

	public static CourseResponse from(Course course, Double averageGradePoint, List<LectureSection> sections,
		Map<Long, List<SectionTime>> timesBySection) {
		double rounded = averageGradePoint == null ? 0 : Math.round(averageGradePoint * 100) / 100.0;
		return new CourseResponse(course.getId(), course.getCourseCode(), course.getName(), course.getSemester(),
			course.getCredit(), course.getDepartment().getId(), course.getDepartment().getName(),
			course.getCategory().getId(), course.getCategory().getName(), course.getRecommendedGrade(),
			course.getTargetGrade(), course.getTargetText(), rounded, sections.stream()
				.map(section -> SectionResponse.from(section, timesBySection.getOrDefault(section.getId(), List.of())))
				.toList());
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
