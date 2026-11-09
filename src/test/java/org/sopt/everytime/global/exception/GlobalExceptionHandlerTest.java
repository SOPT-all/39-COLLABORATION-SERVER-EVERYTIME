package org.sopt.everytime.global.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.everytime.global.code.ErrorCode;
import org.sopt.everytime.global.code.SuccessCode;
import org.sopt.everytime.global.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@Import(GlobalExceptionHandlerTest.TestController.class)
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("성공 응답은 SuccessCode의 상태와 메시지, data를 담는다")
	void success_withData() throws Exception {
		mockMvc.perform(get("/test/success"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("테스트 조회 성공"))
			.andExpect(jsonPath("$.data.name").value("everytime"));
	}

	@Test
	@DisplayName("반환값이 없는 성공 응답은 data가 null이다")
	void success_withoutData() throws Exception {
		mockMvc.perform(post("/test/created"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.message").value("테스트 생성 성공"))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("BusinessException은 ErrorCode의 상태와 메시지로 응답한다")
	void businessException() throws Exception {
		mockMvc.perform(get("/test/business"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("404"))
			.andExpect(jsonPath("$.message").value("존재하지 않는 테스트입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("요청 값 검증 실패는 400과 필드별 메시지로 응답한다")
	void methodArgumentNotValid() throws Exception {
		mockMvc.perform(post("/test/valid")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"\", \"nickname\": \"toolongnickname\"}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.data.name").value("이름은 비어있을 수 없습니다."))
			.andExpect(jsonPath("$.data.nickname").value("닉네임은 10자 이하여야 합니다."));
	}

	@Test
	@DisplayName("잘못된 JSON 형식은 400과 공통 메시지로 응답한다")
	void malformedJson() throws Exception {
		mockMvc.perform(post("/test/valid")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{invalid"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("지원하지 않는 HTTP 메서드는 405와 Allow 헤더로 응답한다")
	void methodNotAllowed() throws Exception {
		mockMvc.perform(delete("/test/success"))
			.andExpect(status().isMethodNotAllowed())
			.andExpect(header().exists(HttpHeaders.ALLOW))
			.andExpect(jsonPath("$.code").value("405"))
			.andExpect(jsonPath("$.message").value("지원하지 않는 HTTP 메서드입니다."));
	}

	@Test
	@DisplayName("존재하지 않는 경로는 404와 공통 메시지로 응답한다")
	void noResourceFound() throws Exception {
		mockMvc.perform(get("/not-exist"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("404"))
			.andExpect(jsonPath("$.message").value("요청한 리소스를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("처리하지 않은 예외는 500과 내부 정보 없는 메시지로 응답한다")
	void unexpectedException() throws Exception {
		mockMvc.perform(get("/test/error"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("500"))
			.andExpect(jsonPath("$.message").value("서버 내부 오류입니다."))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("사용자 ID 외의 필수 헤더 누락도 필드별 오류로 응답한다")
	void missingHeader() throws Exception {
		mockMvc.perform(get("/test/header"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.data['X-Test-Count']").value("X-Test-Count 헤더는 필수입니다."));
	}

	@Test
	@DisplayName("숫자 헤더의 형식 오류에 내부 타입명 대신 안내를 반환한다")
	void invalidHeaderType() throws Exception {
		mockMvc.perform(get("/test/header").header("X-Test-Count", "abc"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.data['X-Test-Count']").value("X-Test-Count 헤더는 숫자여야 합니다."));
	}

	@Test
	@DisplayName("name으로 선언한 헤더도 제약 조건의 안내 문구를 반환한다")
	void invalidHeaderValue() throws Exception {
		mockMvc.perform(get("/test/header").header("X-Test-Count", "0"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.data['X-Test-Count']").value("개수는 1 이상이어야 합니다."));
	}

	@RestController
	static class TestController {

		@GetMapping("/test/header")
		ResponseEntity<BaseResponse<TestResponse>> header(
			@RequestHeader(name = "X-Test-Count") @Min(value = 1, message = "개수는 1 이상이어야 합니다.") Long count) {
			return BaseResponse.success(TestSuccessCode.TEST_FETCHED, new TestResponse(count.toString()));
		}

		@GetMapping("/test/success")
		ResponseEntity<BaseResponse<TestResponse>> success() {
			return BaseResponse.success(TestSuccessCode.TEST_FETCHED, new TestResponse("everytime"));
		}

		@PostMapping("/test/created")
		ResponseEntity<BaseResponse<Void>> created() {
			return BaseResponse.success(TestSuccessCode.TEST_CREATED);
		}

		@GetMapping("/test/business")
		ResponseEntity<BaseResponse<Void>> business() {
			throw new BusinessException(TestErrorCode.TEST_NOT_FOUND);
		}

		@PostMapping("/test/valid")
		ResponseEntity<BaseResponse<Void>> valid(@Valid @RequestBody TestRequest request) {
			return BaseResponse.success(TestSuccessCode.TEST_CREATED);
		}

		@GetMapping("/test/error")
		ResponseEntity<BaseResponse<Void>> error() {
			throw new IllegalStateException("internal detail");
		}
	}

	record TestRequest(
		@NotBlank(message = "이름은 비어있을 수 없습니다.")
		String name,

		@Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
		String nickname
	) {
	}

	record TestResponse(String name) {
	}

	@Getter
	@RequiredArgsConstructor
	enum TestSuccessCode implements SuccessCode {

		TEST_FETCHED(HttpStatus.OK, "테스트 조회 성공"),
		TEST_CREATED(HttpStatus.CREATED, "테스트 생성 성공");

		private final HttpStatus httpStatus;
		private final String message;
	}

	@Getter
	@RequiredArgsConstructor
	enum TestErrorCode implements ErrorCode {

		TEST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 테스트입니다.");

		private final HttpStatus httpStatus;
		private final String message;
	}
}
