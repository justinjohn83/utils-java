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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotSame;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id: GraphTest.java 1892 2010-02-04 21:29:58Z jmontgomery $
 */
public abstract class GraphTest 
{
	private Graph<Integer,Integer> dirGraph,undirGraph;
	private Vertex<Integer> source,dest;
	private Edge<Integer,Integer> edge,reverseEdge,selfEdge;
	
	
	protected abstract Graph<Integer,Integer> getDirectedGraph();
	protected abstract Graph<Integer,Integer> getUndirectedGraph();
	
	private interface GraphTaskExecutor
	{
		void execute(Graph<Integer,Integer> g);
	}
	
	@Before
	public void setUp() throws Exception 
	{
		dirGraph = getDirectedGraph();
		undirGraph = getUndirectedGraph();
		source = new Vertex<Integer>(0);
		dest = new Vertex<Integer>(1);
		edge = new Edge<Integer,Integer>(source,dest,2);
		selfEdge = new Edge<Integer,Integer>(source,source,100);
		reverseEdge = edge.reverse();
	}

	@After
	public void tearDown() throws Exception
	{
		dirGraph = undirGraph = null;
		source = dest = null;
		edge = reverseEdge = selfEdge = null;
	}
	
	@Test
	public void testAddVertexDirected()
	{
		assertTrue("Vertex added",dirGraph.addVertex(source));
		assertTrue("Vertex added",dirGraph.addVertex(dest));
		assertTrue("Vertex in graph",dirGraph.containsVertex(source));
		assertTrue("Vertex in graph",dirGraph.containsVertex(dest));
	}
	
	@Test
	public void testAddVertexUndirected()
	{
		assertTrue("Vertex added",undirGraph.addVertex(source));
		assertTrue("Vertex added",undirGraph.addVertex(dest));
		assertTrue("Vertex in graph",undirGraph.containsVertex(source));
		assertTrue("Vertex in graph",undirGraph.containsVertex(dest));
	}
	
	@Test
	public void testAddAll()
	{
		Graph<Integer,Integer> g1 = getDirectedGraph();
		Graph<Integer,Integer> g2 = getDirectedGraph();
		
		Vertex<Integer> v1 = new Vertex<Integer>(1);
		Vertex<Integer> v2 = new Vertex<Integer>(2);
		Vertex<Integer> v3 = new Vertex<Integer>(3);
		Vertex<Integer> v4 = new Vertex<Integer>(4);
		
		Edge<Integer,Integer> e12 = new Edge<Integer,Integer>(v1,v2,12);
		Edge<Integer,Integer> e34 = new Edge<Integer,Integer>(v3,v4,34);
		
		g1.addVertex(v1);
		g1.addVertex(v2);
		g1.addEdge(e12);
		
		g2.addVertex(v3);
		g2.addVertex(v4);
		g2.addEdge(e34);
		
		g1.addAll(g2);
		
		assertTrue("Vertex added",g1.containsVertex(v1));
		assertTrue("Vertex added",g1.containsVertex(v2));
		assertTrue("Vertex added",g1.containsVertex(v3));
		assertTrue("Vertex added",g1.containsVertex(v4));
		assertTrue("Edge added",g1.containsEdge(e12));
		assertTrue("Edge added",g1.containsEdge(e34));

	}
	
	@Test
	public void testAddEdgeDirected()
	{
		//add the vertices
		testAddVertexDirected();
		assertTrue("Edge added",dirGraph.addEdge(edge));
		assertTrue("Edge in graph",dirGraph.containsEdge(edge));
		assertFalse("Reverse edge not in graph",dirGraph.containsEdge(reverseEdge));
		assertEquals("Edge count correct",1,dirGraph.numEdges());
		assertEquals("Edge count correct",1,dirGraph.numEdges(source));
		assertEquals("Edge count correct",1,dirGraph.numEdges(dest));
		
	}
	
