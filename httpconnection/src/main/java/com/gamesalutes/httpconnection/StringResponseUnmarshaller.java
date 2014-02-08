package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.InputStream;

import com.gamesalutes.utils.FileUtils;

public class StringResponseUnmarshaller implements ResponseUnmarshaller<String> {

	public StringResponseUnmarshaller() {
	}

	public String unmarshall(InputStream in) throws IOException {
    	return FileUtils.readData(in);
	}

}
