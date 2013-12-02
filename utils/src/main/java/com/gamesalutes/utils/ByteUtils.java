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

import java.io.*;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.channels.Channels;
import org.apache.commons.codec.binary.Base64;
/**
 * Contains utility methods for byte and bit manipulations.
 * 
 * @author Justin Montgomery
 * @version $Id: ByteUtils.java 2454 2010-11-18 18:22:18Z jmontgomery $
 *
 */
public final class ByteUtils 
{
	private ByteUtils() {}
	
	
	private static final int NETWORK_BYTE_SIZE = 512;
	private static final int READ_BUFFER_SIZE = 64 * 1024; //64 kb

	
    /**
     * Transfers all data from <code>is</code> to </code>os</code>.
     * <code>is</code> is closed as a result of invoking this operation.
     *
     * @param is the <code>InputStream</code>
     * @param os the <code>OutputStream</code>
     *
     */
    public static void transferData(InputStream is,OutputStream os)
            throws IOException
    {
    	transferData(is,os,-1);
    }

        /**
         * Transfers all data from <code>is</code> to </code>os</code>.
         * <code>is</code> is closed as a result of invoking this operation.
         *
         * @param is the <code>InputStream</code>
         * @param os the <code>OutputStream</code>
         * @param maxBytes approximate maximum number of bytes to transfer or <code>-1</code> to not set a limit
         *
         */
        public static void transferData(InputStream is,OutputStream os,long maxBytes)
                throws IOException
        {
            byte [] buf = new byte[NETWORK_BYTE_SIZE];

            int read;
            long totalRead = 0;

            try
            {
                while((read = is.read(buf)) > 0)
                {
                    os.write(buf,0,read);
                    totalRead += read;
                    if(maxBytes > 0 && totalRead > maxBytes) {
                    	throw new IOException("maxBytes=" + maxBytes + " exceeded");
                    }
                }
                os.flush();
            }
            finally
            {
                MiscUtils.closeStream(is);
            }
        }
	/**
	 * Returns a <code>byte[]</code> containing the byte representation
	 * of the serializable object <code>obj</code>.
	 * 
	 * @param obj the <code>Object</code> to convert to a byte array
	 * @return <code>byte[]</code> containing the byte representation
	 * of <code>obj</code>.
	 * 
	 * @throws IOException if <code>obj</code> is not serializable or error occurs while writing the bytes
	 */
	public static byte[] getObjectBytes(Object obj)
		throws IOException
	{
		ByteArrayOutputStream bout = null;
		ObjectOutputStream out = null;
		byte [] data = null;
		try
		{
			bout = new ByteArrayOutputStream(READ_BUFFER_SIZE);
			out = new ObjectOutputStream(bout);
			out.writeObject(obj);
			out.flush();
			
			data = bout.toByteArray();
		}
		finally
		{
			MiscUtils.closeStream(out);
		} //end finally
		
		return data;
	} //end getObjectBytes
	
	
	/**
	 * Returns a <code>ByteBuffer</code> containing the byte representation
	 * of the serializable object <code>obj</code>.
	 * 
	 * @param obj the <code>Object</code> to convert to its byte representation
	 * @param buf an existing buffer to use for storage or <code>null</code> to create new buffer.
	 *        If <code>buf</code> is not large enough it will be expanded using {@link #growBuffer(ByteBuffer, int)}
	 * @return <code>ByteBuffer</code> containing the byte representation
	 * of <code>obj</code>.
	 * 
	 * @throws IOException if <code>obj</code> is not serializable or error occurs while writing the bytes
	 */
	public static ByteBuffer getObjectBytes(Object obj,ByteBuffer buf)
		throws IOException
	{
		if(buf == null) buf = ByteBuffer.allocate(READ_BUFFER_SIZE);
		
		// note the input position
		int startPos = buf.position();
		ByteBufferChannel channel = new ByteBufferChannel(buf);
		ObjectOutputStream out = null;
		try
		{
			out = new ObjectOutputStream(Channels.newOutputStream(channel));
			out.writeObject(obj);
			out.flush();
		}
		finally
		{
			MiscUtils.closeStream(out);
		}
		
		ByteBuffer returnBuf = channel.getByteBuffer();
		returnBuf.flip();
		// reset starting position to be that of input buffer
		returnBuf.position(startPos);
		return returnBuf;
	}
	
