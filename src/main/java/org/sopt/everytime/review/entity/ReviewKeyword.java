package org.sopt.everytime.review.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "review_keywords",
	uniqueConstraints = @UniqueConstraint(name = "uk_review_keywords_review_keyword", columnNames = {"review_id",
		"keyword_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewKeyword {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "keyword_id", nullable = false)
	private Keyword keyword;

	private Integer highlightStart;

	private Integer highlightEnd;
}
