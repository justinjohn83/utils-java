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

import java.util.List;

/**
 * Traversal interface on a tree.  
 * 
 * @author Justin Montgomery
 * @version $Id: TreeTraversal.java 1672 2009-09-02 19:17:16Z jmontgomery $
 *
 * @param <T> type of the tree node
 */
public interface TreeTraversal<T>
{
	/**
	 * Executes the traversal on the root {@link TreeNode}.
	 * 
	 * @throws TraversalException if error occurs during traversal
	 */
	TreeTraversal<T> execute();
	
	/**
	 * Returns the complete traversal on the graph.
	 * 
	 * @return a <code>List</code> containing the vertex traversal
	 */
	List<TreeNode<T>> getTraversal();
	
	/**
	 * Listener callback during {@link TreeTraversal#execute()}.  The 
	 * time that the callback is executed depends on the type of the 
	 * traversal.
	 * 
	 * @author Justin Montgomery
	 *
	 * @param <T> the type of the tree node
	 */
	public interface Listener<T>
	{
		/**
		 * Callback during {@link TreeTraversal#execute()}.  Returns whether the 
		 * traversal should continue.  If <code>false</code> is returned, then <code>node</code> is
		 * not considered to be part of the traversal.
		 * 
		 * @param node the current {@link TreeNode}
		 * @return <code>true</code> to continue the traversal and <code>false</code> otherwise
		 * @throws TraversalException if error occurs during traversal
		 */
		boolean onTraverse(TreeNode<T> node);
	}
}
