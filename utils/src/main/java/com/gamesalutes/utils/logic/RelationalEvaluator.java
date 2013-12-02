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
package com.gamesalutes.utils.logic;

/**
 * Callback used during evaluation of a {@link LogicStatement} on a tested object.
 * 
 * @author Justin Montgomery
 * @version $Id: RelationalEvaluator.java 1698 2009-09-18 15:26:41Z jmontgomery $
 *
 */
public interface RelationalEvaluator<S,T> 
{
	/**
	 * S &lt T.
	 * 
	 * @param lhs left operand
	 * @param rhs right operand
	 * @return <code>true</code> if less than and <code>false</code> otherwise
	 */
	boolean lessThan(S lhs,T rhs);
	
	/**
	 * S &lt= T.
	 * 
	 * @param lhs left operand
	 * @param rhs right operand
	 * @return <code>true</code> if less than or equal and <code>false</code> otherwise
	 */
	boolean lessThanOrEqual(S lhs,T rhs);
	
	/**
	 * S &gt T.
	 * 
	 * @param lhs left operand
	 * @param rhs right operand
	 * @return <code>true</code> if greater than and <code>false</code> otherwise
	 */
	boolean greaterThan(S lhs,T rhs);
	
	/**
	 * S &gt= T.
	 * 
	 * @param lhs left operand
	 * @param rhs right operand
	 * @return <code>true</code> if greater than or equal and <code>false</code> otherwise
	 */
	boolean greaterThanOrEqual(S lhs,T rhs);
	
	/**
	 * S == T.
	 * 
	 * @param lhs left operand
	 * @param rhs right operand
	 * @return <code>true</code> if equal and <code>false</code> otherwise
	 */
	boolean equal(S lhs,T rhs);
	
	/**
	 * S != T.
	 * 
	 * @param lhs left operand
	 * @param rhs right operand
	 * @return <code>true</code> if not equal and <code>false</code> otherwise
	 */
	boolean notEqual(S lhs,T rhs);
}
