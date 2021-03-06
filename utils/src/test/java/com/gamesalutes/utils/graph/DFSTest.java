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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
/**
 * @author Justin  Montgomery
 * @version $Id: DFSTest.java 1274 2009-01-20 19:40:22Z jmontgomery $
 */
public class DFSTest
{
	private Graph<Integer,Integer> graph;
	private List<Vertex<Integer>> vertices;
	private List<Vertex<Integer>> expectedTraversal;
	private List<Vertex<Integer>> expectedPath;
	private Vertex<Integer> pathDest;
	private Vertex<Integer> source;
	@Before
	public void setUp() throws Exception
	{
		setUpGraph();
		setUpTraversal();
	}
	
	private void setUpGraph()
	{
		int i;
		graph = new AdjacencyList<Integer,Integer>(true);
		vertices = new ArrayList<Vertex<Integer>>();
		
		for(i = 0;i <= 5; ++i)
			vertices.add(new Vertex<Integer>(i));
		Vertex<Integer> v0,v1,v2,v3,v4,v5;
		Edge<Integer,Integer> e01,e02,e03,e14,e25,e22;
		
		//build the graph
		v0 = vertices.get(0);
		v1 = vertices.get(1);
		v2 = vertices.get(2);
		v3 = vertices.get(3);
		v4 = vertices.get(4);
		v5 = vertices.get(5);
		
		//i is currently 5, one past last vertex index
		e01 = new Edge<Integer,Integer>(v0,v1,i++);
		e02 = new Edge<Integer,Integer>(v0,v2,i++);
		e03 = new Edge<Integer,Integer>(v0,v3,i++);
		e14 = new Edge<Integer,Integer>(v1,v4,i++);
		e25 = new Edge<Integer,Integer>(v2,v4,i++);
		e22 = new Edge<Integer,Integer>(v2,v2,i++);
		
		for(Vertex<Integer> v : vertices)
			graph.addVertex(v);
		graph.addEdge(e01);
		graph.addEdge(e02);
		graph.addEdge(e03);
		graph.addEdge(e14);
		graph.addEdge(e25);
		// test adding self edge
		graph.addEdge(e22);
	}

	//set up expected traversal
	private void setUpTraversal()
	{
		source = vertices.get(0);
		pathDest = vertices.get(4);
		
		//get path with dest v4
		expectedPath = 
			Arrays.<Vertex<Integer>>asList(vertices.get(0),vertices.get(1),vertices.get(4));
		
	    //build whole traversal
		//TODO: assuming insertion ordering of vertices and edges
		expectedTraversal = 
			Arrays.<Vertex<Integer>>asList(vertices.get(0),vertices.get(1),vertices.get(4),
					vertices.get(2),vertices.get(4),vertices.get(3));
		
	}
	@After
	public void tearDown() throws Exception 
	{
		graph = null;
		vertices = null;
		source = pathDest = null;
		expectedPath = expectedTraversal = null;
	}

	/**
	 * Tests {@link DFS#execute()}.
	 * 
	 */
	@Test
	public void testExecute() 
	{
		GraphTraversalAlgorithm<Integer,Integer> traversal =
			new DFS<Integer,Integer>(graph,source);
		traversal.execute();
		
		
		List<Vertex<Integer>> actual = traversal.getTraversal();
		//test expected traversal
		assertEquals("Depth first traversal",expectedTraversal,actual);
		
		//test expected path
		assertEquals("Path to v4",expectedPath,traversal.getPath(
				actual.get(actual.indexOf(vertices.get(4)))));
	}

}
