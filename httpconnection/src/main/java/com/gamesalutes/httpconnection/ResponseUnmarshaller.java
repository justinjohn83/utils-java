package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.InputStream;

public interface ResponseUnmarshaller<T> {

	T unmarshall(InputStream in) throws IOException;
}
