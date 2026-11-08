package org.sopt.everytime.global.code;

import org.springframework.http.HttpStatus;

public interface SuccessCode {

	HttpStatus getHttpStatus();

	String getMessage();
}
