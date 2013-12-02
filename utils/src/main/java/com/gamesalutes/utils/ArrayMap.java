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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Array implementation of a map that is more memory conservative than the hash or tree
 * implementations, but whose operations are all O(n).  This should only be used to store
 * a few entries or where performance is not important.
 *  
 * @author Justin Montgomery
 * @version $Id: ArrayMap.java 1131 2008-09-30 17:32:05Z jmontgomery $
 */
public final class ArrayMap<K,V> extends ConvenienceAbstractMap<K, V> implements Serializable,Cloneable
{
	private static final long serialVersionUID = 1L;
	
	private List<Pair<K,V>> entries;
	
	private static final int DEFAULT_CAPACITY = 16;
	
	public ArrayMap()
	{
		this(DEFAULT_CAPACITY);
	}
	
	public ArrayMap(int initialCapacity)
	{
		this.entries = new ArrayList<Pair<K,V>>(initialCapacity);
	}
	public ArrayMap(Map<? extends K,? extends V> m)
	{
		this(m.size());
		putAll(m);
	}
	
	@Override
	public ArrayMap<K,V> clone()
	{
		try
		{
			ArrayMap<K,V> copy = (ArrayMap<K,V>)super.clone();
			// copy the entries
			List<Pair<K,V>> origEntries = copy.entries;
			List<Pair<K,V>> newEntries = new ArrayList<Pair<K,V>>(origEntries.size());
			for(Pair<K,V> p : origEntries)
				newEntries.add(new Pair<K,V>(p));
			copy.entries = newEntries;
			
			return copy;
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError(e);
		}
	}
	
	
	@Override
	protected K getKey(Object value) 
	{
		for(Pair<K,V> p : entries)
		{
			if(MiscUtils.safeEquals(p.second, value))
					return p.first;
		}
		return null;
	}

	@Override
	protected K removeValue(Object value) 
	{
		K key = null;
		for(Iterator<Pair<K,V>> it = entries.iterator(); it.hasNext();)
		{
			Pair<K,V> p = it.next();
			if(MiscUtils.safeEquals(p.second, value))
			{
				it.remove();
				key = p.first;
				break;
			}
		}
		return key;
	}

	@Override
	public void clear() 
	{
		entries.clear();
	}

	@Override
	public boolean containsKey(Object key) 
	{
		for(Pair<K,V> p : entries)
		{
			if(MiscUtils.safeEquals(p.first, key))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value)
	{
		for(Pair<K,V> p : entries)
		{
			if(MiscUtils.safeEquals(p.second, value))
				return true;
		}
		return false;
	}

	@Override
	public V get(Object key) 
	{
		for(Pair<K,V> p : entries)
		{
			if(MiscUtils.safeEquals(p.first, key))
				return p.second;
		}
		return null;
	}

	@Override
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}

	@Override
	public V put(K key, V value)
	{
		boolean found = false;
		V prev = null;
		for(Iterator<Pair<K,V>> it = entries.iterator(); it.hasNext();)
		{
			Pair<K,V> p = it.next();
			if(MiscUtils.safeEquals(p.first, key))
			{
				found = true;
				prev = p.second;
				p.second = value;
				break;
			}
		}
		if(!found)
			entries.add(Pair.makePair(key, value));
		return prev;
	}

	@Override
	public V remove(Object key)
	{
		V prev = null;
		for(Iterator<Pair<K,V>> it = entries.iterator(); it.hasNext();)
		{
			Pair<K,V> p = it.next();
			if(MiscUtils.safeEquals(p.first, key))
			{
				prev = p.second;
				it.remove();
				break;
			}
		}
		return prev;
	}

	@Override
	public int size()
	{
		return entries.size();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.ConvenienceAbstractMap#getKeys()
	 */
	@Override
	protected void initKeys(Set<K> keys)
	{
		for(Pair<K,V> p : entries)
			keys.add(p.first);
	}

}
