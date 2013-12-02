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

import com.gamesalutes.utils.graph.TreeTraversal.Listener;
import com.gamesalutes.utils.AbstractIterator;

/**
 * A node in a tree ADT.  A tree node can store any type as its data.
 * 
 * @author Justin Montgomery
 * @version $Id: TreeNode.java 1852 2010-01-14 23:43:52Z jmontgomery $
 *
 * @param <T> the type of the data stored in the tree
 */
public final class TreeNode<T> extends GraphObject<T>
{
	private ArrayList<TreeNode<T>> children;
	private TreeNode<T> parent;
	private int level;
	
	private static final long serialVersionUID = 1L;
	
	
	public TreeNode()
	{
		this((T)null);
	}
	public TreeNode(T data)
	{
		super(data);
		this.children = new ArrayList<TreeNode<T>>();
	}
	
	/**
	 * Copy constructor.  
	 * 
	 * @param node the <code>TreeNode</code> to copy
	 */
	public TreeNode(TreeNode<T> node)
	{
		super(node.getData());
		this.children = new ArrayList<TreeNode<T>>(node.children);
		this.parent = node.parent;
		this.level = node.level;
	}
	
	/**
	 * returns the path of this node from the root node in the tree.
	 * 
	 * @return the path from the root to this node
	 */
	public List<TreeNode<T>> getPathFromRoot()
	{
		LinkedList<TreeNode<T>> stack = new LinkedList<TreeNode<T>>();
		stack.add(this);
		TreeNode<T> node = this;
		while((node = node.getParent()) != null)
			stack.addFirst(node);
		return stack;
	}
	
	
	private TreeNode<T> clone0(TreeNode<T> node)
	{
		// FIXME: have to use TreeNode constructor here but want to call GraphObject.clone()
		// so must make this class final until this can be changed
		TreeNode<T> copy = new TreeNode<T>(node.getData());
		// don't actually copy the child nodes over yet, just reserve the size
		copy.children.ensureCapacity(node.children.size());
		copy.parent = null;
		copy.level = node.level;
		
		return copy;
		

	}
	/**
	 * Clones this node and all its children.  This operation should only be called on the 
	 * root node.
	 * 
	 * @return a copy of the tree rooted at this <code>TreeNode</code>
	 */
	@Override
	public TreeNode<T> clone()
	{
		final Map<TreeNode<T>,TreeNode<T>> oldToNew = 
			new IdentityHashMap<TreeNode<T>,TreeNode<T>>();
		
		// first traversal pass copies the nodes
		new PostOrderTraversal<T>(
				this,new Listener<T>()
				{

					public boolean onTraverse(TreeNode<T> node)
					{
						if(!oldToNew.containsKey(node))
							oldToNew.put(node,clone0(node));
						return true;
					}
					
				}
		).execute();
		
		// now fix-up the children,parent,level
		new PostOrderTraversal<T>(
				this,new Listener<T>()
				{
					public boolean onTraverse(TreeNode<T> node)
					{
						TreeNode<T> copy = oldToNew.get(node);
						if(copy == null)
							throw new AssertionError("no copy of node=" + node);
						
						for(int i = 0; i < node.children.size(); ++i)
						{
							TreeNode<T> child = node.children.get(i);
							TreeNode<T> childCopy = oldToNew.get(child);
							if(childCopy == null)
								throw new AssertionError("no copy of node=" + child);
							copy.addChildAt(childCopy, i);
						}
						
						return true;
						
					}
					
				}
		).execute();
		
		TreeNode<T> copy = oldToNew.get(this);
		if(copy == null) throw new AssertionError();
		
		return copy;
	}
	
	
	/**
	 * Attaches the <code>child</code> at the end of the child list
	 * if this <code>TreeNode</code> does not already contain that child.
	 * 
	 * 
	 * @param child the child <code>TreeNode</code>
	 * @return <code>true</code> if attached and <code>false</code> otherwise
	 * @throws NullPointerException if <code>child</code> is <code>null</code>
	 */
	public boolean addChild(TreeNode<T> child)
	{
		return addChildAt(child,getChildCount());
	}
	
	/**
	 * Removes the specified <code>child</code> from this <code>TreeNode</code>'s child list.
	 * 
	 * @param child the <code>TreeNode</code> child to remove
	 * @return <code>true</code> if removed and <code>false</code> otherwise
	 * @throws NullPointerException
	 */
	public boolean removeChild(TreeNode<T> child)
	{
		if(child == null) throw new NullPointerException("child");
		return removeChildAt(indexOfChild(child));
	}
	
	/**
	 * Removes this node from its parent if it exists.
	 * 
	 * @return <code>true</code> if removed and <code>false</code> otherwise
	 */
	public boolean removeFromParent()
	{
		if(this.parent != null)
			return this.parent.removeChild(this);
		return false;
	}
	
	/**
	 * Removes all children of this <code>TreeNode</code>.
	 * 
	 */
	public void clearChildren()
	{
		for(TreeNode<T> node : children)
			node.parent = null;
		children.clear();
	}
	
