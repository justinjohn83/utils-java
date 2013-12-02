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
 * Default implementation of <code>LogicPrinter</code> that prints using Java syntax and toString
 * of the operands.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public class DefaultLogicPrinter<S,T> implements LogicPrinter<S, T>
{

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printExpression(com.gamesalutes.utils.logic.LogicalExpression)
	 */
	public String printExpression(RelationalExpression<S, T> exp)
	{
		S lhs = exp.getLhs();
		T rhs = exp.getRhs();
		RelationalOperator op = exp.getOperator();
		
		
		String leftOperand = printLeftOperand(lhs);
		String rightOperand = printRightOperand(rhs);
		
		if(leftOperand != null && rightOperand != null)
		{
			StringBuilder buf = new StringBuilder(256);

			buf.append(leftOperand);
			buf.append(" ");
			buf.append(printRelationalOperator(op,lhs == null || rhs == null));
			buf.append(" ");
			buf.append(rightOperand);
			
			return buf.toString();

		}
		
		return null;
		
	}
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printLeftOperand(java.lang.Object)
	 */
	public String printLeftOperand(S lhs)
	{
		return lhs != null ? lhs.toString() : printNull();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printRelationalOperator(com.gamesalutes.utils.logic.RelationalOperator, boolean)
	 */
	public String printRelationalOperator(RelationalOperator op,
			boolean nullOperand)
	{
		switch(op)
		{
			case EQUAL: return "==";
			case GREATER_THAN: return ">";
			case GREATER_THAN_OR_EQUAL: return ">=";
			case LESS_THAN: return "<" ; 
			case LESS_THAN_OR_EQUAL: return "<=";
			case NOT_EQUAL: return "!=";
		}
		throw new AssertionError();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printRightOperand(java.lang.Object)
	 */
	public String printRightOperand(T rhs)
	{
		return rhs != null ? rhs.toString() : printNull();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printLogicalOperator(com.gamesalutes.utils.logic.LogicalOperator)
	 */
	public String printLogicalOperator(LogicalOperator op)
	{
		return op == LogicalOperator.AND ? "&&" : "||";
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printOpenParensSymbol()
	 */
	public String printOpenParensSymbol()
	{
		return "(";
	}
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printCloseParensSymbol()
	 */
	public String printCloseParensSymbol()
	{
		return ")";
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicPrinter#printNull()
	 */
	public String printNull()
	{
		return "null";
	}



}
