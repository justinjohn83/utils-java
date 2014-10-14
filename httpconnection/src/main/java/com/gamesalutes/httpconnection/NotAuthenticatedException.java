package com.gamesalutes.httpconnection;

public class NotAuthenticatedException extends ClientHttpException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int STATUS_CODE = 401;
	
	public NotAuthenticatedException() {
		super(STATUS_CODE);
	}

	public NotAuthenticatedException(String message) {
		super(STATUS_CODE, message);
	}

	public NotAuthenticatedException(Throwable rootCause) {
		super(STATUS_CODE, rootCause);
	}

	public NotAuthenticatedException(String message,
			Throwable rootCause) {
		super(STATUS_CODE, message, rootCause);
	}

}
