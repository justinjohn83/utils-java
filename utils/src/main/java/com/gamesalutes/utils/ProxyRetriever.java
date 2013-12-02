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

import java.util.Set;

/**
 * Interface for retrieving values by a proxy key, and optionally for noting local data structure changes.
 *  If an optional method is not implemented, then in it 
 *  must do nothing and not throw any exception.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id: ProxyRetriever.java 1282 2009-01-23 23:03:04Z jmontgomery $
 */
public interface ProxyRetriever<K,V> extends Disposable
{
	/**
	 * Returns a <code>Pair</code> where the first element indicates whether
	 * the value associated with <code>key</code> 
	 * exists and if so then the second element contains that value.
	 *  
	 * @param key the proxy key
	 * @return the result pair
	 */
	Pair<Boolean,V> lookup(K key);

	
	/**
	 * Caches the entry to avoid a possible more expensive lookup
	 * <i>(Optional operation)</i>.  Invoking this method may also cause
	 * {@link #update(Object, Object)} to be invoked.
	 *
	 * @param key the key to cache
	 * @param value the value to cache
	 * @throws IllegalArgumentException if entry does not already exist
	 */
	void addCacheEntry(K key,V value);
	
	/**
	 * Removes the cached entry key <i>(Optional operation)</i>.
	 * Invoking this method will also cause {@link #update(Object, Object)} to be invoked.
	 * 
	 * @param key the key the key in the cache
	 * @throws IllegalArgumentException if entry does not already exist
	 */
	void removeCacheEntry(K key);
	
	/**
	 * Clears the remote data <i>(Optional operation)</i>.
	 */
	void clear();
	
	/**
	 * Updates the remote data entry with the <code>key</code>,<code>value</code> pair
	 * <i>(Optional operation)</i>.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	void put(K key,V value);
	
	/**
	 * Removes the remote mapping containing the specified <code>key</code>
	 * <i>(Optional operation)</i>.
	 * 
	 * @param key the key to remove
	 */
	void remove(Object key);
	
	
	/**
	 * Updates an existing mapping's value data <i>(Optional operation)</i>.  If a new value
	 * needs to be associated with a key, then the {@link #put(Object,Object)} method
	 * should be invoked.
	 * 
	 * @param key the existing key
	 * @param value the existing value
	 * @throws IllegalArgumentException  if <code>key</code> does not already exist
	 */
    void update(K key,V value);
    
    /**
     * Returns a <code>Set</code> of all the keys mapped by this retriever.  The returned <code>Set</code> is
     * a copy.  Any changes made to the set will not reflect changes in the retriever.
     * 
     * @return <code>Set</code> or all mapped keys
     */
    Set<K> getKeySet();
	
}
