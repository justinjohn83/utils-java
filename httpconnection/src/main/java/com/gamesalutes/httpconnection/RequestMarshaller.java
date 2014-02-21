package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestMarshaller<T> {

	void marshall(T object,OutputStream out) throws IOException;
	String getContentType();
}
