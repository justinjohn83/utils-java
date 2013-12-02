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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * Interface for describing sending some data from point A to point B.  An implementation may provided
 * for all three operations : <code>exchange</code>,<code>push</code>, and <code>pull</code> or for just
 * <code>exchange</code> or just <code>push</code> and/or </code>pull</code>.  In cases where the operation
 * is not implemented, an <code>UnsupportedOperationException</code> should be thrown.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id: Communicator.java 1486 2009-05-12 22:26:41Z jmontgomery $
 */
public interface Communicator<S,T>
{
	/**
	 * Callback for taking actions between send and receive.
	 * 
	 * @author Justin Montgomery
	 *
	 */
	public interface ConnectionCallback<S,T>
	{
		/**
		 * Default value for connection timeout in ms.
		 * 
		 */
		public static final int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
		
		/**
		 * Default value for read timeout in ms.
		 * 
		 */
		public static final int DEFAULT_READ_TIMEOUT = 5 * 60 * 1000;
		
		
		/**
		 * Content type for plain text.
		 * 
		 */
		public static final String CONTENT_TYPE_PLAIN_TEXT = "text/plain";
		
		/**
		 * Content type for html text.
		 * 
		 */
		public static final String CONTENT_TYPE_HTML = "text/html";
		
		/**
		 * Content type for a serialized Java object.
		 * 
		 */
		public static final String CONTENT_TYPE_JAVA_OBJECT = 
			"application/x-java-serialized-object";
		
		
		/**
		 * Request method for a "POST".
		 * 
		 */
		public static final String REQUEST_METHOD_POST = "POST";
		
		/**
		 * Request method for a "GET".
		 * 
		 */
		public static final String REQUEST_METHOD_GET = "GET";
		
		/**
		 * Called after send but before receive.
		 * 
		 * @param data the data that was sent
		 *
		 */
		public void beforeReceive(S data);
		
		/**
		 * Called when an exception occurs on send.
		 * 
		 * @param data the data that was attempted to be sent
		 * @param e the <code>Exception</code> that occurred
		 */
		public void onSendException(S data,Exception e);
		
		/**
		 * Called when an exception occurs on receive.
		 * 
		 * @param data the data that was sent
		 * @param e the <code>Exception</code> that occurred
		 */
		public void onReceiveException(S data,Exception e);
		
		/**
		 * Returns the connect timeout value in ms.  Specify 0 for infinite timeout.
		 * 
		 * @return the connect timeout value in ms
		 */
		public int getConnectTimeout();
		
		/**
		 * Returns the read timeout value in ms.  Specify 0 for infinite timeout.
		 * 
		 * @return the read timeout value in ms
		 */
		public int getReadTimeout();
		
		/**
		 * Returns the request method, e.g. "POST" or "GET" for HTTP, to use for the 
		 * connection. 
		 * 
		 * @return the request method or <code>null</code> if the communicator does not distinguish
		 */
		public String getRequestMethod();
		
		/**
		 * Returns the content type, e.g. "text/plain","text/html",
		 * "application/x-java-serialized-object" for HTTP, to use for the connection.
		 * 
		 * @return the content type or <code>null</code> if the communicator does not distinguish
		 */
		public String getContentType();
		
		/**
		 * Converts the output object to its byte representation that the server expects.
		 * The input object will be an instance of the <code>Communicator</code> send type
		 * <code>S</code>.
		 * 
		 * @param toSend the object to send
		 * @return the output bytes to send to the server
		 * @throws Exception if error occurs during byte conversion
		 */
		public byte [] toOutputBytes(S toSend) throws Exception;
		
		/**
		 * Converts the raw bytes read from the server response to the object that the client
		 * expects.  The output object must be an instance of the <code>Communicator</code>
		 * receive type <code>T</code>.
		 * 
		 * @param rcvdBytes the raw bytes received from the server
		 * @return the object form of <code>rcvdBytes</code>
		 * @throws Exception if the translation cannot be performed
		 */
		public T toInputObject(byte [] rcvdBytes) throws Exception;
		
		/**
		 * Returns the <code>SSLContext</code> to use for secure communications or
		 * <code>null</code> to use the default.
		 * 
		 * @return the <code>SSLContext</code> to use for secure communications or
		 * <code>null</code> to use the default
		 */
		public SSLContext getSSLContext();
		
		/**
		 * Returns the <code>HostnameVerifier</code> to verify the host names in the
		 * server certificates presented to the client or <code>null</code> to use the
		 * default method.
		 * 
		 * @return the <code>HostnameVerifier</code>
		 */
		public HostnameVerifier getHostNameVerifier();
	}

	/**
	 * Sends <code>data</code> to the destination and gets a response.
	 * 
	 * @param data data to send
	 * @return response from the destination
	 * @throws Exception if problem occurs during the exchange
	 */
	public <U extends S,V extends T> V exchange( U data)
		throws Exception;
	
	/**
	 * Sends some data to a destination where no response is returned.
	 * 
	 * @param data the data to send
	 * @throws Exception if problem occurs during the exchange
	 */
	public <U extends S> void push(U data)
		throws Exception;
	
	/**
	 * Receives some data from the destination, blocking until a response is obtained.
	 * 
	 * @return response from the destination
	 */
	public <V extends T> V pull()
		throws Exception;
	
	/**
	 * Sets the <code>ConnectionCallback</code>.
	 * 
	 * @param connListener listener for taking actions between sending and receiving
	 */
	void setConnectionCallback(ConnectionCallback<S,T> connListener);
}
