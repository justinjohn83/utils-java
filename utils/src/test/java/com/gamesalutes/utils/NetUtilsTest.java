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
 * Test cases for {@link NetUtils NetUtils}.
 * 
 * @author Justin Montgomery
 * @version $Id: NetUtilsTest.java 1285 2009-01-28 00:07:21Z jmontgomery $
 */
public class NetUtilsTest 
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
	 * Test method for {@link com.gamesalutes.utils.NetUtils#isValidAddress(java.lang.String)}.
	 */
	@Test
	public void testIsValidAddress() 
	{
		assertTrue(NetUtils.isValidAddress("35.192.12.1"));
		// not ip
		assertFalse(NetUtils.isValidAddress("no_address"));
		assertFalse(NetUtils.isValidAddress("a.b.c.d"));
		
		// incomplete address
		assertFalse(NetUtils.isValidAddress("35.192.12"));
		// no numbers
		assertFalse(NetUtils.isValidAddress("..."));
		// numbers out of range
		assertFalse(NetUtils.isValidAddress("999.192.12.1"));
		assertFalse(NetUtils.isValidAddress("01.256.000.04"));
		assertFalse(NetUtils.isValidAddress("1234.256.0.4"));
		
		// too many dots
		assertFalse(NetUtils.isValidAddress("1.2.3.4.5"));
		assertFalse(NetUtils.isValidAddress("1.2.3.4.5.6.7..8..9..."));
	}
	
	@Test
	public void testIntToAddress()
	{
		String ip = "35.183.255.112";
		int ipInt = ByteUtils.toInteger((byte)35, (byte)183,(byte)255, (byte)112);
		assertEquals(ip,NetUtils.intToAddress(ipInt));
	}
	
	@Test
	public void testAddressToInt()
	{
		String ip = "35.183.255.112";
		int expected = ByteUtils.toInteger((byte)35, (byte)183,(byte)255, (byte)112);
		assertEquals(expected,NetUtils.addressToInt(ip));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.NetUtils#isNetworkLocal(java.lang.String)}.
	 */
	@Test
	public void testIsNetworkLocal() 
	{
		// not local
		assertFalse(NetUtils.isNetworkLocal("35.192.12.1"));
		// localhost
		assertTrue(NetUtils.isNetworkLocal("127.0.0.1"));
		// class A local
		assertTrue(NetUtils.isNetworkLocal("10.1.0.1"));
		// class B local
		assertTrue(NetUtils.isNetworkLocal("172.16.255.190"));
		// class C local
		assertTrue(NetUtils.isNetworkLocal("192.168.8.1"));
	}
	
	@Test
	public void testCanonicalizeMAC()
	{
		String in = "00:34:4C:0F:84:79".toUpperCase();
		String exp = in;
		
		// identity
		assertEquals(exp,NetUtils.canonicalizeMac(in));
		// case change
		assertEquals(exp,NetUtils.canonicalizeMac(in.toLowerCase()));
		// format change
		in = "0034.4c0f.8479";
		assertEquals(exp,NetUtils.canonicalizeMac(in));
		
	}
	
	@Test
	public void testUrlEncodePath()
	{
		String input = "Here are spaces"; 
		String exp = "Here%20are%20spaces";
		assertEquals(exp,NetUtils.urlEncodePath(input));
	}

}
