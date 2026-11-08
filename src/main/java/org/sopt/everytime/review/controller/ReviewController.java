package org.sopt.everytime.review.controller;

import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.review.code.ReviewSuccessCode;
import org.sopt.everytime.review.dto.request.ReviewSort;
import org.sopt.everytime.review.dto.response.ReviewListResponse;
import org.sopt.everytime.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewApi {

	private final ReviewService reviewService;

	@Override
	public ResponseEntity<BaseResponse<ReviewListResponse>> getReviews(Long sectionId, Long keywordId,
		ReviewSort sort, int page, int size) {
		return BaseResponse.success(ReviewSuccessCode.REVIEWS_FOUND,
			reviewService.getReviews(sectionId, keywordId, sort, page, size));
	}
}
