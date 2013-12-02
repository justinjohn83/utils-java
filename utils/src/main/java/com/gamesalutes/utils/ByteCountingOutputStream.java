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
import java.io.OutputStream;

import com.gamesalutes.utils.ByteCountingInputStream.StreamReadListener;

public class ByteCountingOutputStream extends OutputStream {

	private int writtenCount = 0;
	
	private OutputStream wrapped;
	private StreamWrittenListener listener;
	
	public interface StreamWrittenListener {
		void onWriteComplete(int readCount);
	}
	
	public ByteCountingOutputStream(OutputStream wrapped,StreamWrittenListener listener) {
		this.wrapped = wrapped;
		this.listener = listener;
	}
	
	@Override
	public void write(int arg0) throws IOException {
		wrapped.write(arg0);
		++writtenCount;
	}

	@Override
	public void close() throws IOException {
		try {
			if(listener != null) {
				listener.onWriteComplete(writtenCount);
			}
		}
		finally {
			wrapped.close();
		}
	}

	@Override
	public void flush() throws IOException {
		wrapped.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		wrapped.write(b,off,len);
		writtenCount += len;
	}

	@Override
	public void write(byte[] b) throws IOException {
		wrapped.write(b);
		writtenCount += b.length;
	}

}
