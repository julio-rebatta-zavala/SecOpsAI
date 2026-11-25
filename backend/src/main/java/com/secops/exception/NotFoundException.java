package com.secops.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3934513965027477049L;

	public NotFoundException(String message) {
		super(message);
	}

}