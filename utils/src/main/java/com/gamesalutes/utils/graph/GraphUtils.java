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

import com.gamesalutes.utils.CollectionUtils;

/**
 * Utility class for graphs.
 * 
 * @author Justin Montgomery
 * @version $Id: GraphUtils.java 1895 2010-02-05 23:25:13Z jmontgomery $
 *
 */
public class GraphUtils
{
	private GraphUtils() {}
	
	
	/**
	 * Finds the absolute source in <code>graph</code> if one can be determined,i.e.
	 * if this graph is actually a tree. <code>graph</code> must be directed.
	 * 
	 * @param <V> vertex type of the graph
	 * @param <E> edge type of the graph
	 * @param graph the {@link Graph}.
	 * 
	 * @return the source {@link Vertex} or <code>null</code> if one cannot
	 *         be determined
	 * @throws IllegalArgumentException if <code>graph</code> is undirected
	 */
	public static <V,E> Vertex<V> findSource(Graph<V,E> graph)
	{
		if(graph == null)
			throw new NullPointerException("graph");
		if(!graph.isDirected())
			throw new IllegalArgumentException("graph is undirected");
		
		List<Vertex<V>> sources = findSources(graph);
		if(sources != null && sources.size() == 1)
			return sources.get(0);
		else
			return null;
	}
	
	/**
	 * Finds the absolute sources in <code>graph</code> if they can be determined.
	 * <code>graph</code> must be directed.
	 * 
	 * @param <V> vertex type of the graph
	 * @param <E> edge type of the graph
	 * @param graph the {@link Graph}.
	 * 
	 * @return a <code>List</code> of absolute sources
	 * 
	 * @throws IllegalArgumentException if <code>graph</code> is undirected
	 */
	public static <V,E> List<Vertex<V>> findSources(Graph<V,E> graph)
	{
		if(graph == null)
			throw new NullPointerException("graph");
		if(!graph.isDirected())
			throw new IllegalArgumentException("graph is undirected");
		
		//if a source is used as dest then not absolute source
		Set<Vertex<V>> allDests = new HashSet<Vertex<V>>();
		//iterate over the edges and add all the dests to the set
		for(Iterator<Edge<V,E>> it = graph.edgeIterator(); it.hasNext();)
			allDests.add(it.next().getTo());
		List<Vertex<V>> sources = new ArrayList<Vertex<V>>();
		
		//now iterate over the vertices and see if they are ever used as dest
		for(Iterator<Vertex<V>> it = graph.vertexIterator(); it.hasNext();)
		{
			Vertex<V> v = it.next();
			//check to see if v is an absolute source
			if(!allDests.contains(v))
				sources.add(v);
		}
		return sources;
	}
	
	
	
	/**
	 * Finds the absolute sources in <code>graph</code> sorted using the provided
	 * <code>Comparator</code>. <code>graph</code> must be directed.
	 * 
	 * @param <V> vertex type of the graph
	 * @param <E> edge type of the graph
	 * @param graph the {@link Graph}.
	 * @param comparator comparator for comparing the traversal lists which determines
	 *        the order of the sorted sources
	 * 
	 * @return a <code>List</code> of absolute sources
	 * 
	 * @throws IllegalArgumentException if <code>graph</code> is undirected
	 */
	public static <V,E> List<Vertex<V>> findSortedSources(Graph<V,E> graph,
			Comparator<List<Vertex<V>>> comparator)
	{
		if(graph == null)
			throw new NullPointerException("graph");
		if(comparator == null)
			throw new NullPointerException("comparator");
		if(!graph.isDirected())
			throw new IllegalArgumentException("graph is undirected");
		
		List<Vertex<V>> sources = findSources(graph);
		
		SortedMap<List<Vertex<V>>,Vertex<V>> map = new
			TreeMap<List<Vertex<V>>,Vertex<V>>(comparator);
		
		for(Vertex<V> source : sources)
		{
			GraphTraversalAlgorithm<V,E> alg = 
				new BFS<V,E>(graph,source);
			alg.execute();
			map.put(alg.getTraversal(), source);
		}
		
		return new ArrayList<Vertex<V>>(map.values());
	}
	/**
	 * Finds the absolute sources in <code>graph</code> sorted by the largest component
	 * they form if they can be determined. <code>graph</code> must be directed.
	 * 
	 * 	@param <V> vertex type of the graph
	 * @param <E> edge type of the graph
	 * @param graph the {@link Graph}.
	 * 
	 * @return a <code>List</code> of absolute sources
	 * 
	 * @throws IllegalArgumentException if <code>graph</code> is undirected
	 */
	public static <V,E> List<Vertex<V>> findSortedSources(Graph<V,E> graph)
	{
		return findSortedSources(graph,new GraphSizeComparator<V>());
	}
	
