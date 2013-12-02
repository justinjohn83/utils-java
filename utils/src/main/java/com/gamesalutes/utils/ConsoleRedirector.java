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
/* Copyright 2008 University of Chicago
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.gamesalutes.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

/**
 * Redirects console input/output.
 * 
 * @author Justin Montgomery
 * @version $Id: ConsoleRedirector.java 1151 2008-10-20 22:51:21Z jmontgomery $
 */
public final class ConsoleRedirector
{
	private final InputStream sysIn;
	private final PrintStream sysOut;
	private final PrintStream sysErr;
	
	private ByteBufferChannel outBuf;
	private ByteBufferChannel errBuf;
	private ByteBufferChannel consoleBuf;
	
	private String charEncoding = "UTF-8";
	
	
	private static final ConsoleRedirector INSTANCE = new ConsoleRedirector();
	
	
	public static ConsoleRedirector getInstance() { return INSTANCE; }
	
	
	public synchronized void setBufferCharEncoding(String charEncoding)
	{
		if(charEncoding == null)
			throw new NullPointerException("charEncoding");
		this.charEncoding = charEncoding;
		
	}
	
	public synchronized String getBufferCharEncoding() { return charEncoding; }
	
	private ConsoleRedirector()
	{
		sysIn = System.in;
		sysOut = System.out;
		sysErr = System.err;
	}
	
	public synchronized void setIn(InputStream sysIn)
	{
		if(sysIn == null)
			throw new NullPointerException("sysIn");
		System.setIn(sysIn);
	}
	
	public synchronized void setOut(PrintStream sysOut)
	{
		if(sysOut == null)
			throw new NullPointerException("sysOut");
		System.setOut(sysOut);
	}
	public synchronized void setOut(OutputStream sysOut)
	{
		if(sysOut == null)
			throw new NullPointerException("sysOut");
		setOut(set(sysOut));
	}
	public synchronized void setErr(PrintStream sysErr)
	{
		if(sysErr == null)
			throw new NullPointerException("sysErr");
		System.setErr(sysErr);
	}
	public synchronized void setErr(OutputStream sysErr)
	{
		if(sysErr == null)
			throw new NullPointerException("sysErr");
		setErr(set(sysErr));
		
	}
	
	public synchronized void setOutToBuffer(boolean discard)
	{
		Pair<PrintStream,ByteBufferChannel> p = createStreamBuffer(discard);
		setOut(p.first);
		this.outBuf = p.second;
		this.consoleBuf = null;
	}
	public synchronized void setErrToBuffer(boolean discard)
	{
		Pair<PrintStream,ByteBufferChannel> p = createStreamBuffer(discard);
		setErr(p.first);
		this.errBuf = p.second;
		this.consoleBuf = null;
	}
	
	public synchronized void setConsoleOutputToBuffer(boolean discard)
	{
		Pair<PrintStream,ByteBufferChannel> p = createStreamBuffer(discard);
		setOut(p.first);
		setErr(p.first);
		this.consoleBuf = p.second;
		this.outBuf = this.errBuf = null;
	}
	
	public synchronized String getOutToString()
	{
		if(this.outBuf == null)
			throw new IllegalStateException("System.out is not set to a buffer!");
		return this.getOutputToString(this.outBuf);
	}
	public synchronized String getErrToString()
	{
		if(this.errBuf == null)
			throw new IllegalStateException("System.err is not set to a buffer!");
		return this.getOutputToString(this.errBuf);
	}
	public synchronized byte[] getOutToByteArray()
	{
		if(this.outBuf == null)
			throw new IllegalStateException("System.out is not set to a buffer!");
		return this.getOutputToByteArray(this.outBuf);
	}
	
	public synchronized byte[] getErrToByteArray()
	{
		if(this.errBuf == null)
			throw new IllegalStateException("System.err is not set to a buffer!");
		return this.getOutputToByteArray(this.errBuf);
	}
	
	public synchronized String getConsoleOutputToString()
	{
		if(this.consoleBuf == null)
			throw new IllegalStateException("console output is not set to a buffer!");
		return this.getOutputToString(this.consoleBuf);
	}
	
	public synchronized byte [] getConsoleOutputToByteArray()
	{
		if(this.consoleBuf == null)
			throw new IllegalStateException("console output is not set to a buffer!");
		return this.getOutputToByteArray(this.consoleBuf);
	}
	
	

	
	public synchronized void restoreIn()
	{
		System.setIn(sysIn);
	}
	public synchronized void restoreOut()
	{
		this.outBuf = null;
		this.consoleBuf = null;
		System.setOut(sysOut);
	}
	public synchronized void restoreErr()
	{
		this.errBuf = null;
		this.consoleBuf = null;
		System.setErr(sysErr);
	}
	public synchronized void restoreConsoleOutput()
	{
		restoreOut();
		restoreErr();
	}
	public synchronized void restore()
	{
		restoreIn();
		restoreConsoleOutput();
	}
	
	private PrintStream set(OutputStream out)
	{
		if(!(out instanceof PrintStream))
			return new PrintStream(out,false);
		else
			return (PrintStream)out;
	}
	
	private Pair<PrintStream,ByteBufferChannel> createStreamBuffer(final boolean discard)
	{
		final ByteBufferChannel bbc = new ByteBufferChannel(ByteBuffer.allocate(64 * 1024));
		PrintStream stream = new PrintStream(Channels.newOutputStream(bbc),discard)
		{
			// override flush so that it resets the buffer so that the buffer
			// does not just keep increasing with each written line
			// flush in ByteArrayOutputStream by default does nothing
			public void flush()
			{
				if(discard)
					bbc.reset();
			}
		};
		
		return Pair.makePair(stream,bbc);
	}
	
	private String getOutputToString(ByteBufferChannel bbc)
	{
		// flip from writing to reading
		bbc.flip();
		try
		{
			String str = FileUtils.readData(Channels.newReader(bbc, charEncoding));
			return str;
		}
		catch(IOException e)
		{
			throw new RuntimeException("Unable to convert buffer to string",e);
		}
		finally
		{
			bbc.reset();
		}
	}
	private byte[] getOutputToByteArray(ByteBufferChannel bbc)
	{
		ByteBuffer buf = bbc.getByteBuffer();
		buf.flip();
		byte [] arr = buf.array();
		int off = buf.arrayOffset();
		int len = buf.limit();
		byte [] newData = new byte[len];
		System.arraycopy(arr, off, newData, 0, len);
		
		bbc.reset();
		
		return newData;
		
	}
	
	
}
