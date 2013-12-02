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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Implementation of Djikstra's algorithm.  Based on the implementation given
 * on <a href="http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Wikipedia</a>.
 * 
 * @author Justin Montgomery
 * @version $Id: Djikstra.java 1946 2010-02-26 17:55:34Z jmontgomery $
 */
public final class Djikstra<V,E> extends AbstractGraphTraversal<V, E> 
{
	/**
	 * Constructor.
	 * 
	 * @param g the {@link Graph} to traverse
	 * @param source source {@link Vertex} for traversal
	 */
	public Djikstra(Graph<V,E> g,Vertex<V> source)
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
	public Djikstra(Graph<V,E> g,Vertex<V> source,GraphTraversalCallback<V,E> cb)
	{
		super(g,source,cb);
	}
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.GraphAlgorithm#execute()
	 */
	public Djikstra<V,E> execute() 
	{
		prepareGraph();

		callback.onBegin(this);
		
		// insert all unoptimized vertices
		SortedSet<Vertex<V>> queue = 
			new TreeSet<Vertex<V>>();
		for(Iterator<Vertex<V>> it = graph.vertexIterator();it.hasNext();)
		{
			Vertex<V> v = it.next();
			queue.add(v);
		}
		
		while(!queue.isEmpty())
		{
			Vertex<V> u = queue.first();
			queue.remove(u);
			// TODO: BFS and DFS make copy of vertices before modifying parent,level do we need that here?
			// can't remember why it was done in first place but it was done for a reason
			
			boolean traverse = callback.onTraverse(new GraphTraversalElement<V,E>(this,u,null,null,null));
			if(!traverse) // end the search now
				break;
			
			traversal.add(u);
			
			// for all neighbors v of u
			for(Iterator<Edge<V,E>> it = graph.edgeIterator(u); it.hasNext();)
			{
				Edge<V,E> e = it.next();
				
				// only consider outgoing edges
				Vertex<V> v = e.getTo();
				if(u == v) continue;
				
				float currDist = v.getWeight();
				float newDist = u.getWeight() + e.getWeight();
				// relax u,v
				if(newDist < currDist)
				{
					// must remove from sorted set and re-add since changing the comparison value
					queue.remove(v);
					v.setWeight(newDist);
					v.setParent(u);
					v.setLevel(u.getLevel() + 1);
					queue.add(v);
				}
			}
		}
		
		callback.onFinish(this);
		
		return this;
	}
	
	private void prepareGraph()
	{
		source.setWeight(0.0f);
		source.setLevel(0);
		traversal = new ArrayList<Vertex<V>>(graph.numVerts());
	}

}
