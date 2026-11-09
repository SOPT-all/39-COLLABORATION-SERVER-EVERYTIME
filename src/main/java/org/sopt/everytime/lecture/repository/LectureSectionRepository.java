package org.sopt.everytime.lecture.repository;

import java.util.List;
import java.util.Optional;

import org.sopt.everytime.lecture.entity.LectureSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LectureSectionRepository extends JpaRepository<LectureSection, Long> {

	@Query("select s from LectureSection s join fetch s.course where s.id = :id")
	Optional<LectureSection> findWithCourseById(Long id);

	List<LectureSection> findAllByCourseIdIn(List<Long> courseIds);
}