	/**
	 * Reads all the bytes from the <code>InputStream</code> and
	 * then closes the stream even if exception occurs.
	 * 
	 * @param in the <code>InputStream</code>
	 * @return the read bytes
	 * @throws IOException if error occurs while reading
	 */
	public static byte [] readBytes(InputStream in)
		throws IOException
	{
		try
		{
			ByteArrayOutputStream bOut = 
				new ByteArrayOutputStream(READ_BUFFER_SIZE);
			
			byte [] buf = new byte[NETWORK_BYTE_SIZE];
			int read;
			// read until end of file
			while((read = in.read(buf)) > 0)
				bOut.write(buf, 0, read);
			return bOut.toByteArray();
		}
		finally
		{
			MiscUtils.closeStream(in);
		}
	}
	
	/**
	 * Reads all the bytes from the given input stream and stores them in the specified buffer.
	 * If the input buffer is <code>null</code> or does not have the capacity to store all the input, a 
	 * new buffer is created and returned.  The input stream is closed regardless of whether an
	 * <code>IOException</code> is thrown.
	 * 
	 * 
	 * @param in the <code>InputStream</code> to read
	 * @param buf a <code>ByteBuffer</code> to use for storage or <code>null</code> to just allocate a new one
	 * 		 If <code>buf</code> is not large enough it will be expanded using {@link #growBuffer(ByteBuffer, int)}
	 * @return the buffer containing the read data
	 * @throws IOException
	 */
	public static ByteBuffer readBytes(InputStream in,ByteBuffer buf)
		throws IOException
	{
		try
		{
			if(buf == null) buf = ByteBuffer.allocate(READ_BUFFER_SIZE);
			
			// note the input position
			int startPos = buf.position();
			
			byte [] tmp = new byte[NETWORK_BYTE_SIZE];
			int read;
			// read until end of file
			while((read = in.read(tmp)) > 0)
			{
				if(buf.remaining() < read)
				{
					buf = ByteUtils.growBuffer(buf, buf.limit() + (read - buf.remaining()));
				}
				buf.put(tmp,0,read);
			}


			buf.flip();
			// reset starting position to be that of input buffer
			buf.position(startPos);
			return buf;
		}
		finally
		{
			MiscUtils.closeStream(in);
		}
		
	}
	
	/**
	 * Updates the target position and limit with the source buffer's position and limit taking into account
	 * the stride differences of the buffers.
	 * 
	 * @param source the source buffer
	 * @param target the target buffer
	 * @return the target buffer
	 */
	public static <S extends Buffer,T extends Buffer> T updateBuffer(S source,T target) {
		int sourcePos = source.position();
		int sourceLimit = source.limit();
		int sourceStride = getBufferStrideSize(source);
		int targetStride = getBufferStrideSize(target);
		int stride = targetStride / sourceStride;
		
		target.clear();
		target.position(sourcePos * stride);
		target.limit(sourceLimit * stride);
		
		return target;
	}
	
	public static int getBufferStrideSize(Buffer b) {
		if(b instanceof ByteBuffer) {
			return 1;
		}
		else if(b instanceof CharBuffer) { 
			return 2;
		}
		else if(b instanceof IntBuffer) {
			return 4;
		}
		else if(b instanceof DoubleBuffer) {
			return 8;
		}
		else if(b instanceof LongBuffer) {
			return 8;
		}
		else if(b instanceof FloatBuffer) {
			return 4;
		}
		else {
			throw new IllegalArgumentException("b=" + MiscUtils.getClassName(b));
		}
	}
	
