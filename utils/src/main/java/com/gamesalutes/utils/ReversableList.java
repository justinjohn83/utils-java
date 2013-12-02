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

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A list that can be reversed without modifying the underlying list. The list methods are simply applied
 * in reverse.  Also, reversing the list will simply reverse the order of any sublists created not change their
 * ranges.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ReversableList<E> extends AbstractList<E>
{

	private final List<E> list;
	private boolean reversed = false;
	
	public ReversableList(List<E> l,boolean reverse)
	{
		if(l == null)
			throw new NullPointerException("l");
		this.list = l;
		this.reversed = reverse;
	}
	
	public ReversableList(List<E> l)
	{
		this(l,false);
	}
	
	/**
	 * Reverses the order of the list.
	 * 
	 */
	public void reverse()
	{
		this.reversed = !reversed;
	}
	
	@Override
	public boolean add(E e)
	{
		if(reversed)
		{
			list.add(0,e);
			return true;
		}
		return list.add(e);
	}

	@Override
	public void add(int index, E element)
	{
		if(reversed)
			list.add(list.size() - index, element);
		else
			list.add(index,element);
	}

	@Override
	public void clear()
	{
		list.clear();
	}

	@Override
	public E get(int index)
	{
		if(reversed)
			return list.get(list.size() - index - 1);
		return list.get(index);
	}

	@Override
	public int indexOf(Object o)
	{
		if(reversed)
			return list.lastIndexOf(o);
		return list.indexOf(o);
	}

	@Override
	public Iterator<E> iterator()
	{
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object o)
	{
		if(reversed)
			return list.indexOf(o);
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator()
	{
		if(reversed)
			return new ReverseIterator();
		return list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index)
	{
		if(reversed)
			return new ReverseIterator(index);
		return list.listIterator(index);
	}

	@Override
	public E remove(int index)
	{
		if(reversed)
			return list.remove(size() - index - 1);
		return list.remove(index);
	}

	@Override
	public E set(int index, E element)
	{
		if(reversed)
			return list.set(size() - index - 1, element);
		return list.set(index, element);
	}

	@Override
	public int size()
	{
		return list.size();
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex)
	{
		if(reversed)
			return new SubList(size() - fromIndex - (toIndex - fromIndex),size() - fromIndex);
		return new SubList(fromIndex,toIndex);
	}
	
	private class SubList extends AbstractList<E>
	{

		private int start;
		private int end;
//		private final boolean createdReversed;
		
		public SubList(int start,int end)
		{
			this.start = start;
			this.end = end;
//			createdReversed = ReversableList.this.reversed;
		}
		
		private int translateIndex(int index)
		{
//			if(createdReversed == ReversableList.this.reversed)
			if(!reversed)
				return index + start;
			return end - 1 - index;
		}
		private int reverseTranslateIndex(int index)
		{
//			if(createdReversed == ReversableList.this.reversed)
			if(!reversed)
				return index - start;
			return list.size() - index - end - 1;
		}
		/* (non-Javadoc)
		 * @see java.util.AbstractList#get(int)
		 */
		@Override
		public E get(int index)
		{
			if(index < 0 || index > size())
				throw new IllegalArgumentException("index=" + index);
			return list.get(translateIndex(index));
		}

		/* (non-Javadoc)
		 * @see java.util.AbstractCollection#size()
		 */
		@Override
		public int size()
		{
			return end - start;
		}

		@Override
		public boolean add(E e)
		{
			list.add(translateIndex(end++),e);
			return true;
		}

		@Override
		public void add(int index, E element)
		{
			if(index < 0 || index > size())
				throw new IllegalArgumentException("index=" + index);
			
			list.add(translateIndex(index),element);
		}

		@Override
		public int indexOf(Object o)
		{
			int index = reverseTranslateIndex(list.indexOf(o));
			if(index < 0 || index >= size()) 
				return -1;
			return index;
		}

		@Override
		public int lastIndexOf(Object o)
		{
			int index = reverseTranslateIndex(list.lastIndexOf(o));
			if(index < 0 || index >= size()) 
				return -1;
			return index;

		}

		@Override
		public E remove(int index)
		{
			if(index < 0 || index > size())
				throw new IllegalArgumentException("index=" + index);
			E elm = list.remove(translateIndex(index));
			--end;
			return elm;
		}

		@Override
		public E set(int index, E element)
		{
			if(index < 0 || index > size())
				throw new IllegalArgumentException("index=" + index);
			return list.set(translateIndex(index), element);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex)
		{
			if(fromIndex < 0 || toIndex > size() || toIndex < fromIndex)
				throw new IllegalArgumentException("fromIndex=" + fromIndex + ";toIndex=" + toIndex);
			return new SubList(start + fromIndex,start + toIndex);
		}
		
	}
	private class ReverseIterator implements ListIterator<E>
	{

		private final ListIterator<E> delegate;
		
		
		public ReverseIterator()
		{
			this(0);
		}
		public ReverseIterator(int fromIndex)
		{
			delegate = list.listIterator(list.size() - fromIndex);
		}
		/* (non-Javadoc)
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add(E e)
		{
			delegate.add(e);
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#hasNext()
		 */
		public boolean hasNext()
		{
			return delegate.hasPrevious();
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious()
		{
			return delegate.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#next()
		 */
		public E next()
		{
			return delegate.previous();
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex()
		{
			return delegate.previousIndex();
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#previous()
		 */
		public E previous()
		{
			return delegate.next();
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex()
		{
			return delegate.nextIndex();
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#remove()
		 */
		public void remove()
		{
			delegate.remove();
		}

		/* (non-Javadoc)
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(E e)
		{
			delegate.set(e);
		}
		
	}


}
