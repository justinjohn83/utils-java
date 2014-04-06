package com.gamesalutes.httpconnection;

import java.net.URI;



public class HttpConnectionAndroidTest extends HttpSupportTest{

	@Override
	protected HttpSupport createConnection(String url, int numRetries,
			int timeout) {
		if(url == null) {
			return new HttpConnectionAndroid(numRetries,timeout);
		}
		else {
			return new HttpConnectionAndroid(url,numRetries,timeout);
		}
	}

	@Override
	protected URI createUri(HttpSupport conn,HttpConnectionRequest<?,?> request) {
		return ((HttpConnectionAndroid)conn).createUri(request.getPath(), request.getQueryParameters());
	}
}
