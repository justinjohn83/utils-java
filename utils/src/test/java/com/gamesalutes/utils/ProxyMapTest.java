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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id: ProxyMapTest.java 1147 2008-10-13 23:09:33Z jmontgomery $
 */
public class ProxyMapTest extends AbstractMapTest 
{
	
	protected ProxyRetriever<Integer,Integer> retriever;
	
	protected static class ProxyMapTestRetriever implements ProxyRetriever<Integer,Integer>
	{

		private Map<Integer,Integer> proxyInts;

		public ProxyMapTestRetriever()
		{
			proxyInts = new HashMap<Integer,Integer>();
		}
		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.ProxyRetriever#lookup(java.lang.Object)
		 */
		public final Pair<Boolean, Integer> lookup(Integer key)
		{
			if(proxyInts.containsKey(key))
			{
				Integer value = proxyInts.get(key);
				return Pair.makePair(true, value);
			}
			return Pair.makePair(false, null);
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.ProxyRetriever#clear()
		 */
		public final void clear()
		{
			proxyInts.clear();
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.ProxyRetriever#put(java.lang.Object, java.lang.Object)
		 */
		public final void put(Integer key, Integer value) 
		{
			proxyInts.put(key, value);
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.ProxyRetriever#remove(java.lang.Object)
		 */
		public final void remove(Object key) 
		{
			proxyInts.remove(key);
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.ProxyRetriever#update(java.lang.Object, java.lang.Object)
		 */
		public final void update(Integer key, Integer value)
		{
			put(key,value);
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.Disposable#dispose()
		 */
		public final void dispose() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.ProxyRetriever#addCacheEntry(java.lang.Object, java.lang.Object)
		 */
		public final void addCacheEntry(Integer key, Integer value) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.ProxyRetriever#removeCacheEntry(java.lang.Object)
		 */
		public final  void removeCacheEntry(Integer key) {
			// TODO Auto-generated method stub
			
		}
		

		public Set<Integer> getKeySet() {
			return proxyInts.keySet();
		}

	}
	
	@Override
	public void setUp()
	{
		super.setUp();
		retriever = createProxyRetriever();
		retriever.clear();
	}
	
	@Override
	public void tearDown()
	{
		super.tearDown();
		retriever.dispose();
		retriever = null;
	}
	
	/**
	 * Override to provide own implementation.
	 * 
	 * @return the <code>ProxyRetriever</code>
	 */
	protected ProxyRetriever<Integer,Integer> createProxyRetriever() 
	{
		return new ProxyMapTestRetriever();
	}
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractMapTest#createEmptyMap()
	 */
	@Override
	protected final Map<Integer, Integer> createEmptyMap()
	{
		Map<Wrapper<Integer>,Integer> temp = createLocalStorageMap();
		initProxy(true,getReferenceMap(),temp);
		ProxyMap<Integer,Integer> map = new ProxyMap<Integer,Integer>(
				temp,new HashSet<Wrapper<Integer>>());
		map.setRetriever(retriever);
		return map;
	}
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractMapTest#createMap(java.util.Map)
	 */
	@Override
	protected final Map<Integer, Integer> createMap(Map<Integer, Integer> m)
	{
		Set<Wrapper<Integer>> proxyKeys = new HashSet<Wrapper<Integer>>();
		for(Integer i : m.keySet())
			proxyKeys.add(new Wrapper<Integer>(i,false));
		Map<Wrapper<Integer>,Integer> temp = createLocalStorageMap();
		initProxy(false,getReferenceMap(),temp);
		ProxyMap<Integer,Integer> map = null;
		map = new ProxyMap<Integer,Integer>(
				temp,proxyKeys);
		
		map.setRetriever(retriever);
		return map;
	}
	
	
	/**
	 * Creates a map (unintialized) to be used for local storage for the proxy map.
	 * 
	 * @return the local storage map
	 */
	protected Map<Wrapper<Integer>,Integer> createLocalStorageMap()
	{
		return new HashMap<Wrapper<Integer>,Integer>();
	}
	/**
	 * Initializes the proxy storage and local storage from the global map 
	 * <code>m</code>.
	 * 
	 * @param fromEmpty if this method is being called from empty init
	 * @param m the global reference map
	 * @param localMap the map to use for local storage or <code>null</code> to not use local storage
	 */
	protected void initProxy(boolean fromEmpty,Map<Integer,Integer> m,
		Map<Wrapper<Integer>,Integer> localMap)
	{
		if(!fromEmpty)
		{
			// add half to temp and half to proxy
			int count = 0;
			for(Map.Entry<Integer,Integer > E : m.entrySet())
			{
				Wrapper<Integer> w = new Wrapper<Integer>(E.getKey(),false);
				if(++count < (m.size() >> 1))
					localMap.put(w, E.getValue());
				else
					retriever.put(w.get(),E.getValue());
			}
		}
	}
	

	



}