	/**
	 * Computes the graph "difference" <code>g1 - g2</code>.
	 *   <code>outRemovedVertices</code> will contain
	 * all the vertices present in <code>g2</code> but not present in 
	 * <code>g1</code>.  <code>outRemovedEdges</code> will contain all the
	 * edges present in <code>g2</code> but not present in <code>g1</code>.
	 * <code>outAddedVertices</code> will contain all the vertices 
	 * present in <code>g1</code> but not present in <code>g2</code>.
	 * <code>outAddedEdges</code> will contain all the edges 
	 * present in <code>g1</code> but not present in <code>g2</code>.
	 * If a parameter is <code>null</code> then no calculation will be performed
	 * for that parameter.
	 * 
	 * @param <V> vertex type
	 * @param <E> edge type
	 * @param g1 first graph
	 * @param g2 second graph
	 * @param outRemovedVertices vertices in <code>g1</code> but not <code>g2</code>
	 * @param outRemovedEdges edges in <code>g1</code> but not <code>g2</code>
	 * @param outAddedVertices vertices in <code>g2</code> but not <code>g1</code>
	 * @param outAddedEdges edges in <code>g2</code> but not <code>g1</code>
	 * 
	 */
	public static <V,E> void graphDiff(Graph<V,E> g1,Graph<V,E> g2,
			Collection<Vertex<V>> outRemovedVertices,
			Collection<Edge<V,E>> outRemovedEdges,
			Collection<Vertex<V>> outAddedVertices,
			Collection<Edge<V,E>> outAddedEdges)
	{
		if(g1 == null)
			throw new NullPointerException("g1");
		if(g2 == null)
			throw new NullPointerException("g2");
		
		/*
		if(outRemovedVertices == null)
			throw new NullPointerException("outRemovedVertices");
		if(outRemovedEdges == null)
			throw new NullPointerException("outRemovedEdges");
		if(outAddedVertices == null)
			throw new NullPointerException("outAddedVertices");
		if(outAddedEdges == null)
			throw new NullPointerException("outAddedEdges");
		*/
		
		// process removed by iterating over g2 and checking if in g1
		if(outRemovedVertices != null)
		{
			for(Iterator<Vertex<V>> it = g2.vertexIterator(); it.hasNext();)
			{
				Vertex<V> v = it.next();
				if(!g1.containsVertex(v))
					outRemovedVertices.add(v);
				
			}
		}
		
		if(outRemovedEdges != null)
		{
			for(Iterator<Edge<V,E>> it = g2.edgeIterator(); it.hasNext();)
			{
				Edge<V,E> e = it.next();
				if(!g1.containsEdge(e))
					outRemovedEdges.add(e);
			}
		}
		
		// process added by iterating over g1 and checking if in g2
		if(outAddedVertices != null)
		{
			
			for(Iterator<Vertex<V>> it = g1.vertexIterator(); it.hasNext();)
			{
				Vertex<V> v = it.next();
				if(!g2.containsVertex(v))
					outAddedVertices.add(v);
			}
		}
		
		if(outAddedEdges != null)
		{
			for(Iterator<Edge<V,E>> it = g1.edgeIterator(); it.hasNext();)
			{
				Edge<V,E> e = it.next();
				if(!g2.containsEdge(e))
					outAddedEdges.add(e);
			}
		}
	}
	