	/**
	 * Extends the size of <code>buf</code> to at least meet <code>minCap</code>.
	 * If <code>buf</code> is too small, then a new buffer is allocated and
	 * any existing contents in <code>buf</code> will be transfered.  The position
	 * of the new buffer will be that of the old buffer if it was not <code>null</code>, and
	 * the previous mark will be discarded if one was set.
	 * 
	 * @param buf the input <code>ByteBuffer</code>
	 * @param minCap the minimum capacity
	 * @return a <code>CharBuffer</code> that can meet <code>minCap</code>
	 */
	public static CharBuffer growBuffer(CharBuffer buf,int minCap)
	{
		int myLimit = buf != null ? buf.limit() : 0;
		// limit can accomidate capacity requirements
		if(buf != null && myLimit >= minCap)
			return buf;
		int myCap = buf != null ? buf.capacity() : 0;
		// capacity can accomidate but limit is too small
		if(buf != null && myCap >= minCap)
		{
			buf.limit(myCap);
			return buf;
		}
		else //if(myCap < minCap)
		{
			CharBuffer newBuffer = null;
			if(myCap == 0) myCap = 1;
			while(myCap < minCap)
				myCap <<= 1;
//			if(buf != null && buf.isDirect())
//				newBuffer = CharBuffer.allocateDirect(myCap);
//			else
				newBuffer = CharBuffer.allocate(myCap);
			// copy contents of original buffer
			if(buf != null)
			{
				int pos = buf.position();
				buf.clear();
				newBuffer.put(buf);
				newBuffer.position(pos);
			}
			return newBuffer;
			
		}
	}
	
	/**
	 * Extends the size of <code>buf</code> to at least meet <code>minCap</code>.
	 * If <code>buf</code> is too small, then a new buffer is allocated and
	 * any existing contents in <code>buf</code> will be transfered.  The position
	 * of the new buffer will be that of the old buffer if it was not <code>null</code>, and
	 * the previous mark will be discarded if one was set.
	 * 
	 * @param buf the input <code>ByteBuffer</code>
	 * @param minCap the minimum capacity
	 * @return a <code>ByteBuffer</code> that can meet <code>minCap</code>
	 */
	public static ByteBuffer growBuffer(ByteBuffer buf,int minCap)
	{
		int myLimit = buf != null ? buf.limit() : 0;
		// limit can accomidate capacity requirements
		if(buf != null && myLimit >= minCap)
			return buf;
		int myCap = buf != null ? buf.capacity() : 0;
		// capacity can accomidate but limit is too small
		if(buf != null && myCap >= minCap)
		{
			buf.limit(myCap);
			return buf;
		}
		else //if(myCap < minCap)
		{
			ByteBuffer newBuffer = null;
			if(myCap == 0) myCap = 1;
			while(myCap < minCap)
				myCap <<= 1;
			if(buf != null && buf.isDirect())
				newBuffer = ByteBuffer.allocateDirect(myCap);
			else
				newBuffer = ByteBuffer.allocate(myCap);
			// copy contents of original buffer
			if(buf != null)
			{
				int pos = buf.position();
				buf.clear();
				newBuffer.put(buf);
				newBuffer.position(pos);
			}
			return newBuffer;
			
		}
	}
	
	/**
	 * Reads an object from the input <code>bytes</code>.
	 * 
	 * @param <T> type of the object
	 * @param bytes the input bytes
	 * @return the read object
	 * @throws Exception if error occurs during reading
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readObject(byte [] bytes)
		throws Exception
	{
		if(bytes == null) throw new NullPointerException("bytes");
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return (T)in.readObject();
		}
		finally
		{
			MiscUtils.closeStream(in);
		}
	}
	
	/**
	 * Reads an object from the input <code>buf</code>.
	 * 
	 * @param <T> type of the object
	 * @param buf the input <code>ByteBuffer</code>
	 * @return the read object
	 * @throws Exception if error occurs during reading
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readObject(ByteBuffer buf)
		throws Exception
	{
		if(buf == null) throw new NullPointerException("buf");
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(Channels.newInputStream(new ByteBufferChannel(buf)));
			return (T)in.readObject();
		}
		finally
		{
			MiscUtils.closeStream(in);
		}
	}
	
	/**
	 * Clones an object using serialization.
	 * 
	 * @param obj the input object
	 * @param bufData the <code>ByteBuffer</code> in index 0 to use for temporary storage or <code>null</code> to create a new storage buffer.
	 *            On output it will contain the final buffer used for serializing the object
	 * @return the cloned object
	 * @throws Exception 
	 */
	public static <T> T serializationClone(T obj,ByteBuffer[] bufData)
		throws Exception
	{
		if(obj == null) return null;
		ByteBuffer buf = null;
		int startPos = 0;
		if(!MiscUtils.isEmpty(bufData))
		{
			buf = bufData[0];
			if(buf != null) startPos = buf.position();
		}
		buf = ByteUtils.getObjectBytes(obj, buf);
		T clone = ByteUtils.<T>readObject(buf);
		// copy new buffer over
		buf.position(startPos);
		bufData[0] = buf;
		
		return clone;
		
	}
	
