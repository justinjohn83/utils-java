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
import java.util.Iterator;
/**
 * A directed or undirected graph.
 * 
 * @author Justin Montgomery
 * @version $Id: Graph.java 1892 2010-02-04 21:29:58Z jmontgomery $
 *
 */
public interface Graph<V,E> extends Cloneable
{
	
	
	/**
	 * Makes a copy of the this <code>Graph</code>.  The <code>Vertex</code> and 
	 * <code>Edge</code> objects are also copied, but the data they contain are not.
	 * 
	 * @return a copy of this <code>Graph</code>
	 */
	Graph<V,E> clone();
	/**
	 * Adds a new {@link Vertex} to the graph.
	 * 
	 * @param v <code>Vertex</code> to add
	 * @return <code>true</code> if the vertex was added
	 *         and <code>false</code> otherwise
	 */
	boolean addVertex(Vertex<V> v);
	
	/**
	 * Adds a new {@link Edge} to the graph.
	 * 
	 * @param e <code>Edge</code> to add
	 * @return <code>true</code> if the edge was added and
	 *         <code>false</code> otherwise
	 */
	boolean addEdge(Edge<V,E> e);
	
	/**
	 * Adds all the vertices and edges of <code>g</code> to this graph.
	 * 
	 * @param g another graph
	 */
	void addAll(Graph<V,E> g);
	
	/**
	 * Determines if <code>v</code> is in this <code>Graph</code>.
	 * 
	 * @param v {@link Vertex} to check
	 * @return <code>true</code> if contained and <code>false</code>
	 *          otherwise
	 */
	boolean containsVertex(Vertex<V> v);
	
	/**
	 * Removes <code>v</code> from this <code>Graph</code>.
	 * 
	 * @param v {@link Vertex} to remove from this <code>Graph</code>
	 * @return <code>true</code> if <code>v</code> was removed and
	 *         <code>false</code> otherwise
	 */
	boolean removeVertex(Vertex<V> v);
	
	/**
	 * Determines if <code>e</code> is in this <code>Graph</code>.
	 * 
	 * @param e {@link Edge} to check
	 * @return <code>true</code> if contained and <code>false</code>
	 *          otherwise
	 */
	boolean containsEdge(Edge<V,E> e);
	
	/**
	 * Removes <code>e</code> from this <code>Graph</code>.
	 * 
	 * @param e {@link Edge} to remove from this <code>Graph</code>
	 * @return <code>true</code> if <code>e</code> was removed and
	 *         <code>false</code> otherwise
	 */
	boolean removeEdge(Edge<V,E> e);
	
	/**
	 * Returns whether this <code>Graph</code> is directed.
	 * 
	 * @return <code>true</code> is directed and
	 *         <code>false</code> if undirected
	 */
	boolean isDirected();
	
	/**
	 * Returns an <code>Iterator</code> over the vertices in this
	 * <code>Graph</code>.
	 * 
	 * @return a {@link Vertex} iterator
	 */
	Iterator<Vertex<V>> vertexIterator();
	
	/**
	 * Returns an <code>Iterator</code> over all the edges in this 
	 * <code>Graph</code>.
	 * 
	 * @return an {@link Edge} iterator
	 */
	Iterator<Edge<V,E>> edgeIterator();
	
	/**
	 * Returns an <code>Iterator</code> over the incident and outgoing
	 * edges of <code>v</code>.
	 * 
	 * @param v the {@link Vertex} for which to iterate its edges 
	 * @return an {@link Edge} iterator
	 */
	Iterator<Edge<V,E>> edgeIterator(Vertex<V> v);
	
	/**
	 * Makes an undirected <code>Graph</code> from this graph.  If this 
	 * <code>Graph</code> is already undirected, then this instance is
	 * returned.
	 * 
	 * @return the undirected equivalent of this graph.
	 */
	Graph<V,E> makeUndirected();
	
	/**
	 * Reverses the direction of the edges in a directed <code>Graph</code>.  If this 
	 * <code>Graph</code> is undirected, then this <code>Graph</code> object is simply
	 * returned.
	 * 
	 * @return the reverse of this <code>Graph</code>
	 */
	Graph<V,E> reverse();
	
	/**
	 * Returns the number of vertices in this <code>Graph</code>.
	 * 
	 * @return the number of vertices
	 */
	int numVerts();
	
	/**
	 * Returns the number of edges in this <code>Graph</code>.
	 * 
	 * @return the number of edges
	 */
	int numEdges();
	
	/**
	 * Returns the number of edges to or from <code>v</code>.
	 * 
	 * @param v the vertex
	 * @return the number of edges
	 */
	int numEdges(Vertex<V> v);
	
	/**
	 * Clears all the vertices and edges in this <code>Graph</code>.
	 *
	 */
	void clear();
	
	/**
	 * <code>o</code> is equal to this graph if it is also
	 * a <code>Graph</code>, it has same directed-ness, and 
	 * has same vertices and edges as this graph.
	 * 
	 * @param o another <code>Graph</code>
	 * @return <code>true</code> if this graph is equal to 
	 *         <code>o</code> and <code>false</code> otherwise
	 */
	boolean equals(Object o);
	
	
	/**
	 * Returns the edge from <code>source</code> to <code>dest</code>
	 * if one exists.
	 * 
	 * @param source the source <code>Vertex</code>
	 * @param dest the dest <code>Vertex</code>
	 * 
	 * @return the <code>Edge</code> or <code>null</code> if one does not exist
	 */
	Edge<V,E> findEdge(Vertex<V> source,Vertex<V> dest);
	
}