	@Test
	public void testAddSelfEdgeDirected()
	{
		// add the vertices
		testAddVertexDirected();
		assertTrue("edge added",dirGraph.addEdge(selfEdge));
		assertEquals("Edge count correct",1,dirGraph.numEdges());
		assertEquals("Edge count correct",1,dirGraph.numEdges(source));
	}
	
	
	@Test
	public void testFindEdgeUndirected()
	{
		undirGraph.addVertex(source);
		undirGraph.addVertex(dest);
		undirGraph.addEdge(edge);
		
		assertEquals("Edge found",edge,undirGraph.findEdge(source, dest));
		assertEquals("Reverse edge found",reverseEdge,undirGraph.findEdge(dest, source));
	}
	
	@Test
	public void testFindEdgeDirected()
	{
		dirGraph.addVertex(source);
		dirGraph.addVertex(dest);
		dirGraph.addEdge(edge);
		
		assertEquals("Edge found",edge,dirGraph.findEdge(source, dest));
		assertNull("Reverse edge not found",dirGraph.findEdge(dest, source));
	}
	@Test
	public void testAddEdgeUndirected()
	{
		//add the vertices
		testAddVertexUndirected();
		assertTrue("Edge added",undirGraph.addEdge(edge));
		assertTrue("Edge in graph",undirGraph.containsEdge(edge));
		assertTrue("Reverse edge in graph",undirGraph.containsEdge(reverseEdge));
		assertEquals("Edge count correct",2,undirGraph.numEdges());
		assertEquals("Edge count correct",2,undirGraph.numEdges(dest));
		assertEquals("Edge count correct",2,undirGraph.numEdges(dest));
	}
	
	@Test
	public void testAddSelfEdgeUndirected()
	{
		// add the vertices
		testAddVertexUndirected();
		assertTrue("edge added",undirGraph.addEdge(selfEdge));
		assertEquals("Edge count correct",1,undirGraph.numEdges());
		assertEquals("Edge count correct",1,undirGraph.numEdges(source));
	}



	@Test
	public void testEdgeIterator() 
	{
		//add some edges
		undirGraph.addVertex(source);
		undirGraph.addVertex(dest);
		undirGraph.addEdge(edge);
		undirGraph.addEdge(selfEdge);
		
		int count = 0;
		for(Iterator<Edge<Integer,Integer>> it = undirGraph.edgeIterator();it.hasNext();)
		{
			it.next();
			++count;
		}
		
		assertEquals(3,count);
	}

