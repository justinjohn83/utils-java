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


import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Hash-map implementation of <code>DualMap</code> using two hash maps: one storing keys to values and 
 * the other values to keys.  The mapping between keys and values is 1:1 so adding a key may remove 1 or 2
 * other mappings if the key or value inserted exists as a either a key or value.
 * 
 *  This implementation is unsynchronized. Synchronization can be obtained by wrapping this 
 * Collection in a synchronized map using <code>Collections.synchronizedMap</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: DualHashBidiMap.java 1271 2009-01-16 20:45:44Z jmontgomery $
 */
public final class DualHashBidiMap<K,V> extends ConvenienceAbstractMap<K,V> 
	implements BidiMap<K, V>,Serializable,Cloneable 
{
	private static final long serialVersionUID = 1L;
	
	private Map<K,V> keyMap;
	private transient Map<V,K> valueMap;
	
	/**
	 * Constructor.
	 * Creates an empty map with the default load factor (0.75) and a 
	 * defaul initial capacity (16).
	 * 
	 */
	public DualHashBidiMap()
	{
		this.keyMap = new HashMap<K,V>();
		this.valueMap = new HashMap<V,K>();
	}
	
	/**
	 * Constructor.
	 * Creates a map with the given initial capacity.
	 * 
	 * @param initialCapacity the initial capacity
	 */
	public DualHashBidiMap(int initialCapacity)
	{
		this.keyMap = new HashMap<K,V>(initialCapacity);
		this.valueMap = new HashMap<V,K>(initialCapacity);
	}
	
	/**
	 * Constructor.
	 * Creates a map with the given initial capacity and load factor.
	 * 
	 * @param initialCapacity the initial capacity
	 * @param loadFactor the load factor
	 */
	public DualHashBidiMap(int initialCapacity, float loadFactor)
	{
		this.keyMap = new HashMap<K,V>(initialCapacity,loadFactor);
		this.valueMap = new HashMap<V,K>(initialCapacity,loadFactor);
	}
	public DualHashBidiMap(Map<? extends K,? extends V> m)
	{
		if(m == null) throw new NullPointerException("m");
		final float lf = 0.75f;
		final int sz = m.size();
		this.keyMap = new HashMap<K,V>(CollectionUtils.calcHashCapacity(sz, lf),lf);
		this.valueMap = new HashMap<V,K>(CollectionUtils.calcHashCapacity(sz, lf),lf);
		this.putAll(m);
	}
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.DualMap#getKey(java.lang.Object)
	 */
	public K getKey(Object value)
	{
		return valueMap.get(value);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.DualMap#removeKey(java.lang.Object)
	 */
	public K removeValue(Object value) 
	{
		K key = valueMap.remove(value);
		keyMap.remove(key);
		return key;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() 
	{
		valueMap.clear();
		keyMap.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) 
	{
		return keyMap.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value)
	{
		return valueMap.containsKey(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key)
	{
		return keyMap.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		return keyMap.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value)
	{
		// add new mappings
		V prevValue = keyMap.put(key, value);
		K prevKey = valueMap.put(value, key);
		
		// must remove the previous key and value associations to ensure 1:1 relation
		// except in case where putting over same key,value pair
		// since in that case would remove the pair just inserted
		if(!MiscUtils.safeEquals(key, prevKey) && !MiscUtils.safeEquals(value, prevValue))
		{
			keyMap.remove(prevKey);
			valueMap.remove(prevValue);
		}
		
		return prevValue;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object key)
	{
		V prev = keyMap.remove(key);
		valueMap.remove(prev);
		return prev;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() 
	{
		return keyMap.size();
	}

	
	@Override
	public boolean equals(Object o)
	{
		return keyMap.equals(o);
	}
	
	@Override
	public int hashCode()
	{
		return keyMap.hashCode();
	}
	@Override
	public String toString()
	{
		return keyMap.toString();
	}
	
	@Override
	public DualHashBidiMap<K,V> clone()
	{
		DualHashBidiMap<K,V> copy = null;
		try
		{
			copy = (DualHashBidiMap<K,V>)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError();
		}
		// deep copy the mappings
		copy.keyMap = new HashMap<K,V>(copy.keyMap);
		copy.valueMap = new HashMap<V,K>(copy.valueMap);
		
		return copy;
	}
	 private void writeObject(java.io.ObjectOutputStream out)
     throws IOException
     {
		 out.defaultWriteObject();
     }
     private void readObject(java.io.ObjectInputStream in)
     	throws IOException, ClassNotFoundException
     {
    	 in.defaultReadObject();
    	 // build the value map
    	 final float lf = 0.75f;
    	 final int sz = keyMap.size();
    	 valueMap = new HashMap<V,K>(CollectionUtils.calcHashCapacity(sz,lf),lf);
    	 for(Map.Entry<K, V> E : keyMap.entrySet())
    		 valueMap.put(E.getValue(), E.getKey());
     }

	@Override
	protected java.util.Map.Entry<K, V> getEntryByValue(Object value)
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
	protected void initKeys(Set<K> keys)
	{
		keys.addAll(keyMap.keySet());
	}
    

}
