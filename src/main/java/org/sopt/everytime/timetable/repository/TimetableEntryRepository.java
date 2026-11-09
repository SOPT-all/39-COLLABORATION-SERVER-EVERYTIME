package org.sopt.everytime.timetable.repository;

import org.sopt.everytime.timetable.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
}
