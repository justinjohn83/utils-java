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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of {@link TreeTraversal}.
 * 
 * @author Justin Montgomery
 * @version $Id: AbstractTreeTraversal.java 1672 2009-09-02 19:17:16Z jmontgomery $
 *
 * @param <T> the type of the traversal
 */
public abstract class AbstractTreeTraversal<T> implements TreeTraversal<T>
{
	protected final TreeNode<T> root;
	protected final Listener<T> listener;
	private List<TreeNode<T>> traversal;
	
	
	
	/**
	 * Constructor.
	 * 
	 * <code>DefaultTreeListener</code> will be used as the tree traversal listener.
	 * 
	 * 
	 * @param root the root node in the traversal
	 */
	
	protected AbstractTreeTraversal(TreeNode<T> root)
	{
		this(root,null);
	}
	/**
	 * Constructor.
	 * 
	 * @param root the root node in the traversal
	 * @param listener a {@link TreeTraversal.Listener TreeTraversal.Listener} 
	 *        callback during a traversal operation
	 */
	protected AbstractTreeTraversal (TreeNode<T> root,Listener<T> listener)
	{
		if(root == null)
			throw new NullPointerException("root");
		
		if(listener == null)
			listener = new DefaultTreeListener<T>();
		
		this.listener = new TraversalAddingListener(listener);
		this.root = root;
		
	}
	
	public final TreeTraversal<T> execute()
	{
		traversal = new ArrayList<TreeNode<T>>();
		doExecute();
		return this;
	}
	
	protected abstract void doExecute();
	
	public List<TreeNode<T>> getTraversal()
	{
		return traversal;
	}
	
	private class TraversalAddingListener implements Listener<T>
	{

		private final Listener<T> delegate;
		
		public TraversalAddingListener(Listener<T> delegate)
		{
			if(delegate == null)
				throw new NullPointerException("delegate");
			this.delegate = delegate;
		}
		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.graph.TreeTraversal.Listener#onTraverse(com.gamesalutes.utils.graph.TreeNode)
		 */
		public boolean onTraverse(TreeNode<T> node)
		{
			if(delegate.onTraverse(node))
			{
				traversal.add(node);
				return true;
			}
			return false;
		}
		
	}
}
