package com.gamesalutes.httpconnection;

import org.junit.Ignore;
import org.junit.Test;


public class HttpConnectionAndroidTest extends HttpSupportTest{

	@Override
	protected HttpSupport createConnection(String url, int numRetries,
			int timeout) {
		return new HttpConnectionAndroid(url,numRetries,timeout);
	}

	// TODO: Implement no base url for android
	// comment out the override once this is done
	@Override
	@Ignore
	@Test
	public void testNoBaseUrl() {
		// ignore test for now
	}
}
