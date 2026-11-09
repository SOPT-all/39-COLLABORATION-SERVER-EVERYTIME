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
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RequestHeader;
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
			return GlobalErrorCode.INVALID_INPUT_VALUE.getMessage();
		}
		return fieldError.getDefaultMessage();
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (exception instanceof MissingRequestHeaderException headerException
			&& "X-Member-Id".equalsIgnoreCase(headerException.getHeaderName())) {
			return invalidMemberHeader(exception, headers, "사용자 ID는 필수입니다.");
		}
		return super.handleServletRequestBindingException(exception, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exception, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {
		if (exception instanceof MethodArgumentTypeMismatchException argumentException) {
			RequestHeader header = argumentException.getParameter().getParameterAnnotation(RequestHeader.class);
			if (header != null && "X-Member-Id".equalsIgnoreCase(header.value())) {
				return invalidMemberHeader(exception, headers, TimetableErrorCode.INVALID_USER_ID.getMessage());
			}
		}
		return super.handleTypeMismatch(exception, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (!exception.isForReturnValue()) {
			for (var result : exception.getParameterValidationResults()) {
				RequestHeader header = result.getMethodParameter().getParameterAnnotation(RequestHeader.class);
				if (header != null && "X-Member-Id".equalsIgnoreCase(header.value())) {
					return invalidMemberHeader(exception, headers, result.getResolvableErrors().getFirst().getDefaultMessage());
				}
			}
		}
		return super.handleHandlerMethodValidationException(exception, headers, statusCode, request);
	}

	private ResponseEntity<Object> invalidMemberHeader(Exception exception, HttpHeaders headers, String message) {
		ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
		logException(errorCode.getHttpStatus(), exception);
		return ResponseEntity.status(errorCode.getHttpStatus()).headers(headers)
			.body(BaseResponse.failBody(errorCode.getHttpStatus(), errorCode.getMessage(), Map.of("X-Member-Id", message)));
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
