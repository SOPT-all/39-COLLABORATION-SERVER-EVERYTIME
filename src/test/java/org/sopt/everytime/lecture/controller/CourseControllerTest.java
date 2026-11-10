package org.sopt.everytime.lecture.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
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
			.andExpect(jsonPath("$.data.courses", hasSize(1)))
			.andExpect(jsonPath("$.data.courses[0].courseName").value("글쓰기와 소통"))
			.andExpect(jsonPath("$.data.page").value(1));
	}

	@Test
	void getCourses_appliesFiltersAndPagination() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("categoryId", "1")
				.param("page", "1")
				.param("size", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses", hasSize(1)))
			.andExpect(jsonPath("$.data.courses[0].courseName").value("운영체제"))
			.andExpect(jsonPath("$.data.totalElements").value(2))
			.andExpect(jsonPath("$.data.totalPages").value(2))
			.andExpect(jsonPath("$.data.hasNext").value(true));
	}

	@Test
	void getCourses_appliesDepartmentAndSemesterFilters() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("departmentId", "2")
				.param("semester", "2학기"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses", hasSize(1)))
			.andExpect(jsonPath("$.data.courses[0].courseName").value("글쓰기와 소통"));
	}

	@Test
	void getCourses_appliesDescendingNameSort() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("sort", "NAME_DESC"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses[0].courseName").value("자료구조"));
	}

	@Test
	void getCourses_returnsBadRequestForInvalidPagination() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("page", "0"))
			.andExpect(status().isBadRequest());

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("size", "0"))
			.andExpect(status().isBadRequest());

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("size", "51"))
			.andExpect(status().isBadRequest());

	}

	@Test
	void getCourses_returnsBadRequestForInvalidMemberHeader() throws Exception {
		mockMvc.perform(get("/api/v1/courses"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."))
			.andExpect(jsonPath("$.data").value(nullValue()));

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", "invalid"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자 ID는 숫자여야 합니다."));

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 0))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자 ID는 1 이상이어야 합니다."))
			.andExpect(jsonPath("$.data").value(nullValue()));

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", -1))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자 ID는 1 이상이어야 합니다."));
	}

	@Test
	void getCourses_returnsNotFoundForUnknownUser() throws Exception {
		mockMvc.perform(get("/api/v1/courses").header("X-Member-Id", 999))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
	}
}
