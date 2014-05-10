package com.gamesalutes.httpconnection;

import java.net.URI;

/**
 * Listener for http exceptions.
 * 
 * @author jmontgomery
 *
 */
public interface HttpBadStatusInterceptor {

	/**
	 * Invoked when a non ok status code is returned.
	 * 
	 * @param uri the <code>uri</code> invoked
	 * @param e the http exception
	 */
	void onHttpException(URI uri,HttpBadStatusException e);
}
