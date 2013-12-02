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
package com.gamesalutes.utils;
import java.io.Serializable;
import java.util.*;

/**
 * Pair of data.
 * 
 * @author Justin Montgomery
 * @version $Id: Pair.java 1120 2008-09-26 23:12:33Z jmontgomery $
 * @param <U> first element
 * @param <V> second element
 */
public final class Pair<U,V> implements Comparable<Pair<U,V>>,Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * first element
	 */
	public U first;
	
	/**
	 * second element
	 */
	public V second;
	
	/**
	 * Constructor.
	 *
	 */
	public Pair() {}
	
	/**
	 * Constructor.
	 * 
	 * @param first first element
	 * @param second second element
	 */
	public Pair(U first,V second)
	{
		this.first = first;
		this.second = second;
	}
	
	public static <U,V> Pair<U,V> makePair(U first,V second)
	{
		return new Pair<U,V>(first,second);
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param otherPair another pair
	 */
	public Pair(Pair<U,V> otherPair)
	{
		this.first = otherPair.first;
		this.second = otherPair.second;
	}
	
	/**
	 * Returns the string representation of the
	 * <code>second</code> field.
	 * 
	 */
	@Override
	public String toString()
	{
		if(second != null)
			return second.toString();
		else
			return null;
	}
	
	/**
	 * Returns a complete string representation of this <code>Pair</code> in form
	 * <code>{[first],[second]}</code>.
	 * 
	 * @return a complete string representation of this <code>Pair</code>
	 */
	public String toDebugString()
	{
		return new StringBuilder(128).append('{'
				).append(first).append(','
				).append(second).append('}'
				).toString();
	}
	
	/**
	 * Compares this pair for equality with <code>o</code>.
	 * This pair is equal to <code>o</code> if <code>o</code> is 
	 * also a <code>Pair</code>, and its <code>first</code> and 
	 * <code>second</code> fields are equal to the corresponding
	 * fields of this pair.
	 * 
	 * @return <code>true</code> if equal and <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Pair)) return false;
		Pair p = (Pair)o;
		return MiscUtils.safeEquals(first, p.first) && 
			   MiscUtils.safeEquals(second,p.second);
	}
	@Override
	public int hashCode()
	{
		int result = 17;
		if(first != null)
			result = 37 * result + first.hashCode();
		if(second != null)
			result = 37 * result + second.hashCode();
		return result;
	}
	
	/**
	 * Compares this pair to <code>p</code>.
	 * Compares this pair by first comparing the <code>second</code> fields
	 * and then the <code>first</code> fields if the <code>second</code> fields
	 * are equal.
	 * 
	 * @return a negative,zero,or positive integer if this pair is less than,
	 * equal to, or greater than <code>p</code>
	 */
	public int compareTo(Pair<U,V> p)
	{
		int result =  ((Comparable)second).compareTo(p.second);
		if(result != 0) return result;
		return ((Comparable)first).compareTo(p.first);
	}
	
	/**
	 * Converts <code>pairList</code> into a list of its first elements.
	 * 
	 * @param <U> first element type of pairs in <code>pairList</code>
	 * @param <V> second element type of pairs in <code>pairList</code>
	 * @param pairList list of <code>Pair</code>
	 * @return list of first elements in <code>pairList</code>
	 */ 
	public static<U,V> List<U> 
		convertToFirstList(List<Pair<U,V>> pairList)
	{
		List<U> list = new ArrayList<U>(pairList.size());
		for(Pair<U,V> pair : pairList)
			list.add(pair.first);
		return list;
	}
	
	/**
	 * Converts <code>pairList</code> into a list of its second elements.
	 * 
	 * @param <U> first element type of pairs in <code>pairList</code>
	 * @param <V> second element type of pairs in <code>pairList</code>
	 * @param pairList list of <code>Pair</code>
	 * @return list of second elements in <code>pairList</code>
	 */
	public static<U,V> List<V> 
		convertToSecondList(List<Pair<U,V>> pairList)
	{
		List<V> list = new ArrayList<V>(pairList.size());
		for(Pair<U,V> pair : pairList)
			list.add(pair.second);
		return list;
	}
}
