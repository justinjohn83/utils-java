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

import java.util.*;

/**
 * Skeletal implementation for {@link GraphTraversalAlgorithm}.
 * 
 * @author Justin Montgomery
 * @version $Id: AbstractGraphTraversal.java 1946 2010-02-26 17:55:34Z jmontgomery $
 *
 * @param <V> vertex type of the graph
 * @param <E> edge type of the graph
 */
public abstract class AbstractGraphTraversal<V,E> implements GraphTraversalAlgorithm<V,E>
{
	protected Graph<V,E> graph;
	protected Vertex<V> source;
	protected List<Vertex<V>> traversal;
	protected GraphTraversalCallback<V,E> callback;
	
	/**
	 * Constructor.
	 * 
	 * @param g the {@link Graph} to traverse
	 * @param source the source {@link Vertex} for traversal
	 */
	protected AbstractGraphTraversal(Graph<V,E> g, Vertex<V> source)
	{
		this(g,source,null);
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param g the {@link Graph} to traverse
	 * @param source the source {@link Vertex} for traversal
	 * @param callback a {@link GraphTraversalCallback} for this traversal
	 */
	protected AbstractGraphTraversal(Graph<V,E> g,Vertex<V> source,
			GraphTraversalCallback<V,E> callback)
	{
		if(g == null)
			throw new NullPointerException("g");
		this.graph = g;
		
		if(!g.containsVertex(source))
			throw new IllegalArgumentException("source not in g: " + source);
		this.source = source;
		if(callback != null)
			this.callback = callback;
		else
			this.callback = new DefaultGraphTraversalCallback<V,E>();
		
		prepareGraph();
	}
	
	private void prepareGraph()
	{
		// set all vertex weights to infinity except for the source
		float maxEdgeWt = 0.0f;
		for(Iterator<Edge<V,E>> it = graph.edgeIterator(); it.hasNext();)
		{
			float wt = it.next().getWeight();
			if(wt > maxEdgeWt) wt = maxEdgeWt;
		}
		for(Iterator<Vertex<V>> it = graph.vertexIterator(); it.hasNext();)
		{
			Vertex<V> v = it.next();
			v.setWeight(Float.MAX_VALUE - maxEdgeWt);
			v.setParent(null);
			v.setLevel(-1);
		}	
	}
	public List<Vertex<V>> getPath(Vertex<V> dest) 
	{
		if(traversal == null)
			throw new IllegalStateException("execute not called");
		if(!graph.containsVertex(dest))
			throw new IllegalArgumentException("dest not in graph: " + dest);
//		if(!traversal.contains(dest))
//			throw new IllegalArgumentException("dest not in traversal: " + dest);
		if(!traversal.contains(dest))
			return Collections.emptyList();
		
		LinkedList<Vertex<V>> stack = new LinkedList<Vertex<V>>();
		dest = traversal.get(traversal.indexOf(dest));
		stack.add(dest);
		Vertex<V> parent;
		while((parent = dest.getParent()) != null)
		{
			dest = parent;
			stack.addFirst(parent);
		}
		return stack;
	}
	
	public List<Vertex<V>> getTraversal() { return traversal; }


	public Graph<V, E> getGraph()
	{
		return graph;
	}


	public Vertex<V> getSource()
	{
		return source;
	}
	
	

}
