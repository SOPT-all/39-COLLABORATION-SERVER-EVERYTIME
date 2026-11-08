package org.sopt.everytime.review.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sql/review-data.sql")
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("전체 조회는 강의 정보, 칩별 개수, 등록순 강의평을 응답한다")
	void getReviews_all() throws Exception {
		mockMvc.perform(get("/sections/1/reviews"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("강의평 목록 조회 성공"))
			.andExpect(jsonPath("$.data.lecture.sectionId").value(1))
			.andExpect(jsonPath("$.data.lecture.courseName").value("AI와창의적문제해결"))
			.andExpect(jsonPath("$.data.lecture.averageGradePoint").value(4.33))
			.andExpect(jsonPath("$.data.keywords", hasSize(4)))
			.andExpect(jsonPath("$.data.keywords[0].name").value("교수님"))
			.andExpect(jsonPath("$.data.keywords[0].count").value(1))
			.andExpect(jsonPath("$.data.keywords[1].count").value(2))
			.andExpect(jsonPath("$.data.keywords[2].count").value(1))
			.andExpect(jsonPath("$.data.keywords[3].name").value("팀 프로젝트"))
			.andExpect(jsonPath("$.data.keywords[3].count").value(0))
			.andExpect(jsonPath("$.data.reviews.content[0].reviewId").value(103))
			.andExpect(jsonPath("$.data.reviews.content[0].year").value(2026))
			.andExpect(jsonPath("$.data.reviews.content[0].semester").value("2학기"))
			.andExpect(jsonPath("$.data.reviews.content[0].gradePoint").value(5.0))
			.andExpect(jsonPath("$.data.reviews.content[0].highlight").isEmpty())
			.andExpect(jsonPath("$.data.reviews.content[2].reviewId").value(101))
			.andExpect(jsonPath("$.data.reviews.page").value(1))
			.andExpect(jsonPath("$.data.reviews.size").value(10))
			.andExpect(jsonPath("$.data.reviews.totalElements").value(3))
			.andExpect(jsonPath("$.data.reviews.hasNext").value(false));
	}

	@Test
	@DisplayName("page와 size로 1부터 시작하는 페이지를 조회한다")
	void getReviews_paging() throws Exception {
		mockMvc.perform(get("/sections/1/reviews").param("page", "2").param("size", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.reviews.content", hasSize(1)))
			.andExpect(jsonPath("$.data.reviews.content[0].reviewId").value(101))
			.andExpect(jsonPath("$.data.reviews.page").value(2))
			.andExpect(jsonPath("$.data.reviews.totalPages").value(2))
			.andExpect(jsonPath("$.data.reviews.hasNext").value(false));
	}

	@Test
	@DisplayName("칩별 조회는 키워드가 달린 강의평만 강조 구간과 함께 응답한다")
	void getReviews_byKeyword() throws Exception {
		mockMvc.perform(get("/sections/1/reviews").param("keywordId", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.keywords[1].count").value(2))
			.andExpect(jsonPath("$.data.reviews.totalElements").value(2))
			.andExpect(jsonPath("$.data.reviews.content[0].reviewId").value(102))
			.andExpect(jsonPath("$.data.reviews.content[0].highlight.start").value(3))
			.andExpect(jsonPath("$.data.reviews.content[0].highlight.end").value(5))
			.andExpect(jsonPath("$.data.reviews.content[1].reviewId").value(101));
	}

	@Test
	@DisplayName("강조 구간이 없는 매핑은 highlight가 null이다")
	void getReviews_byKeyword_withoutHighlight() throws Exception {
		mockMvc.perform(get("/sections/1/reviews").param("keywordId", "3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.reviews.content[0].reviewId").value(101))
			.andExpect(jsonPath("$.data.reviews.content[0].highlight").isEmpty());
	}

	@Test
	@DisplayName("강의평이 없는 분반은 평균 0과 빈 목록으로 응답한다")
	void getReviews_empty() throws Exception {
		mockMvc.perform(get("/sections/2/reviews"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.lecture.averageGradePoint").value(0.0))
			.andExpect(jsonPath("$.data.keywords[0].count").value(0))
			.andExpect(jsonPath("$.data.reviews.content", hasSize(0)))
			.andExpect(jsonPath("$.data.reviews.totalElements").value(0));
	}

	@Test
	@DisplayName("존재하지 않는 분반은 404로 응답한다")
	void getReviews_sectionNotFound() throws Exception {
		mockMvc.perform(get("/sections/999/reviews"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 강의입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("존재하지 않는 키워드는 404로 응답한다")
	void getReviews_keywordNotFound() throws Exception {
		mockMvc.perform(get("/sections/1/reviews").param("keywordId", "5"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 키워드입니다."));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"/sections/abc/reviews",
		"/sections/1/reviews?sort=POPULAR",
		"/sections/1/reviews?page=0",
		"/sections/1/reviews?size=51",
		"/sections/1/reviews?keywordId=abc"
	})
	@DisplayName("잘못된 파라미터는 400으로 응답한다")
	void getReviews_invalidParameter(String url) throws Exception {
		mockMvc.perform(get(url))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}
}
