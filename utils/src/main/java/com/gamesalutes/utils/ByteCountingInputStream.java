/*
 * Copyright (c) 2013 Game Salutes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     Game Salutes - Repackaging and modifications of original work under University of Chicago and Apache License 2.0 shown below
 * 
 * Repackaging from edu.uchicago.nsit.iteco.utils to com.gamesalutes.utils
 * 
 * Copyright 2008 - 2011 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.gamesalutes.utils;

import java.io.IOException;
import java.io.InputStream;

public class ByteCountingInputStream extends InputStream {

	private int readCount = 0;
	
	private InputStream wrapped;
	private StreamReadListener listener;
	
	public interface StreamReadListener {
		void onReadComplete(int readCount);
		void onReadBegin();
	}
	
	public ByteCountingInputStream(InputStream wrapped,StreamReadListener listener) {
		this.wrapped = wrapped;
		this.listener = listener;
	}
	
	@Override
	public int read() throws IOException {

		notifyStart();
		int read = wrapped.read();
		++readCount;
		return read;
	}
	
	private void notifyStart() {
		if(listener != null && readCount == 0) {
			listener.onReadBegin();
		}
	}

	@Override
	public int available() throws IOException {
		notifyStart();
		return wrapped.available();
	}

	@Override
	public void close() throws IOException {
		try {
			if(listener != null) {
				listener.onReadComplete(readCount);
			}
		}
		finally {
			wrapped.close();
		}
	}

	@Override
	public void mark(int m) {
		wrapped.mark(m);
	}

	@Override
	public boolean markSupported() {
		return wrapped.markSupported();
	}

	@Override
	public int read(byte[] bytes, int off, int len) throws IOException {
		
		notifyStart();
		int read = wrapped.read(bytes,off,len);

		readCount += read;
		return read;
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		
		notifyStart();
		int read = wrapped.read(bytes);
		readCount += read;
		return read;
	}

	@Override
	public  void reset() throws IOException {
		wrapped.reset();
	}

	@Override
	public long skip(long arg0) throws IOException {
		notifyStart();
		return wrapped.skip(arg0);
	}
	
	public int getReadCount() {
		return readCount;
	}

}
