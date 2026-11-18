package org.sopt.everytime.lecture.repository;

import java.util.Collection;
import java.util.List;

import org.sopt.everytime.lecture.entity.SectionTime;
import org.sopt.everytime.lecture.entity.Weekday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionTimeRepository extends JpaRepository<SectionTime, Long> {

	List<SectionTime> findAllBySectionIdIn(Collection<Long> sectionIds);

	@Query("select distinct t.section.id from SectionTime t where t.dayOfWeek in :days")
	List<Long> findSectionIdsByDayOfWeekIn(@Param("days") Collection<Weekday> days);
}
