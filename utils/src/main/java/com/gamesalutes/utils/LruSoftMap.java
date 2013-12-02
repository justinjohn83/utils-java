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

import java.util.Map;
import java.util.Set;
import static com.gamesalutes.utils.LruMap.*;
/**
 * An <code>LruMap</code> that also maintains soft references to purged entries.  The LRU symantics
 * put a lower bound on the size of the map, and the upper bound is limited by memory availability.
 * This implementation is good for caches that want to maintain at least some number of entries in memory
 * at all times.  Not that since this implementation also uses a <code>SoftHashMap</code>, it is
 * <i><b>not Serializable nor Cloneable</b></i>.  It also does not return its entries in a predictable manner.
 * This implementation is unsynchronized. Synchronization can be obtained by wrapping this 
 * Collection in a synchronized map using <code>Collections.synchronizedMap</code>.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id: LruSoftMap.java 1131 2008-09-30 17:32:05Z jmontgomery $
 */
public final class LruSoftMap<K,V> extends ConvenienceAbstractMap<K,V>
{
	// map that maintains the soft entries that get purged from the LruMap
	private final SoftHashMap<K,V> softMap;
	private final LruMap<K,V> lruMap;
	
//	private final AutoMapRemovalListener<K, V> softMapListener = 
//		new SoftMapListener();
	private final AutoMapRemovalListener<K,V> lruMapListener =
		new LruMapListener();
	
	private static final float MAX_ENTRY_MULT = 0.25f;
	
	public LruSoftMap()
	{
		SoftHashMap<K,V> m = new SoftHashMap<K,V>(
				CollectionUtils.calcHashCapacity((int)(LruMap.DEFAULT_MAX_SIZE * MAX_ENTRY_MULT),
						LruMap.LOAD_FACTOR),LruMap.LOAD_FACTOR);
//		m.addAutoRemovalListener(softMapListener);
		softMap = m;
		lruMap = new LruMap<K,V>();
		lruMap.addAutoRemovalListener(lruMapListener);
	}
	public LruSoftMap(int maxEntries, float loadFactor) 
	{
		lruMap = new LruMap<K,V>(maxEntries,loadFactor);
		lruMap.addAutoRemovalListener(lruMapListener);
		SoftHashMap<K,V> m = new SoftHashMap<K,V>(
				CollectionUtils.calcHashCapacity((int)(maxEntries * MAX_ENTRY_MULT), loadFactor),loadFactor);
//		m.addAutoRemovalListener(softMapListener);
		softMap = m;
	}
	public LruSoftMap(int maxEntries) 
	{
		lruMap = new LruMap<K,V>(maxEntries);
		lruMap.addAutoRemovalListener(lruMapListener);
		SoftHashMap<K,V> m = new SoftHashMap<K,V>(
				CollectionUtils.calcHashCapacity((int)(maxEntries * MAX_ENTRY_MULT), LOAD_FACTOR),LOAD_FACTOR);
//		m.addAutoRemovalListener(softMapListener);
		softMap = m;
	}
	public LruSoftMap(Map<? extends K, ? extends V> m)
	{
		this(m.size(),LruMap.LOAD_FACTOR);
		putAll(m);
	}
	
	public void clear() 
	{
		lruMap.clear();
		softMap.clear();
//		clearView();
	}
	
	public boolean containsValue(Object value)
	{
		boolean result =  lruMap.containsValue(value);
		if(!result)
			result = softMap.containsValue(value);
		return result;
	}
	public V get(Object key) 
	{
		//System.out.println("LruSoftMapSize=" + size() + " time = " + new java.util.Date());
		V result = lruMap.get(key);
		if(result == null)
			result = softMap.get(key);
		return result;
	}
	
	public boolean containsKey(Object key) 
	{
		boolean result = lruMap.containsKey(key);
		if(!result)
			result = softMap.containsKey(key);
		return result;
	}
	
	public boolean isEmpty()
	{
		return lruMap.isEmpty() && softMap.isEmpty();
	}
	
	public V put(K key, V value) 
	{
		// remove from soft map
		boolean changed = false;
		V prev = null;
		if(softMap.containsKey(key))
		{
			changed = true;
			prev = softMap.remove(key);
		}
		
		// add it to LRU map
		if(lruMap.containsKey(key))
		{
			changed = true;
			prev = lruMap.put(key, value);
		}
		else
			lruMap.put(key, value);

//		if(changed)
//			clearView();
		return prev;
			
	}
	
	public void putAll(Map<? extends K, ? extends V> m) 
	{
		// remove all from soft map
		for(K key : m.keySet())
			softMap.remove(key);
//		clearView();
		lruMap.putAll(m);
	}
	
	public V remove(Object key) 
	{
		// remove from soft map
		boolean changed = false;
		V prev = null;
		if(softMap.containsKey(key))
		{
			changed = true;
			prev = softMap.remove(key);
		}
		if(lruMap.containsKey(key))
		{
			// remove it from the LRU map
			prev = lruMap.remove(key);
			changed = true;
		}

//		if(changed)
//			clearView();
		return prev;
	}
	
	public int size() 
	{
		return lruMap.size() + softMap.size();
	}
	
	
	@Override
	public void initKeys(Set<K> keys)
	{
		keys.addAll(lruMap.keySet());
		keys.addAll(softMap.keySet());
	}
	
	/*
	private Object writeReplace() throws ObjectStreamException
	{
		throw new NotSerializableException();
	}
	private Object readResolve() throws ObjectStreamException
	{
		throw new NotSerializableException();
	}
	
	@Override
	public LruSoftMap<K,V> clone()
	{
		throw new UnsupportedOperationException();
	}
	*/
	
    
   
    
//    private final class SoftMapListener implements AutoMapRemovalListener<K, V>
//    {
//
//		/* (non-Javadoc)
//		 * @see com.gamesalutes.utils.SoftHashMap.AutoRemovalListener#entryRemoved()
//		 */
//		public void entryRemoved(Map.Entry<K, V> e) 
//		{
//			clearView();
//		}
//    	
//    }
    
    private final class LruMapListener implements AutoMapRemovalListener<K,V>
    {

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.AutoMapRemovalListener#entryRemoved(java.util.Map.Entry)
		 */
		public void entryRemoved(Map.Entry<K, V> e)
		{
			// add it to the soft map if it is being removed from the LruMap
			softMap.put(e.getKey(), e.getValue());
			
			//System.out.println("lruMapSize=" + lruMap.size());
		}
    	
    }

// caching the view prevents the entries in the soft map from being collected!!!
//	@Override
//	protected boolean cacheView() { return true; }
    



}
