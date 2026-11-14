package org.sopt.everytime.lecture.dto.response;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sopt.everytime.lecture.entity.Course;
import org.sopt.everytime.lecture.entity.LectureSection;
import org.sopt.everytime.lecture.entity.SectionTime;
import org.sopt.everytime.lecture.entity.Weekday;

import io.swagger.v3.oas.annotations.media.Schema;

public record CourseResponse(Long courseId, String courseCode, String courseName, String semester, Integer credit,
	Long departmentId, String departmentName, Long categoryId, String categoryName, Integer recommendedGrade,
	Integer targetGrade, String targetText, List<SectionResponse> sections) {

	public static CourseResponse from(Course course, List<LectureSection> sections,
		Map<Long, List<SectionTime>> timesBySection, Map<Long, Double> averageGradePoints) {
		return new CourseResponse(course.getId(), course.getCourseCode(), course.getName(), course.getSemester(),
			course.getCredit(), course.getDepartment().getId(), course.getDepartment().getName(),
			course.getCategory().getId(), course.getCategory().getName(), course.getRecommendedGrade(),
			course.getTargetGrade(), course.getTargetText(), sections.stream()
				.map(section -> SectionResponse.from(section, timesBySection.getOrDefault(section.getId(), List.of()),
					averageGradePoints.get(section.getId())))
				.toList());
	}

	public record SectionResponse(Long sectionId, String sectionNo, String professor, Integer capacity,
		Integer savedCount,
		@Schema(description = "분반 강의평 평균 평점 (소수 셋째 자리에서 반올림, 강의평이 없으면 0)", example = "3.67")
		double averageGradePoint,
		List<SectionTimeResponse> times, String timeText) {

		public static SectionResponse from(LectureSection section, List<SectionTime> times, Double averageGradePoint) {
			double rounded = averageGradePoint == null ? 0 : Math.round(averageGradePoint * 100) / 100.0;
			return new SectionResponse(section.getId(), section.getSectionNo(), section.getProfessor(),
				section.getCapacity(), section.getSavedCount(), rounded,
				times.stream().map(SectionTimeResponse::from).toList(), createTimeText(times));
		}

		private static String createTimeText(List<SectionTime> times) {
			Map<TimeSlot, List<String>> daysByTimeSlot = times.stream()
				.sorted(Comparator.comparing(SectionTime::getStartTime))
				.collect(Collectors.groupingBy(time -> new TimeSlot(time.getStartTime(), time.getEndTime(),
					time.getClassroom()), LinkedHashMap::new,
					Collectors.mapping(time -> toKoreanDay(time.getDayOfWeek()), Collectors.toList())));
			return daysByTimeSlot.entrySet().stream()
				.map(entry -> String.join("/", entry.getValue()) + " "
					+ entry.getKey().startTime().format(TIME_FORMATTER) + "~"
					+ entry.getKey().endTime().format(TIME_FORMATTER) + " / "
					+ entry.getKey().classroom())
				.collect(Collectors.joining(", "));
		}

		private static String toKoreanDay(Weekday day) {
			return switch (day) {
				case MON -> "월";
				case TUE -> "화";
				case WED -> "수";
				case THU -> "목";
				case FRI -> "금";
				case SAT -> "토";
				case SUN -> "일";
			};
		}

		private record TimeSlot(LocalTime startTime, LocalTime endTime, String classroom) {
		}

		private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	}

	public record SectionTimeResponse(String dayOfWeek, LocalTime startTime, LocalTime endTime, String classroom) {

		public static SectionTimeResponse from(SectionTime time) {
			return new SectionTimeResponse(time.getDayOfWeek().name(), time.getStartTime(), time.getEndTime(),
				time.getClassroom());
		}
	}
}
