package com.gamesalutes.httpconnection;

public class ClientHttpException extends HttpBadStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientHttpException(int statusCode) {
		super(statusCode);
		// TODO Auto-generated constructor stub
	}

	public ClientHttpException(int statusCode, String message) {
		super(statusCode, message);
		// TODO Auto-generated constructor stub
	}

	public ClientHttpException(int statusCode, Throwable rootCause) {
		super(statusCode, rootCause);
		// TODO Auto-generated constructor stub
	}

	public ClientHttpException(int statusCode, String message,
			Throwable rootCause) {
		super(statusCode, message, rootCause);
		// TODO Auto-generated constructor stub
	}

}
