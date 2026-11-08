package org.sopt.everytime.review.entity;

import java.math.BigDecimal;

import org.sopt.everytime.lecture.entity.LectureSection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "section_id", nullable = false)
	private LectureSection section;

	@Column(nullable = false)
	private Long year;

	@Column(nullable = false)
	private Long likeCount;

	@Column(nullable = false, length = 10)
	private String semester;

	@Column(nullable = false, precision = 3, scale = 2)
	private BigDecimal gradePoint;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;
}
