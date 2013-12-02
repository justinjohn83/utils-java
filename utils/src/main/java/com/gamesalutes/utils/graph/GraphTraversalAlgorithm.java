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

import java.util.List;

/**
 * Traversal algorithm for a {@link Graph}.
 * 
 * @author Justin Montgomery
 * @version $Id: GraphTraversalAlgorithm.java 1752 2009-11-03 22:12:20Z jmontgomery $
 *
 * @param <V> vertex type of the graph
 * @param <E> edge type of the graph
 */
public interface GraphTraversalAlgorithm<V,E> extends GraphAlgorithm<V,E>
{
	
	GraphTraversalAlgorithm<V,E> execute();
	
	/**
	 * Returns the path terminating on <code>dest</code>
	 * @param dest the destination {@link Vertex}.
	 * @return a <code>List</code> containing the vertices along the path
	 */
	List<Vertex<V>> getPath(Vertex<V> dest);
	
	/**
	 * Returns the complete traversal on the graph.
	 * 
	 * @return a <code>List</code> containing the vertex traversal
	 */
	List<Vertex<V>> getTraversal();
	
	
	/**
	 * Returns the graph on which this traversal algorithm acts.
	 * 
	 * @return the <code>Graph</code>
	 */
	Graph<V,E> getGraph();
	
	/**
	 * Gets the source of this traversal algorithm.
	 * 
	 * @return the source vertex
	 */
	Vertex<V> getSource();
	
}
