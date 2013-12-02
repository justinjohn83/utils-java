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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import com.gamesalutes.utils.HostTokenStore.HostToken;
import com.gamesalutes.utils.HostTokenStore.TokenIterator;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public class HostTokenStoreTest
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
	 * Test method for {@link com.gamesalutes.utils.HostTokenStore#trim()}.
	 */
	@Test
	public void testTrim()
	{
		HostTokenStore<Object> store = new HostTokenStore<Object>();
		
		List<HostToken<Object>> tokens = Arrays.asList(
				new HostToken<Object>("a","",null), // 0
				new HostToken<Object>("a","a","a"), // 1
				new HostToken<Object>(null,"a",null), // 2
				new HostToken<Object>("","a","a"), // 3
				new HostToken<Object>("b","b",""), // 4
				new HostToken<Object>("","","b"), // 5
				new HostToken<Object>("c","c",""), // 6
				new HostToken<Object>("b","",""), // 7
				new HostToken<Object>("d","d","d"), //8
				new HostToken<Object>("","d",null)); // 9
		
		for(HostToken<Object> ht : tokens)
			store.addToken(ht);
		
		Set<HostToken<Object>> expected = new HashSet<HostToken<Object>>(Arrays.asList(
				tokens.get(1),
				tokens.get(4),
				tokens.get(5),
				tokens.get(6),
				tokens.get(8)
				
				));
		
		
		store.trim();
		
		assertEquals(expected,store.getAllTokens());
		
		
	}
	
	@Test 
	public void testIteratorSet()
	{
		HostTokenStore<Object> store = new HostTokenStore<Object>();		
		
		store.addToken(new HostToken<Object>("0","0","0"));
		store.addToken(new HostToken<Object>("1","1","1"));
		store.addToken(new HostToken<Object>("2","2","2"));
		store.addToken(new HostToken<Object>("3","3","3"));
		store.addToken(new HostToken<Object>("4","4","4"));
		store.addToken(new HostToken<Object>("5","5","5"));
		
		HostTokenStore<Object> exp = new HostTokenStore<Object>();
		exp.addToken(new HostToken<Object>("_0","0","0"));
		exp.addToken(new HostToken<Object>("_0","1","1"));
		exp.addToken(new HostToken<Object>("2","_2","2"));
		exp.addToken(new HostToken<Object>("3","_2","3"));
		exp.addToken(new HostToken<Object>("4","4","_4"));
		exp.addToken(new HostToken<Object>("5","5","_4"));
		
		TokenIterator<Object> it = store.iterator();
		it.next(); it.setName("_0");
		it.next(); it.setName("_0");
		it.next(); it.setIp("_2");
		it.next(); it.setIp("_2");
		it.next(); it.setMac("_4");
		it.next(); it.setMac("_4");
		
		assertEquals(exp,store);
		
	}
	
	@Test
	public void testRemove()
	{
		HostTokenStore<Object> store = new HostTokenStore<Object>();		
		
		store.addToken(new HostToken<Object>("0","0","0"));
		store.addToken(new HostToken<Object>("1","1","1"));
		store.addToken(new HostToken<Object>("2","2","2"));
		store.addToken(new HostToken<Object>("3","3","3"));
		store.addToken(new HostToken<Object>("4","4","4"));
		store.addToken(new HostToken<Object>("5","5","5"));
		
		Set<HostToken<Object>> rem = new HashSet<HostToken<Object>>();
		
		// remove every third one
		rem.add(new HostToken<Object>("0","0","0"));
		rem.add(new HostToken<Object>("1","1","1"));
		rem.add(new HostToken<Object>("3","3","3"));
		rem.add(new HostToken<Object>("4","4","4"));

		
		int count = 0;
		for(TokenIterator<Object> it = store.iterator(); it.hasNext();)
		{
			it.next();
			if(++count % 3 == 0)
				it.remove();
		}
		
		assertEquals(rem,store.getAllTokens());
		// remove rest
		for(TokenIterator<Object> it = store.iterator(); it.hasNext();)
		{
			it.next();
			it.remove();
		}
		
		assertTrue(store.getAllTokens().isEmpty());
	}

}
