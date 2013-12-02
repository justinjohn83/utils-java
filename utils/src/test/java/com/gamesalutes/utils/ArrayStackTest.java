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

import java.util.Collection;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Justin Montgomery
 * @version $Id: ArrayStackTest.java 1498 2009-05-19 19:14:23Z jmontgomery $
 */
public final class ArrayStackTest extends AbstractCollectionTest
{

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractCollectionTest#createCollection(java.util.Collection)
	 */
	@Override
	protected ArrayStack<Integer> createCollection(Collection<Integer> c) 
	{
		ArrayStack<Integer> s = new ArrayStack<Integer>();
		s.addAll(c);
		return s;
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractCollectionTest#createEmptyCollection()
	 */
	@Override
	protected ArrayStack<Integer> createEmptyCollection() 
	{
		return new ArrayStack<Integer>();
	}
	
	@Test
	public void testPush()
	{
		ArrayStack<Integer> s = createEmptyCollection();
		assertEquals(Integer.valueOf(1),s.push(1));
		
	}
	@Test
	public void testPop()
	{
		ArrayStack<Integer> s = createEmptyCollection();
		s.push(1);
		s.push(2);
		s.push(3);
		assertEquals(Integer.valueOf(3),s.pop());
		assertEquals(Integer.valueOf(2),s.pop());
		assertEquals(Integer.valueOf(1),s.pop());
		assertTrue(s.isEmpty());
		
	}
	@Test
	public void testPeek()
	{
		ArrayStack<Integer> s = createEmptyCollection();
		s.push(1);
		s.push(2);
		s.push(3);
		assertEquals(Integer.valueOf(3),s.peek());
		assertEquals(Integer.valueOf(3),s.peek());
		assertEquals(3,s.size());
	}

}