	/**
	 * Returns the largest connected component in <code>g</code>.
	 * Directedness is <b><i>NOT</i></b> considered.
	 * 
	 * @param <V> the vertex type
	 * @param <E> the edge type
	 * @param g the {@link Graph}
	 * @return a <code>Set</code> of the vertices in the largest connected
	 *         component in the graph
	 */
	public static <V,E> Set<Vertex<V>> getLargestConnectedSubgraph(Graph<V,E> g)
	{
		// connections are not directional
		g = g.makeUndirected();
		
		int vertexCount = g.numVerts();
		
		if(vertexCount == 0)
			return Collections.<Vertex<V>>emptySet();
		
		ArrayList<Set<Vertex<V>>> subgraphs = new ArrayList<Set<Vertex<V>>>();
		
		// perform a BFS query on all the vertices to find all the connected
		// components
		for(Iterator<Vertex<V>> vIt = g.vertexIterator(); vIt.hasNext();)
		{
			Vertex<V> v = vIt.next();
			
			// skip nodes that have already been encountered in a previous subgraph since
			// all their reachable connections have already been explored
			boolean skipNode = false;
			// examine all the currently traversed nodes in the subgraph
			// results and do not traverse this vertex if it has already
			// been examined
			for(Set<Vertex<V>> subgraph : subgraphs)
			{
				if(subgraph.contains(v))
				{
					skipNode = true;
					break;
				}
			}
			
			if(!skipNode)
			{
				// perform the BFS query on the current vertex
				 
				 GraphTraversalAlgorithm<V,E> alg = new BFS<V,E>(
						 g,v);
				 alg.execute();
				 Set<Vertex<V>> subgraph = new HashSet<Vertex<V>>(
						 alg.getTraversal());
	            if(subgraph.size()>vertexCount/2) return subgraph; //We are done
	            subgraphs.add(subgraph);  
			}
			
		} // end vertex iterator
		
        int maxSize=0;
        int maxIndex=0;
        for (int j = 0; j<subgraphs.size();j++) {
            if((subgraphs.get(j)).size()>maxSize) {
                maxSize = subgraphs.get(j).size();
                maxIndex = j;
            }
        }   
       
        return subgraphs.get(maxIndex);
		
	}
	
	
	/**
	 * Returns the vertices that are not part of the connected component
	 * that contains <code>source</code>.
	 * Directedness is <b><i>NOT</i></b> considered.
	 * @param <V> vertex type
	 * @param <E> edge type
	 * @param g the {@link Graph}
	 * @param source the source {@link Vertex}
	 * @return a <code>Set</code> of vertices not connected to <code>source</code>
	 */
	public static <V,E> Set<Vertex<V>> getVerticesNotConnectedToSource(
			Graph<V,E> g,Vertex<V> source)
	{
		return getVerticesNotConnectedToSource(g,Arrays.asList(source));
	}
	/**
	 * Returns the vertices that are not part of the connected component
	 * that contains <code>source</code>.
	 * Directedness is <b><i>NOT</i></b> considered.
	 * @param <V> vertex type
	 * @param <E> edge type
	 * @param g the {@link Graph}
	 * @param sources the sources {@link Vertex}
	 * @return a <code>Set</code> of vertices not connected to <code>source</code>
	 */
	public static <V,E> Set<Vertex<V>> getVerticesNotConnectedToSource(
			Graph<V,E> g,Collection<Vertex<V>> sources)
	{
		// connections are not directional
		g = g.makeUndirected();
    	final int size = g.numVerts();
    	if(size == 0) return Collections.<Vertex<V>>emptySet();
    	
    	final Set<Vertex<V>> visited = CollectionUtils.createHashSet(
    			size,CollectionUtils.LOAD_FACTOR);
    	final Set<Vertex<V>> nodes = CollectionUtils.createLinkedHashSet(
    			size,CollectionUtils.LOAD_FACTOR);
    	
    	for(Iterator<Vertex<V>> it = g.vertexIterator(); it.hasNext();)
    		nodes.add(it.next());
    	
    	// Use BFS to traverse all the nodes connected to the source
    	for(Vertex<V> source : sources)
    	{
    		if(visited.contains(source)) continue;
    		
    		
	    	GraphTraversalAlgorithm<V,E> alg = new BFS<V,E>(g,source,
	    			new GraphTraversalCallback<V,E>()
	    	{
	
				public boolean onTraverse(GraphTraversalElement<V,E> e) 
				{
					// note those visited
					visited.add(e.getCurrentVertex());
					return true;
				}
	
				public void onBegin(GraphTraversalAlgorithm<V, E> g){}
	
				public void onFinish(GraphTraversalAlgorithm<V, E> g){}
	    		
	    	});
	    	
	    	alg.execute();
	    	
    	} // for
    	
    	// those not visited is allNodes - visited
    	nodes.removeAll(visited);
    	
    	return nodes;
    	
	}
	
	
	/**
	 * Returns whether <code>g</code> is connected. A graph is connected if given a vertex v in the graph,
	 * every other vertex can be reached via some path from it.  Directedness <b><i>is</i></b> considered.
	 * 
	 * @param g the <code>Graph</code>
	 * @return <code>true</code> if connected and <code>false</code> otherwise
	 */
	public static <V,E> boolean isConnected(Graph<V,E> g)
	{
		// empty graph is connected
		if(g.numVerts() == 0) return true;
		
		// connections are not directional
//		g = g.makeUndirected();
		
    	final int size = g.numVerts();
    	
    	final Set<Vertex<V>> visited = CollectionUtils.createHashSet(
    			size,CollectionUtils.LOAD_FACTOR);
    	final Set<Vertex<V>> nodes = CollectionUtils.createHashSet(
    			size,CollectionUtils.LOAD_FACTOR);
    	
    	for(Iterator<Vertex<V>> it = g.vertexIterator(); it.hasNext();)
    		nodes.add(it.next());
		
		// get an arbitrary source
		Vertex<V> source = g.vertexIterator().next();
		
    	// Use BFS to traverse all the nodes connected to the source
    	GraphTraversalAlgorithm<V,E> alg = new BFS<V,E>(g,source,
    			new GraphTraversalCallback<V,E>()
    	{

			public boolean onTraverse(GraphTraversalElement<V,E> e) 
			{
				visited.add(e.getCurrentVertex());
				return true;
			}

			public void onBegin(GraphTraversalAlgorithm<V, E> g){}

			public void onFinish(GraphTraversalAlgorithm<V, E> g){}
    		
    	});
    	
    	alg.execute();
    	
    	return visited.equals(nodes);
	}
	/**
	 * Returns the connected component sub-graphs within <code>g</code>.  If <code>g</code> is connected then
	 * itself is returned.  Directedness <b><i>is</i></b> considered.
	 * 
	 * @param <V> vertex type
	 * @param <E> edge type
	 * @param g the {@link Graph}
	 * @return a <code>Set</code> of vertices not connected to <code>source</code>
	 */
	public static <V,E> Set<Graph<V,E>> getConnectedComponents(
			Graph<V,E> g)
	{
		boolean isDirected = g.isDirected();
//		if(isDirected)
//		{
//			// connections are not directional
//			g = g.makeUndirected();
//		}
		
		Set<Graph<V,E>> components = new HashSet<Graph<V,E>>();
		
    	final int size = g.numVerts();
    	if(size == 0 || isConnected(g))
    	{
    		components.add(g);
    		return components;
    	}
    	
    	final Set<Vertex<V>> nodes = CollectionUtils.createLinkedHashSet(
    			size,CollectionUtils.LOAD_FACTOR);
    	
    	for(Iterator<Vertex<V>> it = g.vertexIterator(); it.hasNext();)
    		nodes.add(it.next());
    	
    	
    	while(!nodes.isEmpty())
    	{
    		Vertex<V> source = nodes.iterator().next();
    		final Graph<V,E> subgraph = new AdjacencyList<V,E>(isDirected);
    		
	    	// Use BFS to traverse all the nodes connected to the source
	    	GraphTraversalAlgorithm<V,E> alg = new BFS<V,E>(g,source,
	    			new GraphTraversalCallback<V,E>()
	    	{
	
				public boolean onTraverse(GraphTraversalElement<V,E> e) 
				{
					Vertex<V> v = e.getCurrentVertex();
					Edge<V,E> edge = e.getCurrentEdge();
					
					subgraph.addVertex(v);
					if(edge != null)
						subgraph.addEdge(edge);
					// note those visited
					nodes.remove(v);
					
					return true;
				}
	
				public void onBegin(GraphTraversalAlgorithm<V, E> g){}
	
				public void onFinish(GraphTraversalAlgorithm<V, E> g){}
	    		
	    	});
	    	
	    	alg.execute();
	    	
	    	// add the subgraph to graph set
	    	components.add(subgraph);
	    	
    	} // while
    	
    	return components;
	}
	/**
	 * Removes paths returned by {@link GraphTraversalAlgorithm#getPath(Vertex)} that are subsets of each other
	 * so that only the longest paths are returned.
	 * 
	 * @param <V> vertex types
	 * @param paths input paths
	 * @return only disjoint paths
	 */
	public static <V> List<List<Vertex<V>>> removePathSubsets(List<List<Vertex<V>>> paths)
	{
		return removePathSubsets(paths,null);
	}
	/**
	 * Removes paths returned by {@link GraphTraversalAlgorithm#getPath(Vertex)} that are subsets of each other
	 * so that only the longest paths are returned.
	 * 
	 * @param <V> vertex types
	 * @param paths input paths
	 * @param toIgnore <code>Set</code> of vertices to ignore from sublist test
	 * @return only disjoint paths
	 */
	public static <V> List<List<Vertex<V>>> removePathSubsets(List<List<Vertex<V>>> paths,
			Set<Vertex<V>> toIgnore)
	{
		//sort the path data
		//once sorted, paths that are subsets of each
		//other will be adjacent
		GraphSubsetComparator<V> comp = new GraphSubsetComparator<V>(toIgnore);
		
		//dump into linked list for more efficient removing ops
//		List<List<Vertex<V>>>  sortedPaths = new
//			LinkedList<List<Vertex<V>>>(paths);
		
		
		
		// TODO: can't get this to work so revert to n^2 comparisons: seems that ordering condition for non-subsets is incorrect
		// and can't figure out what it should be 
//		Collections.sort(sortedPaths,comp);
//
//        // now remove paths that are subsets of each other
//		for(ListIterator<List<Vertex<V>>> it = sortedPaths.listIterator();
//			it.hasNext();)
//		{
//			List<Vertex<V>> first = it.next();
//			
//			//if there is a second one to compare
//			if(it.hasNext())
//			{
//				List<Vertex<V>> second = it.next();
//				/*int result = */comp.compare(first,second);
//				if(comp.isSubset)
//				{
//					//remove first using the iterator
//					//must go to back to first and remove it
//					//then go back to second
//					// in order to go to first, must call previous twice since
//					// first call returns last element returned by next (second)
//					it.previous();
//					it.previous();
//					//remove second using the iterator
//					it.remove();
//				} //end if(comp.isSubset)
//				else // must compare second again with following vertex!
//				{
//					it.previous();
//				}
//			} //end if(it.hasNext())
//		} //end for
		
		
		List<List<Vertex<V>>>  sortedPaths = new
			ArrayList<List<Vertex<V>>>(paths);
		
		// place largest indices first
		SortedSet<Integer> toRemove = new TreeSet<Integer>(Collections.reverseOrder());
		
		for(int i = 0; i < sortedPaths.size(); ++i)
		{
			for(int j = i + 1; j < sortedPaths.size(); ++j)
			{
				// smallest one is returned first
				int result = comp.compare(sortedPaths.get(i), sortedPaths.get(j));
				if(comp.isSubset)
				{
					if(result <= 0)
					{
						toRemove.add(i);
						break;
					}
					else
						toRemove.add(j);
				}
			}
		}
		
		for(int index : toRemove)
			sortedPaths.remove(index);
		
		return sortedPaths;
		
		//else no changes required
	} //end compressPaths
	
