package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.InputStreamEntity;

import com.gamesalutes.utils.ByteBufferInputStream;
import com.gamesalutes.utils.ByteBufferOutputStream;

/**
 * Repeable non-streaming entity that uses <code>RequestMarshaller</code> to create entity.
 * The stream is created immediately and then the data is cached.
 * 
 * @author jmontgomery
 *
 */
final class MarshallingHttpEntity<S> extends AbstractHttpEntity {

	
	private final ByteBuffer buf;
	
	public MarshallingHttpEntity(RequestMarshaller<S> marshaller,S request) throws IOException {
		buf = ByteBuffer.allocate(8 * 1024);
		ByteBufferOutputStream out = new ByteBufferOutputStream(buf);
		marshaller.marshall(request, out);
		
		// prepare for reading or writing
		buf.flip();
	}
	
	public boolean isRepeatable() {
		return true;
	}

	public long getContentLength() {
		return buf.limit();
	}

	public InputStream getContent() throws IOException, IllegalStateException {

		return new ByteBufferInputStream(buf.duplicate());
	}

	public void writeTo(OutputStream outstream) throws IOException {
		Channels.newChannel(outstream).write(buf.duplicate());
	}

	public boolean isStreaming() {
		return false;
	}

}
