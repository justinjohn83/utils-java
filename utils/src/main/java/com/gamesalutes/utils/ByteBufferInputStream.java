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

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * <code>InputStream</code> wrapper around a <code>ByteBuffer</code>.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ByteBufferInputStream extends InputStream
{
	private final ByteBuffer buf;
	
	public ByteBufferInputStream(ByteBuffer buf)
	{
		if(buf == null) throw new NullPointerException("buf");
		this.buf = buf;
		buf.mark();
	}
	
    public synchronized int read()
    {
        if (!buf.hasRemaining())
            return -1;
        return buf.get();
    }

    public synchronized int read(byte[] bytes, int off, int len)
    {
    	if(len == 0) return 0;
    	
        // Read only what's left
        len = Math.min(len, buf.remaining());
        
        if(len == 0) return -1;
        
        buf.get(bytes, off, len);
        return len;
    }

	@Override
	public synchronized void mark(int readlimit)
	{
		buf.mark();
	}

	@Override
	public boolean markSupported()
	{
		return true;
	}

	@Override
	public synchronized void reset()
	{
		buf.reset();
	}

	@Override
	public synchronized long skip(long n)
	{
		if(available() == 0)
			return -1;
		int skip = (int)Math.min(buf.remaining(), n);
		buf.position(buf.position() + skip);
		return 0;
	}

	@Override
	public synchronized int available()
	{
		return buf.remaining();
	}

	@Override
	public void close() {}


}
