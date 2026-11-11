package org.sopt.everytime.lecture.dto.request;

import org.springframework.data.domain.Sort;

public enum CourseSort {
	NAME_ASC(Sort.by(Sort.Direction.ASC, "name")),
	NAME_DESC(Sort.by(Sort.Direction.DESC, "name"));

	private final Sort sort;

	CourseSort(Sort sort) {
		this.sort = sort;
	}

	public Sort getSort() {
		return sort;
	}
}
