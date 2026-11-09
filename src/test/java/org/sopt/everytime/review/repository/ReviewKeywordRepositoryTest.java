package org.sopt.everytime.review.repository;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql("/sql/review-data.sql")
class ReviewKeywordRepositoryTest {

	private static final String INSERT_REVIEW_KEYWORD =
		"INSERT INTO review_keywords (id, review_id, keyword_id) VALUES (?, ?, ?)";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("한 리뷰에 같은 키워드를 두 번 연결할 수 없다")
	void duplicateKeywordOnSameReview() {
		assertThatThrownBy(() -> jdbcTemplate.update(INSERT_REVIEW_KEYWORD, 100, 103, 1))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("한 리뷰에 서로 다른 키워드는 함께 연결할 수 있다")
	void differentKeywordsOnSameReview() {
		assertThatCode(() -> jdbcTemplate.update(INSERT_REVIEW_KEYWORD, 100, 103, 4))
			.doesNotThrowAnyException();
	}
}
