/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.InputStream;

import com.gamesalutes.utils.FileUtils;

/**
 *
 * @author jmontgomery
 */
public final class HttpResponse
{
    private final int code;
    private final String status;
    private String content;
    private InputStream stream;

    HttpResponse(int code,String status,InputStream stream)
    {
        this.code = code;
        this.status = status;
        this.stream = stream;
    }

    public int getCode() { return code; }
    public String getStatus() { return status; }
    
    /**
     * Consumes the <code>InputStream</code> by reading it as text.
     * 
     * @return the stream content
     * @throws IOException if error occurs reading the stream
     */
    public synchronized String getContent() throws IOException { 
    	if(content == null) {
    		content = FileUtils.readData(stream);
    	}
    	return content;
    }
    
    /**
     * Returns the stream for the response data.  If {@link getContent getContent} is called,
     * then an <code>IllegalStateException</code> is thrown.
     * 
     * 
     * @return the <code>InputStream</code>
     * @throws IllegalStateException if <code>getContent</code> has been called
     */
    public synchronized InputStream getInputStream() { 
    	if(stream == null) {
    		throw new IllegalStateException("stream closed: getContent() called");
    	}
    	return stream;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HttpResponse [code=");
		builder.append(code);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
}
