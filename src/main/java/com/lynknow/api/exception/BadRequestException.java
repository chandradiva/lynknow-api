package com.lynknow.api.exception;

import org.springframework.http.HttpStatus;

/* 
* response status code indicates that the server cannot or will not process 
the request due to something that is perceived to be a client error 
(e.g., malformed request syntax, invalid request message framing, or deceptive request routing).
* 
*/
public class BadRequestException extends BaseException {
	
	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		this.setCode(400);
		this.setMessage("Bad Request");
		this.setStatus(HttpStatus.BAD_REQUEST);
	}

	public BadRequestException(String message) {
		this.setCode(400);
		this.setMessage(message);
		this.setStatus(HttpStatus.BAD_REQUEST);
	}

	public BadRequestException(String message, Integer code) {
		this.setCode(code);
		this.setMessage(message);
		this.setStatus(HttpStatus.BAD_REQUEST);
	}
}