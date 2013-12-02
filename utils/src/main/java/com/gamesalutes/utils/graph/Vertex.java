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
/**
 * Vertex in a graph.
 * 
 * @author Justin Montgomery
 * @version $Id: Vertex.java 1946 2010-02-26 17:55:34Z jmontgomery $
 *
 */
public class Vertex<T> extends GraphObject<T>
{
	private int level;
	private Vertex<T> parent;
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor.
	 * 
	 */
	public Vertex() {}
	/**
	 * Constructor.
	 * 
	 * @param data data to store in this <code>Vertex</code>
	 */
	public Vertex(T data)
	{
		super(data);
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param v another <code>Vertex</code>
	 */
	public Vertex(Vertex<T> v)
	{
		super(v);
		this.level = v.level;
		this.parent = v.parent;
	}
	
	@Override
	protected GraphObject<T> shallowCopy(GraphObject<T> orig,GraphObject<T> copy)
	{
		if(copy == null) // just use copy constructor
			return new Vertex<T>((Vertex<T>)orig);
		else
		{
			Vertex<T> vcopy = (Vertex<T>)copy;
			Vertex<T> vorig = (Vertex<T>) orig;
			
			vcopy.level = vorig.level;
			vcopy.parent = vorig.parent;

			return super.shallowCopy(vorig, vcopy);	
		}
	}
	
	@Override
	public Vertex<T> clone()
	{
		return (Vertex<T>)super.clone();
	}
	
	/**
	 * Sets the level of this vertex during a traversal.
	 * <i>Used by some implementations of {@link GraphAlgorithm} </i>.
	 * 
	 * @param level the level to set
	 */
	public void setLevel(int level) { this.level = level; }
	
	/**
	 * Sets the parent of this {@link Vertex} during a traversal.
	 * <i>Used by some implementations of {@link GraphAlgorithm} </i>.
	 * 
	 * @param parent parent <code>Vertex</code> to set
	 */
	public void setParent(Vertex<T> parent) { this.parent = parent; }
	
	/**
	 * Returns the level of this vertex during a traversal.
	 * <i>Used by some implementations of {@link GraphAlgorithm} </i>.
	 * 
	 * @return the level of this vertex
	 */
	public int getLevel() { return level; }
	
	/**
	 * Returns the parent of this {@link Vertex} during a traversal.
	 * <i>Used by some implementations of {@link GraphAlgorithm} </i>.
	 * 
	 * @return the parent vertex
	 */
	public Vertex<T> getParent() { return parent; }
	
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(!(o instanceof Vertex)) return false;
		return super.equals(o);
	}
}
