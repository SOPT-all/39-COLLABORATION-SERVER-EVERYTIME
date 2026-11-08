package org.sopt.everytime.global.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class PageResponseTest {

	@Test
	@DisplayName("0부터 시작하는 내부 페이지 번호를 1부터 시작하는 번호로 변환한다")
	void from_convertsToOneIndexedPage() {
		PageImpl<String> page = new PageImpl<>(List.of("a", "b"), PageRequest.of(0, 2), 5);

		PageResponse<String> response = PageResponse.from(page);

		assertThat(response.content()).containsExactly("a", "b");
		assertThat(response.page()).isEqualTo(1);
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.totalElements()).isEqualTo(5);
		assertThat(response.totalPages()).isEqualTo(3);
		assertThat(response.hasNext()).isTrue();
	}
}
