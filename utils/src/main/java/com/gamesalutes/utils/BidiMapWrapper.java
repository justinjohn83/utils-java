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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A <code>BidiMap</code> wrapper for a regular <code>Map</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: BidiMapWrapper.java 1271 2009-01-16 20:45:44Z jmontgomery $
 */
final class BidiMapWrapper<K,V> implements BidiMap<K, V>,Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final Map<K,V> map;
	
	
	public BidiMapWrapper(Map<K,V> m)
	{
		if(m == null)
			throw new NullPointerException("m");
		this.map = m;
	}
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.BidiMap#getKey(java.lang.Object)
	 */
	public K getKey(Object value) 
	{
		for(Map.Entry<K, V> e : map.entrySet())
		{
			if(MiscUtils.safeEquals(e.getValue(), value))
				return e.getKey();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.BidiMap#removeValue(java.lang.Object)
	 */
	public K removeValue(Object value) 
	{
		K key = null;
		for(Iterator<Map.Entry<K,V>> it = map.entrySet().iterator();it.hasNext();)
		{
			Map.Entry<K, V> e = it.next();
			if(MiscUtils.safeEquals(e.getValue(), value))
			{
				key = e.getKey();
				it.remove();
				break;
			}
		}
		return key;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear()
	{
		map.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) 
	{
		return map.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) 
	{
		return map.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<java.util.Map.Entry<K, V>> entrySet() 
	{
		return map.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key)
	{
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() 
	{
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
		// remove previous key associated with this value to ensure 1:1 relation between
		// keys and values
		K prevKey = this.removeValue(value);
		V prevValue = map.put(key, value);
		
		// must remove the previous key and value associations to ensure 1:1 relation
		// except in case where putting over same key,value pair
		// since in that case would remove the pair just inserted
		if(!MiscUtils.safeEquals(key, prevKey))
			map.remove(prevKey);
		
		return prevValue;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends K, ? extends V> m)
	{
		if(m == null)
			throw new NullPointerException("m");
		for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
			this.put(e.getKey(), e.getValue());
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object key) 
	{
		return map.remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() 
	{
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
	public boolean equals(Object obj) 
	{
		return map.equals(obj);
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
