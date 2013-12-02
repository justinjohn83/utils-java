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
/**
 * @author Justin Montgomery
 * @version $Id: UnmodifiableBidiMapTest.java 1271 2009-01-16 20:45:44Z jmontgomery $
 */
public final class UnmodifiableBidiMapTest extends AbstractBidiMapTest {

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractBidiMapTest#createEmptyMap()
	 */
	@Override
	protected BidiMap<Integer, Integer> createEmptyMap() 
	{
		return CollectionUtils.unmodifiableBidiMap(new DualHashBidiMap<Integer,Integer>());
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractBidiMapTest#createMap(java.util.Map)
	 */
	@Override
	protected BidiMap<Integer, Integer> createMap(Map<Integer, Integer> m)
	{
		return CollectionUtils.unmodifiableBidiMap(new DualHashBidiMap<Integer,Integer>(m));
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testRemoveValue() 
	{
		super.testRemoveValue();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testClear() {
		// TODO Auto-generated method stub
		super.testClear();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testModifyEntryValue() 
	{
		super.testModifyEntryValue();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testRemove()
	{
		super.testRemove();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testRemoveEntrySet()
	{
		super.testRemoveEntrySet();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testRemoveKeySet() 
	{
		super.testRemoveKeySet();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testRemoveValues() 
	{
		super.testRemoveValues();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testEntrySetCollectionRemove() 
	{
		super.testEntrySetCollectionRemove();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testKeySetCollectionRemove() 
	{
		super.testKeySetCollectionRemove();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testValuesCollectionRemove() 
	{
		super.testValuesCollectionRemove();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testPut() 
	{
		super.testPut();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testPutAll()
	{
		super.testPutAll();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testIdentity()
	{
		super.testIdentity();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneAddIdentity() 
	{
		super.testOneToOneAddIdentity();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval3() 
	{
		super.testOneToOneRemoval3();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval4() 
	{
		super.testOneToOneRemoval4();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval5()
	{
		super.testOneToOneRemoval5();
	}

	
	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval6()
	{
		super.testOneToOneRemoval6();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval7()
	{
		super.testOneToOneRemoval7();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval8()
	{
		super.testOneToOneRemoval8();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval9() 
	{
		super.testOneToOneRemoval9();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemove1_2() 
	{
		super.testOneToOneRemove1_2();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemove1()
	{
		super.testOneToOneRemove1();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemove2_2()
	{
		super.testOneToOneRemove2_2();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemove2() 
	{
		super.testOneToOneRemove2();
	}

	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoveIdentity()
	{
		super.testOneToOneRemoveIdentity();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval10()
	{
		super.testOneToOneRemoval10();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	@Override
	public void testOneToOneRemoval11()
	{
		super.testOneToOneRemoval11();
	}

}
