package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.OutputStream;

public class NullRequestMarshaller implements RequestMarshaller<Void> {

	public NullRequestMarshaller() {
	}

	public void marshall(Void obj,OutputStream out) throws IOException {
		//
	}

	public String getContentType() {
		return "text/plain";
	}

}
