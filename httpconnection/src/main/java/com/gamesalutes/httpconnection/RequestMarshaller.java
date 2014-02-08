package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestMarshaller {

	void marshall(OutputStream out) throws IOException;
}
