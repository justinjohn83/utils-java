package com.gamesalutes.httpconnection;

public class MethodNotSupportedException extends ClientHttpException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int STATUS_CODE = 405;
	public MethodNotSupportedException(String message,
			Throwable rootCause) {
		super(STATUS_CODE, message, rootCause);
		// TODO Auto-generated constructor stub
	}

	public MethodNotSupportedException(String message) {
		super(STATUS_CODE, message);
		// TODO Auto-generated constructor stub
	}

	public MethodNotSupportedException(Throwable rootCause) {
		super(STATUS_CODE, rootCause);
		// TODO Auto-generated constructor stub
	}

	public MethodNotSupportedException() {
		super(STATUS_CODE);
		// TODO Auto-generated constructor stub
	}
	

}
