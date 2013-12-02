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
import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 
 * Compares two objects in an exception-safe fashion.  Although in general a
 * <code>NullPointerException</code> should be thrown if at least one object involved
 * in comparison is <code>null</code>, some implementations may not desire this for
 * their constituent parts.  In this implementation, <code>null</code> is considered
 * less than all <code>non-null</code> objects if <code>nullLessThanAll</code> is 
 * <code>true</code>, and it will be greater than all <code>non-null</code> objects 
 * otherwise.  If <code>first</code> and <code>second</code> are not mutually
 * <code>Comparable</code>, then equality is first checked and if false then a unique numerical value
 *   v1 and v2 is computed for first and second,respectively.
 *  The computed value is such that if first.equals(second) then v1 == v2 and
 *  if !first.equals(second) then v1 != v2.  This is similar to an object's hashcode, but is guaranteed to be unique
 *  in the case where the objects are not equal so that the ordering is unspecified, but deterministic when the same object is present
 *  in multiple collections.
 * 
 * @author Justin Montgomery
 * @version $Id: GeneralComparator.java 1743 2009-10-26 22:56:58Z jmontgomery $
 */
public final class GeneralComparator implements Comparator<Object>,Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final boolean nullLessThanAll;
	
	private static Map<Object,Long> compareMap;
	private static long counter;
	
	private static final int INIT_MAP_SIZE = 10000;
	
	/**
	 * Constructor.
	 * Creates the comparator with <code>null</code> objects being less than all other non-null objects.
	 * 
	 * 
	 */
	public GeneralComparator()
	{
		this(true);
	}
	
	
	/**
	 * Equality is first checked and if false then a unique numerical value
	 *  v1 and v2 is computed for <code>o1</code> and <code>o2</code>,respectively.
	 *  The computed value is such that if o1.equals(o2) then v1 == v2 and
	 *  if !o1.equals(o2) then v1 != v2.  This is similar to an object's hashcode, but is guaranteed to be unique
	 *  in the case where the objects are not equal so that the ordering is unspecified, but deterministic.
	 *  
	 * @param o1 first object
	 * @param o2 second object
	 * @return the unique comparison result
	 */
	public static int uniqueCompareTo(Object o1,Object o2)
	{
		// do equals and hash code comparisons
		if(MiscUtils.safeEquals(o1,o2)) 
			return 0;
		
		// get a unique comparable value for o1 and o2 such that if o1 != o2 then fh != sh
		long fh = getValue(o1);
		long sh = getValue(o2);
		if(fh < sh) return -1;
		else if(fh > sh) return 1;
		// shouldn't happen
		else return 0;
	}
	private static synchronized long getValue(Object o)
	{
		// ensure compare map initialized
		if(compareMap == null)
			compareMap = new WeakHashMap<Object,Long>(CollectionUtils.calcHashCapacity(INIT_MAP_SIZE, CollectionUtils.LOAD_FACTOR),CollectionUtils.LOAD_FACTOR);
		Map<Object,Long> map = compareMap;
		Long value = map.get(o);
		if(value == null)
		{
			++counter;
			value = Long.valueOf(counter);
			map.put(o, value);
		}
		
		return value.longValue();
			
	}
	
	/**
	 * Constructor.
	 * 
	 * @param nullLessThanAll <code>true</code> if <code>null</code> objects are considered less than all other
	 *                        non-null objects and <code>false</code> otherwise
	 */
	public GeneralComparator(boolean nullLessThanAll)
	{
		this.nullLessThanAll = nullLessThanAll;
	}
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public int compare(Object o1, Object o2)
	{
		// if object's are the same just return 0 now
		if(o1 == o2) return 0;
		
		if(o1 != null && o2 != null)
		{
			if(o1 instanceof Comparable)
			{
				try
				{
					return ((Comparable)o1).compareTo(o2);
				}
				// something about o2 prevented it from being compared
				catch(RuntimeException e) {}
			}
			
			return uniqueCompareTo(o1,o2);
		}
		
		if(o1 == null)
			return nullLessThanAll ? -1 : 1;
		//o2 ==null
		return nullLessThanAll ? 1 : -1;
	}

}
