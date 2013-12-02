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
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Convenience extension of <code>AbstractMap</code> that implements
 * the view operations.
 * 
 * @author Justin Montgomery
 * @version $Id: ConvenienceAbstractMap.java 1269 2009-01-14 23:45:32Z jmontgomery $
 */
public abstract class ConvenienceAbstractMap<K,V> extends AbstractMap<K,V>
{
	private transient Set<K> keySet;
	
	@Override
	public final Set<Map.Entry<K, V>> entrySet() 
	{
		return new EntrySet();
	}


	@Override
	public final Set<K> keySet() 
	{
		return new KeySet();
	}


	@Override
	public final Collection<V> values()
	{
		return new ValueSet();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends K, ? extends V> m) 
	{
		if(m == null) throw new NullPointerException("m");
		for(Map.Entry<? extends K, ? extends V> E: m.entrySet())
			put(E.getKey(),E.getValue());
	}

	
	/**
	 * Returns a new Map.Entry with the specified key and value.  Non-modifiable subclasses should override
	 * this to return a non-modifiable entry.
	 * 
	 * @param k the key
	 * @param v the value
	 * @return the Map.Entry
	 * 
	 * @see Entry
	 */
	protected Map.Entry<K, V> newEntry(K k,V v)
	{
		return new Entry(k,v);
	}
	/**
	 * Basic <code>Map.Entry</code> implementation.  Subclasses should not have to override this
	 * unless they are non-modifiable, in which case the {@link #setValue(Object)} method should
	 * be overriden to throw an <code>UnsupportedOperationException</code>.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: ConvenienceAbstractMap.java 1269 2009-01-14 23:45:32Z jmontgomery $
	 */
	protected class Entry implements Map.Entry<K,V>,Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private final K key;
		private V value;
		
		public Entry(K k,V v)
		{
			this.key = k;
			this.value = v;
		}
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getKey()
		 */
		public K getKey() 
		{
			return key;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getValue()
		 */
		public V getValue() 
		{
			return value;
		}

				
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		public V setValue(V value) 
		{
			V prev = ConvenienceAbstractMap.this.put(key, value);
			this.value = value;
			return prev;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(!(o instanceof Map.Entry)) return false;
			Map.Entry<K,V> entry = (Map.Entry<K,V>)o;
			return MiscUtils.safeEquals(key, entry.getKey()) &&
			       MiscUtils.safeEquals(value, entry.getValue());
		}
		@Override
		public int hashCode()
		{
		     return (getKey()==null ? 0 : getKey().hashCode()) ^
		     	(getValue()==null   ? 0 : getValue().hashCode());

		}
		@Override
		public String toString()
		{
			return key + "=" + value;
		}
		
	}
	
	/**
	 * Initializes the keys for the key set view.
	 * 
	 * @param keySet the map keys
	 */
	protected abstract void initKeys(Set<K> keySet);
	
	
	/**
	 * Returns <code>true</code> if views should be cached and <code>false</code>
	 * otherwise.  Returns <code>false</code> by default.
	 * 
	 * @return whether views are cached
	 */
	protected boolean cacheView() { return false; }
	
	
	/**
	 * Should be called whenever the map is structurally modified and the view
	 * is no longer valid if the views are being cached.
	 * 
	 */
	protected final void clearView() 
	{
		if(keySet != null)
			keySet.clear();
		//System.out.println("Views cleared. Map size=" + size());
	}
	
	
	/**
	 * Should be overriden if subclasses have a distinction between 
	 * "local" entries and "remote" entries, such as <code>ProxyMap</code> and
	 * want to cache the query results locally.
	 * This default implementation simply calls {@link #get(Object)}.
	 *
	 * @param key the key to retrieve
	 * @param addLocal whether we should cache the result locally
	 * @return the mapped value or <code>null</code> if no value is mapped
	 */
	protected V get(Object key,boolean addLocal)
	{
		return get(key);
	}
	
	
	/**
	 * Gets the key associated with the value of <code>value</code>.  By default this method
	 * calls {@link #getKey(Object)} and returns the key if an entry was defined.
	 * 
	 * @param value the value for which to find the key
	 * @return the key or <code>null</code> if not found
	 */
	protected K getKey(Object value)
	{
		Map.Entry<K, V> e = getEntryByValue(value);
		if(e != null)
			return e.getKey();
		return null;
	}
	
	/**
	 * Gets the entry associated with the value of <code>value</code>. By default this method
	 * iterates over the entire map entry set and returns the first entry that has the same value
	 * as the input <code>value</code>. 
	 * 
	 * @param value the value for which to find the entry
	 * @return the entry or <code>null</code> if not found
	 */
	protected Map.Entry<K, V> getEntryByValue(Object value)
	{
    	// find the key with this value
		for(Iterator<Map.Entry<K, V>> it = 
			ConvenienceAbstractMap.this.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry<K, V>  entry = it.next();
			if(MiscUtils.safeEquals(entry.getValue(),value))
				return entry;
		}
    	return null;
	}
	
