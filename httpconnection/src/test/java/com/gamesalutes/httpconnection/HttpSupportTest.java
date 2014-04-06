package com.gamesalutes.httpconnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.LoggingUtils;

public abstract class HttpSupportTest {

	private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
	
	@BeforeClass
	public static void beforeClass(){
		LoggingUtils.initializeLogging("log4j.properties");
		
	}
	
	@Test
	public void testNonGzippedConnectionDeprecatedGet() throws Exception {
		HttpSupport conn = createConnection("http://www.google.com");
		HttpResponse response = conn.get("/", null, null);
		
		assertNotNull(response);
		assertNotNull(response.getContent());
		
		//logger.info("Total Bytes=" + conn.getTotalBytes());

	}
	
	@Test
	public void testNonGzippedConnectionGet() throws Exception {
		HttpSupport conn = createConnection("http://www.google.com");
		String response = conn.get( 
				new RequestBuilder<Void,String>().setUnmarshaller(new StringResponseUnmarshaller()).build());
		
		assertNotNull(response);
		
		//logger.info("Total Bytes=" + conn.getTotalBytes());

	}
	
	
	protected abstract URI createUri(HttpSupport conn,HttpConnectionRequest<?,?> request);
	
	@Test
	public void createUriTest() throws Exception {
		
		HttpSupport conn = createConnection("http://www.google.com");
		
		URI expected = new URI("http://www.google.com:80/?q=bestbuy");
		expected = new URI("http://www.google.com/?q=bestbuy");
		
		HttpConnectionRequest<Void,String> request = new RequestBuilder<Void,String>(
				).setUnmarshaller(new StringResponseUnmarshaller()
				).addQueryParameter("q", "bestbuy").build();
		assertEquals("bestbuy",request.getQueryParameters().get("q"));
		
		request = new RequestBuilder<Void,String>(
				).setUnmarshaller(new StringResponseUnmarshaller()
				).setPath("http://www.google.com"
				).addQueryParameter("q", "bestbuy").build();
		assertEquals("bestbuy",request.getQueryParameters().get("q"));
		
		expected = new URI("http://www.google.com/?q=bestbuy");
		request = new RequestBuilder<Void,String>(
				).setUnmarshaller(new StringResponseUnmarshaller()
				).setPath("www.google.com"
				).addQueryParameter("q", "bestbuy").build();
		assertEquals("bestbuy",request.getQueryParameters().get("q"));
		
		expected = new URI("http://www.google.com:80/?q=bestbuy");
		request = new RequestBuilder<Void,String>(
				).setUnmarshaller(new StringResponseUnmarshaller()
				).setPath("localhost:8080"
				).addQueryParameter("q", "bestbuy").build();
		assertEquals("bestbuy",request.getQueryParameters().get("q"));
		
		
		
		assertEquals(expected,createUri(conn,request));
	}
	@Test
	public void testNonGzippedConnectionGetWithParameters() throws Exception {
		//https://www.google.com/#q=bestbuy
		HttpSupport conn = createConnection("http://www.google.com");
		
		URI expected = new URI("http://www.google.com:80/?q=bestbuy");
		HttpConnectionRequest<Void,String> request = new RequestBuilder<Void,String>(
				).setUnmarshaller(new StringResponseUnmarshaller()
				).addQueryParameter("q", "bestbuy").build();
		assertEquals("bestbuy",request.getQueryParameters().get("q"));
		
		String response = conn.get(request);
		
		assertNotNull(response);
		
		//logger.info("Total Bytes=" + conn.getTotalBytes());

	}
	
	@Test
	public void testNoBaseUrl() throws Exception{
		HttpSupport conn = createConnection(null);
		
		String response = conn.get(
				new RequestBuilder<Void,String>().setPath("http://www.google.com").setUnmarshaller(new StringResponseUnmarshaller()).build());
		
		assertNotNull(response);
		
		// test second url
		response = conn.get(
						new RequestBuilder<Void,String>().setPath("http://www.bestbuy.com").setUnmarshaller(new StringResponseUnmarshaller()).build());
						
		assertNotNull(response);
	}
	
	// FIXME: http://www.facebook.com throws an SSL peer not authenticated!
	@Test
	public void testGzippedConnection() throws Exception {
		
		// bestbuy supports gzipped responses:
		// http://www.whatsmyip.org/http-compression-test/
		HttpSupport conn = createConnection("http://www.bestbuy.com");
		
		String response = conn.get(
				new RequestBuilder<Void,String>().setPath("/").setUnmarshaller(new StringResponseUnmarshaller()).build());
		
		
		assertNotNull(response);
		
		//logger.info("Total Bytes=" + conn.getTotalBytes());
		
	}
	
	private HttpSupport createConnection(String url) {
		return createConnection(url,3,5000);
	}
	
	protected abstract HttpSupport createConnection(String url,int numRetries,int timeout);
	
	@Test
	public void testMultithreaded() throws Exception {
		
		final int COUNT = 100;
		
		// create a bunch of resources locking and unlocking
		final CountDownLatch latch = new CountDownLatch(COUNT);
		final HttpSupport conn = createConnection("http://www.bestbuy.com");
		
		ExecutorService exec = Executors.newFixedThreadPool(COUNT);
		
		for(int i = 1; i<= COUNT; ++i) {
			
			final int count = i;
			
			exec.submit(new Callable<Void>() {
	
				
				public Void call() throws Exception {
					
					logger.info("Run: " + count + " getting response...");
					
					try {
					
						String response = conn.get(
										new RequestBuilder<Void,String>().setUnmarshaller(new StringResponseUnmarshaller()).build());
										
						
						assertNotNull(response);
						// create the lock
						//fileLock.lock();
						
						logger.info("Run: " + count + " complete.");
					}
					finally {

						latch.countDown();
					}
					
					return null;
				}
			}); 
			 
			
		} // for
		
		// wait for all connections to complete
		latch.await();
		
		//logger.info("Total Bytes=" + conn.getTotalBytes());

	}
}
