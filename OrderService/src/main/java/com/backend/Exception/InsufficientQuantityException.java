package com.backend.Exception;

public class InsufficientQuantityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InsufficientQuantityException(String message) {
		super(message);
	}
}