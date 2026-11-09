package org.sopt.everytime.review.repository;

import org.sopt.everytime.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findBySectionId(Long sectionId, Pageable pageable);

	@Query(value = """
		select r from Review r
		where r.section.id = :sectionId
		and exists (select 1 from ReviewKeyword rk where rk.review = r and rk.keyword.id = :keywordId)
		""")
	Page<Review> findBySectionIdAndKeywordId(Long sectionId, Long keywordId, Pageable pageable);

	@Query("select avg(r.gradePoint) from Review r where r.section.id = :sectionId")
	Double findAverageGradePointBySectionId(Long sectionId);
}
