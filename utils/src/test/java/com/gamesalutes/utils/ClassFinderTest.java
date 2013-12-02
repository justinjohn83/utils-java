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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * @author Justin Montgomery
 * @version $Id: ClassFinderTest.java 1866 2010-01-25 21:44:06Z jmontgomery $
 */
public class ClassFinderTest 
{

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ClassFinder#getClassesForPackage(java.lang.String)}.
	 */
	@Test
	public void testGetClassesForPackageFileSystem()
		throws ClassNotFoundException
	{
		// the found classes must be at least those in the contained test set
		// more classes could be added later, so won't have to keep updating this method when
		// more are added
		Set<Class<?>> expected = new HashSet<Class<?>>(Arrays.asList(
				ByteUtils.class,ClassFinder.class,CollectionUtils.class,
				CSVReader.class,CSVWriter.class,DateUtils.class,
				Disposable.class,DOMUtils.class,ErrorUtils.class,ArrayStack.class,
				FileLock.class,FileUtils.class,MathUtils.class,MiscUtils.class,Pair.class,
				SoftHashMap.class,ClassFinderTest.class,CSVTest.class,MiscUtilsTest.class,
				TestSuite.class));
		
		Set<Class<?>> actual = new HashSet<Class<?>>(
				ClassFinder.getClassesForPackage(getClass().getPackage().getName()));
		
		assertTrue("expected not subset of actual:\nexpected=" + expected + "\nactual=" + actual,
				actual.containsAll(expected));

		
	}
	
	/**
	 * Test method for {@link com.gamesalutes.utils.ClassFinder#getClassesForPackage(java.lang.String)}.
	 */
	@Test
	public void testGetClassesForPackageJar()
		throws ClassNotFoundException
	{
		Set<Class<?>> expected = new HashSet<Class<?>>(Arrays.asList(
				Logger.class));
		
		Set<Class<?>> actual = new HashSet<Class<?>>(
				ClassFinder.getClassesForPackage(Logger.class.getPackage().getName()));
		
		assertTrue("expected not subset of actual:\nexpected=" + expected + "\nactual=" + actual,
				actual.containsAll(expected));
	}

}
