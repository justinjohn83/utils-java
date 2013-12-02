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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id: ConsoleRedirectorTest.java 1155 2008-10-23 17:24:42Z jmontgomery $
 */
public class ConsoleRedirectorTest 
{
	private enum TestType {OUT,ERR,BOTH};
	
	private static final String TEST_STRING = 
		"Jack and Jill ran up the hill.\n" +
		"Vodka martini, shaken but not stirred.\n" +
		"Take me to your leader!\n";
	
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
		// does not use this class to restore the console
		MiscUtils.restoreConsole();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception 
	{
		// does not use this class to restore the console
		MiscUtils.restoreConsole();
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ConsoleRedirector#getOutToString()}.
	 */
	@Test
	public void testGetOutToString() 
		throws Exception
	{
		testGetOutput(TestType.OUT,true);
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ConsoleRedirector#getErrToString()}.
	 */
	@Test
	public void testGetErrToString() 
		throws Exception
	{
		testGetOutput(TestType.ERR,true);
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ConsoleRedirector#getOutToByteArray()}.
	 */
	@Test
	public void testGetOutToByteArray() 
		throws Exception
	{
		testGetOutput(TestType.OUT,false);
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ConsoleRedirector#getErrToByteArray()}.
	 */
	@Test
	public void testGetErrToByteArray() 
		throws Exception
	{
		testGetOutput(TestType.ERR,false);
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ConsoleRedirector#getConsoleOutputToString()}.
	 */
	@Test
	public void testGetConsoleOutputToString() 
		throws Exception
	{
		testGetOutput(TestType.BOTH,true);
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.ConsoleRedirector#getConsoleOutputToByteArray()}.
	 */
	@Test
	public void testGetConsoleOutputToByteArray()
		throws Exception
	{
		testGetOutput(TestType.BOTH,false);
	}
	
	
	private void testGetOutput(TestType type,boolean strTest)
		throws Exception
	{
		ConsoleRedirector r = ConsoleRedirector.getInstance();
		
		switch(type)
		{
			case OUT:
			{
				r.setOutToBuffer(false);
				System.out.print(TEST_STRING);
				System.out.flush();
				if(strTest)
					assertEquals(TEST_STRING,r.getOutToString());
				else
					assertArrayEquals(TEST_STRING.getBytes(r.getBufferCharEncoding()),
							r.getOutToByteArray());
				break;
			}
			case ERR:
			{
				r.setErrToBuffer(false);
				System.err.print(TEST_STRING);
				System.err.flush();
				if(strTest)
					assertEquals(TEST_STRING,r.getErrToString());
				else
					assertArrayEquals(TEST_STRING.getBytes(r.getBufferCharEncoding()),
							r.getErrToByteArray());
				break;
			}
			case BOTH:
			{
				r.setConsoleOutputToBuffer(false);
				int mid = TEST_STRING.length() / 2;
				// split the output
				String test1 = TEST_STRING.substring(0,mid);
				String test2 = TEST_STRING.substring(mid,TEST_STRING.length());
				System.out.print(test1);
				System.out.flush();
				System.err.print(test2);
				System.err.flush();
				if(strTest)
					assertEquals(TEST_STRING,r.getConsoleOutputToString());
				else
					assertArrayEquals(TEST_STRING.getBytes(r.getBufferCharEncoding()),
							r.getConsoleOutputToByteArray());
				break;
			}
		}
	}

}
