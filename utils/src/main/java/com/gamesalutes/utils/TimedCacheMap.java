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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map that expires entries based on a last update time threshold. Actual storage is passed through a backed map.
 * 
 * @author jmontgomery
 *
 * @param <K> the key 
 * @param <V> the value
 */
public final class TimedCacheMap<K,V> implements Map<K, V>,Serializable {

	private final Map<K,V> storage;
	private final Map<K,Long> timestampMap;
	private final long timeout;
	
	private final Set<TimeoutListener<K, V>> listeners = 
			new LinkedHashSet<TimeoutListener<K, V>>();
	
	private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
	
	
	private static final long serialVersionUID = 1L;
	
	public interface TimeoutListener<K,V> {
		public void onTimeout(K key,V value);
	}
	
	
	public boolean addTimeoutListener(TimeoutListener<K, V> l)
	{
		if(l == null)
			throw new NullPointerException("l");
		return listeners.add(l);
	}
	public boolean removeTimeoutListener(TimeoutListener<K, V> l)
	{
		return listeners.remove(l);
	}
	
	private void notifyListeners(K key,V value)
	{
		for(TimeoutListener<K, V> l : listeners) {
			try {
				l.onTimeout(key,value);
			}
			catch(Exception e) {
				logger.warn("Exception calling onTimeout for listener=" + l,e);
			}
		}
	}
	
	public TimedCacheMap(Map<K,V> storage,long timeoutMs) {
		if(storage == null) {
			throw new NullPointerException("storage");
		}
		this.storage = storage;
		this.timestampMap = populateTimestamps(storage);
		this.timeout = timeoutMs;
	}
	
	private Long getTimestamp() {
		return Long.valueOf((long)(System.nanoTime() / 1e6));
	}
	private Map<K,Long> populateTimestamps(Map<? extends K,? extends V> storage) {
		Map<K,Long> map = CollectionUtils.createHashMap(storage.size() > 0 ? storage.size() : 16,CollectionUtils.LOAD_FACTOR);
		
		populateTimestamps(storage,map);
		
		return map;
	}
	
	private void populateTimestamps(Map<? extends K,? extends V> storage,Map<K,Long> timestamps) {
		Long timestamp = getTimestamp();
		
		for(K key : storage.keySet()) {
			timestamps.put(key, timestamp);
		}
	}
	public void clear() {
		storage.clear();
		timestampMap.clear();
	}
	
	private void pollTimes() {
		
		if(timeout < 0) {
			return;
		}
		
		long now = getTimestamp();
		long threshold = now - timeout;
		
		for(Iterator<Map.Entry<K, Long>> i = timestampMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry<K,Long> timeEntry = i.next();
			K key = timeEntry.getKey();
			Long time = timeEntry.getValue();
			
			// expired
			if(time < threshold) {
				i.remove();
				V value = storage.remove(key);
				
				notifyListeners(key,value);
			}
			
			//System.out.println("diff=" + (time - threshold));
			
		}
	}

	public boolean containsKey(Object key) {
		pollTimes();
		return storage.containsKey(key);
	}

	public boolean containsValue(Object value) {
		pollTimes();
		return storage.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return storage.entrySet();
	}

	public V get(Object key) {
		pollTimes();
		return storage.get(key);
	}

	public boolean isEmpty() {
		pollTimes();
		return storage.isEmpty();
	}

	public Set<K> keySet() {
		return storage.keySet();
	}

	public V put(K key, V value) {
		pollTimes();
		this.timestampMap.put(key, getTimestamp());
		
		return storage.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		pollTimes();
		this.populateTimestamps(m, this.timestampMap);
		
		storage.putAll(m);
	}

	public V remove(Object key) {
		pollTimes();
		timestampMap.remove(key);
		return storage.remove(key);
	}

	public int size() {
		pollTimes();
		return storage.size();
	}

	public Collection<V> values() {
		return storage.values();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return storage.equals(o);
	}
	@Override
	public int hashCode()
	{
		return storage.hashCode();
	}
	@Override
	public String toString()
	{
		return storage.toString();
	}

}