	/**
	 * Removes the entry that has a value of <code>value</code> and returns the previous
	 * key associated with that value if it was removed.
	 * 
	 * @param value the value
	 * @return the previous key or <code>null</code> if no entry removed
	 */
	protected K removeValue(Object value)
	{
		Map.Entry<K, V> e = getEntryByValue(value);
		K prev = null;
		if(e != null)
		{
			prev = e.getKey();
			remove(prev);
		}
		return prev;
		
	}
	
	/////////////////////////////////   VIEWS     ///////////////////////////////////
	private abstract class _Iterator<T> implements Iterator<T>
	{
		private final Iterator<K> idIterator;
		
		protected _Iterator()
		{
			// only create new object once since the synchronized wrapper for a map
			// sets the reference for the key set only the first time
			if(keySet == null)
				keySet = new LinkedHashSet<K>();
			else if(!cacheView())
				clearView();

			// only need to reinitialize keys if current set is empty
			if(keySet.isEmpty())
				initKeys(keySet);

			idIterator = new AbstractIterator<K>(keySet)
			{
				@Override
				protected void doRemove(K o)
				{
					ConvenienceAbstractMap.this.remove(o);
				}
			};
			
			// don't keep the keys around
			if(!cacheView())
				clearView();
		}
		public boolean hasNext()
		{
			return idIterator.hasNext();
		}
		
		public T next()
		{
			K key = idIterator.next();
			T value = nextForKey(key);
			return value;
			
		}
		
		protected abstract T nextForKey(K o);

		public void remove() 
		{
			idIterator.remove();
			/*
			if(lastKey == null || !ProxyMap.this.containsKey(lastKey))
				throw new IllegalStateException();
			ConvenienceAbstractMap.this.remove(lastKey);
			lastKey = null;
			*/
		}
		
		
		
	}
	
	
    private final class EntrySet extends AbstractSet<Map.Entry<K,V>>
    {
        public Iterator<Map.Entry<K,V>> iterator() 
        {
            return new _Iterator<Map.Entry<K, V>>()
            {
				@Override
				protected Map.Entry<K,V> nextForKey(K key) 
				{
					// possible that calling setValue on an entry causes another entry to be removed, so
					// key may no longer be a key in the map
					//if(ConvenienceAbstractMap.this.containsKey(key))
					//{
					V value = ConvenienceAbstractMap.this.get(key, false);
					return newEntry(key,value);
					//}
					//return null;
					
				}
            };
        }
        
        public boolean remove(Object o)
        {
            if(o instanceof Map.Entry)
            {
            	// remove entry from backing maps
            	Map.Entry<K,V> entry = (Map.Entry<K, V>)o;
            	K key = entry.getKey();
            	boolean removed = ConvenienceAbstractMap.this.containsKey(key);
            	if(removed)
            		ConvenienceAbstractMap.this.remove(key);
            	return removed;
            	
            }
            return false;
        }
        public int size() 
        {
            return ConvenienceAbstractMap.this.size();
        }
        public void clear()
        {
        	ConvenienceAbstractMap.this.clear();
        }
        
      
    }
    
    private final class KeySet extends AbstractSet<K>
    {
    	
        public Iterator<K> iterator() 
        {
            return new _Iterator<K>()
            {

				@Override
				protected K nextForKey(K o) 
				{
					return o;
				}
            };
        }
        public boolean remove(Object o)
        {
        	boolean removed = ConvenienceAbstractMap.this.containsKey(o);
        	if(removed)
        		ConvenienceAbstractMap.this.remove(o);
        	return removed;
        }
        public int size() 
        {
            return ConvenienceAbstractMap.this.size();
        }
        public void clear()
        {
        	 ConvenienceAbstractMap.this.clear();
        }
    }
    
    private final class ValueSet extends AbstractSet<V>
    {
    	
    	public Iterator<V> iterator()
    	{
            return new _Iterator<V>()
            {

				@Override
				protected V nextForKey(K o)
				{
					return ConvenienceAbstractMap.this.get(o);
				}
            };
    	}
        public boolean remove(Object o)
        {
        	// find the key with this value
        	boolean removed;
        	Map.Entry<K, V> e = ConvenienceAbstractMap.this.getEntryByValue(o);
        	if(e != null)
        	{
        		removed = true;
        		ConvenienceAbstractMap.this.remove(e.getKey());
        	}
        	else
        		removed = false;
        	return removed;
        		
        }
        public int size() 
        {
            return ConvenienceAbstractMap.this.size();
        }
        public void clear()
        {
        	ConvenienceAbstractMap.this.clear();
        }
 
    	
    }

}
