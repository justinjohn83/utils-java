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

import java.net.*;
import java.security.Security;
import java.util.Arrays;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Communicator for sending objects over the network via HTTP or HTTPS.  The set
 * {@link Communicator.ConnectionCallback ConnectionCallback} determines the 
 * method of sending (e.g. "GET" or "POST") and the content type of the send and the conversion
 * of bytes to objects and vice-versa.
 * 
 * The generic <code>S</code> type is the request type, and the generic
 * <code>T</code> type is the response type.  If a "GET" request is being made,
 * then the request type can be <code>java.lang.Void</code> and is ignored in any case.
 * 
 * @author Justin Montgomery
 * @version $Id: RemoteCommunicator.java 1486 2009-05-12 22:26:41Z jmontgomery $
 *
 */
public final class RemoteCommunicator<S,T> implements Communicator<S,T>
{
	private final URL url;
	private final boolean ssl;
	private ConnectionCallback<S,T> connListener;
	private DataCypher cypher;
	
	private static final Logger logger = LoggerFactory.getLogger(RemoteCommunicator.class.getSimpleName());
	
	//initialize the cipher suites to use for https connections
	static
	{
		EncryptUtils.setHttpsEnabledStrongSuites();
	}
	
	/**
	 * Constructor.
	 * Illegal characters must be properly escaped in the input url as 
	 * no escaping is done internally.
	 * 
	 * @param url url string to use for this communicator
	 */
	public RemoteCommunicator(String url)
		throws MalformedURLException
	{
		this(url,null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param url url string to use for this communicator
	 * @param connListener listener for taking actions between sending and receiving
	 * @throws MalformedURLException if error occurs in constructing a <code>URL</code> from
	 *         the specified <code>url</code>
	 */
	public RemoteCommunicator(String url,ConnectionCallback<S,T> connListener)
		throws MalformedURLException
	{
		this.url = new URL(url);
		String protocol = this.url.getProtocol();
		if(protocol.equalsIgnoreCase("https"))
			ssl = true;
		else if(protocol.equalsIgnoreCase("http"))
			ssl = false;
		else
			throw new IllegalArgumentException("url=" + url + " not http or https");
		setConnectionCallback(connListener);
	}
	
	/**
	 * Sets the <code>ConnectionCallback</code>.
	 * 
	 * @param connListener listener for taking actions between sending and receiving
	 */
	public void setConnectionCallback(ConnectionCallback<S,T> connListener)
	{
		if(connListener != null)
			this.connListener = connListener;
		else
			this.connListener = new _ConnectionCallback();
	}
	
	// see if connection listener requests java object serialization
//	private boolean isUsingObjectSerialization()
//	{
//		return this.connListener != null ? 
//				ConnectionCallback.CONTENT_TYPE_JAVA_OBJECT.equals(
//						this.connListener.getContentType()) : 
//				false;
//	}
	
	/**
	 * Sets the encrypt/decrypt cypher that will be used in the absence of 
	 * ssl encryption.
	 * 
	 * @param cypher the {@link DataCypher}
	 */
	public void setDataCypher(DataCypher cypher)
	{
		this.cypher = cypher;
	}
	
	/**
	 * Returns whether a user supplied {@link DataCypher} is being
	 * using for encrption.  The user supplied cypher is used if it is
	 * not <code>null</code> and ssl is not already being used above the 
	 * transport layer.
	 * 
	 * @return <code>true</code> if a user-supplied <code>DataCypher</code>
	 *         is being used and <code>false</code> otherwise
	 */
	public boolean isUsingDataCypher()
	{
		return !ssl && cypher != null;
	}
	
	/**
	 * Returns the currently assigned {@link DataCypher} to do encryption/decryption
	 * in the absence of ssl.
	 * 
	 * @return the current {@link DataCypher}
	 */
	public DataCypher getDataCypher()
	{
		return cypher;
	}
	
	/**
	 * Sends <code>data</code> to the url.
	 * 
	 * @param data data to send
	 * @return response from the host at url
	 * @throws Exception if problem occurs during the exchange
	 */
	public <U extends S,V extends T> V exchange( U data)
		throws Exception
	{
		HttpURLConnection urlcon = null;
		try
		{
			urlcon = createConnection(data,true,true);
			
			// only send if doing output to server
			if(urlcon.getDoOutput())
				send(urlcon,data);

			connListener.beforeReceive(data);
			
			return this.<U,V>receive(urlcon,data);
		
		}
		finally
		{
			if(urlcon != null)
				urlcon.disconnect();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.Communicator#pull()
	 */
	public <V extends T> V pull() throws Exception
	{
		HttpURLConnection urlcon = null;
		try
		{
			// only send data to server if request method is "POST"
			urlcon = createConnection(null,true,false);
			
			return this.<S,V>receive(urlcon,(S)null);
		
		}
		finally
		{
			if(urlcon != null)
				urlcon.disconnect();
		}
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.Communicator#push(java.lang.Object)
	 */
	public <U extends S> void push(U data) throws Exception
	{
		HttpURLConnection urlcon = null;
		try
		{
			urlcon = createConnection(data,false,true);
			send(urlcon,data);
		
		}
		finally
		{
			if(urlcon != null)
				urlcon.disconnect();
		}
	}
	
	private HttpURLConnection createConnection(S data,boolean doInput,boolean doOutput)
		throws Exception
	{
		HttpURLConnection urlcon = null;
		//open connection and set up I/O
		
		try
		{
			urlcon = (HttpURLConnection)url.openConnection();
		}
		catch(Exception e)
		{
			if(doOutput)
				connListener.onSendException(data,e);
			else
				connListener.onReceiveException(data, e);
			
			throw e;
		}
		
		// set up the urlcon properties
		
		// set timeout values
		urlcon.setConnectTimeout(connListener.getConnectTimeout());
		urlcon.setReadTimeout(connListener.getReadTimeout());
		
		if(ssl)
		{
			if(!(urlcon instanceof HttpsURLConnection))
				throw new AssertionError("HttpsURLConnection not used when required");
			
			// verify the host if its name matches that of url hostname
			HttpsURLConnection httpsURLConn = (HttpsURLConnection)urlcon;
			HostnameVerifier verifier = connListener.getHostNameVerifier();
			if(verifier != null)
				httpsURLConn.setHostnameVerifier(verifier);
			// configure the connection to use the specified ssl context's socket factory if
			// present
			SSLContext sslContext = connListener.getSSLContext();
			if(sslContext != null)
				httpsURLConn.setSSLSocketFactory(sslContext.getSocketFactory());
		}
		
		String contentType =  connListener.getContentType();
		String requestMethod = connListener.getRequestMethod();
		
		urlcon.setUseCaches(false);
		if(requestMethod != null)
			urlcon.setRequestMethod(requestMethod);
		if(contentType != null)
			urlcon.setRequestProperty("Content-type",contentType);
		
		doOutput &= "POST".equalsIgnoreCase(urlcon.getRequestMethod());
		
		urlcon.setDoOutput(doOutput);
		urlcon.setDoInput(doInput);
		
		// commit the connection property changes and establish the connection
		// https will do handshake at this time
		try
		{
			urlcon.connect();
			
			if(logger.isTraceEnabled())
			{
				if(urlcon instanceof HttpsURLConnection)
				{
					logger.trace("Security provider list: " + 
							Arrays.asList(Security.getProviders()) + "\n\t" + 
							"Using Cipher suite: " +
							((HttpsURLConnection)urlcon).getCipherSuite());
				}
			}
		}
		catch(Exception e)
		{
			if(doOutput)
				connListener.onSendException(data,e);
			else
				connListener.onReceiveException(data, e);
			throw e;
		}
		
		return urlcon;
	}
	
	private<U extends S> void send(URLConnection urlcon,U data)
		throws Exception
	{
		OutputStream out = null;
		try
		{
			// to allow for encryption, first get the object bytes.
			byte [] bytes = connListener.toOutputBytes(data);
			if(bytes == null)
				throw new RuntimeException("data=" + data + " converted to null");
			if(isUsingDataCypher())
				bytes = cypher.encrypt(bytes);
			// now actually send the bytes to the stream
			out = urlcon.getOutputStream();
			out.write(bytes);
			out.flush();
		}
		catch(Exception e)
		{
			connListener.onSendException(data,e);
			throw e;
		}
		finally
		{
			MiscUtils.closeStream(out);
		}
	}
	
	@SuppressWarnings("unchecked")
	private<U extends S,V extends T>V receive(URLConnection urlcon,U sentData)
		throws Exception
	{
		InputStream in = null;
		try
		{
			// read in the data bytes
			in = urlcon.getInputStream();
			byte [] bytes = ByteUtils.readBytes(in);
			// decrypt the bytes if cypher encrypted them
			if(isUsingDataCypher())
				bytes = cypher.decrypt(bytes);
			
			// convert the bytes to an object 
			return (V)connListener.toInputObject(bytes);
		}
		catch(Exception e)
		{
			connListener.onReceiveException(sentData,e);
			throw e;
		}
		// not necessary since readBytes closes the stream
		/*
		finally
		{
			try
			{
				if(in != null)
					in.close();
			}
			catch(IOException e) {}
		}
		*/
	}
	/**
	 * Returns string representation of the url used to connect.
	 * 
	 * @return the connection url as a string
	 */
	@Override
	public String toString()
	{
		return getURL();
	}
	
	/**
	 * Returns the url used to connect to the server.
	 * 
	 * @return the url as a <code>String</code>
	 */
	public String getURL()
	{
		return url.toString();
	}
	
	
	private class _ConnectionCallback implements ConnectionCallback<S,T>
	{

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#beforeReceive()
		 */
		public void beforeReceive(S data) {}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#getConnectTimeout()
		 */
		public int getConnectTimeout() { return DEFAULT_CONNECT_TIMEOUT; }

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#getContentType()
		 */
		public String getContentType() { return null; }

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#getHostNameVerifier()
		 */
		public HostnameVerifier getHostNameVerifier() { return null; }

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#getReadTimeout()
		 */
		public int getReadTimeout() { return DEFAULT_READ_TIMEOUT; }

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#getRequestMethod()
		 */
		public String getRequestMethod() { return null; }

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#getSSLContext()
		 */
		public SSLContext getSSLContext() { return null; }

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#onReceiveException(java.lang.Exception)
		 */
		public void onReceiveException(S data,Exception e) {}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#onSendException(java.lang.Exception)
		 */
		public void onSendException(S data,Exception e) {}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#toInputObject(byte[])
		 */
		public T toInputObject(byte[] rcvdBytes) throws Exception 
		{
			return (T)new String(rcvdBytes,"UTF-8");
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Communicator.ConnectionCallback#toOutputBytes(java.lang.Object)
		 */
		public byte[] toOutputBytes(S toSend) throws Exception 
		{
			return MiscUtils.toString(toSend).getBytes("UTF-8");
		}
		
	}
}
