package org.sopt.everytime.lecture.controller;

import static org.hamcrest.Matchers.contains;
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
	void getCourses_returnsAverageGradePointPerSection() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("departmentId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses[0].courseName").value("운영체제"))
			.andExpect(jsonPath("$.data.courses[0].sections[?(@.sectionId == 3)].averageGradePoint")
				.value(contains(5)))
			.andExpect(jsonPath("$.data.courses[1].courseName").value("자료구조"))
			.andExpect(jsonPath("$.data.courses[1].sections[?(@.sectionId == 1)].averageGradePoint")
				.value(contains(4)))
			.andExpect(jsonPath("$.data.courses[1].sections[?(@.sectionId == 2)].averageGradePoint")
				.value(contains(0)))
			.andExpect(jsonPath("$.data.courses[1].averageGradePoint").doesNotExist());
	}

	@Test
	void getCourses_returnsTimeTextPerSection() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("departmentId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses[1].sections[?(@.sectionId == 1)].timeText")
				.value(contains("화/목 10:30~11:45 / 공학관 410")))
			.andExpect(jsonPath("$.data.courses[1].sections[0].times", hasSize(2)));
	}

	@Test
	void getCourses_returnsSectionsWithinMergedTimeBlocks() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("times", "MON-09:00-10:00", "MON-10:00-11:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses", hasSize(1)))
			.andExpect(jsonPath("$.data.courses[0].courseName").value("운영체제"))
			.andExpect(jsonPath("$.data.totalElements").value(1));
	}

	@Test
	void getCourses_returnsOnlyMatchedSectionsWhenTimeFiltered() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("times", "TUE-10:00-12:00", "THU-10:00-12:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses", hasSize(1)))
			.andExpect(jsonPath("$.data.courses[0].courseName").value("자료구조"))
			.andExpect(jsonPath("$.data.courses[0].sections", hasSize(1)))
			.andExpect(jsonPath("$.data.courses[0].sections[0].sectionId").value(1));
	}

	@Test
	void getCourses_excludesSectionsWithTimesOutsideBlocks() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("times", "TUE-10:00-12:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.courses", hasSize(0)))
			.andExpect(jsonPath("$.data.totalElements").value(0));
	}

	@Test
	void getCourses_returnsBadRequestForInvalidTimeFilter() throws Exception {
		for (String time : new String[] {"MON-10:00", "XYZ-09:00-10:00", "MON-11:00-10:00", "MON-9시-10:00"}) {
			mockMvc.perform(get("/api/v1/courses")
					.header("X-Member-Id", 1)
					.param("times", time))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("시간 필터 형식이 올바르지 않습니다."));
		}
	}

	@Test
	void getCourses_returnsBadRequestForInvalidCategoryId() throws Exception {
		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("categoryId", "0"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("카테고리 ID는 1부터 5사이의 숫자 값 입니다"));

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("categoryId", "6"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("카테고리 ID는 1부터 5사이의 숫자 값 입니다"));

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 1)
				.param("categoryId", "a"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("카테고리 ID는 1부터 5사이의 숫자 값 입니다"));
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
			.andExpect(jsonPath("$.message").value("사용자 ID는 1 이상의 숫자 값입니다."));

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", 0))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자 ID는 1 이상의 숫자 값입니다."))
			.andExpect(jsonPath("$.data").value(nullValue()));

		mockMvc.perform(get("/api/v1/courses")
				.header("X-Member-Id", -1))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자 ID는 1 이상의 숫자 값입니다."));
	}

	@Test
	void getCourses_returnsNotFoundForUnknownUser() throws Exception {
		mockMvc.perform(get("/api/v1/courses").header("X-Member-Id", 999))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
	}
}
