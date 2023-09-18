package com.tencoding.bank.handler.exception;

import org.springframework.http.HttpStatus;

// 얘랑 CustomRestfullException이  예외처리 만들어둔거임
public class CustomPageException extends RuntimeException{
	
	private HttpStatus status;
	
	public CustomPageException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
}
