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

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.*;
/**
 * Test case for {@link GraphUtils}.
 * 
 * @author Justin Montgomery
 * @version $Id: GraphUtilsTest.java 1853 2010-01-15 23:15:04Z jmontgomery $
 */
public class GraphUtilsTest 
{

	/**
	 * Tests {@link GraphUtils#findSource(Graph)}.
	 */
	@Test
	public void testFindSource() 
	{
		Graph<Integer,Integer> g = new AdjacencyList<Integer,Integer>(true);
		
		Vertex<Integer> v0 = new Vertex<Integer>(0);
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		
		Edge<Integer,Integer> e01 = new Edge<Integer,Integer>(v0,v1,4);
		Edge<Integer,Integer> e12 = new Edge<Integer,Integer>(v1,v2,5);
		Edge<Integer,Integer> e23 = new Edge<Integer,Integer>(v2,v3,6);
		
		g.addVertex(v0);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addEdge(e01);
		g.addEdge(e12);
		g.addEdge(e23);
		
		assertEquals("Correct source found",v0,GraphUtils.findSource(g));
		
	}
	
	private List<Vertex<Integer>> buildMultiSourceGraph(Graph<Integer,Integer> g)
	{
		Vertex<Integer> v0 = new Vertex<Integer>(0);
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		Vertex<Integer> v4 = new Vertex<Integer>(4);
		Vertex<Integer> v5 = new Vertex<Integer>(5);
		
		Edge<Integer,Integer> e23 = new Edge<Integer,Integer>(v2,v3,8);
		Edge<Integer,Integer> e15 = new Edge<Integer,Integer>(v1,v5,6);
		Edge<Integer,Integer> e01 = new Edge<Integer,Integer>(v0,v1,7);
		Edge<Integer,Integer> e45 = new Edge<Integer,Integer>(v4,v5,9);
		Edge<Integer,Integer> e34 = new Edge<Integer,Integer>(v3,v4,9);
		
		
		g.addVertex(v0);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addEdge(e23);
		g.addEdge(e15);
		g.addEdge(e01);
		g.addEdge(e45);
		g.addEdge(e34);
		
		return Arrays.<Vertex<Integer>>asList(v2,v0);
		
	}
	
	/**
	 * Tests {@link GraphUtils#findSources(Graph)}.
	 */
	@Test
	public void testFindSources() 
	{
		Graph<Integer,Integer> g = new AdjacencyList<Integer,Integer>(true);
		Set<Vertex<Integer>> expected = new HashSet<Vertex<Integer>>(
				buildMultiSourceGraph(g));
		
		Set<Vertex<Integer>> actual = new HashSet<Vertex<Integer>>(
				GraphUtils.findSources(g));
		
		assertEquals("All Sources found",expected,actual);
	}

	
	/**
	 * Tests {@link GraphUtils#findSortedSources(Graph)}.
	 * 
	 */
	@Test
	public void testFindSortedSources() 
	{
		Graph<Integer,Integer> g = new AdjacencyList<Integer,Integer>(true);
		List<Vertex<Integer>> expected = buildMultiSourceGraph(g);
		
		List<Vertex<Integer>> actual = GraphUtils.findSortedSources(g);
		
		assertEquals("All Sources found",expected,actual);
	}
	
	
	/**
	 * Tests {@link GraphUtils#graphDiff(
	 * 	Graph, Graph, Collection, Collection, Collection, Collection)}.
	 * 
	 */
	@Test
	public void testGraphDiff()
	{
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		Vertex<Integer> v4 = new Vertex<Integer>(4);
		Vertex<Integer> v5 = new Vertex<Integer>(5);
		
		Edge<Integer,Integer> e12 = new Edge<Integer,Integer>(v1,v2,12);
		Edge<Integer,Integer> e13 = new Edge<Integer,Integer>(v1,v3,13);
		Edge<Integer,Integer> e34 = new Edge<Integer,Integer>(v3,v4,34);
		Edge<Integer,Integer> e45 = new Edge<Integer,Integer>(v4,v5,45);
		
		// create g1
		Graph<Integer,Integer> g1 = new AdjacencyList<Integer,Integer>(true);
		g1.addVertex(v1);
		g1.addVertex(v2);
		g1.addVertex(v3);
		g1.addEdge(e12);
		g1.addEdge(e13);
		
		//create g2
		Graph<Integer,Integer> g2 = new AdjacencyList<Integer,Integer>(true);
		g2.addVertex(v1);
		g2.addVertex(v3);
		g2.addVertex(v4);
		g2.addVertex(v5);
		g2.addEdge(e13);
		g2.addEdge(e34);
		g2.addEdge(e45);
		
		// create expected collections
		Collection<Vertex<Integer>> expectedRemovedVertices = 
			new HashSet<Vertex<Integer>>(
			Arrays.<Vertex<Integer>>asList(v2));
		Collection<Edge<Integer,Integer>> expectedRemovedEdges = 
			new HashSet<Edge<Integer,Integer>>(
				Arrays.<Edge<Integer,Integer>>asList(
						e12));
		
		Collection<Vertex<Integer>> expectedAddedVertices = 
			new HashSet<Vertex<Integer>>(
			Arrays.<Vertex<Integer>>asList(v4,v5));
		
		Collection<Edge<Integer,Integer>> expectedAddedEdges = 
			new HashSet<Edge<Integer,Integer>>(
				Arrays.<Edge<Integer,Integer>>asList(
						e34,e45));
		
		// output actual collections
		Collection<Vertex<Integer>> outRemovedVertices = 
			new HashSet<Vertex<Integer>>();
		Collection<Vertex<Integer>> outAddedVertices = 
			new HashSet<Vertex<Integer>>();
		
		Collection<Edge<Integer,Integer>> outRemovedEdges = 
			new HashSet<Edge<Integer,Integer>>();
		Collection<Edge<Integer,Integer>> outAddedEdges = 
			new HashSet<Edge<Integer,Integer>>();
		
		GraphUtils.graphDiff(g2, g1,
				outRemovedVertices, outRemovedEdges, outAddedVertices, outAddedEdges);
		
		// assertion tests
		assertEquals("removedVertices",expectedRemovedVertices,outRemovedVertices);
		assertEquals("removedEdges",expectedRemovedEdges,outRemovedEdges);
		assertEquals("addedVertices",expectedAddedVertices,outAddedVertices);
		assertEquals("addedEdges",expectedAddedEdges,outAddedEdges);
		
	}
	
