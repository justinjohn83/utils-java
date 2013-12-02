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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test class for <code>java.util.Map</code> implementations.
 * 
 * @author Justin Montgomery
 * @version $Id: AbstractMapTest.java 1269 2009-01-14 23:45:32Z jmontgomery $
 */
public abstract class AbstractMapTest
{
	
	protected Map<Integer,Integer> orig;
	protected static final int NUM_ENTRIES = 16;
	
	
	protected final Integer getNonExistantKey()
	{
		return NUM_ENTRIES * 3;
	}
	protected final Integer getNonExistantValue()
	{
		return 0;
	}
	
	protected final Map.Entry<Integer, Integer> getExistingEntry()
	{
		return new Map.Entry<Integer, Integer>()
		{

			public Integer getKey()
			{
				return 0;
			}

			public Integer getValue() 
			{
				return NUM_ENTRIES;
			}

			public Integer setValue(Integer value) 
			{
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	@Before
	public void setUp()
	{
		orig = new HashMap<Integer,Integer>();
		for(int i = 0; i < NUM_ENTRIES; ++i)
			orig.put(i, i + NUM_ENTRIES);
		orig = Collections.unmodifiableMap(orig);
	}
	@After
	public void tearDown()
	{
		orig = null;
	}
	
	private <T> Set<T> createTempSet()
	{
		return new HashSet<T>();
	}
	
	/**
	 * Returns the full reference map used during testing
	 * 
	 * @return the reference map
	 */
	protected final Map<Integer,Integer> getReferenceMap()
	{
		return orig;
	}
	
	protected abstract Map<Integer,Integer> createEmptyMap();
	protected abstract Map<Integer,Integer> createMap(Map<Integer,Integer> m);
	
	@Test
	public void testCreateEmptyMap()
	{
		Map<Integer,Integer> empty = createEmptyMap();
		assertTrue(empty.isEmpty());
		assertEquals(0,empty.size());
	}

	
	@Test
	public void testPut()
	{
		Map<Integer,Integer> map = createEmptyMap();
		Integer key = Integer.valueOf(1);
		assertNull(map.put(key, key));
		assertTrue(map.containsKey(key));
		assertEquals(key,map.get(key));
		
		// test putting a second entry and make sure original is removed
		Integer newKey = Integer.valueOf(1);
		Integer oldValue = map.get(key);
		Integer newValue = Integer.MAX_VALUE / 2;
		Integer oldKey = map.put(newKey, newValue);
		assertEquals(key,oldKey);
		assertTrue(map.containsKey(newKey));
		assertFalse(map.containsValue(oldValue));
		assertEquals(newValue,map.get(newKey));
	}

	@Test
	public void testContainsKey()
	{
		Map<Integer,Integer> map = createMap(orig);
		assertFalse(map.containsKey(getNonExistantKey()));
		assertTrue(map.containsKey(getExistingEntry().getKey()));
		
	}

	@Test
	public void testContainsValue()
	{
		Map<Integer,Integer> map = createMap(orig);
		assertFalse(map.containsValue(getNonExistantValue()));
		assertTrue(map.containsValue(getExistingEntry().getValue()));
	}

	@Test
	public void testGet()
	{
		Map<Integer,Integer> map = createMap(orig);
		assertNull(map.get(getNonExistantKey()));
		Map.Entry<Integer, Integer> e = getExistingEntry();
		assertEquals(e.getValue(),map.get(e.getKey()));
	}

	@Test
	public void testIsEmpty()
	{
		Map<Integer,Integer> map = createEmptyMap();
		assertTrue(map.isEmpty());
		map = createMap(orig);
		assertFalse(map.isEmpty());
	}

	

	

	@Test
	public void testCreateMapofMap()
	{
		Map<Integer,Integer> map = createMap(orig);
		assertFalse(map.isEmpty());
		assertContainsAllEntries(map);

	}
	
	private void assertContainsAllEntries(Map<Integer,Integer> map)
	{
		for(Map.Entry<Integer, Integer> E : orig.entrySet())
		{
			assertTrue(map.containsKey(E.getKey()));
			assertEquals(E.getValue(),map.get(E.getKey()));
		}
	}
	
	@Test
	public void testClear()
	{
		Map<Integer,Integer> map = createMap(orig);
		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
		assertEquals(0,map.size());
		
		for(Map.Entry<Integer, Integer> E : orig.entrySet())
		{
			assertFalse(map.containsKey(E.getKey()));
			assertEquals(null,map.get(E.getKey()));
		}
		
	}
	
	@Test
	public void testPutAll()
	{
		Map<Integer,Integer> map = createEmptyMap();
		map.putAll(orig);
		assertContainsAllEntries(map);
		
	}

	@Test
	public void testRemove()
	{
		Map<Integer,Integer> map = createMap(orig);
		Integer key = Integer.valueOf(NUM_ENTRIES/2);
		map.remove(key);
		assertFalse(map.containsKey(key));
		assertNull(map.get(key));
	}

	@Test
	public void testSize()
	{
		Map<Integer,Integer> map = createEmptyMap();
		assertEquals(0,map.size());
		map = createMap(orig);
		assertEquals(orig.size(),map.size());
	}

	@Test
	public void testValues()
	{
		Map<Integer,Integer> map = createMap(orig);
		Set<Integer> addedSet=  createTempSet();
		Set<Integer> origSet = createTempSet();
		
		origSet.addAll(orig.values());
		
		Collection<Integer> values = map.values();
		for(Integer i : values)
			addedSet.add(i);
		
		assertEquals(origSet,addedSet);
		

			
	}
	
	@Test
	public void testValuesCollectionRemove()
	{
		Map<Integer,Integer> map = createMap(orig);
		Collection<Integer> values = map.values();
		
		// test calling remove and clear on the values
		Integer value = values.iterator().next();
		values.remove(value);
		assertFalse(values.contains(value));
		assertFalse(map.containsValue(value));
		
		values.clear();
		assertTrue(values.isEmpty());
		assertTrue(map.isEmpty());
	}
	@Test
	public void testRemoveValues()
	{
		Map<Integer,Integer> map = createMap(orig);
		int size = map.size();
		int i = 0;
		Collection<Integer> values = map.values();
		for(Iterator<Integer> it = values.iterator(); it.hasNext();)
		{
			++i;
			Integer value = it.next();
			it.remove();
			// make sure size decreases in map
			assertEquals(size - i,map.size());
			// make sure size decreases in Collection
			assertEquals(size - i,values.size());
			// make sure map removed value
			assertFalse(map.containsValue(value));
			// make sure Collection removed value
			assertFalse(values.contains(value));
			
		}
	}
	
	@Test
	public void testKeySet()
	{
		Map<Integer,Integer> map = createMap(orig);
		Set<Integer> addedSet=  createTempSet();
		Set<Integer> origSet = createTempSet();
		
		origSet.addAll(orig.keySet());
		
		Collection<Integer> keys = map.keySet();
		for(Integer i : keys)
			addedSet.add(i);
		
		assertEquals(origSet,addedSet);
		

		
	}
	
	@Test
	public void testKeySetCollectionRemove()
	{
		Map<Integer,Integer> map = createMap(orig);
		Collection<Integer> keys = map.keySet();
		
		// test calling remove and clear on the key set
		// test calling remove and clear on the values
		Integer key = keys.iterator().next();
		keys.remove(key);
		assertFalse(keys.contains(key));
		assertFalse(map.containsKey(key));
		
		keys.clear();
		assertTrue(keys.isEmpty());
		assertTrue(map.isEmpty());
	}
	
	@Test
	public void testRemoveKeySet()
	{
		Map<Integer,Integer> map = createMap(orig);
		int size = map.size();
		int i = 0;
		Collection<Integer> keys = map.keySet();
		for(Iterator<Integer> it = keys.iterator(); it.hasNext();)
		{
			++i;
			Integer key = it.next();
			it.remove();
			// make sure size decreases in map
			assertEquals(size - i,map.size());
			// make sure size decreases in Collection
			assertEquals(size - i,keys.size());
			// make sure map removed value
			assertFalse(map.containsKey(key));
			assertNull(map.get(key));
			// make sure Collection removed value
			assertFalse(keys.contains(key));
			
		}
	}
	
	@Test
	public void testModifyEntryValue()
	{
		Map<Integer,Integer> map = createMap(orig);
		
		for(Map.Entry<Integer,Integer> e : map.entrySet())
		{
			Integer oldValue = e.getValue();
			Integer newValue = oldValue * 2;
			e.setValue(newValue);
			
			// make sure value changed
			assertTrue(map.containsValue(newValue));
			assertFalse(map.containsValue(oldValue));
			assertEquals(newValue,map.get(e.getKey()));
			
		}
	}
	
	@Test
	public void testEntrySet()
	{
		Map<Integer,Integer> map = createMap(orig);
		Set<Map.Entry<Integer, Integer>> addedSet = createTempSet();
		Set<Map.Entry<Integer, Integer>> origSet = createTempSet();
		
		origSet.addAll(orig.entrySet());
		
		Collection<Map.Entry<Integer, Integer>> entries = map.entrySet();
		for(Map.Entry<Integer, Integer> E : entries)
			addedSet.add(E);
		
		assertEquals(origSet.size(),addedSet.size());
		assertEquals(origSet,addedSet);
		
		

	}
	
	@Test
	public void testEntrySetCollectionRemove()
	{
		Map<Integer,Integer> map = createMap(orig);
		Collection<Map.Entry<Integer, Integer>> entries = map.entrySet();
		
		// test calling remove and clear on the entry set
		// test calling remove and clear on the entries
		Map.Entry<Integer, Integer> e = entries.iterator().next();
		entries.remove(e);
		assertFalse(entries.contains(e));
		assertFalse(map.containsKey(e.getKey()));
		assertFalse(map.containsValue(e.getValue()));
		
		entries.clear();
		assertTrue(entries.isEmpty());
		assertTrue(map.isEmpty());
	}
	
	@Test
	public void testRemoveEntrySet()
	{
		Map<Integer,Integer> map = createMap(orig);
		int size = map.size();
		int i = 0;
		Collection<Map.Entry<Integer,Integer>> entries = map.entrySet();
		for(Iterator<Map.Entry<Integer, Integer>> it = entries.iterator(); it.hasNext();)
		{
			++i;
			Map.Entry<Integer, Integer> entry = it.next();
			it.remove();
			// make sure size decreases in map
			assertEquals(size - i,map.size());
			// make sure size decreases in Collection
			assertEquals(size - i,entries.size());
			// make sure map removed value
			assertFalse(map.containsKey(entry.getKey()));
			assertFalse(map.containsValue(entry.getValue()));
			
			assertNull(map.get(entry.getKey()));
			
			// make sure Collection removed value
			assertFalse(entries.contains(entry));
			
		}
	}
	
	@Test
	public void testEquals()
	{
		Map<Integer,Integer> em1 = createEmptyMap();
		Map<Integer,Integer> em2 = createEmptyMap();
		Map<Integer,Integer> mm1 = createMap(orig);
		Map<Integer,Integer> mm2 = createMap(orig);
		
		assertEquals(em1,em2);
		assertEquals(em2,em1);
		assertEquals(mm1,mm2);
		assertEquals(mm2,mm1);
		assertFalse(em1.equals(mm1));
		assertFalse(mm1.equals(em1));
	}
	@Test
	public void testHashCode()
	{
		Map<Integer,Integer> em1 = createEmptyMap();
		Map<Integer,Integer> em2 = createEmptyMap();
		Map<Integer,Integer> mm1 = createMap(orig);
		Map<Integer,Integer> mm2 = createMap(orig);
		
		// equal objects have equal hashcodes
		assertEquals(em1.hashCode(),em2.hashCode());
		assertEquals(mm1.hashCode(),mm2.hashCode());
	}

}
