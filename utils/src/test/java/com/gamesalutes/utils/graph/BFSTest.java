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
 * @version $Id: BFSTest.java 1274 2009-01-20 19:40:22Z jmontgomery $
 */
public class BFSTest
{
	Graph<Integer,Integer> graph;
	List<Vertex<Integer>> vertices;
	@Before
	public void setUp() throws Exception
	{
		int i;
		graph = new AdjacencyList<Integer,Integer>(true);
		vertices = new ArrayList<Vertex<Integer>>();
		
		for(i = 0;i < 5; ++i)
			vertices.add(new Vertex<Integer>(i));
		Vertex<Integer> v0,v1,v2,v3,v4;
		Edge<Integer,Integer> e01,e02,e12,e23,e24,e31,e33;
		
		//build the graph
		v0 = vertices.get(0);
		v1 = vertices.get(1);
		v2 = vertices.get(2);
		v3 = vertices.get(3);
		v4 = vertices.get(4);
		
		//i is currently 5, one past last vertex index
		e01 = new Edge<Integer,Integer>(v0,v1,i++);
		e02 = new Edge<Integer,Integer>(v0,v2,i++);
		e12 = new Edge<Integer,Integer>(v1,v2,i++);
		e23 = new Edge<Integer,Integer>(v2,v3,i++);
		e24 = new Edge<Integer,Integer>(v2,v4,i++);
		e31 = new Edge<Integer,Integer>(v3,v1,i++);
		e33 = new Edge<Integer,Integer>(v3,v3,i++);
		
		for(Vertex<Integer> v : vertices)
			graph.addVertex(v);
		graph.addEdge(e01);
		graph.addEdge(e02);
		graph.addEdge(e12);
		graph.addEdge(e23);
		graph.addEdge(e24);
		graph.addEdge(e31);
		// test adding self edge
		graph.addEdge(e33);
		
		
		
	
	}

	@After
	public void tearDown() throws Exception 
	{
		graph = null;
		vertices = null;
	}

	/**
	 * Tests {@link BFS#execute()}.
	 * 
	 */
	@Test(timeout = 5000)
	public void testExecute() 
	{
		GraphTraversalAlgorithm<Integer,Integer> traversal =
			new BFS<Integer,Integer>(graph,vertices.get(0));
		traversal.execute();
		
		//test expected path
		List<Vertex<Integer>> expected = 
			Arrays.<Vertex<Integer>>
			asList(vertices.get(0),vertices.get(2),vertices.get(4));
		
		List<Vertex<Integer>> trav = traversal.getTraversal();
		assertEquals("traversal path",expected,traversal.getPath(trav.get(trav.indexOf(vertices.get(4)))));
		
		expected = Arrays.<Vertex<Integer>>asList(vertices.get(0),vertices.get(2),vertices.get(3));
		
		assertEquals("traversal path",expected,traversal.getPath(trav.get(trav.indexOf(vertices.get(3)))));
		
	}

}
