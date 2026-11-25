package com.secops.exception;

public class ConflictException extends RuntimeException {
	
	private static final long serialVersionUID = -1610015072813367175L;

	public ConflictException(String message) {
		super(message);
	}
	
}