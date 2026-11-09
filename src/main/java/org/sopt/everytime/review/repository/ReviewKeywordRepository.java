package org.sopt.everytime.review.repository;

import java.util.Collection;
import java.util.List;

import org.sopt.everytime.review.entity.ReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {

	@Query("""
		select new org.sopt.everytime.review.repository.KeywordReviewCount(rk.keyword.id, count(rk))
		from ReviewKeyword rk
		where rk.review.section.id = :sectionId
		group by rk.keyword.id
		""")
	List<KeywordReviewCount> countBySectionIdGroupByKeyword(Long sectionId);

	List<ReviewKeyword> findByKeywordIdAndReviewIdIn(Long keywordId, Collection<Long> reviewIds);
}
