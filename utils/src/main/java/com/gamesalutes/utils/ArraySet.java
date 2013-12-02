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
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation of the <code>Set</code> interface that uses an array. It is best suited for 
 * small sets or where space is at a premium and performance is not a big factor.  <i>This implementation
 * is not synchronized.  If synchronization is needed, then use <code>Collections.synchronizedSet</code>
 * to create a synchronization wrapper</i>.
 * 
 * @author Justin Montgomery
 * @version $Id: ArraySet.java 1125 2008-09-27 23:03:24Z jmontgomery $
 */
public final class ArraySet<E> extends AbstractSet<E> implements Serializable,Cloneable
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<E> entries;
	
	/**
	 * Constructor.
	 * 
	 */
	public ArraySet()
	{
		entries = new ArrayList<E>();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param initialCapacity the initial capacity
	 */
	public ArraySet(int initialCapacity)
	{
		entries = new ArrayList<E>(initialCapacity);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param c another <code>Collection</code>
	 */
	public ArraySet(Collection<? extends E> c)
	{
		this(c.size());
		addAll(c);
	}
	@Override
	public ArraySet<E> clone()
	{
		try
		{
			ArraySet<E> copy = (ArraySet<E>)super.clone();
			copy.entries = (ArrayList<E>)copy.entries.clone();
			return copy;
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public boolean add(E e)
	{
		boolean added = !contains(e);
		if(added)
			entries.add(e);
		return added;
	}

	@Override
	public void clear()
	{
		entries.clear();
	}

	@Override
	public boolean contains(Object o) 
	{
		return entries.contains(o);
	}

	@Override
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}

	@Override
	public boolean remove(Object o)
	{
		return entries.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<E> iterator()
	{
		return entries.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() 
	{
		return entries.size();
	}

}
