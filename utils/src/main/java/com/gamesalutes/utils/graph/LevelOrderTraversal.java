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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Performs a level-order traversal on the tree.
 * 
 * @author Justin Montgomery
 * @version $Id: LevelOrderTraversal.java 1672 2009-09-02 19:17:16Z jmontgomery $
 */
public final class LevelOrderTraversal<T> extends AbstractTreeTraversal<T> 
{

	
	public LevelOrderTraversal(TreeNode<T> root)
	{
		super(root);
	}
	public LevelOrderTraversal(TreeNode<T> root,Listener<T> listener)
	{
		super(root,listener);
	}
	

	protected void doExecute()
	{
		Queue<TreeNode<T>> queue = new LinkedList<TreeNode<T>>();
		root.setLevel(0);
		queue.add(root);
		
		while(!queue.isEmpty())
		{
			TreeNode<T> node = queue.poll();
			if(listener.onTraverse(node))
			{
				// examine children
				for(Iterator<TreeNode<T>> it = node.iterator(); it.hasNext();)
				{
					TreeNode<T> child = it.next();
					child.setLevel(node.getLevel() + 1);
					queue.offer(child);
				}
			}
		} // end while
	}

}
