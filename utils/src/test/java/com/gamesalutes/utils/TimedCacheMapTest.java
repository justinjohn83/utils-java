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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TimedCacheMapTest {

	
	@Test
	public void testTimeout() throws Exception {
		
		Map<Integer,Integer> m = new TimedCacheMap<Integer,Integer>(new HashMap<Integer,Integer>(),10);
		m.put(1, 1);
		Thread.sleep(1000);
		assertFalse(m.containsKey(1));
		assertNull(m.get(1));
	}
	
	@Test
	public void testNotTimeout() {
		Map<Integer,Integer> m = new TimedCacheMap<Integer,Integer>(new HashMap<Integer,Integer>(),1000);
		m.put(1, 1);
		assertTrue(m.containsKey(1));
		assertEquals(Integer.valueOf(1),m.get(1));
	}
	
	@Test
	public void testNoTimeout() throws Exception {
		Map<Integer,Integer> m = new TimedCacheMap<Integer,Integer>(new HashMap<Integer,Integer>(),-1);
		m.put(1, 1);
		Thread.sleep(200);
		assertTrue(m.containsKey(1));
		assertEquals(Integer.valueOf(1),m.get(1));
	}
	
	@Test
	public void testPutExisting() {
		
		Map<Integer,Integer> em = new HashMap<Integer,Integer>();
		em.put(1, 1);
		
		Map<Integer,Integer> m = new TimedCacheMap<Integer,Integer>(em,1000);
		assertTrue(m.containsKey(1));
		assertEquals(Integer.valueOf(1),m.get(1));
	}
}
