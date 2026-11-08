package org.sopt.everytime.global.response;

import org.sopt.everytime.global.code.ErrorCode;
import org.sopt.everytime.global.code.SuccessCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public record BaseResponse<T>(
	boolean success,
	String code,
	String message,
	T data
) {

	public static <T> ResponseEntity<BaseResponse<T>> success(SuccessCode successCode, T data) {
		BaseResponse<T> body = new BaseResponse<>(true, toCode(successCode.getHttpStatus()),
			successCode.getMessage(), data);
		return ResponseEntity.status(successCode.getHttpStatus()).body(body);
	}

	public static ResponseEntity<BaseResponse<Void>> success(SuccessCode successCode) {
		return success(successCode, null);
	}

	public static <T> ResponseEntity<BaseResponse<T>> fail(ErrorCode errorCode, T data) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(failBody(errorCode.getHttpStatus(), errorCode.getMessage(), data));
	}

	public static ResponseEntity<BaseResponse<Void>> fail(ErrorCode errorCode) {
		return fail(errorCode, null);
	}

	public static <T> BaseResponse<T> failBody(HttpStatusCode statusCode, String message, T data) {
		return new BaseResponse<>(false, toCode(statusCode), message, data);
	}

	private static String toCode(HttpStatusCode statusCode) {
		return String.valueOf(statusCode.value());
	}
}
