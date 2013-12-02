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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id: LruMapTest.java 1498 2009-05-19 19:14:23Z jmontgomery $
 */
public class LruMapTest {

	/**
	 * Test method for {@link com.gamesalutes.utils.LruMap#LruMap(java.util.Map)}.
	 */
	@Test
	public void testLRUMapMapOfQextendsKQextendsV() 
	{
		Map<Integer,Integer> orig = new HashMap<Integer,Integer>();
		for(int i = 0; i < 10; ++i)
			orig.put(i,i);
		LruMap<Integer,Integer> cache = new LruMap<Integer,Integer>(orig);
		assertEquals(orig.size(),cache.getMaxSize());
		assertEquals(orig.keySet(),cache.keySet());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.LruMap#get(Object)}
	 */
	@Test
	public void testGet() 
	{
		final int max = 10;
		LruMap<Integer,Integer> cache = new LruMap<Integer,Integer>(max);
		for(int i = 0; i < max; ++i)
			cache.put(i,i);
		// calling get should move this entry to end of cache
		cache.get(0);
		assertEquals(Integer.valueOf(1),cache.keySet().iterator().next());
		for(Iterator<Integer> it = cache.keySet().iterator(); it.hasNext();)
		{
			Integer elm = it.next();
			if(!it.hasNext())
				assertEquals(Integer.valueOf(0),elm);
		}
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.LruMap#put(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testPut() 
	{
		final int max = 10;
		LruMap<Integer,Integer> cache = new LruMap<Integer,Integer>(max);
		for(int i = 0; i < max; ++i)
			cache.put(i,i);
		// assert that adding an entry when the cache is full removes the eldest entry
		cache.put(max, max);
		assertTrue(cache.containsKey(max));
		assertFalse(cache.containsKey(0));
		assertEquals(max,cache.size());
	}

}
