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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * @author Justin Montgomery
 * @version $Id: CollectionUtilsTest.java 1950 2010-03-01 19:44:58Z jmontgomery $
 */
public class CollectionUtilsTest {

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
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#containsSome(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	public void testContainsSome() 
	{
		Collection<Integer> source, target;
		
		source = Arrays.asList(1,2,3,4,5);
		target = Arrays.asList(1,2);
		
		// contains some
		assertTrue(CollectionUtils.containsSome(source,target));
		
		target = Arrays.asList(6,7,8,9);
		
		// does not contain some
		assertFalse(CollectionUtils.containsSome(source, target));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#containsOnly(java.util.Collection, java.lang.Object)}.
	 */
	@Test
	public void testContainsOnly() 
	{
		// contains only
		assertTrue(CollectionUtils.containsOnly(Arrays.asList(1), 1));
		assertTrue(CollectionUtils.containsOnly(Arrays.asList(1,1,1), 1));
		
		// does not contain only
		assertFalse(CollectionUtils.containsOnly(
				Collections.<Integer>emptyList(), 1));
		assertFalse(CollectionUtils.containsOnly(
				Arrays.asList(2,3,4),1));
		assertFalse(CollectionUtils.containsOnly(
				Arrays.asList(1,1,1,1,2),1));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#replaceAll(java.util.Collection, java.util.Collection, java.util.Collection)}.
	 */
	@Test
	public void testReplaceAll() 
	{
		Collection<Integer> source,dest,target;
		Collection<Integer> expected;
		
		// test no change
		source = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
		dest = new ArrayList<Integer>(Arrays.asList(6,7,8,9,10,11,12));
		target = new ArrayList<Integer>(Arrays.asList(13,14,15,16,17));
		
		expected = new ArrayList<Integer>(dest);
		boolean result = CollectionUtils.replaceAll(source, dest, target);
		assertEquals("Unchanged",expected,dest);
		assertFalse(result);
		
		// test all changed
		dest = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
		expected = new ArrayList<Integer>(Arrays.asList(13,14,15,16,17));
		result = CollectionUtils.replaceAll(source, dest, target);
		assertEquals("All changed",expected,dest);
		assertTrue(result);
		
		
		// test some changed
		dest = new ArrayList<Integer>(Arrays.asList(1,100,3,4,101,5,6,7));
		expected = new ArrayList<Integer>(Arrays.asList(13,100,15,16,101,17,6,7));
		result = CollectionUtils.replaceAll(source, dest, target);
		assertEquals("Some changed",expected,dest);
		assertTrue(result);
		
		// test set
		source = new HashSet<Integer>(source);
		dest = new HashSet<Integer>(Arrays.asList(1,100,3,4,101,5,6,7));
		expected = new HashSet<Integer>(Arrays.asList(100,101,6,7,13,14,15,16));
		result = CollectionUtils.replaceAll(source, dest, target);
		assertEquals("Set: Some changed",expected,dest);
		assertTrue(result);
		
		
		
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#unorderedCollectionEquals(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	public void testUnorderedCollectionEqualsWithNaturalOrder() 
	{
		Collection<Integer> c1 = Arrays.asList(1,2,3,4,5);
		Collection<Integer> c2 = Arrays.asList(5,4,3,2,1);
		
		assertTrue(CollectionUtils.unorderedCollectionEquals(c1, c2));
		c2 = Arrays.asList(1,2,3,4,0);
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, c2));
		
		assertTrue(CollectionUtils.unorderedCollectionEquals(null, null));
		assertTrue(CollectionUtils.unorderedCollectionEquals(Collections.<Integer>emptyList(),Collections.<Integer>emptySet()));
		assertFalse(CollectionUtils.unorderedCollectionEquals(null, c2));
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, null));
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, Collections.<Integer>emptyList()));
		
		// sizes must be the same
		c2 = Arrays.asList(1,2,3,4,5,1,2,3,4,5);
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, c2));
		
		// must contain all the same objects
		c1 = Arrays.asList(1,2,2,3,3);
		c2 = Arrays.asList(1,1,2,2,3);
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1,c2));
		
	}
	
	@Test
	public void testUnorderedCollectionCompareWithNaturalOrder()
	{
		Collection<Integer> c1 = Arrays.asList(1,2,3,4,5);
		Collection<Integer> c2 = Arrays.asList(5,4,3,2,1);
		
		assertEquals(0,CollectionUtils.unorderedCollectionCompare(c1, c2));
		assertEquals(0,CollectionUtils.unorderedCollectionCompare(null, null));
		
		c1 = Arrays.asList(0);
		assertTrue(CollectionUtils.unorderedCollectionCompare(c1, c2) < 0);
		assertTrue(CollectionUtils.unorderedCollectionCompare(null, c2) < 0);
		c1 = Arrays.asList(1,2,3);
		assertTrue(CollectionUtils.unorderedCollectionCompare(c1, c2) < 0);
		
		
		c1 = Arrays.asList(1,2,3,4,5);
		c2 = Arrays.asList(0);
		assertTrue(CollectionUtils.unorderedCollectionCompare(c1, c2) > 0);
		assertTrue(CollectionUtils.unorderedCollectionCompare(c1,null) > 0);
		c2 = Arrays.asList(1,2,3);
		assertTrue(CollectionUtils.unorderedCollectionCompare(c1, c2) > 0);
		
		
	}
	
	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#unorderedCollectionEquals(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testUnorderedCollectionEqualsWithoutNaturalOrder()
	{
		Collection<Wrapper<Integer>> c1 = Arrays.asList(new Wrapper<Integer>(1),
											   new Wrapper<Integer>(2),
											   new Wrapper<Integer>(3),
											   new Wrapper<Integer>(4),
											   new Wrapper<Integer>(5));
		Collection<Wrapper<Integer>> c2 = Arrays.asList(new Wrapper<Integer>(5),
														new Wrapper<Integer>(4),
														new Wrapper<Integer>(3),
														new Wrapper<Integer>(2),
														new Wrapper<Integer>(1));
		
		assertTrue(CollectionUtils.unorderedCollectionEquals(c1, c2));
		c2 = Arrays.asList(new Wrapper<Integer>(1),
						   new Wrapper<Integer>(2),
						   new Wrapper<Integer>(3),
						   new Wrapper<Integer>(4),
						   new Wrapper<Integer>(0));
		
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, c2));
		
		assertTrue(CollectionUtils.unorderedCollectionEquals(null, null));
		assertFalse(CollectionUtils.unorderedCollectionEquals(null, c2));
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, null));
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, Collections.<Wrapper<Integer>>emptyList()));
		
		// sizes must be the same
		c2 = Arrays.asList(
				new Wrapper<Integer>(1),
				new Wrapper<Integer>(2),
				new Wrapper<Integer>(3),
				new Wrapper<Integer>(4),
				new Wrapper<Integer>(5),
				new Wrapper<Integer>(1),
				new Wrapper<Integer>(2),
				new Wrapper<Integer>(3),
				new Wrapper<Integer>(4),
				new Wrapper<Integer>(5));
			
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1, c2));
		
		// must contain all the same objects
		c1 = Arrays.asList(new Wrapper<Integer>(1),
						   new Wrapper<Integer>(2),
						   new Wrapper<Integer>(2),
						   new Wrapper<Integer>(3),
						   new Wrapper<Integer>(3));
		c2 = Arrays.asList(new Wrapper<Integer>(1),
						   new Wrapper<Integer>(1),
						   new Wrapper<Integer>(2),
						   new Wrapper<Integer>(2),
						   new Wrapper<Integer>(3));
					
		assertFalse(CollectionUtils.unorderedCollectionEquals(c1,c2));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#convertCollectionIntoDelStr(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testConvertCollectionIntoDelStr() 
	{
		String expectedStr = "1,2,3,4,5";
		Collection<String> collection = Arrays.asList("1","2","3","4","5");
		
		
		assertEquals(expectedStr,
				CollectionUtils.convertCollectionIntoDelStr(collection, ","));
		
		// test empty
		assertEquals("",CollectionUtils.convertCollectionIntoDelStr(
				Collections.<String>emptyList(), ","));
		
		// test null
		expectedStr = ",2,3,,";
		collection = Arrays.asList(null,"2","3",null,"");
		
		assertEquals(expectedStr,CollectionUtils.convertCollectionIntoDelStr(collection, ","));
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#convertDelStrIntoCollection(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testConvertDelStrIntoCollection() 
	{
		Collection<String> expected = Arrays.asList("1","2","3","4","5");
		
		String str = "1,2,3,4,5";
		
		assertEquals(expected,CollectionUtils.convertDelStrIntoCollection(
				str, ","));
		
		// test empty
		assertEquals(Collections.<String>emptyList(),
				CollectionUtils.convertDelStrIntoCollection(
				"",","));
		
		
		// test null
		str = ",2,3,,";
		expected = Arrays.asList("","2","3","","");
		
		assertEquals(expected,CollectionUtils.convertDelStrIntoCollection(
				str, ","));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#convertMapIntoDelStr(java.util.Map, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testConvertMapIntoDelStr()
	{
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("1","2");
		map.put("3","4");
		map.put("5", "6");
		String expected = "1,2;3,4;5,6";
		
		assertEquals(expected,CollectionUtils.convertMapIntoDelStr(
				map, ";", ","));
		
		// test empty
		assertEquals("",CollectionUtils.convertMapIntoDelStr(
				Collections.<String,String>emptyMap(), ";", ","));
		
		// test null value and key
		map.put("1", null);
		map.put("5", null);
		map.put(null, "7");
		expected = "1,;3,4;5,;,7";
		
		assertEquals(expected,CollectionUtils.convertMapIntoDelStr(map, ";", ","));
		
		// check null to null
		map.clear();
		map.put("1", null);
		map.put("3", "4");
		map.put("", "");
		map.put(null, null);
		expected = "1,;3,4;,;,";
		
		assertEquals(expected,CollectionUtils.convertMapIntoDelStr(map, ";", ","));

		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#convertDelStrIntoMap(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testConvertDelStrIntoMap() 
	{
		Map<String,String> expected = new LinkedHashMap<String,String>();
		expected.put("1","2");
		expected.put("3","4");
		expected.put("5", "6");
		
		String str = "1,2;3,4;5,6";
		
		assertEquals(expected,
				CollectionUtils.convertDelStrIntoMap(
						str, ";", ","));
		
		// test empty 
		assertEquals(Collections.<String,String>emptyMap(),
				CollectionUtils.convertDelStrIntoMap(
						"", ";", ","));
		
		// test null value
		expected.put("1", "");
		expected.put("5", "");
		expected.put("", "7");
		str = "1,;3,4;5,;,7";
		
		assertEquals(expected,CollectionUtils.convertDelStrIntoMap(str, ";", ","));
		
		// check null to null
		expected.put("", "");
		str = "1,;3,4;5,;,";
		
		assertEquals(expected,CollectionUtils.convertDelStrIntoMap(str, ";", ","));
		
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#retainAll(java.util.ArrayList, java.util.Set)}.
	 */
	@Test
	public void testRetainAll() 
	{
		Set<Integer> universe = 
			new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
		
		ArrayList<Integer> nums = 
			new ArrayList<Integer>(Arrays.asList(1,2,3,100,200,300));
		
		List<Integer> expected = Arrays.asList(1,2,3);
		
		assertEquals(expected,CollectionUtils.retainAll(nums, universe));
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.CollectionUtils#unorderedListEquals(java.util.List, java.util.List)}.
	 */
	@Test
	public void testUnorderedListEquals()
	{
		List<Integer> c1 = Arrays.asList(1,2,3,4,5);
		List<Integer> c2 = Arrays.asList(5,4,3,2,1);
		
		assertTrue(CollectionUtils.unorderedListEquals(c1, c2));
		c2 = Arrays.asList(1,2,3,4,0);
		assertFalse(CollectionUtils.unorderedListEquals(c1, c2));
		
		// must contain all the same objects
		c1 = Arrays.asList(1,2,2,3,3);
		c2 = Arrays.asList(1,1,2,2,3);
		assertFalse(CollectionUtils.unorderedListEquals(c1,c2));
	}
	
	
	/**
	 * Test method for {@link CollectionUtils#valueSortedMap(Map)}.
	 * 
	 */
	@Test
	public void testValueSortedMap()
	{
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("1",6);
		map.put("2",5);
		map.put("3", 4);
		map.put("4", 3);
		map.put("5", 2);
		map.put("6", 1);
		
		List<Integer> expected = Arrays.asList(1,2,3,4,5,6);
		
		assertEquals(expected,new ArrayList<Integer>(
				CollectionUtils.valueSortedMap(map).values()));
	}
	
	@Test(timeout = 1000)
	public void testRemoveDuplicates()
	{
		List<Integer> ints = 
			new ArrayList<Integer>(Arrays.asList(1,1,1,2,3,4,5,5,6,7,7,8,9,10,10));
		Set<Integer> removed = new HashSet<Integer>();
		
		List<Integer> expected = 
			new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
		Set<Integer> expectedRemoved = new HashSet<Integer>(Arrays.asList(1,5,7,10));
		
		CollectionUtils.removeDuplicates(ints, removed);
		assertEquals(expected,ints);
		assertEquals(expectedRemoved,removed);
	}
	
	@Test(timeout = 1000)
	public void testRemoveAll()
	{
		List<Integer> ints = 
			new ArrayList<Integer>(Arrays.asList(1,1,1,2,3,4,5,5,6,7,7,8,9,10,10));
		
		CollectionUtils.removeAll(ints, 1);
		
		assertEquals(Arrays.asList(2,3,4,5,5,6,7,7,8,9,10,10),ints);
		
		CollectionUtils.removeAll(ints, 10);
		
		assertEquals(Arrays.asList(2,3,4,5,5,6,7,7,8,9),ints);
		
		CollectionUtils.removeAll(ints, 0);
		
		assertEquals(Arrays.asList(2,3,4,5,5,6,7,7,8,9),ints);

		CollectionUtils.removeAll(ints, 4);
		
		assertEquals(Arrays.asList(2,3,5,5,6,7,7,8,9),ints);
		
	}

}
