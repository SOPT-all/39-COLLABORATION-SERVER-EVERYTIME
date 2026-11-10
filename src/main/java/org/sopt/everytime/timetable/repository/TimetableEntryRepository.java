package org.sopt.everytime.timetable.repository;

import java.util.List;

import org.sopt.everytime.timetable.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {

	@Query("""
		select e from TimetableEntry e
		join fetch e.section s
		join fetch s.course
		where e.user.id = :userId
		order by e.id
		""")
	List<TimetableEntry> findAllWithCourseByUserId(Long userId);
}
