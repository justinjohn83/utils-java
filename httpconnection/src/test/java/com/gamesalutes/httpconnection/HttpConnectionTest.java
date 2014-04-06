package com.gamesalutes.httpconnection;

import java.net.URI;

public class HttpConnectionTest extends HttpSupportTest {

	@Override
	protected HttpSupport createConnection(String url, int numRetries,
			int timeout) {
		if(url == null) {
			return new HttpConnection(numRetries,timeout);
		}
		else {
			return new HttpConnection(url,numRetries,timeout);
		}
	}

	@Override
	protected URI createUri(HttpSupport conn,HttpConnectionRequest<?,?> request) {
		return ((HttpConnection)conn).createUri(request.getPath(), request.getQueryParameters());
	}

}
