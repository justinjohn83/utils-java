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
 * @version $Id: VertexTest.java 697 2008-02-20 18:29:39Z jmontgomery $
 */
public class VertexTest 
{
	Vertex<Integer> v1,v1_clone,v2;
	@Before
	public void setUp() throws Exception 
	{
		v1 = new Vertex<Integer>(0);
		v1_clone = new Vertex<Integer>(v1.getData());
		v2 = new Vertex<Integer>(1);
	}

	@After
	public void tearDown() throws Exception 
	{
		v1 = v1_clone = v2 = null;
	}

	@Test
	public void testEqualsObjectPass() 
	{
		assertEquals(v1,v1_clone);
	}
	@Test
	public void testEqualsObjectFail()
	{
		assertFalse(v1.equals(v2));
	}

}
