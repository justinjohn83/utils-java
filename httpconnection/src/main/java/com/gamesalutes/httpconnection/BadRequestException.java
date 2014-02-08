package com.gamesalutes.httpconnection;

public class BadRequestException extends ClientHttpException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int STATUS_CODE = 400;
	
	public BadRequestException(String message,
			Throwable rootCause) {
		super(STATUS_CODE, message, rootCause);
		// TODO Auto-generated constructor stub
	}

	public BadRequestException(String message) {
		super(STATUS_CODE, message);
		// TODO Auto-generated constructor stub
	}

	public BadRequestException(Throwable rootCause) {
		super(STATUS_CODE, rootCause);
		// TODO Auto-generated constructor stub
	}

	public BadRequestException() {
		super(STATUS_CODE);
	}


}
