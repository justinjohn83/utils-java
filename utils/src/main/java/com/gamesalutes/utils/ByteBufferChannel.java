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

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * Channel wrapper around a byte buffer.
 * 
 * @author Justin Montgomery
 * @version $Id: ByteBufferChannel.java 1381 2009-03-19 23:06:01Z jmontgomery $
 */
public class ByteBufferChannel implements ByteChannel
{
	protected ByteBuffer buf;
	//private boolean isWrite;
	
	/**
	 * Constructor.
	 * @param buf the <code>ByteBuffer</code> to wrap in a channel
	 * @throws NullPointerException if <code>buf</code> is <code>null</code>
	 */
	public ByteBufferChannel(ByteBuffer buf)
	{
		if(buf == null)
			throw new NullPointerException("buf");
		this.buf = buf;
	}
	
	/**
	 * Returns the <code>ByteBuffer</code> used by this channel.
	 * 
	 * @return the <code>ByteBuffer</code>
	 */
	public synchronized ByteBuffer getByteBuffer()
	{
		return buf;
	}
	
	/* (non-Javadoc)
	 * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
	 */
	public synchronized int read(ByteBuffer dst)
	{
		if(!buf.hasRemaining())
			return -1;
		final int read = Math.min(buf.remaining(),dst.remaining());
		
		// do a bulk read
		if(buf.hasArray())
		{
			int offset = buf.arrayOffset();
			dst.put(buf.array(), buf.position() + offset, read);
			// increment the position
			buf.position(buf.position() + read);
		}
		else
		{
			while(dst.hasRemaining() && buf.hasRemaining())
				dst.put(buf.get());
		}
		return read;
	}


	/**
	 * Does nothing.
	 * 
	 */
	public void close() {}
	
	
	/**
	 * Clears the underlying buffer so that it can be used again for reading or writing.
	 * 
	 */
	public synchronized void reset()
	{
		buf.clear();
	}
	
	/**
	 * Flips the underlying buffer so that it can be used for reading the data written
	 * by {@link #write(ByteBuffer)} or so that it can write the data read by 
	 * {@link #read(ByteBuffer)}.
	 * 
	 * 
	 */
	public synchronized void flip()
	{
		buf.flip();
	}

	/* (non-Javadoc)
	 * @see java.nio.channels.Channel#isOpen()
	 */
	public boolean isOpen() { return true; }

	/* (non-Javadoc)
	 * @see java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)
	 */
	public synchronized int write(ByteBuffer src)
	{
		if(!src.hasRemaining())
			return 0;
		final int written = src.remaining();
		
		if(buf.remaining() < written)
		{
			buf = ByteUtils.growBuffer(buf, buf.limit() + (written - buf.remaining()));
		}
		
		// do a bulk write
		if(src.hasArray())
		{
			int offset = src.arrayOffset();
			buf.put(src.array(), src.position() + offset, written);
			// increment position
			src.position(src.position() + written);
		}
		else
			buf.put(src);
		
		return written;
	}

}
