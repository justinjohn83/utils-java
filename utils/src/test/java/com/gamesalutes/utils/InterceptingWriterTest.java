package com.gamesalutes.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InterceptingWriterTest {

	private StringWriter storeWriter;
	private StringWriter interceptorWriter;
	private InterceptingWriter.CompletionListener listener;
	
	private InterceptingWriter writer;
	
	private boolean success;
	
	@Before
	public void before() {
		storeWriter = new StringWriter(4096);
		interceptorWriter = new StringWriter(4096);
		listener = new InterceptingWriter.CompletionListener() {

			private boolean wasFlushed = false;
			
			public void onFlush(Writer interceptor) {
				wasFlushed = true;
			}
			public void onClose(Writer interceptor) {
				// do assertion test that contents equal
				Assert.assertSame(interceptorWriter, interceptor);
				
				// compare
				Assert.assertEquals(storeWriter.toString(), interceptorWriter.toString());
				
				Assert.assertTrue(wasFlushed);
				
				success = true;
			}
			
		};
		
		writer = new InterceptingWriter(storeWriter,interceptorWriter,listener);
	}
	
	@After
	public void after() {
		storeWriter = interceptorWriter = null;
		listener = null;
		writer = null;
	}
	
	@Test
	public void testAppend() throws IOException {
		writer.append("Testing good stuff\n");
		writer.append("Testing more stuf");
		writer.append('f');
		
		writer.flush();
		// will invoke test assertions
		writer.close();
		Assert.assertTrue(success);
		
	}
	
	@Test
	public void testWrite() throws IOException {
		writer.write("Testing good stuff\n");
		writer.write("Testing more stuf");
		writer.write('f');
		
		writer.flush();
		// will invoke test assertions
		writer.close();
		Assert.assertTrue(success);
		
	}
	
	
}