	/**
	 * Tests {@link GraphUtils#getLargestConnectedSubgraph(Graph)}.
	 */
	@Test
	public void testGetLargestConnectedSubgraph()
	{
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		Vertex<Integer> v4 = new Vertex<Integer>(4);
		Vertex<Integer> v5 = new Vertex<Integer>(5);
		Vertex<Integer> v6 = new Vertex<Integer>(6);
		
		Edge<Integer,Integer> e12 = new Edge<Integer,Integer>(v1,v2,12);
		Edge<Integer,Integer> e23 = new Edge<Integer,Integer>(v2,v3,23);
		Edge<Integer,Integer> e14 = new Edge<Integer,Integer>(v1,v4,14);
		Edge<Integer,Integer> e42 = new Edge<Integer,Integer>(v4,v2,42);
		
		Graph<Integer,Integer> g = new AdjacencyList<Integer,Integer>(true);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		
		g.addEdge(e12);
		g.addEdge(e23);
		g.addEdge(e14);
		g.addEdge(e42);
		
		Set<Vertex<Integer>> expected = new HashSet<Vertex<Integer>>();
		Collections.addAll(expected, v1,v2,v3,v4);
		
		Set<Vertex<Integer>> actual = GraphUtils.getLargestConnectedSubgraph(g);
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void testGetVerticesNotConnectedToSource()
	{
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		Vertex<Integer> v4 = new Vertex<Integer>(4);
		Vertex<Integer> v5 = new Vertex<Integer>(5);
		Vertex<Integer> v6 = new Vertex<Integer>(6);
		
		Vertex<Integer> source = v1;
		
		Edge<Integer,Integer> e12 = new Edge<Integer,Integer>(v1,v2,12);
		Edge<Integer,Integer> e23 = new Edge<Integer,Integer>(v2,v3,23);
		Edge<Integer,Integer> e14 = new Edge<Integer,Integer>(v1,v4,14);
		Edge<Integer,Integer> e42 = new Edge<Integer,Integer>(v4,v2,42);
		
		Graph<Integer,Integer> g = new AdjacencyList<Integer,Integer>(true);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		
		g.addEdge(e12);
		g.addEdge(e23);
		g.addEdge(e14);
		g.addEdge(e42);
		
		Set<Vertex<Integer>> expected = new HashSet<Vertex<Integer>>();
		Collections.addAll(expected,v5,v6);
		
		Set<Vertex<Integer>> actual = GraphUtils.getVerticesNotConnectedToSource(g, source);
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void testIsConnected()
	{
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		Vertex<Integer> v4 = new Vertex<Integer>(4);
		Vertex<Integer> v5 = new Vertex<Integer>(5);
		Vertex<Integer> v6 = new Vertex<Integer>(6);
				
		Edge<Integer,Integer> e12 = new Edge<Integer,Integer>(v1,v2,12);
		Edge<Integer,Integer> e23 = new Edge<Integer,Integer>(v2,v3,23);
		Edge<Integer,Integer> e14 = new Edge<Integer,Integer>(v1,v4,14);
		Edge<Integer,Integer> e42 = new Edge<Integer,Integer>(v4,v2,42);
		Edge<Integer,Integer> e56 = new Edge<Integer,Integer>(v5,v6,56);
		
		Graph<Integer,Integer> g = new AdjacencyList<Integer,Integer>(true);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		
		g.addEdge(e12);
		g.addEdge(e23);
		g.addEdge(e14);
		g.addEdge(e42);
		
		assertTrue(GraphUtils.isConnected(g));
		
		// make it unconnected
		g.addVertex(v5);
		g.addVertex(v6);
		
		assertFalse(GraphUtils.isConnected(g));
		
		// still unconnected
		g.addEdge(e56);
		
		assertFalse(GraphUtils.isConnected(g));
		
	}
	
	@Test
	public void testGetConnectedComponents()
	{
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		Vertex<Integer> v4 = new Vertex<Integer>(4);
		Vertex<Integer> v5 = new Vertex<Integer>(5);
		Vertex<Integer> v6 = new Vertex<Integer>(6);
				
		Edge<Integer,Integer> e12 = new Edge<Integer,Integer>(v1,v2,12);
		Edge<Integer,Integer> e23 = new Edge<Integer,Integer>(v2,v3,23);
		Edge<Integer,Integer> e14 = new Edge<Integer,Integer>(v1,v4,14);
		Edge<Integer,Integer> e42 = new Edge<Integer,Integer>(v4,v2,42);
		Edge<Integer,Integer> e56 = new Edge<Integer,Integer>(v5,v6,56);

		
		Graph<Integer,Integer> g = new AdjacencyList<Integer,Integer>(true);
		
		// empty graph should have itself as a connected component
		Set<Graph<Integer,Integer>> expected = new HashSet<Graph<Integer,Integer>>();
		expected.add(g);
		
		assertEquals(expected,GraphUtils.getConnectedComponents(g));
		
		
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		
		g.addEdge(e12);
		g.addEdge(e23);
		g.addEdge(e14);
		g.addEdge(e42);
		
		expected.clear();
		expected.add(g);
		// graph is completely connected
		assertEquals(expected,GraphUtils.getConnectedComponents(g));
		
		// make it unconnected
		g.addVertex(v5);
		g.addVertex(v6);
		
		// three connected components
		
		Graph<Integer,Integer> g1 = g.clone();
		g1.removeVertex(v5);
		g1.removeVertex(v6);
		
		Graph<Integer,Integer> g2 = new AdjacencyList<Integer,Integer>(true);
		g2.addVertex(v5);
		
		Graph<Integer,Integer> g3 = new AdjacencyList<Integer,Integer>(true);
		g3.addVertex(v6);
		
		expected.clear();
		expected.add(g1); expected.add(g2); expected.add(g3);
		
		assertEquals(expected,GraphUtils.getConnectedComponents(g));
		
		// still unconnected : but now we have two components
		g.addEdge(e56);
		
		g2 = new AdjacencyList<Integer,Integer>(true);
		g2.addVertex(v5);
		g2.addVertex(v6);
		g2.addEdge(e56);
		
		expected.clear();
		expected.add(g1); expected.add(g2);
		
		assertEquals(expected,GraphUtils.getConnectedComponents(g));
		
		
	}
	
	@Test(timeout=1000)
	public void testMakeDirectional()
	{
		Graph<String,Void> g = new AdjacencyList<String,Void>(true);
		
		Vertex<String> source1 = new Vertex<String>("router_1");
		Vertex<String> source2 = new Vertex<String>("router_2");
		List<Vertex<String>> nonSources = new ArrayList<Vertex<String>>();
		
		for(int i = 1; i <= 9; ++i)
			nonSources.add(new Vertex<String>("v" + i));
		
		g.addVertex(source1);
		g.addVertex(source2);
		for(Vertex<String> v : nonSources)
			g.addVertex(v);
		g.addEdge(new Edge<String,Void>(source1,source2));
		g.addEdge(new Edge<String,Void>(source1,nonSources.get(0)));
		g.addEdge(new Edge<String,Void>(source1,nonSources.get(2)));
		g.addEdge(new Edge<String,Void>(nonSources.get(0),nonSources.get(1)));
		g.addEdge(new Edge<String,Void>(nonSources.get(2),nonSources.get(3)));
		
		g.addEdge(new Edge<String,Void>(source1,nonSources.get(0)));
		g.addEdge(new Edge<String,Void>(source2,nonSources.get(4)));
		g.addEdge(new Edge<String,Void>(nonSources.get(4),nonSources.get(5)));
		g.addEdge(new Edge<String,Void>(nonSources.get(4),nonSources.get(6)));
		g.addEdge(new Edge<String,Void>(nonSources.get(4),nonSources.get(7)));
		g.addEdge(new Edge<String,Void>(nonSources.get(7),nonSources.get(8)));
		
		Graph<String,Void> exp = g.clone();
		
		Graph<String,Void> actual = 
			GraphUtils.makeDirectional(g.makeUndirected(),Arrays.asList(source1,source2));
		
		assertEquals(exp,actual);
	}

}
