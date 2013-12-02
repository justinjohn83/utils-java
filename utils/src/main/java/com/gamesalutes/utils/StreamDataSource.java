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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.activation.DataSource;
import org.apache.commons.codec.binary.Base64;


/**
 * <code>DataSource</code> for an arbitrary stream.  MIME type will be "application/octet-stream" unless
 * indicated otherwise.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class StreamDataSource implements DataSource
{

	private final StreamFactory creator;
	private final String name;
	private final String contentType;
	
	/**
	 * Creates a new input stream and new output stream on demand that
	 * encapsulates the desired content.
	 * 
	 * @author Justin Montgomery
	 * @version $Id:$
	 */
	public interface StreamFactory
	{
		InputStream newInputStream(String name) throws IOException;
		OutputStream newOutputStream(String name) throws IOException;
	}
	
	public StreamDataSource(StreamFactory creator,String name)
	{
		this(creator,name,null);
	}
	public StreamDataSource(StreamFactory creator,String name,String contentType)
	{
		if(creator == null)
			throw new NullPointerException("creator");
		this.creator = creator;
		this.name = name;
		if(contentType != null)
			this.contentType = contentType;
		else
			this.contentType = "application/octet-stream";
	}
	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType()
	{
		return contentType;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException
	{
		return creator.newInputStream(name);
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException
	{
		return creator.newOutputStream(name);
	}
	
	/**
	 * Default implementation of stream creator that only supports input streams and stores its content in
	 * memory so that {@link #newInputStream(String)} can return an input stream that points to the position of the
	 * content at the time this object was constructed.
	 * 
	 * @author Justin Montgomery
	 * @version $Id:$
	 */
	public static class DefaultStreamFactory implements StreamFactory
	{
		private final ByteBuffer data;
		
		public DefaultStreamFactory(InputStream in,boolean base64Encode)
			throws IOException
		{
			if(in == null)
				throw new NullPointerException("in");
                        if(!base64Encode)
                        {
                            data = ByteUtils.readBytes(in,null);
                        }
                        else
                        {
                            byte [] ba = ByteUtils.readBytes(in);
                            ba = Base64.encodeBase64(ba);
                            data = ByteBuffer.wrap(ba);
                        }
		}
		
		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.StreamDataSource.StreamCreator#newInputStream(java.lang.String)
		 */
		public InputStream newInputStream(String name)
		{
                        ByteBuffer b = data.duplicate();
                        b.rewind();
			return new ByteBufferInputStream(b);
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.StreamDataSource.StreamCreator#newOutputStream(java.lang.String)
		 */
		public OutputStream newOutputStream(String name)
		{
			throw new UnsupportedOperationException();
		}
		
	}

}
