package com.gamesalutes.httpconnection;

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

}
