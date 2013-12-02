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
 * Turns a <code>LogicStatement</code> into its equivalent string representation.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public interface LogicPrinter<S,T>
{
	/**
	 * Prints a logical expression, or returns <code>null</code> if the expression should be ignored,i.e. either
	 * <code>printLeftOperand</code> or <code>printRightOperand</code> returns <code>null</code>.
	 * 
	 * @param exp the expression
	 * @return the string form of exp
	 */
	String printExpression(RelationalExpression<S,T> exp);
	
	/**
	 * Prints the left operand.  
	 * If <code>lhs</code> is <code>null</code> then, the value of <code>printNull</code> is returned.
	 * 
	 * @param lhs the left operand
	 * @return the string form of lhs
	 */
	String printLeftOperand(S lhs);
	
	/**
	 * Prints the right operand.
	 * If <code>rhs</code> is <code>null</code> then, the value of <code>printNull</code> is returned.
	 * 
	 * @param rhs the right operand
	 * @return the string form of rhs
	 */
	String printRightOperand(T rhs);
	
	/**
	 * Prints the relational operator.
	 * 
	 * @param op the <code>RelationalOperator</code>
	 * @param nullOperand <code>true</code> if at least one of the operands involved is <code>null</code>
	 *                    and <code>false</code> otherwise
	 * @return the string form of the relational operator
	 */
	String printRelationalOperator(RelationalOperator op,boolean nullOperand);
	
	/**
	 * Prints the logical operator.
	 * 
	 * @return the string form of <code>op</code>
	 */
	String printLogicalOperator(LogicalOperator op);
	
	
	/**
	 * Prints an open parenthesis symbol.
	 * 
	 * @return the open parens symbol
	 */
	String printOpenParensSymbol();
	
	/**
	 * Prints a closed parenthesis symbol.
	 * 
	 * @return the close parens symbol
	 */
	String printCloseParensSymbol();
	
	/**
	 * Prints a "Null" value.  If an expression with a "null" operand should be ignored completely,
	 * then return <code>null</code> as the value.
	 * 
	 * 
	 * @return the string form of "Null"
	 */
	String printNull();
}
