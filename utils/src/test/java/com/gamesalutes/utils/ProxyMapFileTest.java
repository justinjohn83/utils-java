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
package com.gamesalutes.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.gamesalutes.utils.UtilsTestSuite;

public class ProxyMapFileTest extends ProxyMapTest {

	@Test
	public void testReload() throws IOException
	{
		ProxyMap<Integer,Integer> map = (ProxyMap<Integer,Integer>)this.createEmptyMap();
		
		map.put(1, 2);
		map.put(3, 4);
		map.dispose();
		retriever = createProxyRetriever();
		
		map = (ProxyMap<Integer,Integer>)this.createEmptyMap();
		
		assertEquals(Integer.valueOf(2),map.get(1));
		assertEquals(Integer.valueOf(4),map.get(3));
		
		map.remove(3);
		map.dispose();
		retriever = createProxyRetriever();

		
		map = (ProxyMap<Integer,Integer>)this.createEmptyMap();
		
		assertEquals(Integer.valueOf(2),map.get(1));
		assertFalse(map.containsKey(3));
		assertNull(map.get(3));
		
		map.put(100,100);
		
		map.clear();
		map.dispose();
		retriever = createProxyRetriever();

		map = (ProxyMap<Integer,Integer>)this.createEmptyMap();
		
		assertFalse(map.containsKey(100));
		assertNull(map.get(100));

		assertFalse(map.containsKey(1));	
		assertNull(map.get(1));	

		
	}
	
	protected ProxyRetriever<Integer,Integer> createProxyRetriever() 
	{
		try {
			return  new FileProxyRetriever<Integer,Integer>(
					UtilsTestSuite.getTempDirectory(),"proxy_map_file_test",true);
			}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
