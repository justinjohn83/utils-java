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

/**
 * Relational or equality operators.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public enum RelationalOperator
{
	/**
	 * operator &lt
	 * 
	 */
	LESS_THAN
	{
		public <S,T> boolean evaluate(S lhs,T rhs,RelationalEvaluator<S,T> eval)
		{
			return eval.lessThan(lhs, rhs);
		}
	},
	/**
	 * operator &lt=
	 */
	LESS_THAN_OR_EQUAL
	{
		public <S,T> boolean evaluate(S lhs,T rhs,RelationalEvaluator<S,T> eval)
		{
			return eval.lessThanOrEqual(lhs, rhs);
		}
	},
	
	/**
	 * operator &gt
	 */
	GREATER_THAN
	{
		public <S,T> boolean evaluate(S lhs,T rhs,RelationalEvaluator<S,T> eval)
		{
			return eval.greaterThan(lhs, rhs);
		}
	},
	
	/**
	 * operator &gt=
	 */
	GREATER_THAN_OR_EQUAL
	{
		public <S,T> boolean evaluate(S lhs,T rhs,RelationalEvaluator<S,T> eval)
		{
			return eval.greaterThanOrEqual(lhs, rhs);
		}
	},
	/**
	 * operator ==
	 */
	EQUAL
	{
		public <S,T> boolean evaluate(S lhs,T rhs,RelationalEvaluator<S,T> eval)
		{
			return eval.equal(lhs, rhs);
		}
	},
	
	/**
	 * operator !=
	 */
	NOT_EQUAL
	{
		public <S,T> boolean evaluate(S lhs,T rhs,RelationalEvaluator<S,T> eval)
		{
			return eval.notEqual(lhs, rhs);
		}
	};
	
	/**
	 * Evaluates this operator applied to <code>lhs</code> as the first operand and 
	 * <code>rhs</code> as the second operand using <code>eval</code>.
	 * 
	 * @param lhs the first operand
	 * @param rhs the second operand
	 * @param eval the <code>RelationalEvaluator</code>
	 * @return <code>true</code> if evaluates to <code>true</code> and <code>false</code> otherwise
	 */
	public abstract <S,T> boolean evaluate(S lhs,T rhs,RelationalEvaluator<S,T> eval);
	
	/**
	 * Evaluates the operands in <code>exp</code> with the operator in <code>exp</code> using the given
	 * <code>eval</code>.
	 * 
	 * @param exp the <code>RelationalExpression</code>
	 * @param eval the <code>RelationalEvaluator</code>
	 * @return <code>true</code> if evaluates to <code>true</code> and <code>false</code> otherwise
	 */
	public static <S,T> boolean evaluate(RelationalExpression<S,T> exp,RelationalEvaluator<S,T> eval)
	{
		return exp.getOperator().evaluate(exp.getLhs(), exp.getRhs(),eval);
	}
}
