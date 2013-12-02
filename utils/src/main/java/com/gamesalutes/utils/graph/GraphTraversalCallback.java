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

/**
 * Callback during the execution of a {@link GraphTraversalAlgorithm}.
 * 
 * @author Justin Montgomery
 * @version $Id: GraphTraversalCallback.java 1752 2009-11-03 22:12:20Z jmontgomery $
 */
public interface GraphTraversalCallback<V,E>
{
	/**
	 * Called before the traversal begins.
	 * 
	 * @param alg
	 */
	void onBegin(GraphTraversalAlgorithm<V,E> alg);
	
	/**
	 * Callback during {@link GraphTraversalAlgorithm#execute()}.  Returns whether the 
	 * traversal should continue.  If <code>false</code> is returned, then <code>e</code> is
	 * not considered to be part of the traversal.
	 * 
	 * @param e the current {@link GraphTraversalElement}
	 * @return <code>true</code> to continue the traversal and <code>false</code> otherwise
	 */
	boolean onTraverse(GraphTraversalElement<V,E> e);
	
	/**
	 * Called after the traversal completes.
	 */
	void onFinish(GraphTraversalAlgorithm<V,E> alg);
}
