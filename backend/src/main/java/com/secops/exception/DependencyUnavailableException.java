package com.secops.exception;

public class DependencyUnavailableException extends RuntimeException {

	private static final long serialVersionUID = -8412279072904242166L;

	public DependencyUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public DependencyUnavailableException(String message) {
		super(message);
	}
	
}