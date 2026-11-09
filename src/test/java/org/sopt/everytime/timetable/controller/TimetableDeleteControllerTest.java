package org.sopt.everytime.timetable.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.sopt.everytime.timetable.repository.TimetableEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:timetabledeletetest;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=YEAR"
})
@AutoConfigureMockMvc
@Transactional
@Sql("/sql/timetable-delete-data.sql")
class TimetableDeleteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TimetableEntryRepository timetableEntryRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("본인의 담기 항목만 삭제하고 강의와 수업 시간 및 다른 항목은 유지한다")
	void deleteTimetableEntry_success() throws Exception {
		mockMvc.perform(delete("/timetable/entries/1001").param("userId", "1001"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("시간표 항목 삭제 성공"))
			.andExpect(jsonPath("$.data").isEmpty());

		timetableEntryRepository.flush();
		assertThat(timetableEntryRepository.existsById(1001L)).isFalse();
		assertThat(timetableEntryRepository.existsById(1002L)).isTrue();
		assertThat(timetableEntryRepository.existsById(1003L)).isTrue();
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM lecture_sections WHERE id = 1001", Long.class))
			.isEqualTo(1L);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM section_times WHERE section_id = 1001", Long.class))
			.isEqualTo(3L);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM courses WHERE id = 1001", Long.class))
			.isEqualTo(1L);
	}

	@Test
	@DisplayName("다른 사용자의 담기 항목은 403을 반환하고 삭제하지 않는다")
	void deleteTimetableEntry_forbidden() throws Exception {
		mockMvc.perform(delete("/timetable/entries/1001").param("userId", "1002"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("해당 시간표 항목을 삭제할 권한이 없습니다."))
			.andExpect(jsonPath("$.data").isEmpty());
		assertThat(timetableEntryRepository.existsById(1001L)).isTrue();
	}

	@Test
	@DisplayName("없는 사용자는 404를 반환하고 항목은 유지한다")
	void deleteTimetableEntry_userNotFound() throws Exception {
		mockMvc.perform(delete("/timetable/entries/1001").param("userId", "999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
		assertThat(timetableEntryRepository.existsById(1001L)).isTrue();
	}

	@Test
	@DisplayName("없는 항목과 이미 삭제한 항목은 404를 반환한다")
	void deleteTimetableEntry_notFound() throws Exception {
		mockMvc.perform(delete("/timetable/entries/999999").param("userId", "1001"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 시간표 항목입니다."));
		mockMvc.perform(delete("/timetable/entries/1001").param("userId", "1001"))
			.andExpect(status().isOk());
		timetableEntryRepository.flush();
		mockMvc.perform(delete("/timetable/entries/1001").param("userId", "1001"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("존재하지 않는 시간표 항목입니다."));
	}

	@Test
	@DisplayName("사용자 ID가 없으면 필드별 오류를 반환한다")
	void deleteTimetableEntry_missingUserId() throws Exception {
		mockMvc.perform(delete("/timetable/entries/1001"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.data.userId").value("userId는 필수 요청 값입니다."));
	}

	@ParameterizedTest
	@CsvSource({
		"0, 1001, entryId, 시간표 항목 ID는 1 이상이어야 합니다.",
		"-1, 1001, entryId, 시간표 항목 ID는 1 이상이어야 합니다.",
		"abc, 1001, entryId, entryId의 형식이 올바르지 않습니다.",
		"1.5, 1001, entryId, entryId의 형식이 올바르지 않습니다.",
		"9223372036854775808, 1001, entryId, entryId의 형식이 올바르지 않습니다.",
		"1001, 0, userId, 사용자 ID는 1 이상이어야 합니다.",
		"1001, -1, userId, 사용자 ID는 1 이상이어야 합니다.",
		"1001, abc, userId, userId의 형식이 올바르지 않습니다.",
		"1001, 1.5, userId, userId의 형식이 올바르지 않습니다.",
		"1001, 9223372036854775808, userId, userId의 형식이 올바르지 않습니다."
	})
	@DisplayName("잘못된 ID는 내부 정보 없이 400과 필드별 메시지를 반환한다")
	void deleteTimetableEntry_invalidId(String entryId, String userId, String field, String message) throws Exception {
		mockMvc.perform(delete("/timetable/entries/{entryId}", entryId).param("userId", userId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.data." + field).value(message));
		assertThat(timetableEntryRepository.count()).isEqualTo(3L);
	}
}
