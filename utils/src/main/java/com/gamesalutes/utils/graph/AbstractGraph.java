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
package com.gamesalutes.utils.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.gamesalutes.utils.AbstractIterator;


/**
 * Abstract base class for {@link Graph} implementations.
 * 
 * @author Justin Montgomery
 * @version $Id: AbstractGraph.java 1892 2010-02-04 21:29:58Z jmontgomery $
 */
public abstract class AbstractGraph<V,E> implements Graph<V,E>
{
	
	
	/**
	 * Returns a new empty instance of a <code>Graph</code> with the given
	 * directed-ness.
	 * 
	 * @param isDirected <code>true</code> for directed and <code>false</code> otherwise
	 * 
	 * @return the new graph
	 */
	protected abstract Graph<V,E> newInstance(boolean isDirected);
	
	@SuppressWarnings("unchecked")
	@Override
	public AbstractGraph<V,E> clone()
	{
		try
		{
			return (AbstractGraph<V,E>)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError(e);
		}
	}
	
	public void addAll(Graph<V,E> g)
	{
		if(g == null)
			throw new NullPointerException("g");
		if(g == this)
			throw new IllegalArgumentException("g == this");
		
		// add all the vertices
		for(Iterator<Vertex<V>> it = g.vertexIterator();it.hasNext();)
			addVertex(it.next());
		// add all the edges
		for(Iterator<Edge<V,E>> it = g.edgeIterator();it.hasNext();)
			addEdge(it.next());
	}

	public Graph<V, E> reverse()
	{
		if(!isDirected()) return this;
		
		// get a new copy of this graph
		Graph<V,E> gReversed = newInstance(true);
		// add all the vertices
		for(Iterator<Vertex<V>> it = vertexIterator();it.hasNext();)
			gReversed.addVertex(it.next());
		// add the reverse of all the current edges of this graph
		for(Iterator<Edge<V,E>> it = edgeIterator();it.hasNext();)
			gReversed.addEdge(it.next().reverse());
		
		return gReversed;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Graph)) return false;
		
		Graph<V,E> g = (Graph<V,E>)o;
		//test directed-ness
		if(isDirected() != g.isDirected())
			return false;
		
		// test vertices
		if(numVerts() != g.numVerts()) return false;
		// make sure every vertex in g is contained in this graph
		for(Iterator<Vertex<V>> it = g.vertexIterator(); it.hasNext();)
			if(!containsVertex(it.next()))
				return false;
		
		// test edges
		if(numEdges() != g.numEdges()) return false;
		// make sure every edge in g is contained in this graph
		for(Iterator<Edge<V,E>> it = g.edgeIterator(); it.hasNext();)
			if(!containsEdge(it.next()))
				return false;
		
		// equal
		return true;
		
	}
	
	@Override
	public int hashCode()
	{
		int result = 0;
		
		// must use sum since iterators can return their components in any order
		result += (isDirected() ? 1 : 0);
		
		// compute for vertices
		for(Iterator<Vertex<V>> it = vertexIterator(); it.hasNext();)
			result += it.next().hashCode();
		
		// compute for edges
		for(Iterator<Edge<V,E>> it = edgeIterator();it.hasNext();)
			result += it.next().hashCode();
		
		return result;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		//print out directed-ness
		str.append("[GRAPH:\n\tisDirected=");
		str.append(isDirected());
		str.append("\n\tVERTICES: { ");
		
		//create sorted set of vertex strings, so can sort and then put together
		//in predicatable order
		SortedSet<String> strSet = new TreeSet<String>();
		for(Iterator<Vertex<V>> it = vertexIterator(); it.hasNext();)
			strSet.add(it.next().toString());
		//append vertex string to the total string
		for(Iterator<String> it = strSet.iterator(); it.hasNext();)
		{
			str.append(it.next());
			if(it.hasNext())
				str.append(",");
		}
		str.append(" }");
		
		//append the edges
		str.append("\n\tEDGES : { ");
		strSet.clear();
		for(Iterator<Edge<V,E>> it = edgeIterator(); it.hasNext();)
			strSet.add(it.next().toString());
		//append edge string to the total string
		for(Iterator<String> it = strSet.iterator(); it.hasNext();)
		{
			str.append(it.next());
			if(it.hasNext())
				str.append(",");
		}
		str.append(" }\n]");
		return str.toString();
		
	}
	
	public int numEdges(Vertex<V> v)
	{
		if(v == null)
			throw new NullPointerException("v");
		if(!containsVertex(v))
			throw new IllegalArgumentException("v=" + v);
		
		int count = 0;
		for(Iterator<Edge<V,E>> it = this.edgeIterator(v);it.hasNext();)
			count++;
		return count;
	}
	
	///////////////////////////////////////////////////////////////////
	//                      ITERATORS                                //
	///////////////////////////////////////////////////////////////////
	
	protected class EdgeIterator extends AbstractIterator<Edge<V,E>>
	{
		protected EdgeIterator(Collection<Edge<V,E>> objects)
		{
			super(objects);
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		protected void doRemove(Edge<V,E> e) 
		{
			removeEdge(e);
		}
		
	}
	
	protected class VertexIterator extends AbstractIterator<Vertex<V>>
	{
		protected VertexIterator(Collection<Vertex<V>> objects)
		{
			super(objects);
		}
		
		protected void doRemove(Vertex<V> v)
		{
			removeVertex(v);
		}
		
	}
}
