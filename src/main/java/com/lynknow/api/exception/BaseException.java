package com.lynknow.api.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseException extends RuntimeException { 

	private static final long serialVersionUID = 1L;

	private Integer code = 500;
	private String message = "";
	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
	private Object data = null;

	public BaseException() {
		super();
	}

}