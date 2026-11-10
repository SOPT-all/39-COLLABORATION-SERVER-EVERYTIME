package org.sopt.everytime.global.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.sopt.everytime.global.code.ErrorCode;
import org.sopt.everytime.global.code.GlobalErrorCode;
import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.lecture.code.LectureErrorCode;
import org.sopt.everytime.timetable.code.TimetableErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		logException(errorCode.getHttpStatus(), exception);
		return BaseResponse.fail(errorCode);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse<Void>> handleException(Exception exception) {
		ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
		logException(errorCode.getHttpStatus(), exception);
		return BaseResponse.fail(errorCode);
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (exception instanceof org.springframework.web.bind.MissingRequestHeaderException missingHeader
			&& "X-Member-Id".equals(missingHeader.getHeaderName())) {
			return handleClientError(GlobalErrorCode.USER_ID_REQUIRED, headers, request, exception);
		}
		return handleExceptionInternal(exception, null, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(org.springframework.beans.TypeMismatchException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (isCategoryIdTypeMismatch(exception)) {
			return handleClientError(LectureErrorCode.CATEGORY_ID_INVALID, headers, request, exception);
		}
		if (isUserIdTypeMismatch(exception)) {
			return handleClientError(GlobalErrorCode.USER_ID_INVALID, headers, request, exception);
		}
		return handleExceptionInternal(exception, null, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (request.getDescription(false).contains("/api/v1/timetable/entries")) {
			return handleClientError(TimetableErrorCode.SECTION_ID_INVALID, headers, request, exception);
		}
		return handleExceptionInternal(exception, null, headers, statusCode, request);
	}

	private ResponseEntity<Object> handleClientError(ErrorCode errorCode, HttpHeaders headers, WebRequest request,
		Exception exception) {
		logException(errorCode.getHttpStatus(), exception);
		return ResponseEntity.status(errorCode.getHttpStatus())
			.headers(headers)
			.body(BaseResponse.failBody(errorCode.getHttpStatus(), errorCode.getMessage(), null));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult()
			.getFieldErrors()
			.forEach(fieldError -> fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));

		logException(statusCode, exception);
		ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.headers(headers)
			.body(BaseResponse.failBody(errorCode.getHttpStatus(), errorCode.getMessage(), fieldErrors));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {
		if (isCategoryIdTypeMismatch(exception)) {
			return handleClientError(LectureErrorCode.CATEGORY_ID_INVALID, headers, request, exception);
		}
		if (isUserIdTypeMismatch(exception)) {
			return handleClientError(GlobalErrorCode.USER_ID_INVALID, headers, request, exception);
		}
		logException(statusCode, exception);
		ErrorCode errorCode = GlobalErrorCode.from(statusCode);
		return ResponseEntity.status(statusCode)
			.headers(headers)
			.body(BaseResponse.failBody(statusCode, errorCode.getMessage(), null));
	}

	private void logException(HttpStatusCode statusCode, Exception exception) {
		if (statusCode.is5xxServerError()) {
			log.error("[{}] {}", statusCode.value(), exception.getMessage(), exception);
			return;
		}
		log.warn("[{}] {}", statusCode.value(), exception.getMessage());
	}

	private boolean isUserIdTypeMismatch(Exception exception) {
		if (!(exception instanceof MethodArgumentTypeMismatchException mismatch)) {
			return false;
		}
		return "userId".equals(mismatch.getName())
			|| "X-Member-Id".equals(mismatch.getName())
			|| mismatch.getMessage().contains("X-Member-Id");
	}

	private boolean isCategoryIdTypeMismatch(Exception exception) {
		if (!(exception instanceof MethodArgumentTypeMismatchException mismatch)) {
			return false;
		}
		return "categoryId".equals(mismatch.getName())
			|| mismatch.getMessage().contains("categoryId");
	}
}
