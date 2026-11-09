package org.sopt.everytime.lecture.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sql/course-data.sql")
class CourseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getCourses_filtersTakenAndRestrictedCourses() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("hideTaken", "true")
				.param("hideRestricted", "true"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("강의 목록 조회 성공"))
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].name").value("글쓰기와 소통"))
			.andExpect(jsonPath("$.data.page").value(1));
	}

	@Test
	void getCourses_appliesFiltersAndPagination() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("category", "전공필수")
				.param("page", "1")
				.param("size", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].name").value("운영체제"))
			.andExpect(jsonPath("$.data.totalElements").value(2))
			.andExpect(jsonPath("$.data.totalPages").value(2))
			.andExpect(jsonPath("$.data.hasNext").value(true));
	}

	@Test
	void getCourses_returnsNotFoundForUnknownUser() throws Exception {
		mockMvc.perform(get("/api/v1/courses").header("X-Member-Id", 999))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
	}
}
