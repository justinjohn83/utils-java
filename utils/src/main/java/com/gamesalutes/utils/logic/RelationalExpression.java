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
/* Copyright 2008 - 2009 University of Chicago
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
package com.gamesalutes.utils.logic;

import com.gamesalutes.utils.MiscUtils;

/**
 * Encapsulates left operand (op) right operand in a logical expression.  e.g. x < 5.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class RelationalExpression<S,T>
{
	private final S lhs;
	private final RelationalOperator op;
	private final T rhs;
	
	

	/**
	 * Constructor.
	 * 
	 * @param lhs the left operand
	 * @param op the <code>RelationalOperator</code>
	 * @param rhs the right operand
	 */
	public RelationalExpression(S lhs,RelationalOperator op,T rhs)
	{
		if(op == null)
			throw new NullPointerException("op");
		
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}
	
	/**
	 * Constructor.
	 * 
	 * Initializes the operator to <code>RelationalOperator.EQUALS</code> and
	 * the right operand to <code>null</code>.
	 * 
	 * @param lhs the left operand
	 */
	public RelationalExpression(S lhs)
	{
		this(lhs,RelationalOperator.EQUAL,null);
	}
	
	
	/**
	 * Returns the left operand.
	 * 
	 * @return the left operand
	 */
	public S getLhs() { return lhs; }
	
	/**
	 * Returns the right operand.
	 * 
	 * @return the right operand
	 */
	public T getRhs() { return rhs; }
	
	/**
	 * Returns the <code>RelationalOperator</code>.
	 * 
	 * @return the <code>RelationalOperator</code>
	 */
	public RelationalOperator getOperator() { return op; }
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof RelationalExpression)) return false;
		
		RelationalExpression<S,T> le = (RelationalExpression)o;
		
		return op == le.op &&
		       MiscUtils.safeEquals(lhs,le.lhs) &&
		       MiscUtils.safeEquals(rhs, le.rhs);
	}
	
	@Override
	public int hashCode()
	{
		int result = 17;
		final int mult = 31;
		
		result = result * mult + op.hashCode();
		result = result * mult + MiscUtils.safeHashCode(lhs);
		result = result * mult + MiscUtils.safeHashCode(rhs);
		
		return result;
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder(512);
		
		str.append("lhs=").append(lhs).append(";op=").append(op).append(";rhs=").append(rhs);
		
		return str.toString();
	}
	
	/**
	 * Evaluates this relational expression using <code>eval</code>.
	 * 
	 * @param eval the <code>RelationalEvaluator</code>
	 * @return <code>true</code> if this expression evaluates to <code>true</code> and
	 *         <code>false</code> otherwise
	 */
	public boolean evaluate(RelationalEvaluator<S,T> eval)
	{
		return RelationalOperator.evaluate(this, eval);
	}
}
