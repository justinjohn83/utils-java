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

import java.util.Iterator;


/**
 * A post order traversal on a {@link TreeNode}. Return value of 
 * {@link TreeTraversal.Listener#onTraverse(TreeNode)} is ignored since a <code>TreeNode</code> is only
 * examined after all its children have been examined recursively.
 * 
 * @author Justin Montgomery
 * @version $Id: PostOrderTraversal.java 1672 2009-09-02 19:17:16Z jmontgomery $
 *
 * @param <T> type of the tree node
 */
public final class PostOrderTraversal<T> extends AbstractTreeTraversal<T>
{
	
	public PostOrderTraversal(TreeNode<T> root)
	{
		super(root);
	}
	public PostOrderTraversal(TreeNode<T> root,Listener<T> listener)
	{
		super(root,listener);
	}

	protected void doExecute()
	{
		root.setLevel(0);
		execute(root);
		
	}
	
	private void execute(TreeNode<T> node) 
	{
		for(Iterator<TreeNode<T>> i = 
				node.iterator(); i.hasNext();)
		{
			TreeNode<T> child = i.next();
			child.setLevel(node.getLevel() + 1);
			execute(child);
		}
		
		listener.onTraverse(node);
	}

}
