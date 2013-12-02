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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public class NormalizedMapTest
{

	private class _Normalizer implements NormalizedMap.KeyNormalizer<String>
	{

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.NormalizedMap.KeyNormalizer#normalize(java.lang.Object)
		 */
		public String normalize(String key)
		{
			return key != null ? key.toLowerCase() : null;
		}
		
	}
	
	private Map<String,String> map;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		map = new NormalizedMap<String,String>(new HashMap<String,String>(),String.class,new _Normalizer());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		map = null;
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#size()}.
	 */
	@Test
	public void testSize()
	{
		assertEquals(0,map.size());
		map.put("test", "test");
		assertEquals(1,map.size());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#isEmpty()}.
	 */
	@Test
	public void testIsEmpty()
	{
		assertTrue(map.isEmpty());
		map.put("test", "test");
		assertFalse(map.isEmpty());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#clear()}.
	 */
	@Test
	public void testClear()
	{
		map.put("test", "test");
		map.clear();
		assertTrue(map.isEmpty());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#clone()}.
	 */
	@Test
	public void testClone()
	{
		map.put("test", "test");
		Map<String,String> m2 = ((NormalizedMap)map).clone();
		m2.clear();
		assertTrue(m2.isEmpty());
		assertFalse(map.isEmpty());
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#containsKey(java.lang.Object)}.
	 */
	@Test
	public void testContainsKeyObject()
	{
		map.put("test", "test");
		assertTrue(map.containsKey("test"));
		assertTrue(map.containsKey("test".toUpperCase()));
		assertFalse(map.containsKey("nothing"));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#containsValue(java.lang.Object)}.
	 */
	@Test
	public void testContainsValueObject()
	{
		map.put("test", "test");
		assertTrue(map.containsValue("test"));
		assertFalse(map.containsValue("test".toUpperCase()));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#entrySet()}.
	 */
	@Test
	public void testEntrySet()
	{
		assertNotNull(map.entrySet());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#get(java.lang.Object)}.
	 */
	@Test
	public void testGetObject()
	{
		map.put("test", "test");
		assertEquals("test",map.get("test"));
		assertEquals("test",map.get("test".toUpperCase()));
		assertNull(map.get("nothing"));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#keySet()}.
	 */
	@Test
	public void testKeySet()
	{
		assertNotNull(map.keySet());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#put(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testPutKV()
	{
		map.put("test", "test");
		
		assertEquals(1,map.size());
		assertTrue(map.containsKey("test"));
		assertTrue(map.containsKey("test".toUpperCase()));
		assertFalse(map.containsKey("nothing"));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#putAll(java.util.Map)}.
	 */
	@Test
	public void testPutAllMapOfQextendsKQextendsV()
	{
		Map<String,String> strings = new HashMap<String,String>();
		strings.put("one", "one");
		strings.put("one".toUpperCase(), "one");
		strings.put("two", "two");
		
		map.putAll(strings);
		
		assertEquals(2,map.size());
		assertEquals(new HashSet<String>(Arrays.asList("one","two")),map.keySet());
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#remove(java.lang.Object)}.
	 */
	@Test
	public void testRemoveObject()
	{
		map.put("test", "test");
		
		assertNotNull(map.remove("test".toUpperCase()));
		assertEquals(0,map.size());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NormalizedMap#values()}.
	 */
	@Test
	public void testValues()
	{
		assertNotNull(map.values());
	}

	/**
	 * Test method for {@link java.util.AbstractMap#hashCode()}.
	 */
	@Test
	public void testHashCode()
	{
		map.put("test", "test");
		Map<String,String> m2 = ((NormalizedMap)map).clone();
		assertEquals(map.hashCode(),m2.hashCode());
	}

	/**
	 * Test method for {@link java.util.AbstractMap#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals()
	{
		map.put("test", "test");
		Map<String,String> m2 = ((NormalizedMap)map).clone();
		assertEquals(map,m2);
		
		m2.clear();
		
		assertFalse(map.equals(m2));
	}
	
	@Test
	public void testConstructor()
	{
		Map<String,String> m = new HashMap<String,String>();
		m.put("one", "one");
		m.put("one".toUpperCase(), "one");
		m.put("TWO","two");
		
		NormalizedMap<String,String> nm = new NormalizedMap<String,String>(
				m,String.class,new _Normalizer());
		
		assertEquals(2,nm.size());
		assertEquals(new HashSet<String>(Arrays.asList("one","two")),nm.keySet());
	}

}
