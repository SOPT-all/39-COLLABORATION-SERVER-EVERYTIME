package org.sopt.everytime.lecture.repository;

import org.sopt.everytime.lecture.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query("""
		select c
		from Course c
		join c.department d
		join c.category category
		where (:category is null or category.name = :category)
		and (:departmentId is null or d.id = :departmentId)
		and (:semester is null or c.semester = :semester)
		and (:hideTaken = false or not exists (
			select history.id from CourseHistory history
			where history.user.id = :userId and history.course.id = c.id
		))
		and (:hideRestricted = false or c.targetGrade is null or c.targetGrade = (
			select u.grade from User u where u.id = :userId
		))
		""")
	Page<Course> findCourses(@Param("userId") Long userId, @Param("category") String category,
		@Param("departmentId") Long departmentId, @Param("semester") String semester,
		@Param("hideTaken") boolean hideTaken, @Param("hideRestricted") boolean hideRestricted, Pageable pageable);
}
