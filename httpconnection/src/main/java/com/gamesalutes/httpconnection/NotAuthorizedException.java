package com.gamesalutes.httpconnection;

public class NotAuthorizedException extends ClientHttpException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int STATUS_CODE = 403;

	public NotAuthorizedException() {
		super(STATUS_CODE);
		// TODO Auto-generated constructor stub
	}

	public NotAuthorizedException(String message) {
		super(STATUS_CODE, message);
		// TODO Auto-generated constructor stub
	}

	public NotAuthorizedException(Throwable rootCause) {
		super(STATUS_CODE, rootCause);
		// TODO Auto-generated constructor stub
	}

	public NotAuthorizedException(String message,
			Throwable rootCause) {
		super(STATUS_CODE, message, rootCause);
		// TODO Auto-generated constructor stub
	}

}
