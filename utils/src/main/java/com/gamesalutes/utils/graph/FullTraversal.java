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
/* Copyright 2008 - 2009 University of Chicago
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class FullTraversal<V,E> extends AbstractGraphTraversal<V, E>
{

	private List<List<Vertex<V>>> allPaths;
	
	public FullTraversal(Graph<V, E> g, Vertex<V> source,
			GraphTraversalCallback<V, E> callback)
	{
		super(g, source, callback);
	}

	/**
	 * @param g
	 * @param source
	 */
	public FullTraversal(Graph<V, E> g, Vertex<V> source)
	{
		super(g, source);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.GraphTraversalAlgorithm#execute()
	 */
	public GraphTraversalAlgorithm<V, E> execute()
	{
		allPaths = new ArrayList<List<Vertex<V>>>(graph.numEdges());
		traversal = new ArrayList<Vertex<V>>(graph.numVerts());
		Queue<Vertex<V>> queue = new LinkedList<Vertex<V>>();
		Queue<List<Vertex<V>>> pathQueue = new LinkedList<List<Vertex<V>>>();
		
		Queue<Integer> pathIdQueue = new LinkedList<Integer>();
		int pathCount = 0;
		
		source.setLevel(0);
		queue.add(source);
		pathIdQueue.add(pathCount);
		
		{
			List<Vertex<V>> sourcePath = new ArrayList<Vertex<V>>(1);
			sourcePath.add(source);
			pathQueue.add(sourcePath);
			allPaths.add(sourcePath);
		}
		
		Set<Edge<V,E>> visited = new HashSet<Edge<V,E>>();
		traversal.add(source);
		
		callback.onBegin(this);
		callback.onTraverse(new GraphTraversalElement<V,E>(this,source,null,
				Integer.valueOf(pathCount),Integer.valueOf(pathCount)));
		
		while(!queue.isEmpty())
		{
			Vertex<V> vertex = queue.poll();
			List<Vertex<V>> parentPath = pathQueue.poll();
			Integer pathId = pathIdQueue.poll();
			
			int edgeCount = graph.numEdges(vertex);
			
			for(Iterator<Edge<V,E>> it = graph.edgeIterator(vertex); 
				it.hasNext();)
			{
				Edge<V,E> edge = it.next();
				
				// make sure we don't go in a cycle
				if(!visited.add(edge))
					continue;
				
				Vertex<V> next = edge.getTo();
				// only considering outgoing edges
				if(!next.equals(vertex))
				{
					// must make copy of vertex
					next = next.clone();
					next.setParent(vertex);
					next.setLevel(vertex.getLevel() + 1);	
					
					Integer newPathId;
					if(edgeCount > 1)
						newPathId = Integer.valueOf(++pathCount);
					else
						newPathId = pathId;
					
					boolean traverse = callback.onTraverse(new GraphTraversalElement<V,E>(this,next,edge,pathId,newPathId));
					
					if(traverse)
					{
						
						traversal.add(next);
						
						List<Vertex<V>> newVSet;
								
						//add the parent relations
						newVSet = new ArrayList<Vertex<V>>(
								parentPath.size() + 1);
						newVSet.addAll(parentPath);
						
						//add the current vertex to ongoing path
						newVSet.add(next);
						allPaths.add(newVSet);
						
						// queue up
						pathQueue.add(newVSet);
						pathIdQueue.add(newPathId);
						queue.add(next);

					} // traverse
				}
			} // end for
		} //end while
		
		callback.onFinish(this);
		
		return this;
	}
	
	/**
	 * Returns all the paths traversed during the traversal.
	 * 
	 * @return all the traversed paths
	 */
	public List<List<Vertex<V>>> getAllPaths()
	{
		return allPaths;
	}

}
