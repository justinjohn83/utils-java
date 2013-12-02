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
/* Copyright 2008 - 2009 University of Chicago
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
 * Map that wraps another implementation and changes the equality of the keys.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class NormalizedMap<K,V> extends ConvenienceAbstractMap<K,V> implements Serializable,Cloneable
{

	private static final long serialVersionUID = 1L;
	
	public interface KeyNormalizer<K>
	{
		public K normalize(K key);
	}
	
	private final Map<K,V> map;
	private final KeyNormalizer<K> normalizer;
	private final Class<K> keyClass;
	
	protected boolean cacheView() { return true; }

	public NormalizedMap<K,V> clone()
	{
		return new NormalizedMap(MiscUtils.deepMapCopy(map),keyClass,normalizer);
	}
	
	public NormalizedMap(Map<K,V> map,Class<K> keyClass,KeyNormalizer<K> normalizer)
	{
		if(map == null)
			throw new NullPointerException("map");
		if(keyClass == null)
			throw new NullPointerException("keyClass");
		if(normalizer == null)
			throw new NullPointerException("normalizer");
		
		this.map = map;
		this.keyClass = keyClass;
		this.normalizer = normalizer;
		
		if(!map.isEmpty())
		{
			Map<K,V> normalizedMap = CollectionUtils.createHashMap(map.size(), CollectionUtils.LOAD_FACTOR);
			for(Map.Entry<K, V> e : map.entrySet())
				normalizedMap.put(normalizer.normalize(e.getKey()), e.getValue());
			this.map.clear();
			this.map.putAll(normalizedMap);
		}
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
		if(keyClass.isInstance(key))
			return map.containsKey(normalizer.normalize(keyClass.cast(key)));
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
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key)
	{
		if(keyClass.isInstance(key))
			return map.get(normalizer.normalize(keyClass.cast(key)));
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
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value)
	{
		return map.put(normalizer.normalize(keyClass.cast(key)),value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends K, ? extends V> m)
	{
		if(m == null)
			throw new NullPointerException("m");
		for(Map.Entry<? extends K, ? extends V> E : m.entrySet())
			put(E.getKey(),E.getValue());
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object key)
	{
		if(keyClass.isInstance(key))
			return map.remove(normalizer.normalize(keyClass.cast(key)));
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
	 * @see com.gamesalutes.utils.ConvenienceAbstractMap#initKeys(java.util.Set)
	 */
	@Override
	protected void initKeys(Set<K> keySet)
	{
		keySet.addAll(map.keySet());
	}
}
