package org.sopt.everytime.timetable.controller;

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

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:timetabletest;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=YEAR"
})
@AutoConfigureMockMvc
@Transactional
@Sql("/sql/timetable-data.sql")
class TimetableControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("담긴 강의 전체를 담기 ID 순으로 조회하고 필요한 정보만 반환한다")
	void getTimetable_all() throws Exception {
		mockMvc.perform(get("/api/v1/timetable").header("X-Member-Id", "1001"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("시간표 조회 성공"))
			.andExpect(jsonPath("$.data.entries", hasSize(2)))
			.andExpect(jsonPath("$.data.entries[0].entryId").value(1001))
			.andExpect(jsonPath("$.data.entries[0].courseName").value("운영체제"))
			.andExpect(jsonPath("$.data.entries[1].entryId").value(1002))
			.andExpect(jsonPath("$.data.entries[1].courseName").value("데이터베이스"))
			.andExpect(jsonPath("$.data.entries[0].sectionId").doesNotExist())
			.andExpect(jsonPath("$.data.entries[0].courseCode").doesNotExist())
			.andExpect(jsonPath("$.data.entries[0].professor").doesNotExist())
			.andExpect(jsonPath("$.data.entries[0].times[0].classroom").doesNotExist())
			.andExpect(jsonPath("$.data.page").doesNotExist());
	}

	@Test
	@DisplayName("한 강의의 모든 수업 시간을 요일순과 같은 요일의 시작 시각순으로 반환한다")
	void getTimetable_sortedTimes() throws Exception {
		mockMvc.perform(get("/api/v1/timetable").header("X-Member-Id", "1001"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.entries[0].times", hasSize(3)))
			.andExpect(jsonPath("$.data.entries[0].times[0].dayOfWeek").value("MON"))
			.andExpect(jsonPath("$.data.entries[0].times[0].startTime").value("09:00:00"))
			.andExpect(jsonPath("$.data.entries[0].times[0].endTime").value("10:15:00"))
			.andExpect(jsonPath("$.data.entries[0].times[1].dayOfWeek").value("MON"))
			.andExpect(jsonPath("$.data.entries[0].times[1].startTime").value("13:30:00"))
			.andExpect(jsonPath("$.data.entries[0].times[2].dayOfWeek").value("WED"));
	}

	@Test
	@DisplayName("요청한 사용자에게 담긴 강의만 반환한다")
	void getTimetable_onlyRequestedUser() throws Exception {
		mockMvc.perform(get("/api/v1/timetable").header("X-Member-Id", "1002"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.entries", hasSize(1)))
			.andExpect(jsonPath("$.data.entries[0].entryId").value(1003))
			.andExpect(jsonPath("$.data.entries[0].courseName").value("자료구조"))
			.andExpect(jsonPath("$.data.entries[0].times[0].dayOfWeek").value("FRI"));
	}

	@Test
	@DisplayName("존재하는 사용자에게 담긴 강의가 없으면 빈 배열로 응답한다")
	void getTimetable_empty() throws Exception {
		mockMvc.perform(get("/api/v1/timetable").header("X-Member-Id", "1003"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.entries", hasSize(0)));
	}

	@Test
	@DisplayName("담긴 강의에 수업 시간이 없으면 강의는 유지하고 times를 빈 배열로 응답한다")
	void getTimetable_withoutTimes() throws Exception {
		mockMvc.perform(get("/api/v1/timetable").header("X-Member-Id", "1001"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.entries[1].courseName").value("데이터베이스"))
			.andExpect(jsonPath("$.data.entries[1].times", hasSize(0)));
	}

	@Test
	@DisplayName("존재하지 않는 사용자는 404로 응답한다")
	void getTimetable_userNotFound() throws Exception {
		mockMvc.perform(get("/api/v1/timetable").header("X-Member-Id", "999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("404"))
			.andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("사용자 ID 헤더를 생략하면 기존 공통 400 응답을 반환한다")
	void getTimetable_missingUserId() throws Exception {
		mockMvc.perform(get("/api/v1/timetable"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("쿼리 파라미터만으로는 사용자 ID 헤더를 대신할 수 없다")
	void getTimetable_queryParameterWithoutHeader() throws Exception {
		mockMvc.perform(get("/api/v1/timetable").param("userId", "1001"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "-1", "abc", "1.5", "9223372036854775808"})
	@DisplayName("잘못된 사용자 ID는 내부 정보 없이 기존 공통 400 응답을 반환한다")
	void getTimetable_invalidUserId(String userId) throws Exception {
		mockMvc.perform(get("/api/v1/timetable").header("X-Member-Id", userId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("0".equals(userId) || "-1".equals(userId)
				? "사용자 ID는 1 이상의 숫자 값입니다." : "사용자 ID는 1 이상의 숫자 값입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}
}
