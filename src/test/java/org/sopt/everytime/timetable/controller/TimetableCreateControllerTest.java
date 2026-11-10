package org.sopt.everytime.timetable.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:timetablecreatetest;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=YEAR"
})
@AutoConfigureMockMvc
@Transactional
@Sql("/sql/timetable-delete-data.sql")
class TimetableCreateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("존재하는 분반을 시간표에 추가한다")
	void addTimetableEntry_success() throws Exception {
		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{\"sectionId\":1003}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.message").value("시간표 추가 성공"))
			.andExpect(jsonPath("$.data.entryId").isNumber());

		assertThat(jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM timetable_entries WHERE user_id = 1001 AND section_id = 1003", Long.class))
			.isEqualTo(1L);
	}

	@Test
	@DisplayName("이미 담긴 분반을 다시 추가하면 409를 반환한다")
	void addTimetableEntry_duplicated() throws Exception {
		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{\"sectionId\":1001}"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("409"))
			.andExpect(jsonPath("$.message").value("이미 시간표에 담긴 강의입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("존재하지 않는 분반은 404를 반환한다")
	void addTimetableEntry_sectionNotFound() throws Exception {
		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{\"sectionId\":999999}"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 강의 분반입니다."));
	}

	@Test
	@DisplayName("존재하지 않는 사용자는 404를 반환한다")
	void addTimetableEntry_userNotFound() throws Exception {
		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "999999")
				.contentType("application/json")
				.content("{\"sectionId\":1003}"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
	}

	@Test
	@DisplayName("기존 시간표와 시간이 겹치는 분반은 400을 반환한다")
	void addTimetableEntry_timeConflict() throws Exception {
		jdbcTemplate.update("""
			INSERT INTO section_times (id, section_id, day_of_week, start_time, end_time)
			VALUES (1005, 1003, 'MON', '13:00:00', '14:00:00')
			""");

		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{\"sectionId\":1003}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("시간이 겹치는 강의가 있습니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("수강 대상 학년이 아닌 강의는 400을 반환한다")
	void addTimetableEntry_gradeRestricted() throws Exception {
		jdbcTemplate.update("UPDATE courses SET target_grade = 2 WHERE id = 1003");

		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{\"sectionId\":1003}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("수강 대상 학년이 아닙니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("분반 ID가 없거나 1 미만이면 400을 반환한다")
	void addTimetableEntry_invalidSectionId() throws Exception {
		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.data.sectionId").value("분반 ID는 필수입니다."));

		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{\"sectionId\":0}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.data.sectionId").value("분반 ID는 1 이상의 숫자 값입니다."));

		mockMvc.perform(post("/api/v1/timetable/entries")
				.header("X-Member-Id", "1001")
				.contentType("application/json")
				.content("{\"sectionId\":\"abc\"}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("분반 ID는 1 이상의 숫자 값입니다."));
	}
}
