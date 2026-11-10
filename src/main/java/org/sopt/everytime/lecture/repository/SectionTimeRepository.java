package org.sopt.everytime.lecture.repository;

import java.util.Collection;
import java.util.List;

import org.sopt.everytime.lecture.entity.SectionTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionTimeRepository extends JpaRepository<SectionTime, Long> {

	List<SectionTime> findAllBySectionIdIn(Collection<Long> sectionIds);
}
