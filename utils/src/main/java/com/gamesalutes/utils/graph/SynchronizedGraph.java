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

import java.io.Serializable;
import java.util.Iterator;


/**
 * Synchronized wrapper around a {@link Graph}.
 * 
 * @author Justin Montgomery
 * @version $Id: SynchronizedGraph.java 1892 2010-02-04 21:29:58Z jmontgomery $
 */
public final class SynchronizedGraph<V,E> implements Graph<V,E>,Serializable
{
	private Graph<V,E> graph;
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor.
	 * 
	 * @param g the {@link Graph} to wrap
	 */
	public SynchronizedGraph(Graph<V,E> g)
	{
		if(g == null)
			throw new NullPointerException("g");
		this.graph = g;
		
	}
	
	@Override
	public synchronized boolean equals(Object o)
	{
		return graph.equals(o);
	}
	@Override
	public synchronized int hashCode()
	{
		return graph.hashCode();
	}
	@Override
	public synchronized String toString()
	{
		return graph.toString();
	}
	
	public synchronized SynchronizedGraph<V,E> clone()
	{
		try
		{
			SynchronizedGraph<V,E> clone = (SynchronizedGraph<V,E>)super.clone();
			clone.graph = clone.graph.clone();
			return clone;
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError(e);
		}
	}
	public synchronized boolean addEdge(Edge<V,E> e) 
	{
		return graph.addEdge(e);
	}

	public synchronized boolean addVertex(Vertex<V> v) 
	{
		return graph.addVertex(v);
	}

	public synchronized void clear() 
	{
		graph.clear();
	}

	public synchronized boolean containsEdge(Edge<V,E> e) 
	{
		return graph.containsEdge(e);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.Graph#containsVertex(com.gamesalutes.utils.graph.Vertex)
	 */
	public synchronized boolean containsVertex(Vertex<V> v) 
	{
		return graph.containsVertex(v);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.Graph#edgeIterator()
	 */
	public synchronized Iterator<Edge<V,E>> edgeIterator() 
	{
		return graph.edgeIterator();
	}

	public synchronized Iterator<Edge<V,E>> edgeIterator(Vertex<V> v)
	{
		return graph.edgeIterator(v);
	}

	public synchronized Edge<V,E> findEdge(Vertex<V> source, Vertex<V> dest)
	{
		return graph.findEdge(source, dest);
	}


	public synchronized boolean isDirected() 
	{
		return graph.isDirected();
	}


	public synchronized Graph<V,E> makeUndirected()
	{
		if(!isDirected())
			return this;
		else
			return new SynchronizedGraph<V,E>(graph.makeUndirected());
	}
	
	public synchronized Graph<V,E> reverse()
	{
		if(!isDirected())
			return this;
		else
			return new SynchronizedGraph<V,E>(graph.reverse());
	}


	public synchronized int numEdges()
	{
		return graph.numEdges();
	}
	
	public synchronized int numEdges(Vertex<V> v)
	{
		return graph.numEdges(v);
	}


	public synchronized int numVerts()
	{
		return graph.numVerts();
	}


	public synchronized boolean removeEdge(Edge<V,E> e) 
	{
		return graph.removeEdge(e);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.Graph#removeVertex(com.gamesalutes.utils.graph.Vertex)
	 */
	public synchronized boolean removeVertex(Vertex<V> v) 
	{
		return graph.removeVertex(v);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.Graph#vertexIterator()
	 */
	public synchronized Iterator<Vertex<V>> vertexIterator()
	{
		return graph.vertexIterator();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.Graph#addAll(com.gamesalutes.utils.graph.Graph)
	 */
	public synchronized void addAll(Graph<V,E> g)
	{
		graph.addAll(g);
	}

	
}
