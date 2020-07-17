package com.lynknow.api.exception;

import org.springframework.http.HttpStatus;

/* 
* The 415 (Unsupported Media Type) status code indicates that the
* origin server is refusing to service the request because the payload
* is in a format not supported by this method on the target resource.
* The format problem might be due to the request's indicated
* Content-Type or Content-Encoding, or as a result of inspecting the
* data directly.
*/
public class UnsupportedMediaTypeException extends BaseException {
	
	private static final long serialVersionUID = 1L;

	public UnsupportedMediaTypeException() {
		this.setCode(415);
		this.setMessage("Unsupported Media Type");
		this.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	public UnsupportedMediaTypeException(String message) {
		this.setCode(415);
		this.setMessage(message);
		this.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	public UnsupportedMediaTypeException(String message, Integer code) {
		this.setCode(code);
		this.setMessage(message);
		this.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
}