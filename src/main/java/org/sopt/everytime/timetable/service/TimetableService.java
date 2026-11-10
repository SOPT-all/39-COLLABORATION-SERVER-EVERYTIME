package org.sopt.everytime.timetable.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sopt.everytime.global.exception.BusinessException;
import org.sopt.everytime.lecture.entity.SectionTime;
import org.sopt.everytime.lecture.repository.SectionTimeRepository;
import org.sopt.everytime.timetable.code.TimetableErrorCode;
import org.sopt.everytime.timetable.dto.response.TimetableEntryResponse;
import org.sopt.everytime.timetable.dto.response.TimetableResponse;
import org.sopt.everytime.timetable.dto.response.TimetableTimeResponse;
import org.sopt.everytime.timetable.entity.TimetableEntry;
import org.sopt.everytime.timetable.repository.TimetableEntryRepository;
import org.sopt.everytime.user.code.UserErrorCode;
import org.sopt.everytime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimetableService {

	private final UserRepository userRepository;
	private final TimetableEntryRepository timetableEntryRepository;
	private final SectionTimeRepository sectionTimeRepository;

	public TimetableResponse getTimetable(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		List<TimetableEntry> entries = timetableEntryRepository.findAllWithCourseByUserId(userId);
		if (entries.isEmpty()) {
			return new TimetableResponse(List.of());
		}
		List<Long> sectionIds = entries.stream().map(entry -> entry.getSection().getId()).toList();
		Map<Long, List<TimetableTimeResponse>> timesBySection = sectionTimeRepository.findAllBySectionIdIn(sectionIds)
			.stream()
			.sorted(Comparator.comparing(SectionTime::getDayOfWeek).thenComparing(SectionTime::getStartTime))
			.collect(Collectors.groupingBy(time -> time.getSection().getId(),
				Collectors.mapping(TimetableTimeResponse::from, Collectors.toList())));

		return new TimetableResponse(entries.stream()
			.map(entry -> TimetableEntryResponse.of(entry,
				timesBySection.getOrDefault(entry.getSection().getId(), List.of())))
			.toList());
	}

	@Transactional
	public void deleteTimetableEntry(Long entryId, Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		TimetableEntry entry = timetableEntryRepository.findById(entryId)
			.orElseThrow(() -> new BusinessException(TimetableErrorCode.TIMETABLE_ENTRY_NOT_FOUND));
		if (!entry.getUser().getId().equals(userId)) {
			throw new BusinessException(TimetableErrorCode.TIMETABLE_ENTRY_FORBIDDEN);
		}
		timetableEntryRepository.delete(entry);
	}
}
