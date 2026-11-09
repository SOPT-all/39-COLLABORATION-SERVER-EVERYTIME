package org.sopt.everytime.timetable.service;

import org.sopt.everytime.global.exception.BusinessException;
import org.sopt.everytime.timetable.code.TimetableErrorCode;
import org.sopt.everytime.timetable.entity.TimetableEntry;
import org.sopt.everytime.timetable.repository.TimetableEntryRepository;
import org.sopt.everytime.user.code.UserErrorCode;
import org.sopt.everytime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimetableService {

	private final UserRepository userRepository;
	private final TimetableEntryRepository timetableEntryRepository;

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
