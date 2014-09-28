package com.gamesalutes.utils;

import java.io.IOException;
import java.io.Writer;

/**
 * Writes to multiple targets so that the stream can be intercepted.
 * 
 * @author jmontgomery
 *
 */
public class InterceptingWriter extends Writer {

	/**
	 * Listener for completion of output.  The interceptor stream is not closed as a result.
	 * 
	 * @author jmontgomery
	 *
	 */
	public interface CompletionListener {
		public void onClose(Writer interceptor);
		public void onFlush(Writer interceptor);
	}
	
	/**
	 * Helper implementation that closes interceptor stream onClose and does nothing on close
	 * @author jmontgomery
	 *
	 */
	public static class DefaultCompletionListener implements CompletionListener {
		
		public void onClose(Writer interceptor) {
			MiscUtils.closeStream(interceptor);
		}
		
		public void onFlush(Writer interceptor) {
			//
		}
		
	}
	
	private final Writer store;
	private final Writer interceptor;
	private final CompletionListener listener;

	public InterceptingWriter(Writer store,Writer interceptor,CompletionListener listener) {
		if(store == null) {
			throw new NullPointerException("store");
		}
		if(interceptor == null) {
			throw new NullPointerException("interceptor");
		}
		if(listener == null) {
			throw new NullPointerException("listener");
		}
		
		this.store = store;
		this.interceptor = interceptor;
		this.listener = listener;
	}
	@Override
	public Writer append(char c) throws IOException {
		store.append(c);
		interceptor.append(c);
		
		return this;
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) 
			throws IOException {
		store.append(csq,start,end);
		interceptor.append(csq,start,end);
		
		return this;
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {
		store.append(csq);
		interceptor.append(csq);
		
		return this;
	}

	@Override
	public void close() throws IOException {
		try {
			store.close();
		}
		finally {
			// we do not close the underlying inteceptor stream
			this.listener.onClose(interceptor);
		}
	}

	@Override
	public void flush() throws IOException {
		store.flush();		
		interceptor.flush();
		
		listener.onFlush(interceptor);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		store.write(cbuf,off,len);
		interceptor.write(cbuf,off,len);
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		store.write(cbuf);
		interceptor.write(cbuf);
	}

	@Override
	public void write(int c) throws IOException {
		store.write(c);
		interceptor.write(c);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		store.write(str,off,len);
		interceptor.write(str,off,len);
	}

	@Override
	public void write(String str) throws IOException {
		store.write(str);
		interceptor.write(str);
	}

}
