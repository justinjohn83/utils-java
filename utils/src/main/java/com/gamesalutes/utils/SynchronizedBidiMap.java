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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Synchronization wrapper for <code>BidiMap</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: SynchronizedBidiMap.java 1117 2008-09-24 21:48:09Z jmontgomery $
 */
final class SynchronizedBidiMap<K,V> implements BidiMap<K, V>,Serializable 
{
	private static final long serialVersionUID = 1L;
	
	
	private final BidiMap<K,V> map;
	
	public SynchronizedBidiMap(BidiMap<K,V> m)
	{
		if(m == null) throw new NullPointerException("m");
		this.map = m;
	}
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.BidiMap#getKey(java.lang.Object)
	 */
	public synchronized K getKey(Object value)
	{
		return map.getKey(value);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.BidiMap#removeValue(java.lang.Object)
	 */
	public synchronized K removeValue(Object value) 
	{
		return map.removeValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public synchronized void clear()
	{
		map.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public synchronized boolean containsValue(Object value) 
	{
		return map.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public synchronized Set<java.util.Map.Entry<K, V>> entrySet() 
	{
		return map.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public synchronized V get(Object key) 
	{
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public synchronized boolean isEmpty()
	{
		return map.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public synchronized Set<K> keySet() 
	{
		return map.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized V put(K key, V value)
	{
		return map.put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public synchronized void putAll(Map<? extends K, ? extends V> m) 
	{
		map.putAll(m);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public synchronized V remove(Object key) 
	{
		return map.remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public synchronized int size() 
	{
		return map.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public synchronized Collection<V> values() 
	{
		return map.values();
	}
	
	@Override
	public synchronized boolean equals(Object o)
	{
		return map.equals(o);
	}
	@Override
	public synchronized int hashCode()
	{
		return map.hashCode();
	}
	@Override
	public synchronized String toString()
	{
		return map.toString();
	}

}
