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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Map that can store only some of its mappings locally, while others must be
 * retrieved externally.  Use this class when values are memory intensive
 * relative to the keys.  The keys of the input <code>Map</code> and the 
 * <code>Collection</code> of all keys must be wrapped using <code>Wrapper</code>.
 * All subsequent access to the map uses the key type <code>K</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: ProxyMap.java 1269 2009-01-14 23:45:32Z jmontgomery $
 */
public final class ProxyMap<K,V> 
	extends ConvenienceAbstractMap<K,V> implements Serializable,Disposable 
{
	private static final long serialVersionUID = 1L;
	
	private final Map<Wrapper<K>,V> data;
	private final Set<Wrapper<K>> ids;
	private transient ProxyRetriever<K,V> retriever;
	
	
	/**
	 * Map implementation that does not store any entries.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: ProxyMap.java 1269 2009-01-14 23:45:32Z jmontgomery $
	 * 
	 * @param <K> key type
	 * @param <V> value type
	 */
	private static class NullMap<K,V> extends AbstractMap<K,V> implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		public NullMap() {}
		
		@Override
		public void clear() {}

		@Override
		public boolean isEmpty() { return true; }

		@Override
		public V put(K key, V value) { return null; }

		@Override
		public V remove(Object key) { return null; }

		@Override
		public int size() { return 0; }

		@Override
		public Set<java.util.Map.Entry<K, V>> entrySet()
		{
			return Collections.<java.util.Map.Entry<K,V>>emptySet();
		}
		
	}
	/**
	 * Constructor.
	 * 
	 * @param data the local data <code>Map</code> or <code>null</code> to not use local storage
	 * @param allKeys all the currently defined keys
	 * @throws NullPointerException if allKeys is <code>null</code>
	 */
	public ProxyMap(Map<Wrapper<K>,V> data,Collection<Wrapper<K>> allKeys)
	{
		if(allKeys == null)
			throw new NullPointerException("allKeys");
		if(data == null)
			data = new NullMap<Wrapper<K>,V>();
		if(data.containsKey(null))
			throw new IllegalArgumentException("data cannot contain null key");
		if(!allKeys.containsAll(data.keySet()))
			throw new IllegalArgumentException();
		this.data = data;
		// make sure that ids do not keep a hard reference to same keys in data
		this.ids = new HashSet<Wrapper<K>>(CollectionUtils.calcHashCapacity(
				allKeys.size(), CollectionUtils.LOAD_FACTOR),CollectionUtils.LOAD_FACTOR);
		for(Wrapper<K> w : allKeys)
			this.ids.add(w.clone());
		
	}
	
	/**
	 * Constructor.  Does not store any data locally.  All map operations requiring value
	 * lookup must use the set {@link ProxyRetriever ProxyRetriever}.
	 * 
	 * @param allKeys all the currently defined keys
	 * @throws NullPointerException if allKeys is <code>null</code>
	 */
	public ProxyMap(Collection<Wrapper<K>> allKeys)
	{
		this(null,allKeys);
	}
	
	/**
	 * Constructor. Does not store any data locally.  Initial ids are empty.  All map operation requiring
	 * value lookup must use the set {@link ProxyRetriever ProxyRetriever}.
	 * 
	 * 
	 * 
	 * 
	 */
	public ProxyMap() {
		this(null,Collections.<Wrapper<K>>emptySet());
	}
	/**
	 * Sets the {@link ProxyRetriever}.
	 * 
	 * @param retriever the <code>ProxyRetriever</code>
	 */
	public void setRetriever(ProxyRetriever<K,V> retriever)
	{
		if(retriever == null)
			throw new NullPointerException("retriever");
		this.retriever = retriever;
		
		// copy the key set into the ids
		Set<K> retrieverIds = this.retriever.getKeySet();
		if(!MiscUtils.isEmpty(retrieverIds))
		{
			for(K id : retrieverIds)
			{
				this.ids.add(new Wrapper<K>(id,false));
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() 
	{
		ids.clear();
		data.clear();
		if(retriever != null)
			retriever.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key)
	{
		return ids.contains(new Wrapper<Object>(key,false));
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key) 
	{
		return get(key,true);
	}
	@SuppressWarnings("unchecked")
	protected V get(Object key,boolean addLocal)
	{
		// make sure key is in valid ids
		if(!containsKey(key)) return null;
		
		
		Wrapper wk = new Wrapper(key,false);
		// do a get and not containsKey and then get since
		// a collection on a cache map could be performed in between
		// the containsKey and get operation
		V value = data.get(wk);
		if(value != null || data.containsKey(wk))
			return value;
		
		// else must do a lookup
		if(retriever == null)
			throw new IllegalStateException("No ProxyRetriever!");
		K kKey = (K)key;
		Pair<Boolean,V> found = retriever.lookup(kKey);
		if(addLocal && found.first)
			data.put(wk, found.second);
		return found.second;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() 
	{
		return ids.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value)
	{
		if(key == null) throw new NullPointerException("key");
		V prev = null;
		if(containsKey(key))
			prev = get(key);
		
		Wrapper<K> wk = new Wrapper<K>(key,false);
		data.put(wk, value);
		// make sure that ids do not keep hard reference to key in data otherewise
		// if data is a cache map then the cache value may never be purged
		ids.add(wk.clone());
		if(retriever != null)
			retriever.put(key, value);
		
		return prev;
		
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object key) 
	{
		if(key == null) return null;
		V prev = null;
		if(containsKey(key))
			prev = get(key);
		else
			return null;
		
		Wrapper<Object> wk = new Wrapper<Object>(key,false);
		data.remove(wk);
		ids.remove(wk);	
		if(retriever != null)
			retriever.remove(key);
		
		return prev;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() 
	{
		return ids.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value)
	{
		if(data.containsValue(value)) return true;
		// must do a proxy search
		Set<Wrapper<K>> toSearch = new HashSet<Wrapper<K>>(ids);
		toSearch.removeAll(data.keySet());
		
		if(retriever == null)
			throw new IllegalStateException("No ProxyRetriever!");
		for(Wrapper<K> w : toSearch)
		{
			Pair<Boolean,V> p = retriever.lookup(w.get());
			if(p.first && MiscUtils.safeEquals(p.second, value))
				return true;
		}
		return false;
		
	}

	@Override
	protected void initKeys(Set<K> keys)
	{
		for(Wrapper<K> w : ids)
			keys.add(w.get());
	}

	public void dispose() 
	{
		if(retriever != null)
		{
			retriever.dispose();
		}
	}

}
