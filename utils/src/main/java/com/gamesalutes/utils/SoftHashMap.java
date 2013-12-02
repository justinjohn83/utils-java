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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Hashtable-based map implementation that has "soft keys."  Soft references
 * are described in <code>java.lang.ref.SoftReference</code>.  Basically,
 * they are held on more strongly in memory than the weak references used
 * in <code>WeakHashMap</code>, but are guaranteed to be garbage-collected before a
 * <code>java.lang.OutofMemoryError</code> is thrown.  Objects of this class can be
 * used to implement memory-sensitive caches.  This implementation is unsynchronized.
 * Synchronization can be obtained by wrapping this Collection in a synchronized map
 * using <code>Collections.synchronizedMap</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: SoftHashMap.java 1731 2009-10-15 18:32:46Z jmontgomery $
 */
public final class SoftHashMap<K,V> implements Map<K,V>
{
	// since there is no "soft reference" map, must use weak hash map, and then
	// keep a collection of soft references to the keys to keep the weak references
	// from being collected too soon.
	// It is unnecessary to make the values a soft reference since if memory demands are great,
	// the keys will be collected, which will remove the value mapping as well and allow the
	// mapped value to be collected
	private final WeakHashMap<K,V> map;
	
	private final Set<SoftReference<K>> softKeys;
	// use reference queue to remove soft references from the set
	// when their referents are collected
	private final ReferenceQueue<K> refQueue;
	
	private final Set<AutoMapRemovalListener<K, V>> removalListeners = 
		new LinkedHashSet<AutoMapRemovalListener<K, V>>();
	
	
	// operations poll the reference queue to remove collected references
	// from the soft reference set
	
	/**
	 * Constructor.
	 * Creates an empty map with the default load factor (0.75) and a 
	 * defaul initial capacity (16).
	 * 
	 */
	public SoftHashMap()
	{
		this.map = new WeakHashMap<K,V>();
		this.softKeys = new HashSet<SoftReference<K>>();
		this.refQueue = new ReferenceQueue<K>();
	}
	
	/**
	 * Constructor.
	 * Creates a map with the given initial capacity.
	 * 
	 * @param initialCapacity the initial capacity
	 */
	public SoftHashMap(int initialCapacity)
	{
		this.map = new WeakHashMap<K,V>(initialCapacity);
		this.softKeys = new HashSet<SoftReference<K>>(initialCapacity);
		this.refQueue = new ReferenceQueue<K>();
	}
	
	/**
	 * Constructor.
	 * Creates a map with the given initial capacity and load factor.
	 * 
	 * @param initialCapacity the initial capacity
	 * @param loadFactor the load factor
	 */
	public SoftHashMap(int initialCapacity, float loadFactor)
	{
		this.map = new WeakHashMap<K,V>(initialCapacity,loadFactor);
		this.softKeys = new HashSet<SoftReference<K>>(initialCapacity,loadFactor);
		this.refQueue = new ReferenceQueue<K>();
	}
	public SoftHashMap(Map<? extends K,? extends V> m)
	{
		this.map = new WeakHashMap<K,V>(m);
		this.refQueue = new ReferenceQueue<K>();
		float loadFactor = 0.75f;
		this.softKeys = new HashSet<SoftReference<K>>(
				CollectionUtils.calcHashCapacity(m.size(), loadFactor),
				loadFactor);
		// add soft references to all the keys in the map
		addSoftKeys(m);
		
		
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
	
	private void notifyListeners()
	{
		for(AutoMapRemovalListener<K, V> l : removalListeners)
			l.entryRemoved(null);
	}
	
	private void addSoftKeys(Map<? extends K,? extends V> m)
	{
		for(K key : m.keySet())
			softKeys.add(new SoftReference<K>(key,refQueue));
	}
	
	// poll the queues and remove the references from the set
	// if their referents have been collected
	private void pollRefQueue()
	{
		Reference<? extends K> key;
		boolean purged = false;
		while((key = refQueue.poll()) != null)
		{
			purged = true;
			softKeys.remove(key);
		}
		if(purged)
			notifyListeners();
	}
	
	/**
	 * Explicitly purges expired entries.
	 * 
	 */
	public void purge()
	{
		pollRefQueue();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() 
	{
		map.clear();
		softKeys.clear();
	}


	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key)
	{
		pollRefQueue();
		return map.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) 
	{
		pollRefQueue();
		return map.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<Map.Entry<K, V>> entrySet() 
	{
		return map.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key) 
	{
		pollRefQueue();
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() 
	{
		pollRefQueue();
		return map.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<K> keySet() 
	{
		return map.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value) 
	{
		pollRefQueue();
		softKeys.add(new SoftReference<K>(key,refQueue));
		return map.put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends K,? extends V> m) 
	{
		pollRefQueue();
		// add soft references to all the keys in the map
		addSoftKeys(m);
		map.putAll(m);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object key)
	{
		pollRefQueue();
		return map.remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() 
	{
		pollRefQueue();
		return map.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<V> values() 
	{
		return map.values();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return map.equals(o);
	}
	@Override
	public int hashCode()
	{
		return map.hashCode();
	}
	@Override
	public String toString()
	{
		return map.toString();
	}

}
