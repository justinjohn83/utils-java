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
package com.gamesalutes.utils.html;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.nio.CharBuffer;

import org.junit.Test;

public class CharBufferListTest {

	
	@Test
	public void testAppendSingle() throws Exception {
		
		CharBufferList list = new CharBufferList(16);
		
		String s = "test";
		CharBuffer data = CharBuffer.wrap(s);
		
		list.add(data);
		
		StringWriter actual = new StringWriter();
		
		list.write(actual);
		
		assertEquals(s,actual.toString());
		
	}
	
	@Test
	public void testAppendSingleMultiFill() throws Exception {
		CharBufferList list = new CharBufferList(4);
		
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < 25; ++i) {
			s.append('a' + (i % 26));
		}
		
		CharBuffer data = CharBuffer.wrap(s);
		list.add(data);
		
		StringWriter actual = new StringWriter();
		list.write(actual);
		
		assertEquals(s.toString(),actual.toString());
	}
	
	@Test
	public void testMultiAppend() throws Exception {
		CharBufferList list = new CharBufferList(16);
		
		String segment = "Hello";
		
		StringWriter expected = new StringWriter();
		StringWriter actual = new StringWriter();
		
		for(int i = 0; i < 20; ++i) {
			expected.append(segment);
			list.add(CharBuffer.wrap(segment));
		}
		
		list.write(actual);
		
		assertEquals(expected.toString(),actual.toString());
		
	}
	
	@Test
	public void testClear() throws Exception {
		CharBufferList list = new CharBufferList(16);
		
		String segment = "Hello";
		
		StringWriter expected = new StringWriter();
		StringWriter actual = new StringWriter();
		
		for(int i = 0; i < 20; ++i) {
			expected.append(segment);
			list.add(CharBuffer.wrap(segment));
		}
		
		list.write(actual);
		
		assertEquals(expected.toString(),actual.toString());
		
		// test that data was cleared out after writing
		assertTrue(list.isEmpty());
		
		// test re-writing some data
		actual = new StringWriter();
		expected = new StringWriter();
		
		for(int i = 0; i < 25; ++i) {
			list.add(CharBuffer.wrap(segment));
			expected.append(segment);
		}
		
		list.write(actual);
		
		assertEquals(expected.toString(),actual.toString());
	}
	
	@Test
	public void testContains() {
		CharBufferList list = new CharBufferList(8);
		String s = "Jack and Jill ran up the hill and Tom came running after them";
		
		list.add(CharBuffer.wrap(s));
		
		assertTrue(list.contains("Jack"));
		assertTrue(list.contains("Jill"));
		assertTrue(list.contains("running"));
		assertTrue(list.contains(" came "));
		assertFalse(list.contains("john"));
		
		assertTrue(list.caseInsensitiveContains("Jack".toUpperCase()));
		assertTrue(list.caseInsensitiveContains("Jill".toUpperCase()));
		assertTrue(list.caseInsensitiveContains("running".toUpperCase()));
		assertTrue(list.caseInsensitiveContains(" came ".toUpperCase()));
		assertFalse(list.caseInsensitiveContains("john".toUpperCase()));
		
		
		
		
	}
	
	@Test
	public void testLengthSingleBuffer() {
		CharBufferList list = new CharBufferList(16);
		String s = "Justin";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		assertEquals(s.length(),list.length());
	}
	
	@Test
	public void testLengthMultiBuffer() {
		CharBufferList list = new CharBufferList(5);
		String s = "Good morning America.  Today is Wednesday!";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		assertEquals(s.length(),list.length());

	}
	@Test
	public void testCharAtSingleBuffer() {
		CharBufferList list = new CharBufferList(16);
		String s = "Justin";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		int len = s.length();
		
		assertEquals(s.charAt(0),list.charAt(0));
		assertEquals(s.charAt(len-1),list.charAt(len-1));
		
		StringBuilder actual = new StringBuilder();
		for(int i = 0; i < len; ++i) {
			actual.append(list.charAt(i));
		}
		
		assertEquals(s,actual.toString());
	}
	
	@Test
	public void testCharAtMultiBuffer() {
		CharBufferList list = new CharBufferList(5);
		String s = "Good morning America.  Today is Wednesday!";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		
		int len = s.length();
		
		assertEquals(s.charAt(0),list.charAt(0));
		assertEquals(s.charAt(len-1),list.charAt(len-1));
		
		StringBuilder actual = new StringBuilder();
		for(int i = 0; i < len; ++i) {
			actual.append(list.charAt(i));
		}
		
		assertEquals(s,actual.toString());

	}
	
	
	
	@Test
	public void testToStringSingleBuffer() {
		CharBufferList list = new CharBufferList(16);
		String s = "Justin";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		assertEquals(s,list.toString());
	}
	
	@Test
	public void testToStringMultiBuffer() {
		CharBufferList list = new CharBufferList(5);
		String s = "Good morning America.  Today is Wednesday!";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		assertEquals(s,list.toString());

	}
	
	@Test
	public void testSubSequenceSingleBuffer() {
		CharBufferList list = new CharBufferList(16);
		String s = "Justin";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		assertEquals(s.subSequence(2, 6),list.subSequence(2,6).toString());
		assertEquals(s.subSequence(0, 4),list.subSequence(0,4).toString());


	}
	@Test
	public void testSubSequenceMultiBuffer() {
		CharBufferList list = new CharBufferList(5);
		String s = "Good morning America.  Today is Wednesday!";
		
		list.add(CharBuffer.wrap(s));
		list.finish();
		
		assertEquals(s.subSequence(0, 4),list.subSequence(0,4).toString());
		assertEquals(s.subSequence(1, 10),list.subSequence(1,10).toString());
		assertEquals(s.subSequence(7, 24),list.subSequence(7,24).toString());


		assertEquals(s.subSequence(0, s.length()),list.subSequence(0,s.length()).toString());


	}
}
