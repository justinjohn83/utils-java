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
/* Copyright 2008 - 2009 University of Chicago
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

import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * <code>OutputStream</code> wrapper around a <code>ByteBuffer</code>.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ByteBufferOutputStream extends OutputStream
{

	private ByteBuffer buf;
	
	public ByteBufferOutputStream()
	{
		this(null);
	}
	public ByteBufferOutputStream(ByteBuffer buf)
	{
		if(buf == null)
			buf = ByteBuffer.allocate(4096);
		this.buf = buf;
	}
	
    public synchronized void write(int b)
    {
    	grow(1);
        buf.put((byte)b);
    }

    public synchronized void write(byte[] bytes, int off, int len)
    {
    	grow(len);
        buf.put(bytes, off, len);
    }
    
    private void grow(int len)
    {
		if(buf.remaining() < len)
		{
			buf = ByteUtils.growBuffer(buf, buf.limit() + (len - buf.remaining()));
		}
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
	@Override
	public void close(){}
	
	@Override
	public void flush() {}
}
