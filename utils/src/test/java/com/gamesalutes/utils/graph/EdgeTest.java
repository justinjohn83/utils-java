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
package com.gamesalutes.utils.graph;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * @author Justin  Montgomery
 * @version $Id: EdgeTest.java 1274 2009-01-20 19:40:22Z jmontgomery $
 */
public class EdgeTest
{
	Edge<Integer,Integer> e12,e23,e12_clone,e21;
	Vertex<Integer> v1,v2,v3;
	@Before
	public void setUp() throws Exception
	{
		v1 = new Vertex<Integer>(0);
		v2 = new Vertex<Integer>(1);
		v3 = new Vertex<Integer>(2);
		
		e12 = new Edge<Integer,Integer>(v1,v2,3);
		e12_clone = new Edge<Integer,Integer>(v1,v2,3);
		e21 = new Edge<Integer,Integer>(v2,v1,3);
		e23 = new Edge<Integer,Integer>(v2,v3,4);
		
	}

	@After
	public void tearDown() throws Exception 
	{
		e12 = e12_clone = e21 = e23 = null;
		v1 = v2 = v3 = null;
	}

	@Test
	public void testEqualsObjectPass() 
	{
		assertEquals(e12,e12_clone);
	}
	@Test
	public void testEqualsObjectFail()
	{
		assertFalse(e12.equals(e21));
		assertFalse(e12.equals(e23));
	}

	@Test
	public void testGetFrom()
	{
		assertSame(v1,e12.getFrom());
	}

	@Test
	public void testGetTo()
	{
		assertSame(v2,e12.getTo());
	}

	@Test
	public void testGetOtherEndPoint()
	{
		assertSame(v1,e12.getOtherEndPoint(v2));
		assertSame(v2,e12.getOtherEndPoint(v1));
	}
	@Test(expected=IllegalArgumentException.class)
	public void testGetOtherEndPoint_Fail()
	{
		e12.getOtherEndPoint(v3);
	}

	@Test
	public void testReverse() 
	{
		assertEquals(e21,e12.reverse());
	}
	
	@Test
	public void testSelfLoop()
	{
		Edge<Integer,Integer> e = new Edge<Integer,Integer>(v1,v1,1);
		assertTrue(e.isSelfLoop());
		assertFalse(e12.isSelfLoop());
	}

}
