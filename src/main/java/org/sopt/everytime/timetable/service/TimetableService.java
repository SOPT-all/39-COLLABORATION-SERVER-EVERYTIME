package org.sopt.everytime.timetable.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sopt.everytime.global.exception.BusinessException;
import org.sopt.everytime.global.code.GlobalErrorCode;
import org.sopt.everytime.lecture.entity.SectionTime;
import org.sopt.everytime.lecture.entity.LectureSection;
import org.sopt.everytime.lecture.repository.LectureSectionRepository;
import org.sopt.everytime.lecture.repository.SectionTimeRepository;
import org.sopt.everytime.timetable.code.TimetableErrorCode;
import org.sopt.everytime.timetable.dto.request.TimetableEntryCreateRequest;
import org.sopt.everytime.timetable.dto.response.TimetableEntryResponse;
import org.sopt.everytime.timetable.dto.response.TimetableEntryCreateResponse;
import org.sopt.everytime.timetable.dto.response.TimetableResponse;
import org.sopt.everytime.timetable.dto.response.TimetableTimeResponse;
import org.sopt.everytime.timetable.entity.TimetableEntry;
import org.sopt.everytime.timetable.repository.TimetableEntryRepository;
import org.sopt.everytime.user.code.UserErrorCode;
import org.sopt.everytime.user.entity.User;
import org.sopt.everytime.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimetableService {

	private final UserRepository userRepository;
	private final LectureSectionRepository lectureSectionRepository;
	private final TimetableEntryRepository timetableEntryRepository;
	private final SectionTimeRepository sectionTimeRepository;

	public TimetableResponse getTimetable(Long userId) {
		if (userId <= 0) {
			throw new BusinessException(GlobalErrorCode.USER_ID_INVALID);
		}
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
	public TimetableEntryCreateResponse addTimetableEntry(Long userId, TimetableEntryCreateRequest request) {
		validateUserId(userId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		LectureSection section = lectureSectionRepository.findById(request.sectionId())
			.orElseThrow(() -> new BusinessException(TimetableErrorCode.SECTION_NOT_FOUND));
		if (timetableEntryRepository.existsByUserIdAndSectionId(userId, request.sectionId())) {
			throw new BusinessException(TimetableErrorCode.TIMETABLE_ENTRY_DUPLICATED);
		}
		if (hasTimeConflict(userId, section.getId())) {
			throw new BusinessException(TimetableErrorCode.TIMETABLE_TIME_CONFLICT);
		}
		try {
			TimetableEntry entry = timetableEntryRepository.saveAndFlush(TimetableEntry.of(user, section));
			return new TimetableEntryCreateResponse(entry.getId());
		} catch (DataIntegrityViolationException exception) {
			throw new BusinessException(TimetableErrorCode.TIMETABLE_ENTRY_DUPLICATED);
		}
	}

	private boolean hasTimeConflict(Long userId, Long sectionId) {
		List<TimetableEntry> entries = timetableEntryRepository.findAllWithCourseByUserId(userId);
		if (entries.isEmpty()) {
			return false;
		}
		List<Long> sectionIds = entries.stream()
			.map(entry -> entry.getSection().getId())
			.toList();
		List<SectionTime> existingTimes = sectionTimeRepository.findAllBySectionIdIn(sectionIds);
		List<SectionTime> requestedTimes = sectionTimeRepository.findAllBySectionIdIn(List.of(sectionId));
		return requestedTimes.stream().anyMatch(requested -> existingTimes.stream().anyMatch(existing ->
			requested.getDayOfWeek() == existing.getDayOfWeek()
				&& requested.getStartTime().isBefore(existing.getEndTime())
				&& existing.getStartTime().isBefore(requested.getEndTime())));
	}

	@Transactional
	public void deleteTimetableEntry(Long entryId, Long userId) {
		validateUserId(userId);
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

	private void validateUserId(Long userId) {
		if (userId <= 0) {
			throw new BusinessException(GlobalErrorCode.USER_ID_INVALID);
		}
	}
}
