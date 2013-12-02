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
import java.util.*;

import com.gamesalutes.utils.CollectionUtils;

/**
 * Adjacency list implementation of the {@link Graph} interface.
 * 
 * @author Justin Montgomery
 * @version $Id: AdjacencyList.java 1639 2009-07-20 19:28:25Z jmontgomery $
 *
 * @param <V> vertex type
 * @param <E> edge type
 */
public class AdjacencyList<V,E> extends AbstractGraph<V,E> implements Serializable
{
	// map vertices to themselves
	private Map<Vertex<V>,Vertex<V>> vertices;
	private Map<Vertex<V>,Set<Edge<V,E>>> edgeMap;
	private boolean isDirected;
	private int numEdges;
	
	private static final long serialVersionUID = 2L;
	/**
	 * Constructor.
	 * 
	 * @param isDirected <code>true</code> for a directed graph
	 *                   <code>false</code> for undirected graph
	 */
	public AdjacencyList(boolean isDirected)
	{
		this.isDirected = isDirected;
		vertices = new LinkedHashMap<Vertex<V>,Vertex<V>>();
		edgeMap = new LinkedHashMap<Vertex<V>,Set<Edge<V,E>>>();
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * @param graph <code>Graph</code> to copy
	 * @param isDirected <code>true</code> for a directed graph
	 *                   <code>false</code> for undirected graph
	 */
	public AdjacencyList(Graph<V,E> graph,boolean isDirected)
	{
		this(isDirected);
		
		for(Iterator<Vertex<V>> it = graph.vertexIterator(); it.hasNext();)
			addVertex(it.next());
		for(Iterator<Edge<V,E>> it = graph.edgeIterator(); it.hasNext();)
			addEdge(it.next());
			
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AdjacencyList<V,E> clone()
	{
		AdjacencyList<V,E> g;
		g = (AdjacencyList<V,E>)super.clone();
		// deep copy the fields
		
		// don't use IdentityHashMap since to and from in edge may not be same object as that in vertices
		// the vertex and edge data structures use the regular equals method to test for containment so
		// we should do that as well during copying
		Map<GraphObject,GraphObject> oldToNew = CollectionUtils.createHashMap(
				g.numVerts() + g.numEdges(), CollectionUtils.LOAD_FACTOR);
		
		Map<Vertex<V>,Vertex<V>> newVerts = 
			CollectionUtils.createLinkedHashMap(g.vertices.size(), CollectionUtils.LOAD_FACTOR);
		Map<Vertex<V>,Set<Edge<V,E>>> newEdgeMap =
			CollectionUtils.createLinkedHashMap(g.edgeMap.size(),CollectionUtils.LOAD_FACTOR);
		
		// copy the vertices
		for(Vertex<V> origV : g.vertices.keySet())
		{
			Vertex<V> newV = origV.clone();
			oldToNew.put(origV, newV);
			newVerts.put(newV,newV);
		}
		// fixup vertex set parents
		for(Vertex<V> v : newVerts.keySet())
		{
			v.setParent((Vertex<V>)oldToNew.get(v.getParent()));
		}
		
		// copy the edge maps
		for(Map.Entry<Vertex<V>,Set<Edge<V,E>>> E : g.edgeMap.entrySet())
		{
			Vertex<V> oldV = E.getKey();
			Vertex<V> newV = (Vertex<V>)oldToNew.get(oldV);
			if(newV == null) throw new AssertionError("No copy made of oldV=" + oldV);
			
			Set<Edge<V,E>> oldEdgeSet = E.getValue();
			Set<Edge<V,E>> newEdgeSet = CollectionUtils.createLinkedHashSet(oldEdgeSet.size(),
					CollectionUtils.LOAD_FACTOR);
			// copy the set of edges for each vertex
			for(Edge<V,E> oldE : oldEdgeSet)
			{
				Edge<V,E> newE = (Edge<V,E>)oldToNew.get(oldE);
				if(newE == null)
				{
					newE = oldE.clone();
					// fixup source and dest
					Vertex<V> newSource = (Vertex<V>)oldToNew.get(oldE.getFrom());
					if(newSource == null) throw new AssertionError("e= " + oldE + ";No copy made of source=" + oldE.getFrom());
					Vertex<V> newDest = (Vertex<V>)oldToNew.get(oldE.getTo());
					if(newDest == null) throw new AssertionError("e=" + oldE + "No copy made of dest=" + oldE.getTo());
					
					newE.setFrom(newSource);
					newE.setTo(newDest);
					
					oldToNew.put(oldE, newE);
				}
				newEdgeSet.add(newE);
					
			}
			newEdgeMap.put(newV, newEdgeSet);
		}
		// copy over new data structures
		g.vertices = newVerts;
		g.edgeMap = newEdgeMap;
		
		return g;
	}
	
	public boolean addEdge(Edge<V,E> e) 
	{
		//assert that vertices are present
		if(e == null)
			throw new NullPointerException("e");
		// make copy of input edge
		e = e.clone();
		Vertex<V> to = e.getTo();
		Vertex<V> from = e.getFrom();
		
		if(!vertices.containsKey(to))
			throw new IllegalArgumentException("e.to: " + to + " not in graph");
		if(!vertices.containsKey(from))
			throw new IllegalArgumentException("e.from: " + from + " not in graph");
		
		// set the edge from and to to point to same instances as in this graph
		e.setFrom(vertices.get(from));
		e.setTo(vertices.get(to));
		
		//directed graph only adds e
		if(isDirected)
		{
			boolean added =  _addEdge(e);
			if(added)
				numEdges++;
			return added;
		}
		
		//undirected graph must also add inverse of e
		else
		{
			boolean first;
			first = _addEdge(e);
			
			// for self-loops _addEdge(e.reverse()) always false
			if(!e.isSelfLoop())
			{
				boolean second = _addEdge(e.reverse());
				if(first != second)
					throw new AssertionError("Inconsitent edge lists for undirected");
				if(first)
					numEdges+=2;
			}
			// self-loop only adds one edge
			else if(first)
				numEdges++;
				
			return first;
		}
	}
	
	private boolean _addEdge(Edge<V,E> e)
	{
		Vertex<V> from = e.getFrom();
		Vertex<V> to = e.getTo();
		
		//must add to edge list of both from and to
		boolean first = _addEdge(from,e);
		boolean second = _addEdge(to,e);
		
		return first || second;
	}
	
	private boolean _addEdge(Vertex<V> v,Edge<V,E> e)
	{
		Set<Edge<V,E>> edgeSet = edgeMap.get(v);
		//add new edge set
		if(edgeSet == null)
		{
			edgeSet = new LinkedHashSet<Edge<V,E>>();
			edgeMap.put(v, edgeSet);
		}
		return edgeSet.add(e);
	}

	public boolean addVertex(Vertex<V> v) 
	{
		if(v == null)
			throw new NullPointerException("v");
		
		if(vertices.containsKey(v)) return false;
		
		v = v.clone();
		vertices.put(v, v);
		return true;
	}

	@SuppressWarnings("unchecked")
	public Iterator<Edge<V,E>> edgeIterator() 
	{
		Set<Edge<V,E>> allEdges = new LinkedHashSet<Edge<V,E>>();
		for(Set<Edge<V,E>> edgeSet : edgeMap.values())
			allEdges.addAll(edgeSet);
		return new EdgeIterator(allEdges);
	}

	@SuppressWarnings("unchecked")
	public Iterator<Edge<V,E>> edgeIterator(Vertex<V> v) 
	{
		if(!containsVertex(v))
			throw new IllegalArgumentException("v=" + v + " not in graph");
		
		if(edgeMap.containsKey(v))
			return new EdgeIterator(edgeMap.get(v));
		else
			return Collections.<Edge<V,E>>emptyList().iterator();
	}

	public boolean isDirected() 
	{
		return isDirected;
	}

	public boolean removeEdge(Edge<V,E> e) 
	{
		if(e == null)
			throw new NullPointerException("e");
		
		if(isDirected)
		{
			boolean removed = _removeEdge(e);
			if(removed)
				numEdges--;
			return removed;
		}
		else
		{
			boolean first;
			first = _removeEdge(e);
			// for self-loops _removeEdge(e.reverse()) always returns false
			if(!e.isSelfLoop())
			{
				boolean second = _removeEdge(e.reverse());
				if(first != second)
					throw new AssertionError("Inconsistent edge lists for undirected");
				if(first)
					numEdges-=2;
			}
			// for self-loops only one edge exists
			else if(first)
				numEdges--;
			
			return first;
		}
	}
	
	private boolean _removeEdge(Edge<V,E> e)
	{
		if(e == null)
			throw new NullPointerException("e");
		Vertex<V> from = e.getFrom();
		Vertex<V> to = e.getTo();
		boolean modified = false;
		// since an edge is added to edge sets of both to and from
		// it must also be removed from both to and from
		if(edgeMap.containsKey(from))
			modified |= edgeMap.get(from).remove(e);
		if(edgeMap.containsKey(to))
			modified |= edgeMap.get(to).remove(e);
		return modified;
	}

	public boolean removeVertex(Vertex<V> v) 
	{
		if(v == null)
			throw new NullPointerException("v");
		boolean removed = vertices.remove(v) != null;
		
		if(removed)
		{
			//remove edges of this vertex
			edgeMap.remove(v);
			//must also remove edges that contained v
			for(Set<Edge<V,E>> set : edgeMap.values())
			{
				for(Iterator<Edge<V,E>> it = set.iterator(); it.hasNext();)
				{
					Edge<V,E> edge = it.next();
					
					//remove the edge
					if(edge.getFrom().equals(v) ||
					   edge.getTo().equals(v))
					{
						it.remove();
					}
						
				} //end for
			} //end for
		}
			
		return removed;
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<Vertex<V>> vertexIterator() 
	{
		return new VertexIterator(vertices.keySet());
	}
	public boolean containsEdge(Edge<V,E> e) 
	{
		if(e == null)
			throw new NullPointerException("e");
		Vertex<V> from = e.getFrom();
		if(edgeMap.containsKey(from))
			return edgeMap.get(from).contains(e);
		else
			return false;
	}
	public boolean containsVertex(Vertex<V> v) 
	{
		if(v == null)
			throw new NullPointerException("v");
		return vertices.containsKey(v);
	}
	
	public int numVerts() { return vertices.size(); }
	public int numEdges() { return numEdges;}
	
	public int numEdges(Vertex<V> v)
	{
		if(v == null)
			throw new NullPointerException("v");
		if(!containsVertex(v))
			throw new IllegalArgumentException("v=" + v);
		
		Set<Edge<V,E>> edges = edgeMap.get(v);
		return edges != null ? edges.size() : 0;
		
	}
	
	public void clear() 
	{
		vertices.clear();
		edgeMap.clear();
		numEdges = 0;
	}


	public Edge<V,E> findEdge(Vertex<V> source, Vertex<V> dest) 
	{
		if(source == null)
			throw new NullPointerException("source");
		if(dest == null)
			throw new NullPointerException("dest");
		if(!vertices.containsKey(source))
			throw new IllegalArgumentException("source not in graph");
		if(!vertices.containsKey(dest))
			throw new IllegalArgumentException("dest not in graph");
		
		//examine edge list of source
		Set<Edge<V,E>> edges = edgeMap.get(source);
		if(edges != null)
		{
			for(Edge<V,E> e : edges)
			{
				if(e.getTo().equals(dest))
					return e;
			}
		}
		return null;
	}
	
	public Graph<V,E> makeUndirected()
	{
		if(!isDirected)
			return this;
		else
			return new AdjacencyList<V,E>(this,false);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.AbstractGraph#newInstance(boolean)
	 */
	@Override
	protected Graph<V, E> newInstance(boolean isDirected)
	{
		return new AdjacencyList<V,E>(isDirected);
	}
	


}
