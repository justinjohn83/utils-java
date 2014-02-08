package com.gamesalutes.httpconnection;

public class NotFoundException extends ClientHttpException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int STATUS_CODE = 404;
	
	public NotFoundException() {
		super(STATUS_CODE);
	}

	public NotFoundException(String message) {
		super(STATUS_CODE,message);
		// TODO Auto-generated constructor stub
	}

	public NotFoundException(Throwable rootCause) {
		super(STATUS_CODE,rootCause);
		// TODO Auto-generated constructor stub
	}

	public NotFoundException(String message, Throwable rootCause) {
		super(STATUS_CODE,message, rootCause);
		// TODO Auto-generated constructor stub
	}

}