	/**
	 * Clones an object using serialization.
	 * 
	 * @param obj the input object
	 * @throws Exception if <code>obj</code> is not serializable 
	 *         or error occurs during read or write operation
	 */
	public static <T> T serializationClone(T obj)
		throws Exception
	{
		byte [] data = ByteUtils.getObjectBytes(obj);
		return ByteUtils.<T>readObject(data);
	}
	
	/**
	 * Serializes an object as a string using serialization and Base64 encoding.
	 * 
	 * @param obj the input <code>Object</code>
	 * @return the string form of the object
	 * @throws Exception if error occurs in serialization
	 */
	public static String stringSerialize(Object obj)
		throws Exception
	{
		String data = "";
		
		if(obj != null)
		{
			byte [] bytes = ByteUtils.getObjectBytes(obj);
			// encodeBase64 will not "chunk" (add "\r\n") the bytes by default
			bytes = Base64.encodeBase64(bytes);
			return new String(bytes,"UTF-8");
		}
		return data;
	}
	
	/**
	 * Deserializes a string serialized using {@link #stringSerialize}.
	 * 
	 * @param data the serialized string data
	 * @param type the type of the deserialized object
	 * @throws Exception if error occurs during deserialization
	 */
	public static <T> T stringDeserialize(String data,Class<T> type)
		throws Exception
	{
		if(MiscUtils.isEmpty(data))
			return null;
		// decode the Base64 String into the raw object bytes
		byte [] bytes = Base64.decodeBase64(data.getBytes("UTF-8"));
		Object object = ByteUtils.readObject(bytes);
		if(!type.isInstance(object))
		{
			throw new IllegalArgumentException(MiscUtils.getClassName(object) +
					" : " + object + " is not: " +
					type.getName());
		}
		return (T)object;
	}
	
	
	/**
	 * Returns a byte array containing the byte represenation of an <code>int</code> in
	 * big endian or network byte order.
	 * 
	 * @param value integer to convert to bytes
	 * @return <code>byte[]</code> containing the byte representation of
	 *         <code>value</code>
	 */
	public static byte[] getIntegerBytes(int value)
	{
		byte[] bytes = new byte[4];

		for( int i=0; i<bytes.length; ++i )
		{
		    int offset = (bytes.length-i-1)*8;
		    bytes[i] = (byte)((value & (0xff << offset)) >>> offset);
		}
		
		return bytes;
	}
	
	/**
	 * Returns the byte representation for a <code>boolean</code>.
	 * 
	 * @param value the <code>boolean</code> value to convert to a byte
	 * @return the <code>byte</code> form of <code>value</code>
	 */
	public static byte getBooleanByte(boolean value)
	{
		if(value)
			return 1;
		else
			return 0;
	}
	
	/**
	 * Converts the input <code>data</code> array to a hex string.
	 * 
	 * @param data the input data
	 * @return the hex version of <code>data</code>
	 */
	public static String toHexString(byte [] data)
	{
		return new BigInteger(1,data).toString(16);
	}
	
	/**
	 * Creates an integer from four bytes.
	 * 
	 * @param b1 the most significant byte
	 * @param b2 the second most significant byte
	 * @param b3 the third most significant byte
	 * @param b4 the least significant byte
	 * @return the integer
	 */
	public static int toInteger(byte b1,byte b2,byte b3,byte b4)
	{
		return ((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) |
		       ((b3 & 0xFF) << 8) | (b4 & 0xFF);
	}
	
	
	/**
	 * Creates an unsigned byte from the input signed byte.
	 * 
	 * @param b the input byte
	 * @return the unsigned variant of <code>b</code>
	 */
	public static int toUnsigned(byte b)
	{
		return b & 0xFF;
	}
	
	/**
	 * Creates an unsigned integer from <code>i</code>.
	 * 
	 * @param i the integer
	 * @return the unsigned integer
	 */
	public static long toUnsigned(int i)
	{
		return i & 0XFFFFFFFFL;
	}
	
	/**
	 * Returns whether <code>l</code> fits in the range for an unsigned integer.
	 * 
	 * @param l
	 * @return <code>true</code> if unsigned int and <code>false</code> otherwise
	 */
	public static boolean isUnsignedInt(long l)
	{
		return l >= 0 && l <= toUnsigned(0xFFFFFFFF);
	}


}
