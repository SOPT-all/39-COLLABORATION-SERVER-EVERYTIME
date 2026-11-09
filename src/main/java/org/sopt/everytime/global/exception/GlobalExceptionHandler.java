package org.sopt.everytime.global.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.sopt.everytime.global.code.ErrorCode;
import org.sopt.everytime.global.code.GlobalErrorCode;
import org.sopt.everytime.global.response.BaseResponse;
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
		if (exception instanceof MissingRequestHeaderException headerException) {
			String headerName = headerException.getHeaderName();
			return invalidHeaders(exception, headers, Map.of(headerName, headerLabel(headerName) + "는 필수입니다."));
		}
		return super.handleServletRequestBindingException(exception, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exception, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {
		if (exception instanceof MethodArgumentTypeMismatchException argumentException
			&& argumentException.getParameter().hasParameterAnnotation(RequestHeader.class)) {
			String headerName = argumentException.getName();
			Class<?> requiredType = argumentException.getRequiredType();
			String message = requiredType != null && Number.class.isAssignableFrom(requiredType)
				? headerLabel(headerName) + "는 숫자여야 합니다."
				: "요청 헤더 값의 형식이 올바르지 않습니다.";
			return invalidHeaders(exception, headers, Map.of(headerName, message));
		}
		return super.handleTypeMismatch(exception, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException exception,
		HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (!exception.isForReturnValue()) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			for (var result : exception.getParameterValidationResults()) {
				RequestHeader header = result.getMethodParameter().getParameterAnnotation(RequestHeader.class);
				if (header != null) {
					String headerName = header.name().isBlank() ? header.value() : header.name();
					if (headerName.isBlank()) {
						headerName = result.getMethodParameter().getParameterName();
					}
					for (var error : result.getResolvableErrors()) {
						fieldErrors.putIfAbsent(headerName, error.getDefaultMessage() == null
							? GlobalErrorCode.INVALID_INPUT_VALUE.getMessage() : error.getDefaultMessage());
					}
				}
			}
			if (!fieldErrors.isEmpty()) {
				return invalidHeaders(exception, headers, fieldErrors);
			}
		}
		return super.handleHandlerMethodValidationException(exception, headers, statusCode, request);
	}

	private String headerLabel(String headerName) {
		return "X-Member-Id".equalsIgnoreCase(headerName) ? "사용자 ID" : headerName + " 헤더";
	}

	private ResponseEntity<Object> invalidHeaders(Exception exception, HttpHeaders headers,
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
