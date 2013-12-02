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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Base test class for new <code>java.util.Collection</code> implementations.
 * 
 * @author Justin Montgomery
 * @version $Id: AbstractCollectionTest.java 1946 2010-02-26 17:55:34Z jmontgomery $
 */
public abstract class AbstractCollectionTest
{
	
	protected Collection<Integer> orig;
	private static final int NUM_ENTRIES = 16;
	
	@Before
	public void setUp()
	{
		orig = new LinkedHashSet<Integer>();
		if(!reverse())
		{
			for(int i = 0; i < NUM_ENTRIES; ++i)
				orig.add(i);
		}
		else
		{
			for(int i = NUM_ENTRIES-1; i >=0; --i)
				orig.add(i);
		}
	}
	
	protected boolean reverse() { return false; }
	
	@After
	public void tearDown()
	{
		orig = null;
	}
	
	private <T> Collection<T> createTempCollection()
	{
		return new LinkedHashSet<T>();
	}
	
	protected abstract Collection<Integer> createEmptyCollection();
	protected abstract Collection<Integer> createCollection(Collection<Integer> c);
	
	@Test
	public void testCreateEmptySet()
	{
		Collection<Integer> empty = createEmptyCollection();
		assertTrue(empty.isEmpty());
		assertEquals(0,empty.size());
	}

	
	@Test
	public void testAdd()
	{
		Collection<Integer> s = createEmptyCollection();
		Integer key = Integer.valueOf(1);
		assertFalse(s.contains(key));
		assertTrue(s.add(key));
		assertTrue(s.contains(key));
	}

	@Test
	public void testContains()
	{
		Collection<Integer> s = createEmptyCollection();
		Integer key = Integer.valueOf(1);
		assertFalse(s.contains(key));
		assertTrue(s.add(key));
		assertTrue(s.contains(key));
		
	}

	@Test
	public void testIsEmpty()
	{
		Collection<Integer> s = createEmptyCollection();
		assertTrue(s.isEmpty());
		assertTrue(s.add(Integer.valueOf(1)));
		assertFalse(s.isEmpty());
	}

	

	

	@Test
	public void testCreateSetofCollection()
	{
		Collection<Integer> s = createCollection(orig);
		assertFalse(s.isEmpty());
		assertContainsAllEntries(s);

	}
	
	private void assertContainsAllEntries(Collection<Integer> s)
	{
		for(Integer i : orig)
			assertTrue(s.contains(i));
		assertTrue(s.containsAll(orig));
	}
	
	@Test
	public void testClear()
	{
		Collection<Integer> s = createCollection(orig);
		assertFalse(s.isEmpty());
		s.clear();
		assertTrue(s.isEmpty());
		assertEquals(0,s.size());
		
		for(Integer i : orig)
			assertFalse(s.contains(i));
	}
	
	@Test
	public void testAddAll()
	{
		Collection<Integer> s = createEmptyCollection();
		s.addAll(orig);
		assertContainsAllEntries(s);
		
	}
	
	@Test
	public void testContainsAll()
	{
		testAddAll();
	}

	@Test
	public void testRemove()
	{
		Collection<Integer> s = createCollection(orig);
		Integer key = Integer.valueOf(NUM_ENTRIES/2);
		assertTrue(s.remove(key));
		assertFalse(s.contains(key));
		assertEquals(orig.size() - 1,s.size());
	}

	@Test
	public void testSize()
	{
		Collection<Integer> s = createEmptyCollection();
		assertEquals(0,s.size());
		for(int i = 0; i < NUM_ENTRIES;++i)
		{
			s.add(i);
			assertEquals(i+1,s.size());
		}
	}
	
	@Test
	public void testIterator()
	{
		Collection<Integer> s = createCollection(orig);
		Collection<Integer> added = createTempCollection();
		for(Iterator<Integer> it = s.iterator(); it.hasNext();)
			added.add(it.next());
		assertEquals(orig,added);
		
	}
	@Test
	public void testIteratorRemove()
	{
		Collection<Integer> s = createCollection(orig);
		int count = orig.size();
		for(Iterator<Integer> it = s.iterator(); it.hasNext();)
		{
			it.next();
			it.remove();
			assertEquals(--count,s.size());
		}
		assertTrue(s.isEmpty());
	}
	
	@Test
	public void testRemoveAll()
	{
		Collection<Integer> s = createCollection(orig);
		Collection<Integer> toRemove = createTempCollection();
		for(int i = 0; i < NUM_ENTRIES/2; ++i)
			toRemove.add(i);
		Collection<Integer> expected = createTempCollection();
		for(int i = NUM_ENTRIES - 1; i >= NUM_ENTRIES/2; --i)
			expected.add(i);
		s.removeAll(toRemove);
		CollectionUtils.unorderedCollectionEquals(expected,s);
	}
	
	@Test
	public void testRetainAll()
	{
		Collection<Integer> s = createCollection(orig);
		Collection<Integer> toRetain = createTempCollection();
		for(int i = 0; i < NUM_ENTRIES/2; ++i)
			toRetain.add(i);
		s.retainAll(toRetain);
		CollectionUtils.unorderedCollectionEquals(toRetain,s);
	}
	
	@Test
	public void testEquals()
	{ 
		Collection<Integer> ec1 = createEmptyCollection();
		Collection<Integer> ec2 = createEmptyCollection();
		Collection<Integer> cc1 = createCollection(orig);
		Collection<Integer> cc2 = createCollection(orig);
		
		assertEquals(ec1,ec2);
		assertEquals(ec2,ec1);
		assertEquals(cc1,cc2);
		assertEquals(cc2,cc1);
		assertFalse(ec1.equals(cc1));
		assertFalse(cc1.equals(ec1));
		
	}
	@Test
	public void testHashCode()
	{
		Collection<Integer> ec1 = createEmptyCollection();
		Collection<Integer> ec2 = createEmptyCollection();
		Collection<Integer> cc1 = createCollection(orig);
		Collection<Integer> cc2 = createCollection(orig);
		
		// equal objects have equal hash codes
		assertEquals(ec1.hashCode(),ec2.hashCode());
		assertEquals(cc1.hashCode(),cc2.hashCode());
	}

}
