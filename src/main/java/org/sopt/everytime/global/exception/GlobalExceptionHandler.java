package org.sopt.everytime.global.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.sopt.everytime.global.code.ErrorCode;
import org.sopt.everytime.global.code.GlobalErrorCode;
import org.sopt.everytime.global.response.BaseResponse;
import org.sopt.everytime.timetable.code.TimetableErrorCode;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
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
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult()
			.getFieldErrors()
			.forEach(fieldError -> fieldErrors.putIfAbsent(fieldError.getField(),
				resolveValidationMessage(fieldError)));

		logException(statusCode, exception);
		ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.headers(headers)
			.body(BaseResponse.failBody(errorCode.getHttpStatus(), errorCode.getMessage(), fieldErrors));
	}

	private String resolveValidationMessage(FieldError fieldError) {
		if (fieldError.isBindingFailure()) {
			if ("typeMismatch".equals(fieldError.getCode())
				&& "timetableRequest".equals(fieldError.getObjectName()) && "userId".equals(fieldError.getField())) {
				return TimetableErrorCode.INVALID_USER_ID.getMessage();
			}
			return GlobalErrorCode.INVALID_INPUT_VALUE.getMessage();
		}
		return fieldError.getDefaultMessage();
	}

	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (exception.isForReturnValue()) {
			return handleExceptionInternal(exception, null, headers, statusCode, request);
		}
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getParameterValidationResults().forEach(result -> {
			String name = result.getMethodParameter().getParameterName();
			result.getResolvableErrors().forEach(error -> fieldErrors.putIfAbsent(name,
				error.getDefaultMessage()));
		});
		return invalidRequest(exception, headers, fieldErrors);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exception, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {
		if (exception instanceof MethodArgumentTypeMismatchException argumentException) {
			String name = argumentException.getName();
			String message = switch (name) {
				case "entryId" -> TimetableErrorCode.INVALID_ENTRY_ID.getMessage();
				case "userId" -> TimetableErrorCode.INVALID_USER_ID.getMessage();
				default -> GlobalErrorCode.INVALID_INPUT_VALUE.getMessage();
			};
			return invalidRequest(exception, headers, Map.of(name, message));
		}
		return handleExceptionInternal(exception, null, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
		MissingServletRequestParameterException exception, HttpHeaders headers, HttpStatusCode statusCode,
		WebRequest request) {
		String name = exception.getParameterName();
		String message = "userId".equals(name) ? "사용자 ID는 필수입니다." : "필수 요청 값이 누락되었습니다.";
		return invalidRequest(exception, headers, Map.of(name, message));
	}

	private ResponseEntity<Object> invalidRequest(Exception exception, HttpHeaders headers,
		Map<String, String> fieldErrors) {
		ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
		logException(errorCode.getHttpStatus(), exception);
		return ResponseEntity.status(errorCode.getHttpStatus()).headers(headers)
			.body(BaseResponse.failBody(errorCode.getHttpStatus(), errorCode.getMessage(), fieldErrors));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {
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
}
