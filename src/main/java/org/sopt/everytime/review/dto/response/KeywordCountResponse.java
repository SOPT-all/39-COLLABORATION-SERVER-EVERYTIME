package org.sopt.everytime.review.dto.response;

import org.sopt.everytime.review.entity.Keyword;

import io.swagger.v3.oas.annotations.media.Schema;

public record KeywordCountResponse(
	@Schema(description = "키워드 ID", example = "1")
	Long keywordId,

	@Schema(description = "칩 이름", example = "교수님")
	String name,

	@Schema(description = "분반 전체에서 해당 키워드가 달린 강의평 수", example = "1")
	long count
) {

	public static KeywordCountResponse of(Keyword keyword, long count) {
		return new KeywordCountResponse(keyword.getId(), keyword.getName(), count);
	}
}