	private static final class GraphSizeComparator<V> implements Comparator<List<Vertex<V>>>
	{
		public int compare(List<Vertex<V>> trav1,List<Vertex<V>> trav2)
		{
			return trav2.size() - trav1.size();
		}
	}
	
	private static final class GraphSubsetComparator<V> implements Comparator<List<Vertex<V>>>
	{
		public boolean isSubset;
		
		private final Set<Vertex<V>> toIgnore;
		
		private final List<Vertex<V>> list1 = new LinkedList<Vertex<V>>();
		private final List<Vertex<V>> list2 = new LinkedList<Vertex<V>>();
//		private final Set<List<Vertex<V>>> subsetSet = new HashSet<List<Vertex<V>>>();
		
		
		private void populateList(List<Vertex<V>> input,List<Vertex<V>> output)
		{
			output.clear();
			for(Vertex<V> v : input)
			{
				if(!toIgnore.contains(v))
					output.add(v);
			}
		}
		public GraphSubsetComparator(Set<Vertex<V>> toIgnore)
		{
			this.toIgnore = toIgnore != null ? toIgnore : Collections.<Vertex<V>>emptySet();
		}
		public int compare(List<Vertex<V>> trav1,List<Vertex<V>> trav2)
		{
			// see if trav1 subset of trav2 or vice-versa
			populateList(trav1,list1);
			populateList(trav2,list2);
			
	        // larger one is source and smaller one is target
			List<Vertex<V>> source = list1.size() >= list2.size() ? list1 : list2;
			List<Vertex<V>> target = source == list1 ? list2 : list1;
			
            isSubset = Collections.indexOfSubList(source, target) != -1;
            
            if(isSubset)
            {
//            	subsetSet.add(trav1);
//            	subsetSet.add(trav2);
            	
            	// smallest first
            	return list1.size() - list2.size();
            }
            else
            {
            	// FIXME: cannot figure out ordering condition here!?!?
            	return 0;
            	
//            	boolean trav1InSubset = subsetSet.contains(trav1);
//            	boolean trav2InSubset = subsetSet.contains(trav2);
//            	if(trav1InSubset || trav2InSubset) 
//            		return 0;
//            	
//            	return GeneralComparator.uniqueCompareTo(trav1,trav2);
            	
            }
		}
	}
	
