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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gamesalutes.utils.graph.TreeTraversal.Listener;

/**
 * @author Justin Montgomery
 * @version $Id: TreeNodeTest.java 1636 2009-07-18 00:40:01Z jmontgomery $
 */
public class TreeNodeTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() 
	{
		assertEquals(new TreeNode<Integer>(1),new TreeNode<Integer>(1));
		assertFalse(new TreeNode<Integer>(1).equals(new TreeNode<Integer>(2)));
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#TreeNode(java.lang.Object)}.
	 */
	@Test
	public void testTreeNode() 
	{
		TreeNode<Integer> node = new TreeNode<Integer>(0);
		assertEquals(Integer.valueOf(0),node.getData());
		assertNull(node.getParent());
		
	}
	

	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#addChild(com.gamesalutes.utils.graph.TreeNode)}.
	 */
	@Test
	public void testAddChild() 
	{
		TreeNode<Integer> parent1 = new TreeNode<Integer>(0);
		TreeNode<Integer> parent2 = new TreeNode<Integer>(1);
		TreeNode<Integer> child = new TreeNode<Integer>(3);
		parent1.addChild(child);
		assertTrue("Parent has child",parent1.containsChild(child));
		assertEquals("Child has parent",parent1,child.getParent());
		
		// test reasigning children
		parent2.addChild(child);
		assertTrue("Parent2 has child",parent2.containsChild(child));
		assertEquals("Child has parent2",parent2,child.getParent());
		assertFalse("Parent1 has child",parent1.containsChild(child));
		
		
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#removeChild(com.gamesalutes.utils.graph.TreeNode)}.
	 */
	@Test
	public void testRemoveChild()
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		
		parent.addChild(child1);
		parent.addChild(child2);
		
		assertTrue(parent.removeChild(child1));
		assertNull(child1.getParent());
		assertEquals(1,parent.getChildCount());
		
		assertTrue(parent.removeChild(child2));
		assertNull(child2.getParent());
		assertEquals(0,parent.getChildCount());
		assertTrue(parent.isLeaf());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#clearChildren()}.
	 */
	@Test
	public void testClearChildren() 
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		
		parent.addChild(child1);
		parent.addChild(child2);
		
		parent.clearChildren();
		
		assertEquals(0,parent.getChildCount());
		assertTrue(parent.isLeaf());
		assertNull(child1.getParent());
		assertNull(child2.getParent());
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#containsChild(com.gamesalutes.utils.graph.TreeNode)}.
	 */
	@Test
	public void testContainsChild()
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child = new TreeNode<Integer>(2);
		TreeNode<Integer> orphan = new TreeNode<Integer>(3);
		
		parent.addChild(child);
		
		assertTrue(parent.containsChild(child));
		assertFalse(parent.containsChild(orphan));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#getParent()}.
	 */
	@Test
	public void testGetParent() 
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child = new TreeNode<Integer>(2);
		TreeNode<Integer> orphan = new TreeNode<Integer>(3);
		
		parent.addChild(child);
		
		assertEquals(parent,child.getParent());
		assertNull(orphan.getParent());
	}
	
	/**
	 * Test method for {@link com.gamesalutes.utils.graph.TreeNode#iterator()}.
	 */
	@Test
	public void testIterator() 
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		TreeNode<Integer> child3 = new TreeNode<Integer>(4);
		
		
		List<TreeNode<Integer>> expected = new ArrayList<TreeNode<Integer>>();
		List<TreeNode<Integer>> actual = new ArrayList<TreeNode<Integer>>();
		
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addChild(child3);
		
		Collections.addAll(expected, child1,child2,child3);
		for(Iterator<TreeNode<Integer>> it = parent.iterator(); it.hasNext();)
			actual.add(it.next());
		
		assertEquals(expected.size(),actual.size());
		assertEquals(expected,actual);
	}
	
	
	/**
	 * Test method for the remove operation on a returned
	 *  {@link com.gamesalutes.utils.graph.TreeNode#iterator()}.
	 */
	@Test
	public void testIteratorRemove()
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		TreeNode<Integer> child3 = new TreeNode<Integer>(4);
		
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addChild(child3);
		
		// remove every child
		int size = parent.getChildCount();
		for(Iterator<TreeNode<Integer>> it = parent.iterator(); it.hasNext();)
		{
			TreeNode<Integer> e = it.next();
			it.remove();
			assertFalse(parent.containsChild(e));
			assertEquals(--size,parent.getChildCount());
		}
		
		// assert empty iteration
		assertFalse(parent.iterator().hasNext());
		assertTrue(parent.isLeaf());
	}
	
	@Test
	public void testTreeNodeCopyCtor()
	{
		TreeNode<Integer> node = new TreeNode<Integer>(1);
		node.setLevel(100);
		node.addChild(new TreeNode<Integer>(2));
		node.addChild(new TreeNode<Integer>(3));
		
		TreeNode<Integer> copy = new TreeNode<Integer>(node);
		assertEquals(node,copy);
		assertEquals(node.getChildCount(),copy.getChildCount());
		assertEquals(node.getLevel(),copy.getLevel());
		List<TreeNode<Integer>> expected = new ArrayList<TreeNode<Integer>>();
		List<TreeNode<Integer>> actual = new ArrayList<TreeNode<Integer>>();
		
		// test that child arrays are equal
		for(Iterator<TreeNode<Integer>> it = node.iterator();it.hasNext();)
			expected.add(it.next());
		for(Iterator<TreeNode<Integer>> it = copy.iterator(); it.hasNext();)
			actual.add(it.next());
		assertEquals(expected,actual);
	}
	
	@Test
	public void testTreeClone()
	{
		int count = 0;
		// prepare tree for testing
		TreeNode<Integer> root = new TreeNode<Integer>(count++);
		TreeNode<Integer> left = new TreeNode<Integer>(count++);
		TreeNode<Integer> right = new TreeNode<Integer>(count++);
		
		root.addChild(left);
		root.addChild(right);
		
		left.addChild(new TreeNode<Integer>(count++));
		left.addChild(new TreeNode<Integer>(count++));
		left.addChild(new TreeNode<Integer>(count++));
		right.addChild(new TreeNode<Integer>(count++));
		
		TreeNode<Integer> rootClone = root.clone();
		
		final List<TreeNode<Integer>> origTraversal = 
			new ArrayList<TreeNode<Integer>>();
		final List<TreeNode<Integer>> cloneTraversal = 
			new ArrayList<TreeNode<Integer>>();
		
		
		final Map<TreeNode<Integer>,TreeNode<Integer>> cloneMap = 
			new IdentityHashMap<TreeNode<Integer>,TreeNode<Integer>>();
		
		new LevelOrderTraversal<Integer>(root,
				new Listener<Integer>()
				{

					public boolean onTraverse(TreeNode<Integer> node) 
					{
						origTraversal.add(node);
						return true;
					}
			
				}
		).execute();
		
		new LevelOrderTraversal<Integer>(rootClone,
				new Listener<Integer>()
				{

					public boolean onTraverse(TreeNode<Integer> node)
					{
						cloneTraversal.add(node);
						cloneMap.put(node, node);
						return true;
					}
			
				}
		).execute();
		
		// assert equal
		assertEquals(origTraversal,cloneTraversal);
		
		// assert not same
		for(Iterator<TreeNode<Integer>> origIt = origTraversal.iterator(),
			cloneIt = cloneTraversal.iterator(); origIt.hasNext();)
		{
			TreeNode<Integer> orig = origIt.next();
			TreeNode<Integer> clone = cloneIt.next();
			
			assertNotSame(orig,clone);
			// assert children of clone are in the tree
			for(Iterator<TreeNode<Integer>> childIt = clone.iterator();childIt.hasNext();)
			{
				assertTrue(cloneMap.containsKey(childIt.next()));
			}
		}
		
	}
	
	@Test
	public void testGetChildAt()
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		TreeNode<Integer> child3 = new TreeNode<Integer>(4);
		
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addChild(child3);
		
		assertEquals(child1,parent.getChildAt(0));
		assertEquals(child2,parent.getChildAt(1));
		assertEquals(child3,parent.getChildAt(2));
		
	}
	
	@Test
	public void testIndexOfChild()
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		TreeNode<Integer> child3 = new TreeNode<Integer>(4);
		
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addChild(child3);
		
		assertEquals(0,parent.indexOfChild(child1));
		assertEquals(1,parent.indexOfChild(child2));
		assertEquals(2,parent.indexOfChild(child3));
	}
	@Test
	public void testAddChildAt()
	{		
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		TreeNode<Integer> child3 = new TreeNode<Integer>(4);
		
		parent.addChild(child1);
		parent.addChild(child2);
		
		assertTrue(parent.addChildAt(child3, 0));
		assertTrue(parent.containsChild(child3));
		assertEquals(0,parent.indexOfChild(child3));
		assertTrue(parent.containsChild(child1));
		assertTrue(parent.containsChild(child2));
		assertEquals(3,parent.getChildCount());
		
	}
	
	@Test
	public void testRemoveChildAt()
	{
		TreeNode<Integer> parent = new TreeNode<Integer>(1);
		TreeNode<Integer> child1 = new TreeNode<Integer>(2);
		TreeNode<Integer> child2 = new TreeNode<Integer>(3);
		TreeNode<Integer> child3 = new TreeNode<Integer>(4);
		
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addChild(child3);
		
		assertTrue(parent.removeChildAt(1));
		
		assertFalse(parent.containsChild(child2));
		assertTrue(parent.containsChild(child1));
		assertTrue(parent.containsChild(child3));
		assertEquals(2,parent.getChildCount());
	}
	

}
