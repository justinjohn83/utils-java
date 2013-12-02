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
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.gamesalutes.utils.CollectionUtils;

/**
 * An implementation of the A* Algorithm based on 
 * <a href="http://en.wikipedia.org/wiki/A-star_algorithm">Wikipedia</a>.
 * 
 * @author Justin Montgomery
 * @version $Id: AStar.java 1672 2009-09-02 19:17:16Z jmontgomery $
 */
public final class AStar<V,E> extends AbstractGraphTraversal<V, E> 
{
	private Vertex<V> target;
	private AStarCallback<V> callback;
	
	private static final int START_SIZE = 64;
	
	public interface AStarCallback<V>
	{
		float heuristicDistance(Vertex<V> source,Vertex<V> target);
	}
	
	/**
	 * Data structure to hold the goal and heuristic scores.  The final score which is
	 * the sum of the current best goal score from source to vertex v and the current heuristic
	 * score from v to target is stored as the weight in vertex v.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: AStar.java 1672 2009-09-02 19:17:16Z jmontgomery $
	 *
	 */
	private static class Score
	{
		float goalScore;
		float heuristicScore;
		
		public Score()
		{
			this(0.0f,0.0f);
		}
		public Score(float gScore,float hScore)
		{
			this.goalScore = gScore;
			this.heuristicScore = hScore;
		}
	}
	/**
	 * Constructor.
	 * 
	 * @param g
	 * @param source
	 */
	public AStar(Graph<V, E> g, Vertex<V> source,Vertex<V> target,
			AStarCallback<V> cb) 
	{
		super(g, source);
		if(!g.containsVertex(target))
			throw new IllegalArgumentException("target not in g");
		if(cb == null)
			throw new NullPointerException("cb");
		this.target = target;
		this.callback = cb;
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.graph.GraphAlgorithm#execute()
	 */
	public AStar<V,E> execute()
	{
		final int expSize = graph.numVerts() >= START_SIZE ? START_SIZE : graph.numVerts();
		
		traversal = new ArrayList<Vertex<V>>(expSize);
		Set<Vertex<V>> closedSet = CollectionUtils.createHashSet(
				expSize, CollectionUtils.LOAD_FACTOR);
		SortedSet<Vertex<V>> openSet = new TreeSet<Vertex<V>>();
		Map<Vertex<V>,Score> scoreMap = CollectionUtils.createHashMap(expSize,
				CollectionUtils.LOAD_FACTOR);
		
		// do this on demand
//		for(Iterator<Vertex<V>> it = graph.vertexIterator(); it.hasNext();)
//			scoreMap.put(it.next(), new Score());
			
		// set initial score of source
		Score sourceScore = new Score();
		sourceScore.goalScore = 0.0f;
		sourceScore.heuristicScore = callback.heuristicDistance(source, target);
		scoreMap.put(source, sourceScore);
		
		source.setWeight(sourceScore.heuristicScore);
		source.setParent(null);
		source.setLevel(0);
		
		openSet.add(source);
		
		while(!openSet.isEmpty())
		{
			Vertex<V> x = openSet.first();
			traversal.add(x);
			
			if(x == target) return this;
			
			openSet.remove(x);
			closedSet.add(x);
			
			Score xScore = scoreMap.get(x);
			
			// for all the neighbors of x
			for(Iterator<Edge<V,E>> it = graph.edgeIterator(x); it.hasNext();)
			{
				Edge<V,E> e = it.next();
				Vertex<V> y = e.getTo();
				// only considering outgoing edges
				if(y == x) continue;
				
				if(closedSet.contains(y)) continue;
				
				Score yScore = scoreMap.get(y);
				// encountered this vertex for first time
				if(yScore == null)
				{
					yScore = new Score();
					scoreMap.put(y, yScore);
				}
				
				float newGoalScore = xScore.goalScore + e.getWeight();
				boolean newScoreBetter = false;
				boolean inOpen = true;
				
				if(!openSet.contains(y))
				{
					//openSet.add(y);
					y.setParent(null);
					y.setLevel(-1);
					yScore.heuristicScore = callback.heuristicDistance(y, target);
					newScoreBetter = true;
					inOpen = false;
				}
				else if(newGoalScore < yScore.goalScore)
				{
					newScoreBetter = true;
				}
				
				if(newScoreBetter)
				{
					y.setParent(x);
					y.setLevel(x.getLevel() + 1);
					yScore.goalScore = newGoalScore;
					float score = newGoalScore + yScore.heuristicScore;
					// must remove and re-add since changing comparison value
					if(inOpen)
						openSet.remove(y);
					y.setWeight(score);
					openSet.add(y);
				}
			} // end for
	
		} // end while
		
		// couldn't reach destination
		throw new RuntimeException("Unable to reach target; source=" + source + ";target=" + target);
		
	} // end execute

}
