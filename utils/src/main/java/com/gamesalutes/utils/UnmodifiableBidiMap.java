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
import java.util.Map;
import java.util.Set;

/**
 * Unmodifiable version of <code>BidiMap</code> where mutating operations
 * throw a <code>UnsupportedOperationException</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: UnmodifiableBidiMap.java 1269 2009-01-14 23:45:32Z jmontgomery $
 */
final class UnmodifiableBidiMap<K,V> extends ConvenienceAbstractMap<K, V>
		implements BidiMap<K, V>,Serializable
{

	public static final long serialVersionUID = 1L;
	
	private final BidiMap<K,V> map;
	
	public UnmodifiableBidiMap(BidiMap<K,V> m)
	{
		if(m == null) throw new NullPointerException("m");
		this.map = m;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Map.Entry<K,V> newEntry(final K k, final V v)
	{
		
		return new ConvenienceAbstractMap.Entry(k,v)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object setValue(Object value)
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	@Override
	protected boolean cacheView() 
	{
		return true;
	}
	
	@Override
	protected Map.Entry<K, V> getEntryByValue(Object value)
	{
		if(containsValue(value))
		{
			K key = getKey(value);
			V mapValue = get(key);
			
			return newEntry(key,mapValue);
		}
		else
			return null;
	}

	@Override
	public K getKey(Object value) 
	{
		return map.getKey(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public K removeValue(Object value) 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) 
	{
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) 
	{
		return map.containsValue(value);
	}

	@Override
	public boolean equals(Object o) 
	{
		return map.equals(o);
	}

	@Override
	public V get(Object key) 
	{
		return map.get(key);
	}

	@Override
	public int hashCode() 
	{
		return map.hashCode();
	}

	@Override
	public boolean isEmpty() 
	{
		return map.isEmpty();
	}

	@Override
	public V put(K key, V value) 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() 
	{
		return map.size();
	}

	@Override
	public String toString() 
	{
		return map.toString();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.ConvenienceAbstractMap#getKeys()
	 */
	@Override
	protected void initKeys(Set<K> keys) 
	{
		keys.addAll(map.keySet());
	}

}
