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
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Unsynchronized version of <code>java.util.Stack</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: ArrayStack.java 1225 2008-12-09 22:32:38Z jmontgomery $
 */
public final class ArrayStack<E> implements List<E>,RandomAccess,Serializable,Cloneable
{
	private ArrayList<E> stack; 
	private static final long serialVersionUID = 1L;
	
	public ArrayStack()
	{
		stack = new ArrayList<E>();
	}
	
	@Override
	public ArrayStack<E> clone()
	{
		try
		{
			ArrayStack<E> copy = (ArrayStack)super.clone();
			copy.stack = (ArrayList)copy.stack.clone();
			return copy;
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError(e);
		}
	}

	/**
	 * Pushes <code>e</code> onto the stack.
	 * 
	 * @param e the element
	 * @return <code>e</code>
	 */
	public E push(E e) 
	{
		if(stack.add(e))
			return e;
		else
			throw new IllegalStateException("unable to add: " + e);
	}

	/**
	 * Returns the top of the stack.
	 * 
	 * @throws EmptyStackException if the stack is empty
	 * @return the top element on the stack
	 */
	public E peek() 
	{
		if(stack.isEmpty())
			throw new EmptyStackException();
		return stack.get(end());
	}
	
	/**
	 * Removes the top element of the stack.
	 * 
	 * @throws EmptyStackException if the stack is empty
	 * @return the top element on the stack that was removed
	 */
	public E pop() 
	{
		if(stack.isEmpty())
			throw new EmptyStackException();
		return stack.remove(end());
	}
	
	private int end()
	{
		return stack.size() - 1;
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(E e) 
	{
		return stack.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) 
	{
		return stack.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() 
	{
		stack.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o) 
	{
		return stack.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) 
	{
		return stack.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty()
	{
		return stack.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	public Iterator<E> iterator() 
	{
		return stack.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o)
	{
		return stack.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c)
	{
		return stack.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) 
	{
		return stack.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() 
	{
		return stack.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray()
	{
		return stack.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) 
	{
		return stack.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, E element) 
	{
		stack.add(index, element);
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends E> c) 
	{
		return stack.addAll(index,c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public E get(int index) 
	{
		return stack.get(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o)
	{
		return stack.indexOf(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) 
	{
		return stack.lastIndexOf(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator() 
	{
		return stack.listIterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(int index) 
	{
		return stack.listIterator(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public E remove(int index) 
	{
		return stack.remove(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public E set(int index, E element) 
	{
		return stack.set(index, element);
	}

	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList(int fromIndex, int toIndex) 
	{
		return stack.subList(fromIndex, toIndex);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return stack.equals(o);
	}
	@Override
	public int hashCode()
	{
		return stack.hashCode();
	}
	@Override
	public String toString()
	{
		return stack.toString();
	}

}
