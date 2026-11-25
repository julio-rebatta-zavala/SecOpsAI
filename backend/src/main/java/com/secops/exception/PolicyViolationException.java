package com.secops.exception;

public class PolicyViolationException extends RuntimeException {

	private static final long serialVersionUID = -2352233899866782628L;

	private final String code;

	public PolicyViolationException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String code() {
		return code;
	}
	
}