	@Test
	public void testEdgeIteratorVertexOfT()
	{
		//add some edges
		Vertex<Integer> third = new Vertex<Integer>(
				3);
		undirGraph.addVertex(source);
		undirGraph.addVertex(dest);
		undirGraph.addVertex(third);
		undirGraph.addEdge(edge);
		undirGraph.addEdge(selfEdge);
		
		int count = 0;
		for(Iterator<Edge<Integer,Integer>> it = 
			undirGraph.edgeIterator(source);it.hasNext();)
		{
			it.next();
			++count;
		}
		assertEquals(3,count);
		
		//make sure vertex with no edges doesn't return any
		Iterator<Edge<Integer,Integer>> it = 
			undirGraph.edgeIterator(third);
		assertFalse("Iterator is empty",it.hasNext());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEdgeIteratorVertexOfT_Fail()
	{
		undirGraph.edgeIterator(
				new Vertex<Integer>(Integer.MAX_VALUE));
	}

	@Test
	public void testIsDirected() 
	{
		assertTrue("Graph is directed",dirGraph.isDirected());
		assertFalse("Graph is undirected",undirGraph.isDirected());
	}

	@Test
	public void testRemoveEdgeDirected()
	{
		testRemoveEdgeDirected(edge);
	}
	
	@Test
	public void testRemoveSelfEdgeDirected()
	{
		testRemoveEdgeDirected(selfEdge);
	}
	
	private void testRemoveEdgeDirected(final Edge<Integer,Integer> e)
	{
		populateWithEdge(dirGraph,e);
		
		testRemoveEdgeDirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				assertTrue(g.removeEdge(e));
			}
		},e);
	}
	
	@Test
	public void testRemoveEdgeDirectedViaIterator()
	{
		testRemoveEdgeDirectedViaIterator(edge);
	}
	@Test
	public void testRemoveSelfEdgeDirectedViaIterator()
	{
		testRemoveEdgeDirectedViaIterator(selfEdge);
	}
	
	private void testRemoveEdgeDirectedViaIterator(final Edge<Integer,Integer> edge)
	{
		populateWithEdge(dirGraph,edge);
		
		testRemoveEdgeDirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				boolean removed = false;
				for(Iterator<Edge<Integer,Integer>> it = g.edgeIterator(); it.hasNext();)
				{
					Edge<Integer,Integer> e = it.next();
					if(e.equals(edge))
					{
						it.remove();
						removed = true;
						break;
					}
				}
				if(!removed)
					fail("Edge: " + edge + " not found");
			}
		},edge);
	}
	
	private void testRemoveEdgeDirected(GraphTaskExecutor callback,Edge<Integer,Integer> e)
	{	
		if(!e.isSelfLoop())
			assertTrue("Able to add reverse edge",dirGraph.addEdge(e.reverse()));
		else
			assertFalse("Cannot add reverse of self-edge",dirGraph.addEdge(e.reverse()));
		
		
		assertEquals("Edge count correct",e.isSelfLoop() ? 1 : 2,dirGraph.numEdges());
		callback.execute(dirGraph);
		for(Iterator<Edge<Integer,Integer>> it = dirGraph.edgeIterator(); it.hasNext();)
			if(it.next().equals(e))
				fail("Edge: " + e + " exists in edge iterator");
		assertFalse("Edge removed",dirGraph.containsEdge(e));
		if(!e.isSelfLoop())
			assertTrue("Reverse edge not removed",dirGraph.containsEdge(e.reverse()));
		else
			assertFalse("Reverse edge not in graph",dirGraph.containsEdge(e.reverse()));
		
		boolean found = false;
		for(Iterator<Edge<Integer,Integer>> it = dirGraph.edgeIterator(); it.hasNext();)
			if(it.next().equals(e.reverse()))
				found = true;
		assertEquals("Reverse edge not removed from iterator",e.isSelfLoop() ? false : true,found);
		assertEquals("Edge count correct",e.isSelfLoop() ? 0 : 1,dirGraph.numEdges());
	}
	

	
	@Test
	public void testMakeUndirected()
	{
		populateGraph(dirGraph);
		populateGraph(undirGraph);
		
		assertEquals(undirGraph,dirGraph.makeUndirected());
	}
	
	@Test
	public void testReverseGraph()
	{
		Graph<Integer,Integer> rGraph = getDirectedGraph();
		
		populateGraph(dirGraph);
		populateReverseGraph(rGraph);
		
		assertEquals(rGraph,dirGraph.reverse());
		
	}
	
	private void populateGraph(Graph<Integer,Integer> g)
	{
		g.addVertex(source);
		g.addVertex(dest);
		g.addEdge(edge);
		g.addEdge(selfEdge);
	}
	
	private void populateReverseGraph(Graph<Integer,Integer> g)
	{
		g.addVertex(source);
		g.addVertex(dest);
		g.addEdge(edge.reverse());
		g.addEdge(selfEdge.reverse());
	}
	
	@Test
	public void testCloneUndirected()
	{
		testClone(undirGraph);
	}
	@Test
	public void testCloneDirected()
	{
		testClone(dirGraph);
	}
	
	@SuppressWarnings("unchecked")
	private void testClone(Graph<Integer,Integer> orig)
	{
		populateGraph(orig);
		
		SortedSet<GraphObject> origObjects = new TreeSet<GraphObject>();
		SortedSet<GraphObject> cloneObjects = new TreeSet<GraphObject>();
		float wt = 0.0f;
		
		// use weights to sort deterministically
		for(Iterator<Vertex<Integer>> it = orig.vertexIterator(); it.hasNext();)
		{
			Vertex<Integer> v = it.next();
			v.setWeight(wt += 1.0f);
			origObjects.add(v);
		}
		// put edges after all vertices
		for(Iterator<Edge<Integer,Integer>> it = orig.edgeIterator(); it.hasNext();)
		{
			Edge<Integer,Integer> e = it.next();
			e.setWeight(wt += 1.0f);
			origObjects.add(e);
		}
		
		
		Graph<Integer,Integer> clone = 
			orig.clone();
		
		for(Iterator<Vertex<Integer>> it = clone.vertexIterator(); it.hasNext();)
			cloneObjects.add(it.next());
		for(Iterator<Edge<Integer,Integer>> it = clone.edgeIterator();it.hasNext();)
			cloneObjects.add(it.next());
		
		// make sure graphs logically equal
		assertEquals(orig,clone);
		
		
		// tests comparable interface
		assertEquals(origObjects,cloneObjects);
		
		
		Map<Vertex<Integer>,Vertex<Integer>> cloneVertexMap = 
			new IdentityHashMap<Vertex<Integer>,Vertex<Integer>>();
		
		// make sure cloned objects are not the same objects as originals
		for(Iterator<GraphObject> itOrig = origObjects.iterator(),
			itClone = cloneObjects.iterator(); itOrig.hasNext();)
		{
			GraphObject o = itOrig.next();
			GraphObject c = itClone.next();
			assertNotSame(o,c);
			if(c instanceof Vertex)
				cloneVertexMap.put((Vertex<Integer>)c,(Vertex<Integer>)
						c);
			// if object is also an edge, assert that its vertices
			// are the same as those in vertex array
			else if(c instanceof Edge)
			{
				Edge<Integer,Integer> e = (Edge<Integer,Integer>)c;
				Vertex<Integer> from = e.getFrom();
				Vertex<Integer> to = e.getTo();
				
				assertTrue(cloneVertexMap.containsKey(from));
				assertTrue(cloneVertexMap.containsKey(to));
				
			}
		}
			
	}
	
	
	private void testRemoveEdgeUndirected(GraphTaskExecutor callback,Edge<Integer,Integer> e)
	{
		callback.execute(undirGraph);
		assertFalse("Edge removed",undirGraph.containsEdge(e));
		// also check iterators
		for(Iterator<Edge<Integer,Integer>> it = undirGraph.edgeIterator(); it.hasNext();)
			if(it.next().equals(e))
				fail("Edge: " + e + " exists in edge iterator");
		assertFalse("Reverse edge removed",undirGraph.containsEdge(e.reverse()));
		for(Iterator<Edge<Integer,Integer>> it = undirGraph.edgeIterator(); it.hasNext();)
			if(it.next().equals(e.reverse()))
				fail("Edge: " + e.reverse() + " exists in edge iterator");
		assertEquals("Edge count correct",0,undirGraph.numEdges());
	}
	
	@Test
	public void testRemoveEdgeUndirected()
	{
		testRemoveEdgeUndirected(edge);
	}
	
	@Test
	public void testRemoveSelfEdgeUndirected()
	{
		testRemoveEdgeUndirected(selfEdge);
	}
	
	private void populateWithEdge(Graph<Integer,Integer> g,Edge<Integer,Integer> e)
	{
		g.addVertex(e.getFrom());
		g.addVertex(e.getTo());
		g.addEdge(e);
	}
	private void testRemoveEdgeUndirected(final Edge<Integer,Integer> e)
	{
		populateWithEdge(undirGraph,e);
		
		testRemoveEdgeUndirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				assertTrue(g.removeEdge(e));
			}
		},e);
	}
	

	@Test
	public void testRemoveEdgeUndirectedViaIterator()
	{
		testRemoveEdgeUndirectedViaIterator(edge);
	}
	
	@Test
	public void testRemoveSelfEdgeUndirectedViaIterator()
	{
		testRemoveEdgeUndirectedViaIterator(selfEdge);
	}
	
	private void testRemoveEdgeUndirectedViaIterator(final Edge<Integer,Integer> edge)
	{
		populateWithEdge(undirGraph,edge);
		
		testRemoveEdgeUndirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				boolean removed = false;
				for(Iterator<Edge<Integer,Integer>> it = g.edgeIterator(); it.hasNext();)
				{
					Edge<Integer,Integer> e = it.next();
					if(e.equals(edge))
					{
						it.remove();
						removed = true;
						break;
					}
				}
				if(!removed)
					fail("Edge: " + edge + " not found");
			}
		},edge);
	}

	
	private void testRemoveVertexDirected(GraphTaskExecutor callback)
	{
		testAddEdgeDirected();
		callback.execute(dirGraph);
		assertFalse(dirGraph.containsVertex(source));
		assertTrue(dirGraph.containsVertex(dest));
		assertFalse(dirGraph.containsEdge(edge));
	}
	
	@Test
	public void testRemoveVertexDirected() 
	{
		testRemoveVertexDirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				assertTrue(g.removeVertex(source));
			}
		});
	}
	

	
	@Test
	public void testRemoveVertexDirectedViaIterator()
	{
		testRemoveVertexDirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				boolean removed = false;
				for(Iterator<Vertex<Integer>> it = g.vertexIterator(); it.hasNext();)
				{
					Vertex<Integer> v = it.next();
					if(v.equals(source))
					{
						it.remove();
						removed = true;
						break;
					}
				}
				if(!removed)
					fail("Vertex: " + source + " not found");
			}
		});
	}
	
	
	private void testRemoveVertexUndirected(GraphTaskExecutor callback)
	{
		testAddEdgeUndirected();
		callback.execute(undirGraph);
		assertFalse(undirGraph.containsVertex(source));
		assertTrue(undirGraph.containsVertex(dest));
		assertFalse(undirGraph.containsEdge(edge));
		assertFalse(undirGraph.containsEdge(reverseEdge));
	}
	
	@Test
	public void testRemoveVertexUndirected() 
	{
		testRemoveVertexUndirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				assertTrue(g.removeVertex(source));
			}
		});
	}
	

	
	@Test
	public void testRemoveVertexUndirectedViaIterator()
	{
		testRemoveVertexUndirected(new GraphTaskExecutor()
		{
			public void execute(Graph<Integer,Integer> g)
			{
				boolean removed = false;
				for(Iterator<Vertex<Integer>> it = g.vertexIterator(); it.hasNext();)
				{
					Vertex<Integer> v = it.next();
					if(v.equals(source))
					{
						it.remove();
						removed = true;
						break;
					}
				}
				if(!removed)
					fail("Vertex: " + source + " not found");
			}
		});
	}

	@Test
	public void testVertexIterator()
	{
		testAddEdgeDirected();
		Iterator<Vertex<Integer>> it = dirGraph.vertexIterator();
		int count = 0;
		while(it.hasNext())
		{
			it.next();
			++count;
		}
		assertEquals(2,count);
	}

	@Test
	public void testContainsEdge() 
	{
		testAddEdgeDirected();
		testAddEdgeUndirected();
		
		assertTrue(dirGraph.containsEdge(edge));
		assertTrue(undirGraph.containsEdge(edge));
		assertFalse(dirGraph.containsEdge(reverseEdge));
		assertTrue(undirGraph.containsEdge(reverseEdge));
	}

	@Test
	public void testHasVertex()
	{
		testAddVertexDirected();
		
		assertTrue(dirGraph.containsVertex(source));
		assertTrue(dirGraph.containsVertex(dest));
		assertFalse(dirGraph.containsVertex(new Vertex<Integer>(
				Integer.MAX_VALUE)));
	}

	@Test
	public void testNumVerts()
	{
		testAddVertexDirected();
		//2 vertices
		assertEquals(2,dirGraph.numVerts());
	}

	@Test
	public void testClear() 
	{
		testAddEdgeUndirected();
		
		undirGraph.clear();
		assertFalse(undirGraph.vertexIterator().hasNext());
		assertFalse(undirGraph.edgeIterator().hasNext());
		assertEquals(0,undirGraph.numVerts());
		assertEquals(0,undirGraph.numEdges());
		
		testAddEdgeDirected();
		
		dirGraph.clear();
		
		assertFalse(dirGraph.vertexIterator().hasNext());
		assertFalse(dirGraph.edgeIterator().hasNext());
		assertEquals(0,dirGraph.numVerts());
		assertEquals(0,dirGraph.numEdges());
		
	}

}
