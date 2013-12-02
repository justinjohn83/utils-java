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

import java.util.Map;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Base test class for testing {@link BidiMap BidiMap} implementations.
 * 
 * @author Justin Montgomery
 * @version $Id: AbstractBidiMapTest.java 1498 2009-05-19 19:14:23Z jmontgomery $
 */
public abstract class AbstractBidiMapTest extends AbstractMapTest 
{

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractMapTest#createEmptyMap()
	 */
	@Override
	protected abstract BidiMap<Integer, Integer> createEmptyMap();

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractMapTest#createMap(java.util.Map)
	 */
	@Override
	protected abstract BidiMap<Integer, Integer> createMap(Map<Integer, Integer> m);
	
	
	/**
	 * Test case for {@link BidiMap#getKey(Object)}.
	 * 
	 */
	@Test
	public void testGetKey()
	{
		BidiMap<Integer,Integer> map = createMap(orig);
		
		assertNull(map.getKey(getNonExistantValue()));
		Map.Entry<Integer, Integer> e = getExistingEntry();
		assertEquals(e.getKey(),map.getKey(e.getValue()));
	}
	
	/**
	 * Test case for {@link BidiMap#removeValue(Object)}.
	 * 
	 */
	@Test
	public void testRemoveValue()
	{
		BidiMap<Integer,Integer> map = createEmptyMap();
		map.put(1, 2);
		map.put(3, 4);
		map.put(5, 6);
		
		assertEquals(Integer.valueOf(3),map.removeValue(4));
		assertFalse(map.containsValue(4));
		assertFalse(map.containsKey(3));
		assertEquals(2,map.size());
	}
	
	@Test
	public void testIdentity()
	{
		Map<Integer,Integer> m = createEmptyMap();
		// identity
		m.put(0, 0);
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(0));
		assertEquals(1,m.size());
		
	}
	@Test
	public void testOneToOneRemove1()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(1, 0);
		
		assertEquals(2,m.size());
		assertTrue(m.containsKey(0));
		assertTrue(m.containsKey(1));
		assertTrue(m.containsValue(1));
		assertTrue(m.containsValue(0));
		
		
	}
	@Test
	public void testOneToOneRemoveIdentity()
	{
		Map<Integer,Integer> m = createEmptyMap();
		// identity
		m.put(0, 0);
		m.put(1, 0);
		
		assertEquals(1,m.size());
		assertFalse(m.containsKey(0));
		assertTrue(m.containsKey(1));
		assertTrue(m.containsValue(0));
	}
	
	@Test
	public void testOneToOneAddIdentity()
	{
		Map<Integer,Integer> m = createEmptyMap();
		// identity
		m.put(1, 0);
		m.put(0, 0);
		
		assertEquals(1,m.size());
		assertTrue(m.containsKey(0));
		assertFalse(m.containsKey(1));
		assertTrue(m.containsValue(0));
	}
	
	@Test
	public void testOneToOneRemove2()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(1, 0);
		m.put(2, 3);
		m.put(0, 2);
		
		assertEquals(3,m.size());
		
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(2));
		assertTrue(m.containsKey(1));
		assertTrue(m.containsKey(2));
		assertTrue(m.containsValue(3));
		assertTrue(m.containsValue(0));
	}
	
	@Test
	public void testOneToOneRemove2_2()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(2, 3);
		m.put(0, 2);
		
		assertEquals(2,m.size());
		
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(2));
		assertFalse(m.containsValue(1));
		assertTrue(m.containsKey(2));
		assertTrue(m.containsValue(3));
	}
	
	@Test
	public void testOneToOneRemove1_2()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(0, 2);
		assertEquals(1,m.size());
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(2));
		assertFalse(m.containsValue(1));
	}
	
	@Test
	public void testOneToOneRemoval3()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(1, 2);
		assertEquals(2,m.size());
		assertTrue(m.containsKey(1));
		assertTrue(m.containsValue(2));
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(1));
	}
	@Test
	public void testOneToOneRemoval4()
	{
		BidiMap<Integer,Integer> m = (BidiMap<Integer,Integer>)createEmptyMap();
		Integer origKey = new Integer(1);
		Integer origValue = new Integer(2);
		Integer newKey = new Integer(origKey);
		Integer newValue = new Integer(origValue);
		
		m.put(origKey,origValue);
		m.put(newKey, newValue);
		assertEquals(1,m.size());
		assertSame(newValue,m.get(newKey));
		assertSame(newKey,m.getKey(newValue));
	}
	
	@Test
	public void testOneToOneRemoval5()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(2, 3);
		m.put(1, 3);
		
		assertEquals(2,m.size());
		assertTrue(m.containsKey(1));
		assertTrue(m.containsValue(3));
		assertTrue(m.containsKey(0));
		assertFalse(m.containsKey(2));
		
	}
	
	@Test
	public void testOneToOneRemoval6()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(2, 3);
		m.put(0, 2);
		assertEquals(2,m.size());
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(2));
		assertFalse(m.containsValue(1));
		assertTrue(m.containsValue(3));
		
	}
	
	@Test
	public void testOneToOneRemoval7()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(2, 3);
		m.put(0, 3);
		assertEquals(1,m.size());
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(3));
		assertFalse(m.containsValue(1));
		assertFalse(m.containsKey(2));
	}
	
	@Test
	public void testOneToOneRemoval8()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(3, 2);
		m.put(2, 3);
		m.put(2, 0);
		
		assertEquals(3,m.size());
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(1));
		assertFalse(m.containsValue(3));
		assertTrue(m.containsKey(3));
		assertTrue(m.containsValue(2));
		assertTrue(m.containsKey(2));
		assertTrue(m.containsValue(0));
	}
	
	@Test
	public void testOneToOneRemoval9()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(1, 0);
		m.put(2, 3);
		m.put(3, 2);
		m.put(0, 2);
		
		assertEquals(3,m.size());
		assertTrue(m.containsKey(1));
		assertTrue(m.containsValue(0));
		assertTrue(m.containsKey(2));
		assertTrue(m.containsValue(3));
		assertFalse(m.containsKey(3));
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(2));
	}
	
	@Test
	public void testOneToOneRemoval10()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(0, 1);
		m.put(1 ,2);
		m.put(2, 0);
		m.put(3, 0);
		
		assertEquals(3,m.size());
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(1));
		assertTrue(m.containsKey(1));
		assertTrue(m.containsValue(2));
		assertFalse(m.containsKey(2));
		assertTrue(m.containsKey(3));
		assertTrue(m.containsValue(0));
		
		
	}
	
	@Test
	public void testOneToOneRemoval11()
	{
		Map<Integer,Integer> m = createEmptyMap();
		m.put(1, 0);
		m.put(2 ,1);
		m.put(0, 2);
		m.put(0, 3);
		
		assertEquals(3,m.size());
		assertTrue(m.containsKey(1));
		assertTrue(m.containsValue(0));
		assertTrue(m.containsKey(2));
		assertTrue(m.containsValue(1));
		assertFalse(m.containsValue(2));
		assertTrue(m.containsKey(0));
		assertTrue(m.containsValue(3));
	}
}