	/**
	 * Returns the <code>TreeNode</code> child at <code>index</code>.
	 * 
	 * @param index the index of the child
	 * @return the child
	 * @throws IllegalArgumentException if index is not a child index
	 */
	public TreeNode<T> getChildAt(int index)
	{
		if(index < 0 || index >= getChildCount())
			throw new IllegalArgumentException("index=" + index);
		return children.get(index);
		
	}
	
	/**
	 * Attaches the <code>child</code> at the specified <code>index</code>
	 * if this <code>TreeNode</code> does not already contain that child.
	 * 
	 * 
	 * @param child the child <code>TreeNode</code>
	 * @param index the index to attach it at
	 * @return <code>true</code> if attached and <code>false</code> otherwise
	 * @throws NullPointerException if <code>child</code> is <code>null</code>
	 * @throws IllegalArgumentException if <code>index &lt 0 or index &gt getChildCount() </code>
	 */
	public boolean addChildAt(TreeNode<T> child,int index)
	{
		if(child == null)
			throw new NullPointerException("child");
		if(index < 0 || index > getChildCount())
			throw new IllegalArgumentException("index=" + index);
		if(!containsChild(child))
			children.add(index,child);
		else
			return false;
		
		// fix up parent
		TreeNode<T> oldParent = child.parent;
		// remove the old parent if it exists
		if(oldParent != null && oldParent != this)
			oldParent.removeChild(child);
		child.parent = this;
		// child one level below this one
		// don't do this since would have to do it recursively
		// and this best done by traversals
		//child.level = getLevel() + 1;
		
		return true;
	}
	
	/**
	 * Removes the child at the specified <code>index</code>.
	 * 
	 * @param index the index of the child
	 * @return <code>true</code> if removed and <code>false</code> otherwise
	 * @throws IllegalArgumentException if <code>index</code> is invalid
	 */
	public boolean removeChildAt(int index)
	{
		if(index < 0 || index >= getChildCount())
			throw new IllegalArgumentException("index=" + index);
		TreeNode<T> child = getChildAt(index);
		child.parent = null;
		//child.level = 0;
		return children.remove(index) != null;
	}
	
	/**
	 * Returns the index of <code>child</code> in this <code>TreeNode</code>'s child list
	 * or <code>-1</code> if it does not exist.
	 * 
	 * @param child the <code>TreeNode</code> child
	 * @return the index in this node's child list or <code>-1</code> if not found
	 * @throws NullPointerException if <code>child</code> is <code>null</code>
	 */
	public int indexOfChild(TreeNode<T> child)
	{
		if(child == null)
			throw new NullPointerException("child");
		return children.indexOf(child);
	}
	
	/**
	 * Returns an iterator over the children of this <code>TreeNode</code>.
	 * 
	 * @return child list iterator
	 */
	public Iterator<TreeNode<T>> iterator()
	{
		// The AbstractGraph.GraphObjectIterator does what we want,
		// we simply need to implement the remove method
		return new AbstractIterator<TreeNode<T>>(children)
		{
			protected void doRemove(TreeNode<T> n)
			{
				removeChild(n);
			}
			
		};
	}
	
	/**
	 * Returns whether <code>child</code> is a child of this <code>TreeNode</code>.
	 * 
	 * @param child the child
	 * @return <code>true</code> if a child and <code>false</code> otherwise
	 */
	public boolean containsChild(TreeNode<T> child)
	{
		return children.contains(child);
	}
	
	/**
	 * Returns the parent <code>TreeNode</code> or <code>null</code> if this node is a root.
	 * 
	 * @return the parent or <code>null</code> if it does not exist
	 */
	public TreeNode<T> getParent()
	{
		return parent;
	}
	
	/**
	 * Returns whether this node contains no children and is therefore a leaf node.
	 * 
	 * @return <code>true</code> if a leaf node and <code>false</code> otherwise
	 */
	public boolean isLeaf() { return children.isEmpty(); }
	
	/**
	 * Returns whether this node has no parent and is therefore a root.
	 * 
	 * @return <code>true</code> if a root node and <code>false</code> otherwise
	 */
	public boolean isRoot() { return parent == null; }
	
	/**
	 * Returns the number of children of this node.
	 * 
	 * @return the number of children
	 */
	public int getChildCount() { return children.size(); }
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof TreeNode)) return false;
		return super.equals(o);
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder(128);
		str.append(super.toString());
		str.append(";parent=").append(parent != null ? parent.getData() : null
				).append(";children=").append(children);
		return str.toString();
	}
	
	/**
	 * Sets the level of this vertex during a traversal.
	 * <i>Used by some implementations of {@link TreeTraversal} </i>.
	 * 
	 * @param level the level to set
	 */
	public void setLevel(int level) { this.level = level; }
	
	/**
	 * Returns the level of this vertex during a traversal.
	 * <i>Used by some implementations of {@link TreeTraversal} </i>.
	 * 
	 * @return the level of this vertex
	 */
	public int getLevel() { return level; }
}
