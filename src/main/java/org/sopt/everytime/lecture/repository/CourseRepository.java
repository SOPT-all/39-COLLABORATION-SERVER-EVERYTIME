package org.sopt.everytime.lecture.repository;

import org.sopt.everytime.lecture.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query(value = """
		select c
		from Course c
		join fetch c.department d
		join fetch c.category category
		where (:categoryId is null or category.id = :categoryId)
		and (:departmentId is null or d.id = :departmentId)
		and (:semester is null or c.semester = :semester)
		and (:hideTaken = false or not exists (
			select history.id from CourseHistory history
			where history.user.id = :userId and history.course.id = c.id
		))
		and (:hideRestricted = false or c.targetGrade is null or c.targetGrade = (
			select u.grade from User u where u.id = :userId
		))
		""",
		countQuery = """
		select count(c)
		from Course c
		where (:categoryId is null or c.category.id = :categoryId)
		and (:departmentId is null or c.department.id = :departmentId)
		and (:semester is null or c.semester = :semester)
		and (:hideTaken = false or not exists (
			select history.id from CourseHistory history
			where history.user.id = :userId and history.course.id = c.id
		))
		and (:hideRestricted = false or c.targetGrade is null or c.targetGrade = (
			select u.grade from User u where u.id = :userId
		))
		""")
	Page<Course> findCourses(@Param("userId") Long userId, @Param("categoryId") Long categoryId,
		@Param("departmentId") Long departmentId, @Param("semester") String semester,
		@Param("hideTaken") boolean hideTaken, @Param("hideRestricted") boolean hideRestricted, Pageable pageable);
}
