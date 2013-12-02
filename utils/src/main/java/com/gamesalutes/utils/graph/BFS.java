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
 * Breadth-first search traversal for a graph.
 * 
 * @author Justin Montgomery
 * @version $Id: BFS.java 1946 2010-02-26 17:55:34Z jmontgomery $
 *
 * @param <V> vertex type of the graph
 * @Parma <E> edge type of the graph
 */
public final class BFS<V,E> extends AbstractGraphTraversal<V,E>
{
	
	private boolean traverseMulti = false;
	
	/**
	 * Constructor.
	 * 
	 * @param g the {@link Graph} to traverse
	 * @param source source {@link Vertex} for traversal
	 */
	public BFS(Graph<V,E> g,Vertex<V> source)
	{
		super(g,source);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param g the {@link Graph} to traverse
	 * @param source source {@link Vertex} for traversal
	 * @param cb a {@link GraphTraversalCallback} for this traversal
	 */
	public BFS(Graph<V,E> g,Vertex<V> source,GraphTraversalCallback<V,E> cb)
	{
		super(g,source,cb);
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param g the {@link Graph} to traverse
	 * @param source source {@link Vertex} for traversal
	 * @param cb a {@link GraphTraversalCallback} for this traversal
	 * @param examineEqualPaths <code>true</code> to traverse multiple paths to a target at same level 
	 *        and <code>false</code> to just consider the first one encountered
	 */
	public BFS(Graph<V,E> g,Vertex<V> source,GraphTraversalCallback<V,E> cb,boolean examineEqualPaths)
	{
		super(g,source,cb);
		this.traverseMulti = examineEqualPaths;
	}
	
	public BFS<V,E> execute()
	{
		traversal = new ArrayList<Vertex<V>>(graph.numVerts());
		Queue<Vertex<V>> queue = new LinkedList<Vertex<V>>();
		Queue<Integer> pathQueue = new LinkedList<Integer>();
		
		int pathCount = 0;
		
		source.setLevel(0);
		queue.add(source);
		pathQueue.add(pathCount);
		
		
		Set<Vertex<V>> visited = new HashSet<Vertex<V>>();
		visited.add(source);
		traversal.add(source);
		
		callback.onBegin(this);
		callback.onTraverse(new GraphTraversalElement<V,E>(this,source,null,
				Integer.valueOf(pathCount),Integer.valueOf(pathCount)));
		
		List<Vertex<V>> queueCurrLevel = new ArrayList<Vertex<V>>();
		List<Integer> pathQueueCurrLevel = new ArrayList<Integer>();
		
		while(!queue.isEmpty())
		{
			queueCurrLevel.clear();
			pathQueueCurrLevel.clear();
			
			// for current level
			while(!queue.isEmpty())
			{
				Vertex<V> vertex = queue.poll();
				Integer pathId = pathQueue.poll();
				
				int edgeCount = graph.numEdges(vertex);
				
				for(Iterator<Edge<V,E>> it = graph.edgeIterator(vertex); 
					it.hasNext();)
				{
					Edge<V,E> edge = it.next();
					Vertex<V> next = edge.getTo();
					// only considering outgoing edges
					// and don't want to go backwards again
					if(!next.equals(vertex) && !next.equals(vertex.getParent()))
					{
						// must make copy of vertex
						next = next.clone();
						
						boolean firstEncounter = next.getParent() == null;
						if(firstEncounter)
						{
							next.setParent(vertex);
							next.setLevel(vertex.getLevel() + 1);	
						}
						
						Integer newPathId;
						if(edgeCount > 1)
							newPathId = Integer.valueOf(++pathCount);
						else
							newPathId = pathId;
						
						boolean traverse = callback.onTraverse(new GraphTraversalElement<V,E>(this,next,edge,pathId,newPathId));
						
						if(traverse)
						{
							if(firstEncounter)
								traversal.add(next);
							queueCurrLevel.add(next);
							pathQueueCurrLevel.add(newPathId);
						}
					} // if
				} // end for
			} //end inner while
			
			for(int i = 0; i < queueCurrLevel.size(); ++i)
			{
				Vertex<V> v = queueCurrLevel.get(i);
				if((this.traverseMulti && !visited.contains(v)) || (!this.traverseMulti && visited.add(v)))
				{
					queue.add(v);
					pathQueue.add(pathQueueCurrLevel.get(i));
				}
			}
			
			// post add them to visited list
			if(this.traverseMulti)
				visited.addAll(queueCurrLevel);
		}  // outer while
		
		callback.onFinish(this);
		
		return this;
	}

}
