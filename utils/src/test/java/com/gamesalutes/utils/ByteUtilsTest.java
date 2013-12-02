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

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id: ByteUtilsTest.java 1544 2009-06-02 21:29:28Z jmontgomery $
 */
public class ByteUtilsTest {

	/**
	 * Test method for {@link com.gamesalutes.utils.ByteUtils#toUnsigned(byte)}.
	 */
	@Test
	public void testToUnsigned() 
	{
		byte b = (byte)0xFF;
		assertEquals(0xFF,ByteUtils.toUnsigned(b));
		b = (byte)0x80;
		assertEquals(0x80,ByteUtils.toUnsigned(b));
		b = (byte)0x7F;
		assertEquals(0x7F,ByteUtils.toUnsigned(b));
	}
	
	/**
	 * Test method for {@link com.gamesalutes.utils.ByteUtils#getIntegerBytes(int)}.
	 */
	@Test
	public void testGetIntegerBytes() 
	{
		int mask = 0x7FFF800F;
		byte [] data = ByteUtils.getIntegerBytes(mask);
		assertEquals(0x7F,ByteUtils.toUnsigned(data[0]));
		assertEquals(0xFF,ByteUtils.toUnsigned(data[1]));
		assertEquals(0x80,ByteUtils.toUnsigned(data[2]));
		assertEquals(0xF,ByteUtils.toUnsigned(data[3]));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ByteUtils#toInteger(byte, byte, byte, byte)}.
	 */
	@Test
	public void testToInteger() 
	{
		byte b1 = 0x7F;
		byte b2 = (byte)0xFF;
		byte b3 = 0;
		byte b4 = 0xF;
		int actual = ByteUtils.toInteger(b1, b2, b3, b4);
		int expected = 0x7FFF000F;
		assertEquals("expected=" + Integer.toHexString(expected) + ";actual=" + Integer.toHexString(actual),
				expected,actual);
		
	}
	
	/**
	 * Test method for {@link ByteUtils#growBuffer(java.nio.ByteBuffer, int)}.
	 * 
	 */
	@Test
	public void testGrowBuffer()
	{
		int len = 10;
		ByteBuffer buf = ByteBuffer.allocate(3 * Integer.SIZE - Integer.SIZE / 2);
		buf.limit(1 * Integer.SIZE);
		List<Integer> expected = new ArrayList<Integer>();
		List<Integer> actual = new ArrayList<Integer>();
		
		for(int i = 0; i < len; ++i)
		{
			buf = ByteUtils.growBuffer(buf, (i + 1) * Integer.SIZE);
			buf.putInt(i);
			expected.add(i);
		}
		
		buf.flip();
		while(buf.hasRemaining())
			actual.add(buf.getInt());
		
		assertEquals(expected,actual);
		
	}
	
	/**
	 * Test method for {@link ByteUtils#serializationClone(Object)}.
	 * 
	 */
	@Test
	public void testSerializationClone()
		throws Exception
	{
		// test on a collection of string
		List<String> exp = Arrays.asList("Testing"," serialization"," clone!");
		List<String> act = ByteUtils.serializationClone(exp);
		assertNotSame(exp,act);
		assertEquals(exp,act);
		
	}
	
	
	/**
	 * Test method for {@link ByteUtils#serializationClone(Object)}.
	 * 
	 */
	@Test
	public void testSerializationCloneBuffer()
		throws Exception
	{
		ByteBuffer buf = ByteBuffer.allocate(64*1024);
		ByteBuffer [] bufAr = {buf};
		// test on a collection of string
		List<String> exp = Arrays.asList("Testing"," serialization"," clone!");
		List<String> act = ByteUtils.serializationClone(exp,bufAr);
		assertNotSame(exp,act);
		assertEquals(exp,act);
		assertSame(buf,bufAr[0]);
		
	}
	
	/**
	 * Test method for {@link ByteUtils#serializationClone(Object)}.
	 * 
	 */
	@Test
	public void testSerializationCloneBufferSmall()
		throws Exception
	{
		// allocate too small a buffer
		ByteBuffer buf = ByteBuffer.allocate(4);
		ByteBuffer [] bufAr = {buf};
		// test on a collection of string
		List<String> exp = Arrays.asList("Testing"," serialization"," clone!");
		List<String> act = ByteUtils.serializationClone(exp,bufAr);
		assertNotSame(exp,act);
		assertEquals(exp,act);
		assertNotSame(buf,bufAr[0]);
		
	}
	
	
	@Test
	public void testGetAndReadObjectBytes()
		throws Exception
	{
		List<String> exp = Arrays.asList("Testing"," serialization"," clone!");
		byte [] data = ByteUtils.getObjectBytes(exp);
		
		List<String> act = ByteUtils.readObject(data);
		
		assertEquals(exp,act);
	}
	@Test
	public void testGetAndReadObjectBytesBuffer()
		throws Exception
	{
		ByteBuffer buf = ByteBuffer.allocate(64*1024);
		// test on a collection of string
		List<String> exp = Arrays.asList("Testing"," serialization"," clone!");
		// size should have been large enough to accomodate
		ByteBuffer returnBuf = ByteUtils.getObjectBytes(exp, buf);
		assertSame(buf,returnBuf);
		
		List<String> act = ByteUtils.readObject(returnBuf);
		assertEquals(exp,act);
		
	}
	
	
	@Test
	public void testGetAndReadObjectBytesBufferSmall()
		throws Exception
	{
		ByteBuffer buf = ByteBuffer.allocate(4);
		// test on a collection of string
		List<String> exp = Arrays.asList("Testing"," serialization"," clone!");
		// size should have been large enough to accomodate
		ByteBuffer returnBuf = ByteUtils.getObjectBytes(exp, buf);
		assertNotSame(buf,returnBuf);
		
		List<String> act = ByteUtils.readObject(returnBuf);
		assertEquals(exp,act);
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testStringSerialization()
		throws Exception
	{
		// test on a collection of string
		List<String> exp = Arrays.asList("Testing"," serialization"," clone!");
		
		String data = ByteUtils.stringSerialize(exp);
		
		List<String> act = ByteUtils.stringDeserialize(data, List.class);
		
		assertEquals(exp,act);
	}



}
