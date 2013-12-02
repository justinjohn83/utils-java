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

/**
 * Encapsulation of the current <code>Vertex</code> and/or <code>Edge</code> that is 
 * encountered during the execution of a {@link GraphTraversalAlgorithm GraphTraversalAlgorithm}.
 * 
 * @author Justin Montgomery
 * @version $Id: GraphTraversalElement.java 1946 2010-02-26 17:55:34Z jmontgomery $
 */
public final class GraphTraversalElement<V,E>
{
	//private final boolean isSource;
	private final Vertex<V> currentVertex;
	private final Edge<V,E> currentEdge;
	private final GraphTraversalAlgorithm<V,E> alg;
	private final Integer prevPathId;
	private final Integer pathId;
	
	GraphTraversalElement(GraphTraversalAlgorithm<V,E> alg,Vertex<V> v,Edge<V,E> e,
			Integer prevPathId,Integer pathId)
	{
		this.currentVertex = v;
		this.currentEdge = e;
		this.alg = alg;
		this.prevPathId = prevPathId;
		this.pathId = pathId;
		
	}
	
	/**
	 * Returns previous identifier for this path prior to adding this edge.
	 * 
	 * @return previous path identifer or <code>null</code> if path tracking is not maintained for this traversal
	 */
	public Integer getPreviousPathId() { return prevPathId; }
	
	/**
	 * Returns current identifier for this path after adding this edge.
	 * 
	 * @return current path identifer or <code>null</code> if path tracking is not maintained for this traversal
	 */
	public Integer getCurrentPathId() { return pathId; }
	
	
	/**
	 * Returns the algorithm that produced this traversal element.
	 * 
	 * @return the calling <code>GraphTraversalAlgorithm</code>
	 */
	public GraphTraversalAlgorithm<V,E> getTraversingAlgorithm()
	{
		return alg;
	}
	/**
	 * Returns the current vertex that was traversed.  Traversed edges are always the targets of 
	 * the current edge if it is defined.
	 * 
	 * @return the current vertex in the traversal
	 */
	public Vertex<V> getCurrentVertex() { return currentVertex; }
	
	/**
	 * The current edge that was traversed.  May be <code>null</code> if the 
	 * source vertex was traversed or if the traversal does not consider edges.
	 * 
	 * @return the current edge in the traversal
	 */
	public Edge<V,E> getCurrentEdge() { return currentEdge; }
	
	
	///**
	// * Returns whether the traversed vertex is the source vertex.
	// * 
	// * @return <code>true</code> if the source vertex and <code>false</code> otherwise
	// */
	//public boolean isSourceVertex() { return isSource; }

}
