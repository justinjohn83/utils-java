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

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id: ByteBufferChannelTest.java 1291 2009-02-02 21:38:01Z jmontgomery $
 */
public class ByteBufferChannelTest 
{
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ByteBufferChannel#isOpen()}.
	 */
	@Test
	public void testIsOpen() 
	{
		ByteBuffer buf = ByteBuffer.allocate(4);
		ByteBufferChannel ch = new ByteBufferChannel(buf);
		assertTrue(ch.isOpen());
	}

	
	/**
	 * Test method for {@link com.gamesalutes.utils.ByteBufferChannel#read(java.nio.ByteBuffer)}.
	 */
	@Test
	public void testRead() 
	{
		int len = 16;
		
		ByteBuffer buf = ByteBuffer.allocate(len * Integer.SIZE);
		List<Integer> expected = new ArrayList<Integer>();
		List<Integer> actual = new ArrayList<Integer>();
		
		for(int i = 0; i < len; ++i)
		{
			buf.putInt(i);
			expected.add(i);
		}
		buf.flip();
		// initialize with ready to read buffer
		ByteBufferChannel ch = new ByteBufferChannel(buf);
		
		// read it in two passes
		buf = ByteBuffer.allocate(len * Integer.SIZE);
		buf.limit(len / 2 * Integer.SIZE);
		ch.read(buf);
		// read the rest
		buf.limit(buf.capacity());
		ch.read(buf);
		
		buf.flip();
		while(buf.hasRemaining())
			actual.add(buf.getInt());
		
		assertEquals(expected,actual);
			
		
	}
	/**
	 * Test method for {@link com.gamesalutes.utils.ByteBufferChannel#write(java.nio.ByteBuffer)}.
	 */
	@Test
	public void testWrite() 
	{
		int len = 16;
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.limit(7);
		
		ByteBufferChannel ch = new ByteBufferChannel(buf);
		
		List<Integer> expected = new ArrayList<Integer>();
		List<Integer> actual = new ArrayList<Integer>();
		
		// prepare the data
		buf = ByteBuffer.allocate(len * Integer.SIZE);
		for(int i = 0; i < len; ++i)
		{
			buf.putInt(i);
			expected.add(i);
		}
		
		buf.flip();
		
		// write the data
		ch.write(buf);
		
		// flip the channel
		
		ch.flip();
		
		buf = ch.getByteBuffer();
		
		while(buf.hasRemaining())
			actual.add(buf.getInt());
		
		assertEquals(expected,actual);
		
		
		
	}
	


}
