package org.sopt.everytime.review.dto.request;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSort {

	LATEST(Sort.by(Sort.Direction.DESC, "id"));

	private final Sort sort;
}
