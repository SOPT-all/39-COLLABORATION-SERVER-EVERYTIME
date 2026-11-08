package org.sopt.everytime.review.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sopt.everytime.global.exception.BusinessException;
import org.sopt.everytime.global.response.PageResponse;
import org.sopt.everytime.lecture.code.LectureErrorCode;
import org.sopt.everytime.lecture.entity.LectureSection;
import org.sopt.everytime.lecture.repository.LectureSectionRepository;
import org.sopt.everytime.review.code.ReviewErrorCode;
import org.sopt.everytime.review.dto.request.ReviewSort;
import org.sopt.everytime.review.dto.response.HighlightResponse;
import org.sopt.everytime.review.dto.response.KeywordCountResponse;
import org.sopt.everytime.review.dto.response.LectureInfoResponse;
import org.sopt.everytime.review.dto.response.ReviewListResponse;
import org.sopt.everytime.review.dto.response.ReviewResponse;
import org.sopt.everytime.review.entity.Review;
import org.sopt.everytime.review.entity.ReviewKeyword;
import org.sopt.everytime.review.repository.KeywordRepository;
import org.sopt.everytime.review.repository.KeywordReviewCount;
import org.sopt.everytime.review.repository.ReviewKeywordRepository;
import org.sopt.everytime.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final LectureSectionRepository lectureSectionRepository;
	private final ReviewRepository reviewRepository;
	private final KeywordRepository keywordRepository;
	private final ReviewKeywordRepository reviewKeywordRepository;

	public ReviewListResponse getReviews(Long sectionId, Long keywordId, ReviewSort sort, int page, int size) {
		LectureSection section = lectureSectionRepository.findWithCourseById(sectionId)
			.orElseThrow(() -> new BusinessException(LectureErrorCode.LECTURE_SECTION_NOT_FOUND));
		if (keywordId != null && !keywordRepository.existsById(keywordId)) {
			throw new BusinessException(ReviewErrorCode.KEYWORD_NOT_FOUND);
		}

		Pageable pageable = PageRequest.of(page - 1, size, sort.getSort());
		Page<ReviewResponse> reviews = keywordId == null
			? reviewRepository.findBySectionId(sectionId, pageable).map(review -> ReviewResponse.of(review, null))
			: findReviewsByKeyword(sectionId, keywordId, pageable);

		return new ReviewListResponse(
			LectureInfoResponse.of(section, reviewRepository.findAverageGradePointBySectionId(sectionId)),
			getKeywordCounts(sectionId),
			PageResponse.from(reviews)
		);
	}

	private Page<ReviewResponse> findReviewsByKeyword(Long sectionId, Long keywordId, Pageable pageable) {
		Page<Review> reviews = reviewRepository.findBySectionIdAndKeywordId(sectionId, keywordId, pageable);
		List<Long> reviewIds = reviews.map(Review::getId).getContent();
		Map<Long, ReviewKeyword> reviewKeywords = reviewKeywordRepository
			.findByKeywordIdAndReviewIdIn(keywordId, reviewIds)
			.stream()
			.collect(Collectors.toMap(reviewKeyword -> reviewKeyword.getReview().getId(), Function.identity()));

		return reviews.map(review -> ReviewResponse.of(review,
			HighlightResponse.from(reviewKeywords.get(review.getId()))));
	}

	private List<KeywordCountResponse> getKeywordCounts(Long sectionId) {
		Map<Long, Long> counts = reviewKeywordRepository.countBySectionIdGroupByKeyword(sectionId)
			.stream()
			.collect(Collectors.toMap(KeywordReviewCount::keywordId, KeywordReviewCount::count));

		return keywordRepository.findAll(Sort.by("id"))
			.stream()
			.map(keyword -> KeywordCountResponse.of(keyword, counts.getOrDefault(keyword.getId(), 0L)))
			.toList();
	}
}
