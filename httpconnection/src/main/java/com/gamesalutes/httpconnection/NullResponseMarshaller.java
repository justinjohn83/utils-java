package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.InputStream;

public class NullResponseMarshaller implements ResponseUnmarshaller<Void> {

	public NullResponseMarshaller() {
		// TODO Auto-generated constructor stub
	}

	public Void unmarshall(InputStream in) throws IOException {
		return null;
	}

}
