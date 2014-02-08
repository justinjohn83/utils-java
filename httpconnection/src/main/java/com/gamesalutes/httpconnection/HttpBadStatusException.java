package com.gamesalutes.httpconnection;

public class HttpBadStatusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int statusCode;

	public HttpBadStatusException(int statusCode) {
		this.statusCode = statusCode;
	}

	public HttpBadStatusException(int statusCode,String message) {
		super(message);
		this.statusCode = statusCode;
	}

	public HttpBadStatusException(int statusCode,Throwable rootCause) {
		super(rootCause);
		this.statusCode = statusCode;
	}

	public HttpBadStatusException(int statusCode,String message, Throwable rootCause) {
		super(message, rootCause);
		this.statusCode = statusCode;
	}
	
	public final int getStatusCode() {
		return statusCode;
	}

}
