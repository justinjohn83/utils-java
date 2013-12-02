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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gamesalutes.utils.UtilsTestSuite;


import static com.gamesalutes.utils.FileProxyRetrieverTest.Type.*;
/**
 * @author Justin Montgomery
 * @version $Id: FileProxyRetrieverTest.java 1755 2009-11-06 21:55:44Z jmontgomery $
 */
public class FileProxyRetrieverTest 
{
	protected ProxyRetriever<String,String> retriever;
	
	enum Type {EQUAL,NOT_EQUAL,ABSENT}
	
	private static final Map<String,String> INVALID_ENTRIES_MAP;
	
	static
	{
		Map<String,String> m = new LinkedHashMap<String,String>();
		m.put("NOT_A_KEY", "NOT_A_VALUE");
		INVALID_ENTRIES_MAP = Collections.unmodifiableMap(m);
	}
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
		retriever = new FileProxyRetriever<String,String>(
				UtilsTestSuite.getTempDirectory(),"file_proxy_test");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception 
	{
		if(retriever != null)
		{
			retriever.dispose();
			retriever = null;
		}
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.FileProxyRetriever#put(java.lang.Object, java.io.Serializable)}.
	 */
	@Test
	public void testPutAndLookup() 
	{
		Map<String,String> m = addToRetriever(null,1,5);
		assertMap(m,Type.EQUAL);
		assertMap(INVALID_ENTRIES_MAP,Type.ABSENT);
	}
	
	
	private void assertMap(Map<String,String> m,Type t)
	{
		if(t == null) throw new NullPointerException("t");
		
		for(Map.Entry<String, String> e : m.entrySet())
		{
			Pair<Boolean,String> result = retriever.lookup(e.getKey());
			if(t == Type.EQUAL)
			{
				assertTrue(result.first);
				assertEquals(e.getValue(),result.second);
			}
			else if(t == Type.NOT_EQUAL)
			{
				if(result.first)
				{
					assertFalse("value=" + result.second,
							MiscUtils.safeEquals(e.getValue(), 
									result.second));
				}
			}
			else if(t == Type.ABSENT)
			{
				assertFalse(result.first);
			}
		}
	}
	
	@Test
	public void testPutReplaceAndLookup()
	{
		Map<String,String> m = addToRetriever(null,1,5);
		Map<String,String> newM = addToRetriever(m,1,2);
		
		assertMap(newM,EQUAL);
		m.keySet().retainAll(Arrays.asList(1,2));
		assertMap(m,NOT_EQUAL);
			
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.FileProxyRetriever#clear()}.
	 */
	@Test
	public void testClear() 
	{
		Map<String,String> m = addToRetriever(null,1,5);
		retriever.clear();
		assertMap(m,ABSENT);
	}

	private Map<String,String> addToRetriever(
			Map<String,String> map,int start,int end)
	{
		Map<String,String> m;
		if(map == null)
		{
			m = new LinkedHashMap<String,String>();
			map = Collections.<String,String>emptyMap();
		}
		else
		{
			m = new LinkedHashMap<String,String>(map);
		}
		
		
		for(int i = start; i <=end; ++i)
		{
			String key = "key" + i;
			String value = map.get(key);
			if(value != null)
				value += "x";
			else
				value = "value" + i;
			m.put(key, value);
			
			retriever.put(key,value);
			
		}
		
		return m;
	}
	
	private Map<String,String> removeFromRetriever(
			Map<String,String> origMap,int start,int end)
	{
		Map<String,String> m = new LinkedHashMap<String,String>(origMap);
		
		Iterator<Map.Entry<String, String>> it = 
			m.entrySet().iterator();
		
		for(int i = start; i <= end && it.hasNext(); ++i)
		{
			Map.Entry<String, String> e = it.next();
			retriever.remove(e.getKey());
			it.remove();
		}
		
		return m;
	}
	/**
	 * Test method for {@link com.gamesalutes.utils.FileProxyRetriever#remove(java.lang.Object)}.
	 */
	@Test
	public void testRemoveFirst() 
	{
		Map<String,String> m = addToRetriever(null,1,5);
		Map<String,String> newM = removeFromRetriever(m,1,1);
		
		assertMap(newM,EQUAL);
		m.keySet().removeAll(newM.keySet());
		assertMap(m,ABSENT);
		
	}
	
	@Test
	public void testRemoveMiddle()
	{
		Map<String,String> m = addToRetriever(null,1,6);
		Map<String,String> newM = removeFromRetriever(m,3,3);
		
		assertMap(newM,EQUAL);
		m.keySet().removeAll(newM.keySet());
		assertMap(m,ABSENT);
	}
	@Test
	public void testRemoveEnd()
	{
		Map<String,String> m = addToRetriever(null,1,6);
		Map<String,String> newM = removeFromRetriever(m,6,6);
		
		assertMap(newM,EQUAL);
		m.keySet().removeAll(newM.keySet());
		assertMap(m,ABSENT);
		
	}
	
	@Test
	public void testRemoveMixed()
	{
		Map<String,String> m = addToRetriever(null,1,6);
		Map<String,String> newM = removeFromRetriever(m,2,3);
		newM = removeFromRetriever(newM,5,5);
		
		assertMap(newM,EQUAL);
		m.keySet().removeAll(newM.keySet());
		assertMap(m,ABSENT);
		
	}
	
	@Test
	public void testMixedPutAndRemove()
	{
		Map<String,String> allM = addToRetriever(null,1,6);
		Map<String,String> currM = removeFromRetriever(allM,3,3);
		
		currM = addToRetriever(currM,7,8);
		allM.putAll(currM);
		
		currM = removeFromRetriever(currM,2,3);
		allM.putAll(currM);
		
		currM = addToRetriever(currM,1,2);
		allM.putAll(currM);
		
		currM = removeFromRetriever(currM,7,7);
		allM.putAll(currM);
		
		currM = addToRetriever(currM,9,10);
		allM.putAll(currM);
		
		currM = removeFromRetriever(currM,10,10);
		allM.putAll(currM);
		
		currM = removeFromRetriever(currM,1,1);
		allM.putAll(currM);
		
		assertMap(currM,EQUAL);
		
		allM.keySet().removeAll(currM.keySet());
		assertMap(allM,ABSENT);
		
		
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.FileProxyRetriever#update(java.lang.Object, java.io.Serializable)}.
	 */
	@Test
	public void testUpdate() 
	{
		String key = "key";
		String value = "value";
		
		retriever.put(key,value);
		retriever.addCacheEntry(key,value);
		
		value = "value2";
		retriever.update(key, value);
		
		Pair<Boolean,String> result = retriever.lookup(key);
		assertTrue(result.first);
		assertEquals(value,result.second);
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.FileProxyRetriever#addCacheEntry(java.lang.Object, java.io.Serializable)}.
	 */
	@Test
	public void testAddCacheEntry()
	{
		String key = "key";
		String value = "value";
		
		retriever.put(key,value);
		retriever.addCacheEntry(key,value);
		Pair<Boolean,String> result = retriever.lookup(key);
		assertTrue(result.first);
		assertEquals(value,result.second);
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.FileProxyRetriever#removeCacheEntry(java.lang.Object)}.
	 */
	@Test
	public void testRemoveCacheEntry() 
	{
		String key = "key";
		String value = "value";
		
		retriever.put(key,value);
		retriever.addCacheEntry(key,value);
		retriever.removeCacheEntry(key);
		
		// make sure entry still exists in data
		Pair<Boolean,String> result = retriever.lookup(key);
		assertTrue(result.first);
		assertEquals(value,result.second);
	}

}