	/**
	 * Turns a directed or undirected graph into a directed one where there is at most one edge between any two
	 * given vertices.
	 * 
	 * @param _graph the input graph
	 * @param sources the source vertices in the graph
	 * @return the converted graph
	 */
	public static <V,E> Graph<V,E> makeDirectional(Graph<V,E> _graph,List<Vertex<V>> sources)
	{
		if(_graph == null)
			throw new NullPointerException("graph");
		if(sources == null)
			throw new NullPointerException("sources");
		
		Graph<V,E> g = _graph.clone().makeUndirected();
		
		
		
		/*
    		  1) Run BFS on all sources of graph
			  2) Find the set of "unpathed edges in 1)" - those edges that are not in a shortest path
			       but are part of a connected component of the sources in 1)
			  3)  Construct sets of connected edges from the unpathed edges in 2)
			  4) For every set of connected edges, remove the edge of an incident vertex, that is in a shortest path, 
			      to an  endpoint vertex in the connected set, and rerun 1) with source that is in 
			      the shortest path of the removed edge.  Note the new path and remove the edges in that
			      path from the set of unpathed edges
			  5) Repeat 4 until there are no more unpathed edges
		 */
		
		sources = new ArrayList<Vertex<V>>(sources);
		// unconnected vertices must be sources themselves
		sources.addAll(getVerticesNotConnectedToSource(g,sources));
		
		List<List<Edge<V,E>>> allPaths = new ArrayList<List<Edge<V,E>>>();
		
		final Set<Vertex<V>> visited = new HashSet<Vertex<V>>();
		final Set<Edge<V,E>> visitedEdges = new HashSet<Edge<V,E>>();
		
		// 1)
		for(Vertex<V> source : sources)
		{
    		if(visited.contains(source)) continue;
    		
    		
	    	GraphTraversalAlgorithm<V,E> alg = new BFS<V,E>(g,source,
	    			new GraphTraversalCallback<V,E>()
	    	{
	
				public boolean onTraverse(GraphTraversalElement<V,E> e) 
				{
					// note those visited
					visited.add(e.getCurrentVertex());
					Edge<V,E> edge = e.getCurrentEdge();
					if(edge != null)
					{
						visitedEdges.add(edge);
						visitedEdges.add(edge.reverse());
					}
					return true;
				}
	
				public void onBegin(GraphTraversalAlgorithm<V, E> g){}
	
				public void onFinish(GraphTraversalAlgorithm<V, E> g){}
	    		
	    	});
	    	
	    	alg.execute();
	    	
	    	// TODO: remove duplicate paths?
	    	allPaths.addAll(createEdgePaths(g,alg));
	    	
		} // for all source vertices
		
		// 2)
		List<Edge<V,E>> unpathedEdges = new ArrayList<Edge<V,E>>();
		
		// must not add back edge for source otherwise won't find any roots!
    	for(Iterator<Edge<V,E>> i = g.edgeIterator(); i.hasNext();)
    	{
    		Edge<V,E> e = i.next();
    		if(!visitedEdges.contains(e))
    			unpathedEdges.add(e);
    	}
    	
    	// 3)
    	List<List<Edge<V,E>>> allUnpathed = createEdgePaths(unpathedEdges,sources);
    	
    	allPaths.addAll(allUnpathed);
    	// 4)
//    	while(!allUnpathed.isEmpty())
//    	{
//    		List<Edge<V,E>> pathSubset = null;
//    		List<Edge<V,E>> foundList = null;
//    		
//    		outer:
//    		for(List<Edge<V,E>> upath : allUnpathed)
//    		{
//    			for(int ui = 0; ui < upath.size(); ++ui)
//    			{
//    				Edge<V,E> uedge = upath.get(ui);
//    				
//    				for(List<Edge<V,E>> path : allPaths)
//    				{
//    					for(Edge<V,E> edge : path)
//    					{
//    						if(edge.getTo().equals(uedge.getTo()))
//    						{
//    							foundList = upath;
//    							pathSubset = upath.subList(0, ui+1);
//    							g.removeEdge(edge);
//    							break outer;
//    						}
//    					}
//    				}
//    			}
//    		} // outer
//    		
//    		if(foundList == null)
//    			throw new AssertionError();
//    		
//    		// add the path subset to the set of all paths
//    		allPaths.add(new ArrayList<Edge<V,E>>(pathSubset));
//    		
//    		// update unpathed list
//    		foundList.removeAll(pathSubset);
//    		if(foundList.isEmpty())
//    			allUnpathed.remove(foundList);
//
//    		
//    	}
    	
    	return createGraphFromPaths(allPaths);
		
	}
	
