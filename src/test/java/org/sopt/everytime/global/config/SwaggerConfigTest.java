package org.sopt.everytime.global.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "server.forward-headers-strategy=framework")
@AutoConfigureMockMvc
class SwaggerConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("OpenAPI 문서에 API 기본 정보가 포함된다")
	void apiDocs() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.info.title").value("Everytime API"))
			.andExpect(jsonPath("$.info.version").value("v1"));
	}

	@Test
	@DisplayName("리버스 프록시의 X-Forwarded 헤더로 서버 URL을 만든다")
	void apiDocs_behindProxy() throws Exception {
		mockMvc.perform(get("/v3/api-docs")
				.header("X-Forwarded-Proto", "https")
				.header("X-Forwarded-Host", "api.everytime.example"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.servers[0].url").value("https://api.everytime.example"));
	}

	@Test
	@DisplayName("Swagger UI 페이지에 접근할 수 있다")
	void swaggerUi() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html"))
			.andExpect(status().isOk());
	}
}
