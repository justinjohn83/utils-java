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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A Least-recently used (LRU) bounded map implementation using a hashtable for the backing store.
 * In a LRU map, if an entry added to the map
 * causes the map to exceed its max entry size, then the least recently used entry is removed.  Thus,
 * an upper bound is placed on the size of the map.  The LRU order
 * is only affected by calls to {@link #put(Object, Object)}, {@link #putAll(Map)}, and {@link #get(Object)}.  No
 * other method calls, including method calls on returned iterators of the map affect the LRU ordering. 
 * This implementation is unsynchronized. Synchronization can be obtained by wrapping this 
 * Collection in a synchronized map using <code>Collections.synchronizedMap</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: LruMap.java 1168 2008-11-04 22:40:48Z jmontgomery $
 */
public class LruMap<K,V> extends LinkedHashMap<K,V>
{

	private static final long serialVersionUID = 1L;
	
	private final int maxSize;
	
	protected static final float LOAD_FACTOR = 0.75f;
	protected static final int DEFAULT_MAX_SIZE = 16;
	
	private final Set<AutoMapRemovalListener<K, V>> removalListeners = 
		new LinkedHashSet<AutoMapRemovalListener<K, V>>();
	
	/**
	 * Creates an empty map with a max entry capacity of 16 and a load factor of 0.75.
	 * 
	 */
	public LruMap()
	{
		this(DEFAULT_MAX_SIZE,LOAD_FACTOR);
	}
	/**
	 * Constructs an empty map with the specified number of max entries and a load factor of 0.75.
	 * 
	 * @param maxEntries the maximum number of entries permitted
	 * @throws IllegalArgumentException if <code>maxEntries &lt; 0</code>
	 */
	public LruMap(int maxEntries)
	{
		this(maxEntries,LOAD_FACTOR);
	}
	
	/**
	 * Constructs an empty map with the specified number of max entries and the specified
	 * hashtable load factor.
	 * 
	 * @param maxEntries the maximum number of entries permitted
	 * @param loadFactor the load factor
	 * @throws IllegalArgumentException if <code>maxEntries &lt; 0</code>
	 */
	public LruMap(int maxEntries,float loadFactor)
	{
		super(CollectionUtils.calcHashCapacity(maxEntries, loadFactor),loadFactor,true);
		if(maxEntries < 0)
			throw new IllegalArgumentException("maxEntries = " + maxEntries + " < 0");
		maxSize = maxEntries;
	}
	
	/**
	 * Constructs an <code>LRUMap</code> from the specified <code>Map</code>.  The maximum number
	 * of entries permitted is equal to <code>m.size()</code>.
	 * 
	 * @param m the input map
	 */
	public LruMap(Map<? extends K,? extends V> m)
	{
		this(m.size(),LOAD_FACTOR);
		putAll(m);
	}
	
	public final int getMaxSize() { return maxSize; }
	
	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest)
	{
		boolean removed =  size() > maxSize;
		if(removed)
			notifyListeners(eldest);
		return removed;
		
	}
	
	public boolean addAutoRemovalListener(AutoMapRemovalListener<K, V> l)
	{
		if(l == null)
			throw new NullPointerException("l");
		return removalListeners.add(l);
	}
	public boolean removeAutoRemovalListener(AutoMapRemovalListener<K, V> l)
	{
		return removalListeners.remove(l);
	}
	
	private void notifyListeners(Map.Entry<K, V> e)
	{
		for(AutoMapRemovalListener<K, V> l : removalListeners)
			l.entryRemoved(e);
	}
}
