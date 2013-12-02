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
package com.gamesalutes.utils.graph;

import java.io.Serializable;

import com.gamesalutes.utils.MiscUtils;

/**
 * Node or edge in a {@link Graph}.
 * Note: The implementation of <code>Comparable</code> <i>is not</i> consistent
 * with equals.  The equals method compares the equality of the stored data object, while
 * the compareTo method compares the weight values.  Comparisons are only intended to be 
 * used in the graph algorithms.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id: GraphObject.java 1946 2010-02-26 17:55:34Z jmontgomery $
 *
 * @param <T> type stored in this node or edge
 */
public abstract class GraphObject<T> implements Serializable,Comparable<GraphObject<T>>,Cloneable
{
	private T data;
	private float weight;
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor.
	 * 
	 */
	protected GraphObject() {}
	/**
	 * Constructor.
	 * 
	 * @param data data to store in this object
	 */
	protected GraphObject(T data)
	{
//		if(data == null)
//			throw new NullPointerException("data");
		this.data = data;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param o another <code>GraphObject</code>
	 */
	protected GraphObject(GraphObject<T> o)
	{
		this.data = o.data;
		this.weight = o.weight;
	}
	
	
	 /**
	  * Makes a shallow copy of <code>orig</code>. <b>Every subclass must override this method</b> and if the class is final must create
	  * a new instance of its class.  If it is not final, then it must only create a new instance if
	  * <code>copy</code> is <code>null</code>.  At the end of the method, <code>super.shallowCopy</code>
	  * must be called to initialize base member fields.
	  * 
	  * @param orig the original object
	  * @param copy the copy object
	  * @return the copy object
	  */
	 protected GraphObject<T> shallowCopy(GraphObject<T> orig,GraphObject<T> copy)
	 {
		 if(copy == null)
			 throw new AssertionError("copy is null");
		 copy.data = orig.data;
		 copy.weight = orig.weight;
		 
		 return copy;
	 }
	/**
	 * Returns the stored object.
	 * 
	 * @return the stored object
	 */
	public T getData() { return data; }
	
	@Override
	@SuppressWarnings("unchecked")
	public GraphObject<T> clone()
	{
		return shallowCopy(this,null);
	}
	
	
	/**
	 * Sets a weight value for this <code>GraphObject</code>.
	 * 
	 * @param wt
	 */
	public void setWeight(float wt)
	{
		this.weight = wt;
	}
	
	/**
	 * Returns the set weight value.
	 * 
	 * @return the weight value
	 */
	public float getWeight() { return weight; }
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(!(o instanceof GraphObject)) return false;
		GraphObject go = (GraphObject)o;
		return MiscUtils.safeEquals(data,go.data);
	}
	
	@Override
	public int hashCode()
	{
		return MiscUtils.safeHashCode(data);
	}
	
	/**
	 * Returns a negative integer,positive integer, or zero if 
	 * the weight of this <code>GraphObject</code> is less than,
	 * greater than, or equal to the weight of <code>another</code>.
	 * 
	 * @param another a second <code>GraphObject</code>
	 * @return comparison result
	 */
	public int compareTo(GraphObject<T> another)
	{
		if(this == another) return 0;
		
		float w1 = weight;
		float w2 = another.weight;
		if(w1 < w2) return -1;
		if(w1 > w2) return 1;
		
		if(equals(another)) return 0;
		
		// break ties in some other deterministic manner : use System.identityHashCode
		w1 = System.identityHashCode(this);
		w2 = System.identityHashCode(another);
		if(w1 < w2) return -1;
		if(w1 > w2) return 1;
		// shouldn't happen
		return 0;
	}
	
	@Override
	public String toString()
	{
		String dataStr = String.valueOf(data);
		StringBuilder str = new StringBuilder(dataStr.length() + 2);
		str.append("[");
		str.append(dataStr);
		str.append("]");
		return str.toString();
	}
}
