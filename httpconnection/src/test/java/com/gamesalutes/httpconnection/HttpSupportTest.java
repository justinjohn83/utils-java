package com.gamesalutes.httpconnection;

import static org.junit.Assert.assertNotNull;

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
	public void testNonGzippedConnection() throws Exception {
		HttpSupport conn = createConnection("http://www.google.com");
		HttpResponse response = conn.get("/", null, null);
		
		assertNotNull(response);
		assertNotNull(response.getContent());
		
		//logger.info("Total Bytes=" + conn.getTotalBytes());

	}
	
	@Test
	public void testNoBaseUrl() throws Exception{
		HttpSupport conn = createConnection(null);
		
		HttpResponse response = conn.get("http://www.google.com", null, null);
		
		assertNotNull(response);
		assertNotNull(response.getContent());
		
		// test second url
		response = conn.get("http://www.bestbuy.com", null, null);
		
		assertNotNull(response);
		assertNotNull(response.getContent());
	}
	
	// FIXME: http://www.facebook.com throws an SSL peer not authenticated!
	@Test
	public void testGzippedConnection() throws Exception {
		
		// bestbuy supports gzipped responses:
		// http://www.whatsmyip.org/http-compression-test/
		HttpSupport conn = createConnection("http://www.bestbuy.com");
		HttpResponse response = conn.get("/", null, null);
		
		assertNotNull(response);
		assertNotNull(response.getContent());
		
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
					
						HttpResponse response = conn.get("/", null, null);
						
						assertNotNull(response);
						assertNotNull(response.getContent());
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
