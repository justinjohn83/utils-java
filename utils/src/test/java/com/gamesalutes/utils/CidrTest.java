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

import org.junit.Test;

/**
 * Test case for the <code>Cidr</code> class.
 * 
 * @author Justin Montgomery
 * @version $Id: CidrTest.java 2726 2011-03-29 21:41:44Z jmontgomery $
 */
public class CidrTest {

	/**
	 * Test method for {@link com.gamesalutes.utils.Cidr#Cidr(java.lang.String)}.
	 */
	@Test
	public void testCidrString() 
	{
		String cidr1 = "127.1.0.0/24";
		String cidr2 = "255.255.128.0/17";
		Cidr c1 = new Cidr(cidr1);
		Cidr c2 = new Cidr(cidr2);
		Cidr c3 = new Cidr("0.0.0.0/32");
		Cidr c4 = new Cidr("127.0.0.1/32");
		

	}
	
	@Test
	public void testGetMaskBits()
	{
		String cidr1 = "127.1.0.0/24";
		String cidr2 = "255.255.128.0/17";
		Cidr c1 = new Cidr(cidr1);
		Cidr c2 = new Cidr(cidr2);
		
		assertEquals(24,c1.getMaskBits());
		assertEquals(17,c2.getMaskBits());
	}
	
	@Test
	public void testGetSubnetMask()
	{
		String cidr1 = "127.1.0.0/24";
		String cidr2 = "255.255.128.0/17";
		Cidr c1 = new Cidr(cidr1);
		Cidr c2 = new Cidr(cidr2);
		
	    assertEquals("255.255.255.0",NetUtils.intToAddress(c1.getSubnetMask()));
	    assertEquals("255.255.128.0",NetUtils.intToAddress(c2.getSubnetMask()));
	}


        @Test
        public void testRangeConstructor()
        {
		String cidr1 = "127.1.0.0/24";
		String cidr2 = "255.255.128.0/17";

                String startIp = "127.1.0.0";
                String endIp = "127.1.0.255";

                assertEquals(cidr1,new Cidr(startIp,endIp).toString());

                startIp = "255.255.128.0";
                endIp = "255.255.255.255";

                assertEquals(cidr2,new Cidr(startIp,endIp).toString());
        }
	@Test
	public void testGetRangeFirst()
	{
		String cidr1 = "127.1.0.0/24";
		String cidr2 = "255.255.128.0/17";
		Cidr c1 = new Cidr(cidr1);
		Cidr c2 = new Cidr(cidr2);
		
		assertEquals("127.1.0.0",c1.getRangeFirst());
		assertEquals("255.255.128.0",c2.getRangeFirst());
	}
	
	@Test
	public void testGetRangeLast()
	{
		String cidr1 = "127.1.0.0/16";
		String cidr2 = "255.255.0.0/17";
		Cidr c1 = new Cidr(cidr1);
		Cidr c2 = new Cidr(cidr2);
		
		assertEquals("127.1.255.255",c1.getRangeLast());
		assertEquals("255.255.127.255",c2.getRangeLast());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.Cidr#Cidr(int, int)}.
	 */
	@Test
	public void testCidrIntInt()
	{
		String strAddr = "35.100.0.0";
		int address = NetUtils.addressToInt(strAddr);
		int subnetMask = NetUtils.addressToInt("255.255.128.000");
		Cidr c = new Cidr(address,subnetMask);
		//assertEquals(mask,c.getSubnetMask());
		assertEquals(subnetMask,c.getSubnetMask());
		assertEquals(strAddr,c.getRangeFirst());
	}
	
	/**
	 * Test method for {@link com.gamesalutes.utils.Cidr#hashCode()}.
	 */
	@Test
	public void testHashCode() 
	{
		// equal objects have equal hash codes
		Cidr c1 = new Cidr("255.128.0.0/24");
		Cidr c2 = new Cidr("255.128.0.0/24");
		assertEquals(c1.hashCode(),c2.hashCode());
	}



	/**
	 * Test method for {@link com.gamesalutes.utils.Cidr#containsAddress(java.lang.String)}.
	 */
	@Test
	public void testContainsAddressString() 
	{
		Cidr c = new Cidr("128.255.128.0/25");
		
		String addr = "128.255.128.0";
		assertTrue(c.containsAddress(addr));
		addr = "128.255.128." + 0x7F;
		assertTrue(c.containsAddress(addr));
		
		
		addr = "128.255." + 0x81 + "." + 0x01;
		assertFalse(c.containsAddress(addr));
		addr = "128.255.128.255";
		assertFalse(c.containsAddress(addr));
		addr = "128.128.128.0";
		assertFalse(c.containsAddress(addr));
		addr = "255.255.128.0";
		assertFalse(c.containsAddress(addr));
		
		// /25 is only 128 addresses
		addr = "128.255.128.128";
		assertFalse(c.containsAddress(addr));
		
		c = new Cidr("128.255.128.192/26");
		assertTrue(c.containsAddress("128.255.128.255"));
		assertFalse(c.containsAddress("128.255.128.0"));
		
		c = new Cidr("128.255.128.0/24");
		assertTrue(c.containsAddress("128.255.128.255"));

                // check to see if /32 cidr matches the address
                c = new Cidr("128.135.23.45/32");
                assertTrue(c.containsAddress("128.135.23.45"));
		
	}
	
	/**
	 * Test method for {@link Cidr#containsCidr(Cidr)}.
	 * 
	 */
	@Test
	public void testContainsCidr()
	{
		Cidr parent = new Cidr("128.135.0.0/16");
		Cidr child1 = new Cidr("128.135.128.0/17");
		Cidr child2 = new Cidr("128.135.1.0/24");
		Cidr child3 = new Cidr("255.135.0.0/16");
		Cidr child4 = new Cidr("128.134.5.128/25");
		Cidr parentCopy = new Cidr(NetUtils.addressToInt(parent.getRangeFirst()),parent.getSubnetMask());
		
		assertTrue(parent.containsCidr(child1));
		assertFalse(child1.containsCidr(parent));
		assertTrue(parent.containsCidr(child2));
		assertFalse(child2.containsCidr(parent));
		
		assertFalse(parent.containsCidr(child3));
		assertFalse(child3.containsCidr(parent));
		assertFalse(parent.containsCidr(child4));
		assertFalse(child4.containsCidr(parent));
		
		assertTrue(parent.containsCidr(parentCopy));
		assertTrue(parentCopy.containsCidr(parent));
		
	}
	
	@Test
	public void testIntersectsCidr()
	{
		Cidr parent = new Cidr("128.135.0.0/26");
		Cidr child1 = new Cidr("128.135.0.32/25");
		Cidr child2 = new Cidr("128.135.0.64/26");
		Cidr child3 = new Cidr("128.135.0.100/28");
		
		assertTrue(parent.intersectsCidr(child1));
		assertFalse(parent.intersectsCidr(child2));
		assertFalse(parent.intersectsCidr(child3));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.Cidr#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject()
	{
		Cidr c1 = new Cidr("255.128.0.0/24");
		Cidr c2 = new Cidr("255.128.0.0/24");
		assertEquals(c1,c2);
		c2 = new Cidr("255.127.0.0/24");
		assertFalse(c1.equals(c2));
		c2 = new Cidr("255.128.0.0/26");
		assertFalse(c1.equals(c2));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.Cidr#toString()}.
	 */
	@Test
	public void testToString()
	{
		String str = "128.255.1.0/24";
		Cidr c = new Cidr(str);
		assertEquals(str,c.toString());
	}

}
