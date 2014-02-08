package com.gamesalutes.httpconnection;

public class ConflictException extends HttpBadStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConflictException() {
		super(409);
		// TODO Auto-generated constructor stub
	}

	public ConflictException(String message) {
		super(409, message);
		// TODO Auto-generated constructor stub
	}

	public ConflictException(Throwable rootCause) {
		super(409, rootCause);
		// TODO Auto-generated constructor stub
	}

	public ConflictException(String message, Throwable rootCause) {
		super(409, message, rootCause);
		// TODO Auto-generated constructor stub
	}

}
