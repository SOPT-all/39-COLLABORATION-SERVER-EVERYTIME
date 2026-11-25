package org.sopt.everytime.lecture.dto.request;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.sopt.everytime.global.exception.BusinessException;
import org.sopt.everytime.lecture.code.LectureErrorCode;
import org.sopt.everytime.lecture.entity.SectionTime;
import org.sopt.everytime.lecture.entity.Weekday;

public class CourseTimeFilter {
	private final List<TimeBlock> blocks;

	private CourseTimeFilter(List<TimeBlock> blocks) {
		this.blocks = blocks;
	}

	public static CourseTimeFilter from(List<String> times) {
		if (times == null || times.isEmpty()) {
			return new CourseTimeFilter(List.of());
		}
		return new CourseTimeFilter(times.stream().map(TimeBlock::parse).toList());
	}

	public boolean isEnabled() {
		return !blocks.isEmpty();
	}

	public Set<Weekday> getDays() {
		return blocks.stream().map(TimeBlock::dayOfWeek).collect(Collectors.toSet());
	}

	public boolean overlapsAny(Collection<SectionTime> times) {
		return times.stream().anyMatch(this::overlaps);
	}

	private boolean overlaps(SectionTime time) {
		return blocks.stream()
			.anyMatch(block -> block.dayOfWeek() == time.getDayOfWeek()
				&& block.startTime().isBefore(time.getEndTime())
				&& time.getStartTime().isBefore(block.endTime()));
	}

	private record TimeBlock(Weekday dayOfWeek, LocalTime startTime, LocalTime endTime) {
		private static final String END_OF_DAY = "24:00";

		private static TimeBlock parse(String value) {
			String[] parts = value.split("-");
			if (parts.length != 3) {
				throw new BusinessException(LectureErrorCode.TIME_FILTER_INVALID);
			}
			try {
				Weekday dayOfWeek = Weekday.valueOf(parts[0]);
				LocalTime startTime = LocalTime.parse(parts[1]);
				LocalTime endTime = parseEndTime(parts[2]);
				if (!startTime.isBefore(endTime)) {
					throw new BusinessException(LectureErrorCode.TIME_FILTER_INVALID);
				}
				return new TimeBlock(dayOfWeek, startTime, endTime);
			} catch (IllegalArgumentException | DateTimeParseException exception) {
				throw new BusinessException(LectureErrorCode.TIME_FILTER_INVALID);
			}
		}

		private static LocalTime parseEndTime(String value) {
			if (END_OF_DAY.equals(value)) {
				return LocalTime.MAX;
			}
			return LocalTime.parse(value);
		}
	}
}