	private static <V,E> List<List<Edge<V,E>>> createEdgePaths(List<Edge<V,E>> edges,Collection<Vertex<V>> sources)
	{
		List<List<Edge<V,E>>> edgePaths = new ArrayList<List<Edge<V,E>>>();
		
		// must reorder edges so that connected groups are together
		
		Set<Integer> rootIndices = new HashSet<Integer>();
		
		// find the root edges: those that go through a source vertex
		for(int i = 0; i < edges.size(); ++i)
		{
			Edge<V,E> e = edges.get(i);
			if(sources.contains(e.getFrom()))
				rootIndices.add(i);
		}
//		for(int i = 0, size = edges.size(); i < size; ++i)
//		{
//			Edge<V,E> e1 = edges.get(i);
//			
//			boolean isRoot = true;
//			for(int j = 0; j < size; ++j)
//			{
//				if(i != j)
//				{
//					Edge<V,E> e2 = edges.get(j);
//					if(e1.getTo().equals(e2.getFrom()))
//					{
//						isRoot = false;
//						break;
//					}
//					
//				}
//			}
//			if(isRoot)
//				rootIndices.add(i);
//		}
		
		
		// FIXME: this doesn't work in all cases!  Will have to try the gradual add method that the caller of this method proposed
		// don't add "back" edges
		Set<Edge<V,E>> addedEdges = new HashSet<Edge<V,E>>();
		// connect other edges to each root in order
		for(int rootIndex : rootIndices)
		{
			Edge<V,E> currEdge = edges.get(rootIndex);
			List<Edge<V,E>> edgePath = new ArrayList<Edge<V,E>>();
			edgePath.add(currEdge);
			addedEdges.add(currEdge);
			
			boolean found;
			final int size = edges.size();
			int count = 0;
			do
			{
				found = false;
				for(int i = 0; i < size; ++i)
				{
					if(i != rootIndex)
					{
						Edge<V,E> e = edges.get(i);
						
						// don't add a backward edge
						if(currEdge.getTo().equals(e.getFrom()) && !addedEdges.contains(e) && 
								!addedEdges.contains(e.reverse()))
						{
							currEdge = e;
							edgePath.add(e);
							addedEdges.add(e);
							found = true;
							++count;
							break;
						}
					}
				}
			}
			while(found && count < size - 1);
			
			edgePaths.add(edgePath);
		} // for
		
		return edgePaths;
	}
	
//	private static <V> boolean isLeaf(Vertex<V> vertex,Collection<Vertex<V>> all)
//	{
//		for(Vertex<V> v : all)
//		{
//			if(vertex != v && v.getParent() != null && v.getParent().equals(vertex))
//				return false;
//		}
//		return true;
//		
//	}
	private static <V,E> List<List<Edge<V,E>>> createEdgePaths(Graph<V,E> g,GraphTraversalAlgorithm<V,E> alg)
	{
		// form a tree from the traversal
		List<List<Edge<V,E>>> edgeLists = new ArrayList<List<Edge<V,E>>>();
		List<Vertex<V>> traversal = alg.getTraversal();
		
		if(!traversal.isEmpty())
		{
			Set<Vertex<V>> proc = new HashSet<Vertex<V>>();
			for(Vertex<V> dest : traversal)
			{
				if(proc.add(dest) /* && isLeaf(dest,traversal)*/)
				{
					List<Edge<V,E>> edges = new ArrayList<Edge<V,E>>();
					
					// find the path in original graph traversal
					List<Vertex<V>> vPath = alg.getPath(dest);
					// turn vPath into edges
					for(Vertex<V> v : vPath)
					{
						Vertex<V> parent = v.getParent();
						if(parent != null)
						{
							// get the original edge data
							E edgeData = null;
							Edge<V,E> origEdge = g.findEdge(parent, v);
							if(origEdge == null)
								origEdge = g.findEdge(v, parent);
							if(origEdge != null)
								edgeData = origEdge.getData();
							edges.add(new Edge<V,E>(parent,v,edgeData));
						}
					}
					edgeLists.add(edges);
				}
			}
		}
			
			
		// remove path subsets
//		return GraphUtils.removePathSubsets(edgeLists);
		return edgeLists;
		
//	if(!traversal.isEmpty())
//	{
//			TreeNode<Vertex<V>> root = null;
//			
//			Map<Vertex<V>,TreeNode<Vertex<V>>> nodeMap = new HashMap<Vertex<V>,TreeNode<Vertex<V>>>();
//			for(Vertex<V> v : traversal)
//			{
//				Vertex<V> parent = v.getParent();
//				if(parent != null)
//				{
//					TreeNode<Vertex<V>> parentNode = nodeMap.get(parent);
//					if(parentNode == null)
//						throw new AssertionError();
//					
//					TreeNode<Vertex<V>> node = nodeMap.get(v);
//					if(node == null)
//					{
//						node = new TreeNode<Vertex<V>>(v);
//						nodeMap.put(v, node);
//					}
//					if(node.isLeaf())
//						parentNode.addChild(node);
//				}
//				else
//				{
//					root = new TreeNode<Vertex<V>>(v);
//					nodeMap.put(v, root);
//
//				}
//			} // for
//			
//			// run a level order traversal
//			List<TreeNode<Vertex<V>>> nodePath = new LevelOrderTraversal<Vertex<V>>(root).execute().getTraversal();
//			// look for leaves
//			for(TreeNode<Vertex<V>> n : nodePath)
//			{
//				if(n.isLeaf())
//				{
//					List<Edge<V,E>> edges = new ArrayList<Edge<V,E>>();
//					
//					// find the path in original graph traversal
//					List<Vertex<V>> vPath = alg.getPath(n.getData());
//					// turn vPath into edges
//					for(Vertex<V> v : vPath)
//					{
//						Vertex<V> parent = v.getParent();
//						if(parent != null)
//						{
//							// get the original edge data
//							E edgeData = null;
//							Edge<V,E> origEdge = g.findEdge(parent, v);
//							if(origEdge == null)
//								origEdge = g.findEdge(v, parent);
//							if(origEdge != null)
//								edgeData = origEdge.getData();
//							edges.add(new Edge<V,E>(parent,v,edgeData));
//						}
//					}
//					edgeLists.add(edges);
//				}
//			}
//			
//		}
//		
//		return edgeLists;
	}
	
	private static <V,E> Graph<V,E> createGraphFromPaths(List<List<Edge<V,E>>> paths)
	{
		Graph<V,E> g = new AdjacencyList<V,E>(true);
		
		for(List<Edge<V,E>> edges : paths)
		{
			for(Edge<V,E> edge : edges)
			{
				g.addVertex(edge.getFrom());
				g.addVertex(edge.getTo());
				g.addEdge(edge);
			}
		}
		
		return g;
	}
}
