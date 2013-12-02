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


import com.gamesalutes.utils.graph.LevelOrderTraversal;
import com.gamesalutes.utils.graph.TreeNode;
import com.gamesalutes.utils.graph.TreeTraversal.Listener;
import com.gamesalutes.utils.UniqueWrapper;



import java.util.*;

/**
 * Represents a boolean expression containing a series of "ands" and "ors" for constraints.
 * This class is made generic by allowing any <code>Object</code> to be passed in
 * as a constraint that is stored in the internal data structure nodes.  A class
 * implementing the {@link RelationalEvaluator} interface specifies what constitutes
 * a result of <code>true</code> or <code>false</code> for each expression when
 * {@link #evaluate() evaluate} is called.  The concatentation methods apply
 * the either and "and" or "or" to the whole result of the previous statement.
 * 
 * @author Justin Montgomery
 * @version $Id: LogicStatement.java 1704 2009-09-23 17:44:18Z jmontgomery $
 *
 */
public final class LogicStatement<S,T> 
{
	private TreeNode<UniqueWrapper<Object>> currentNode;
	private RelationalEvaluator<S,T> eval;
	
	
//	private static final int AND = 1;
//	private static final int OR = 2;
	//protected int length;	
	/**
	 * Constructs a <code>Constraint</code> object with the specified starting constraint and the specified checker
	 * @param exp The LogicalExpression associated with this new constraint object
	 * @param eval the <code>RelationalEvaluator to evaluate the expression
	 */
	public LogicStatement(RelationalExpression<S,T> exp, RelationalEvaluator<S,T> eval)
	{
		if(exp == null)
			throw new NullPointerException("exp");
		if(eval == null)
			throw new NullPointerException("eval");

		this.eval = eval;
		
		//init root
		currentNode = new TreeNode<UniqueWrapper<Object>>(new UniqueWrapper<Object>(exp));
		
	}
	
	/**
	 * Constucts an empty <code>Constraint</code> object with the specified checker
	 * @param eval the <code>RelationalEvaluator to evaluate the expression
	 * 
	 */
	public LogicStatement(RelationalEvaluator<S,T> eval)
	{
		this.eval = eval;
	}
	
	/**
	 * Adds a new node to this tree and returns the parent node
	 * @param exp new constraint object to add to the current constraint tree
	 * @param op The LogicalOperator
	 * @return <code>TreeNode<Object></code> for the parent node
	 */
	protected TreeNode<UniqueWrapper<Object>> addNode(RelationalExpression<S,T> exp,LogicalOperator op)
	{
		
		TreeNode<UniqueWrapper<Object>> newParent = 
			new TreeNode<UniqueWrapper<Object>>(new UniqueWrapper<Object>(op));
		TreeNode<UniqueWrapper<Object>> newChild = 
			new TreeNode<UniqueWrapper<Object>> (new UniqueWrapper<Object>(exp));
			
		//currentNode.setParent(newParent);
		
		if(currentNode != null)
			newParent.addChild(currentNode);
		newParent.addChild(newChild);
		//add the constraint as a child to the new parent
		//currentNode.setParent(newParent);
		
		
		return newParent;
	}
	/**
	 * Concatenates <code>Constraint</code> c to this <code>Constraint</code>
	 * @param c the <code>LogicStatement</code> to concatenate
	 * @param op the kind of concatentation
	 * @return the resulting concatenated <code>Constraint</code>
	 */
	protected LogicStatement<S,T> concatenateSingle(LogicStatement<S,T> c,LogicalOperator op)
	{
		LogicStatement<S,T> temp = new LogicStatement<S,T>(eval);
		
		TreeNode<UniqueWrapper<Object>> newParent = new TreeNode<UniqueWrapper<Object>>(new UniqueWrapper<Object>(op));
		if(currentNode != null)
			newParent.addChild(currentNode);
		if(c.currentNode != null)
			newParent.addChild(c.currentNode);
		
		temp.currentNode = newParent;
		
		return temp;
		
	}
	
	/**
	 * Concatenates a constraint to this <code>Constraint</code>
	 * @param constraint the constraint to concatenate
	 * @param op The LogicalOperator to concatenate
	 * @return the resulting concatenated <code>Constraint</code>
	 */
	protected LogicStatement<S,T> concatenateSingle(RelationalExpression<S,T> constraint,LogicalOperator op)
	{
		LogicStatement<S,T> temp = new LogicStatement<S,T>(eval);
		
		temp.currentNode = addNode(constraint,op);
		return temp;
	}
	
	/**
	 * Concatenates the <code>Collection</code> of constraints to this <code>Constraint</code>
	 * @param constraints constraints to concatenate
	 * @param op The LogicalOperator
	 * @return the resulting concatenated <code>Constraint</code>
	 */
	protected LogicStatement<S,T> concatenateMultiple(Collection<?> constraints, LogicalOperator op)
	{
		//init the parent
		TreeNode<UniqueWrapper<Object>> newParent = 
			new TreeNode<UniqueWrapper<Object>>(new UniqueWrapper<Object>(op));
		
		//currentNode.setParent(newParent);
		if(currentNode != null)
			newParent.addChild(currentNode);
		
		
		for(Iterator<?> iter = constraints.iterator();
			iter.hasNext();)
		{
			newParent.addChild(new TreeNode<UniqueWrapper<Object>>(
					new UniqueWrapper<Object>(iter.next())));
		}
		
		LogicStatement<S,T> temp = new LogicStatement<S,T>(eval);
		temp.currentNode = newParent;
		return temp;
	}
	
	/**
	 * Concatenates the array of constraints to this <code>Constraint</code>
	 * @param constraints the array of constraints to concatenate
	 * @param op the <code>LogicalOperator</code>
	 * @return the resulting concatenated <code>Constraint</code>
	 */
	protected LogicStatement<S,T> concatenateMultiple(RelationalExpression<S,T> [] constraints,LogicalOperator op)
	{
		//init the parent
		TreeNode<UniqueWrapper<Object>> newParent = 
			new TreeNode<UniqueWrapper<Object>>(new UniqueWrapper<Object>(op));
		
		//currentNode.setParent(newParent);
		if(currentNode != null)
			newParent.addChild(currentNode);
		
		
		for(int i = 0; i < constraints.length; i ++)
		{
			newParent.addChild(new TreeNode<UniqueWrapper<Object>>(
					new UniqueWrapper<Object>(constraints[i])));
		}
		
		LogicStatement<S,T> temp = new LogicStatement<S,T>(eval);
		temp.currentNode = newParent;
		return temp;
	}
	
	/**
	 * Returns the tree node holding an object <code>constraint</code>
	 * @param constraint the constraint object for which to get its tree node
	 * @return <code>TreeNode<Object></code> associated with <code>constraint</code>
	 *         if it exists and <code>null</code> otherwise
	 */
	protected TreeNode<UniqueWrapper<Object>> getConstraintNode(RelationalExpression<S,T> constraint)
	{
		//currentNode is always the root
		if(currentNode == null)
			return null;
		
		List<TreeNode<UniqueWrapper<Object>>> enumeration = 
			new LevelOrderTraversal<UniqueWrapper<Object>>(currentNode).execute().getTraversal();
		for(TreeNode<UniqueWrapper<Object>> node : enumeration)
		{
			if(node.getData().get().equals(constraint))
				return node;
		}
		return null;
	}
	
	
	/**
	 * Removes the specified tree node from this tree
	 * @param node the node to remove
	 */
	protected void removeNode(TreeNode<UniqueWrapper<Object>> node)
	{
		//removing final node
		if(node.getParent() == null && node.isLeaf())
			currentNode = null;
		else //just a plain old leaf
			node.getParent().removeChild(node);
	}
	
	/**
	 * Removes the specified constraint from the current expression
	 * @param exp the expression to remove
	 * @return <code>true</code> if node removed and <code>false</code> otherwise
	 */
	public boolean remove(RelationalExpression<S,T> exp)
	{
		TreeNode<UniqueWrapper<Object>> node = getConstraintNode(exp);
		
		if(node != null)
		{
			removeNode(node);
			return true;
		}
		else
			return false;
		
	}
	
	/**
	 * Logically "ands" the specified constraint to the current constraint object
	 * Let {@code (x < 1 or x < 2)} be logical equivalence of current constraint
	 * Then calling <code>concatAnd(new Integer(5)</code> creates the logical expression
	 * {@code (x < 1 or x < 2) and x < 5)} 
	 * @param exp the new constraint to append
	 * @return the <code>Constraint</code> object containing the concatenated constraint
	 */
	public LogicStatement<S,T> and(RelationalExpression<S,T> exp)
	{
		return concatenateSingle(exp,LogicalOperator.AND);
	}
	
	/**
	 * Logicially "ands" a series of constraints to the current constraint object
	 * Each element of the array is "anded" 
	 * @param exps an array of expression objects
	 * @return the <code>Constraint</code> object containing the concatenated constraints
	 */
	public LogicStatement<S,T> and(RelationalExpression<S,T>... exps)
	{
		return concatenateMultiple(exps,LogicalOperator.AND);
	}
	
	/**
	 * Logicially "ands" a series of constraints to the current constraint object
	 * Each element of the array is "anded".
	 * @param exps a collection of expression objects
	 * @return the <code>Constraint</code> object containing the concatenated constraints
	 */
	public LogicStatement<S,T> and(Collection<RelationalExpression<S,T>> exps)
	{
		return concatenateMultiple(exps,LogicalOperator.AND);
	}
	
	/**
	 * Logicially "ands" a series of constraints to the current constraint object
	 * Each element of the array is "anded".
	 * @param stmts a collection of statements
	 * @return the <code>Constraint</code> object containing the concatenated constraints
	 */
	public LogicStatement<S,T> and(LogicStatement<S,T>...stmts)
	{
		return concatenateMultiple(Arrays.asList(stmts),LogicalOperator.AND);
	}
	
	/**
	 * Makes the current <code>Constraint</code> "and"
	 * @return the new <code>Constraint</code> object
	 */
	public LogicStatement<S,T> and()
	{
		return concatenateSingle(this,LogicalOperator.AND);
	}
	
	/**
	 * Appends <code>Constraint</code> c to this constraint in an "and" fashion
	 * @param stmt another <code>LogicStatement</code>
	 * @return the result of the concatenation
	 */
	public LogicStatement<S,T> and(LogicStatement<S,T> stmt)
	{
		return concatenateSingle(stmt,LogicalOperator.AND);
	}
	
	/**
	 * Logically "ors" the specified constraint to the current constraint object
	 * Let {@code (x < 1 and x < 2)} be logical equivalence of current constraint
	 * Then calling <code>concatOR(new Integer(5)</code> creates the logical expression
	 * {@code (x < 1 and x < 2) or x < 5)} 
	 * @param exp the new constraint to append
	 * @return the <code>Constraint</code> object containing the concatenated constraint
	 */
	public LogicStatement<S,T> or(RelationalExpression<S,T> exp)
	{
		return concatenateSingle(exp,LogicalOperator.OR);
	}
	
	/**
	 * Logicially "ors" a series of constraints to the current constraint object
	 * Each element of the array is "or-ed".
	 * 
	 * @param exps an array of constraint objects
	 * @return the <code>Constraint</code> object containing the concatenated constraints
	 */
	public LogicStatement<S,T> or(RelationalExpression<S,T>... exps)
	{
		return concatenateMultiple(exps,LogicalOperator.OR);
	}
	
	/**
	 * Logicially "ors" a series of constraints to the current constraint object
	 * Each element of the array is "or-ed".
	 * @param exps a collection of expression objects
	 * @return the <code>Constraint</code> object containing the concatenated constraints
	 */
	public LogicStatement<S,T> or(Collection<RelationalExpression<S,T>> exps)
	{
		return concatenateMultiple(exps,LogicalOperator.OR);
	}
	
	/**
	 * Logicially "ors" a series of constraints to the current constraint object
	 * Each element of the array is "or-ed".
	 * @param stmts a collection of LogicStatement objects
	 * @return the <code>Constraint</code> object containing the concatenated constraints
	 */
	public LogicStatement<S,T> or(LogicStatement<S,T>... stmts)
	{
		return concatenateMultiple(Arrays.asList(stmts),LogicalOperator.OR);
	}
	
	/**
	 * Makes the current <code>Constraint</code> "or"
	 * @return the new <code>Constraint</code> object
	 */
	public LogicStatement<S,T> or()
	{
		return concatenateSingle(this,LogicalOperator.OR);
	}
	
	/**
	 * Appends <code>Constraint</code> c to this constraint in an "or" fashion
	 * @param stmt another <code>LogicStatement</code>
	 * @return the result of the concatenation
	 */
	public LogicStatement<S,T> or(LogicStatement<S,T> stmt)
	{
		return concatenateSingle(stmt,LogicalOperator.OR);
	}
	
	/**
	 * Evaluates this <code>LogicStatement</code> 
	 * based on the provided {@link RelationalEvaluator} object provided
	 * at construction time.  Each passed in expression object is evaluated using logic
	 * rules.
	 * 
	 * @return <code>true</code> if statement evaluates to <code>true</code> and
	           and <code>false</code> otherwise
	 */
	public boolean evaluate()
	{
		return evaluate(this,null,null,null);
	} //end method evaluate
	
	/**
	 * Evaluates this <code>LogicStatement</code> by using the provided <code>lhs</code> for the 
	 * left operand of every <code>LogicalExpression</code> instead of the one in each <code>LogicalExpression</code>.
	 * 
	 * @param lhs the overriding parameter to use as the left operand for every <code>LogicalExpression</code>
	 * 
	 * @return <code>true</code> if statement evaluates to <code>true</code> and
	           and <code>false</code> otherwise
	 */
	public boolean evaluateOverrideLeft(S lhs)
	{
		return evaluate(this,lhs,null,null);
	}
	
	/**
	 * Evaluates this <code>LogicStatement</code> by using the provided <code>rhs</code> for the 
	 * right operand of every <code>LogicalExpression</code> instead of the one in each <code>LogicalExpression</code>.
	 * 
	 * @param rhs the overriding parameter to use as the right operand for every <code>LogicalExpression</code>
	 * 
	 * @return <code>true</code> if statement evaluates to <code>true</code> and
	           and <code>false</code> otherwise
	 */
	public boolean evaluateOverrideRight(T rhs)
	{
		return evaluate(this,null,rhs,null);
	}
	/**
	 * Evaluates this <code>LogicStatement</code> by using both the provided <code>lhs</code> and 
	 * <code>rhs</code> for both 
	 * operands of every <code>LogicalExpression</code> instead of the ones in each <code>LogicalExpression</code>.
	 * 
	 * @param rhs the overriding parameter to use for both operands for every <code>LogicalExpression</code>
	 * 
	 * @return <code>true</code> if statement evaluates to <code>true</code> and
	           and <code>false</code> otherwise
	 */
	public boolean evaluateOverrideBoth(S lhs,T rhs)
	{
		return evaluate(this,lhs,rhs,null);
	}
	
	@SuppressWarnings("unchecked")
	private boolean evaluate(LogicStatement<S,T> c,S lhsOverride,T rhsOverride,Map<TreeNode<UniqueWrapper<Object>>,Boolean> evalMap)
	{
		// empty logic tree means everything passes
		if(c.currentNode == null)
			return true;
		
		List<TreeNode<UniqueWrapper<Object>>> nodes = 
					new LevelOrderTraversal<UniqueWrapper<Object>>(c.currentNode).execute().getTraversal();
		Collections.reverse(nodes);
		
		if(evalMap == null)
			evalMap = new HashMap<TreeNode<UniqueWrapper<Object>>,Boolean>();
		
		Boolean currValue = null;
		boolean isAnd = true;
		TreeNode<UniqueWrapper<Object>> currParent = null;
		
		for(Iterator<TreeNode<UniqueWrapper<Object>>> it = nodes.iterator();it.hasNext();)
		{
			TreeNode<UniqueWrapper<Object>> node = it.next();
			
			// next evaluation block
			if(currParent != node.getParent() /* || !it.hasNext()*/)
			{
				if(currParent != null)
					evalMap.put(currParent, currValue != null ? currValue : Boolean.TRUE);
				currValue = null;
				currParent = node.getParent();
				if(currParent != null)
					isAnd = currParent.getData().get() == LogicalOperator.AND;
			}
			
			boolean result;
			// evaluate it
			// try to do lookup first
			if(evalMap.containsKey(node))
				result = evalMap.get(node);
			// a leaf will either be another concatenated constraint object or it will be a constraint itself
			// it should never be a ConstraintBool
			else if(node.isLeaf())
			{
//				if(!node.isRoot())
//				{
					Object data = node.getData().get();
					if(data instanceof LogicStatement)
						result = evaluate((LogicStatement<S,T>)data,lhsOverride,rhsOverride,evalMap);
					// dangling constraint bool: maybe this is not possible?
					else if(data instanceof LogicalOperator)
						result = true;
					// evaluate using the evaluator
					else
					{
						RelationalExpression<S,T> exp = (RelationalExpression<S,T>)data;
						result = exp.getOperator().evaluate(
								lhsOverride != null ? lhsOverride : exp.getLhs(),
								rhsOverride != null ? rhsOverride : exp.getRhs(), 
								eval);
					}
//				}
//				else
//				{
//					result = true;
//				}
			}
			// this should never happen
			else
				throw new AssertionError("Unable to evaluate: " + node);
			
			if(currValue != null)
			{
				if(isAnd) currValue &= result;
				else currValue |= result;
			}
			else
				currValue = result;
		}
		
		return currValue;
	}
	
	@SuppressWarnings("unchecked")
	
	/**
	 * Stringifies this statement using the given printer.
	 * 
	 * @param printer the <code>LogicPrinter</code>
	 * 
	 * @return the string form of this statement
	 */
	public String print(LogicPrinter<S,T> printer)
	{
		if(printer == null)
			throw new NullPointerException("printer");
		Map<TreeNode<UniqueWrapper<Object>>,Integer> map 
			= new HashMap<TreeNode<UniqueWrapper<Object>>,Integer>();
		
		Map<TreeNode<UniqueWrapper<Object>>,Integer> skipParensCount = 
			new HashMap<TreeNode<UniqueWrapper<Object>>,Integer>();
		
		
		StringBuilder buf = new StringBuilder(2048);
		
		// start at the bottom of the tree and bubble up
		
		// must expand the tree inline
		TreeNode<UniqueWrapper<Object>> root = expandInAll(this);
		
		// eliminate null printout nodes
		removeNullPrintouts(root,printer);
		
		// nodes already level marked by expandInAll
		
		TreeNode<UniqueWrapper<Object>> node = getFirstPrintNode(root);
		
		if(node.getParent() == null)
		{
			Object data = node.getData().get();
			if(data instanceof RelationalExpression)
			{
				String exp = printer.printExpression((RelationalExpression)data);
				if(exp != null)
					buf.append(exp);
			}
		}
		else
		{
			node = node.getParent();
			while(node != null)
			{
//				if(node.getData() instanceof RelationalExpression)
//				{
//					buf.append(printer.printExpression((RelationalExpression)node.getData()));
//					node = node.getParent();
//				}
//				else
//				{
					LogicalOperator op = (LogicalOperator)node.getData().get();
					
					Integer lastChild = map.get(node);
					// parens at start of new level = #ancestors
					if(!map.containsKey(node))
					{
						// figure out how many ancestors this has started at 
						TreeNode<UniqueWrapper<Object>> tmp = node;
						int count = 0; //node.getChildCount() > 1 ? 1 : 0;
						
						while((tmp = tmp.getParent()) != null)
						{
							if(tmp != root && !map.containsKey(tmp))
								count++;
						}
						if(node.getParent() != null && node.getChildCount() > 1)
							count++;
						
						if(!node.isLeaf() && !(
								node.getChildAt(0).getData().get() instanceof LogicalOperator))
						{
							for(int i = 0; i < count; ++i)
								buf.append(printer.printOpenParensSymbol());
						}
						else
							skipParensCount.put(node, count);
						
						map.put(node, null);
					}
					//
					else if(lastChild != null && lastChild == node.getChildCount() - 1)
					{
						// figure out how many ancestors this has started at 
						TreeNode<UniqueWrapper<Object>> tmp = node;
						int count = 0; //node.getChildCount() > 1 ? 1 : 0;
						
						while((tmp = tmp.getParent()) != null)
						{
							Integer value = map.get(tmp);
							
							if(tmp != root && map.containsKey(tmp) && (value == tmp.getChildCount() - 1 || value == Integer.MAX_VALUE))
								count++;
						}
						
						if(node.getParent() != null && node.getChildCount() > 1)
							count++;
						
						Integer skipCount = skipParensCount.get(node);
						if(skipCount != null)
							count -= skipCount;
						
						for(int i = 0; i < count; ++i)
							buf.append(printer.printCloseParensSymbol());
						
						// don't do this more than once
						map.put(node, Integer.MAX_VALUE);
						
						// pop up
						node = node.getParent();
					}
					// process the children
					else if(lastChild == null || lastChild != Integer.MAX_VALUE)
					{
						boolean broke = false;
						for(int i = lastChild != null ? lastChild + 1 : 0; i < node.getChildCount(); ++i)
						{
							TreeNode<UniqueWrapper<Object>> child = node.getChildAt(i);
							Object cData = child.getData().get();
							
							if(buf.length() > 0)
							{
								char c = buf.charAt(buf.length() - 1);
								if(c != '(' && c != ' ')
									buf.append(" ");
							}
							
							// print logical operator
							if(i != 0)
								buf.append(printer.printLogicalOperator(op)).append(" ");
							
							if(cData instanceof RelationalExpression)
							{
								String exp = printer.printExpression((RelationalExpression)cData);
								if(exp != null)
									buf.append(exp);
							}
							else // it is logical operator : must pop down
							{
								map.put(node, i);
								node = child;
								broke = true;
								break;
							}
							

						} // for
						
						if(!broke)
							map.put(node, node.getChildCount()-1);
					} // else if
					else
						node = node.getParent();
//				} // outer else
					
			} // while
		} // else
		
		// cleanup the string
		return buf.toString();
//		return cleanupPrint(buf.toString().trim(),printer);
	}
	
	
//	private String cleanupPrint(final String raw,LogicPrinter<S,T> printer)
//	{
//		String mod = raw;
//		
//		// remove "()" caused by null expressions
//		String openParens = printer.printOpenParensSymbol();
//		String closeParens = printer.printCloseParensSymbol();
//		String logicalAnd = printer.printLogicalOperator(LogicalOperator.AND);
//		String logicalOr = printer.printLogicalOperator(LogicalOperator.OR);
//		
//		StringBuilder sequence = new StringBuilder(128);
//		sequence.append(openParens).append(closeParens);
//		
//		// replace all "()" sequences
//		
//		mod = MiscUtils.literalReplaceAll(mod,sequence, "");
//		
//		MiscUtils.clearStringBuilder(sequence);
//		
//		// replace dangling logical operators
//		// TODO: could use regex but not sure if need to escape the sequence since the operators are custom
//		// remove first of "|| ||","&& &&","|| &&","&& ||"
//		
//		// must loop until nothing changes
//		String orig;
//		do
//		{
//			orig = mod;
//			
//			sequence.append(logicalOr).append(logicalOr);
//			mod = mod.replace(sequence, logicalOr);
//			MiscUtils.clearStringBuilder(sequence);
//			
//			sequence.append(logicalAnd).append(logicalAnd);
//			mod = mod.replace(sequence, logicalAnd);
//			MiscUtils.clearStringBuilder(sequence);
//			
//			sequence.append(logicalOr).append(logicalAnd);
//			mod = mod.replace(sequence, logicalAnd);
//			MiscUtils.clearStringBuilder(sequence);
//			
//			sequence.append(logicalAnd).append(logicalOr);
//			mod = mod.replace(sequence, logicalOr);
//			MiscUtils.clearStringBuilder(sequence);
//		}
//		while(!orig.equals(mod));
//		
//		// remove any trailing logical operands
//		mod = mod.trim();
//		if(mod.endsWith(logicalOr))
//			mod = mod.substring(0,mod.length() - logicalOr.length());
//		else if(mod.endsWith(logicalAnd))
//			mod = mod.substring(0,mod.length() - logicalAnd.length());
//
//		return mod.trim();
//		
//		
//	}
	
	@SuppressWarnings("unchecked")
	private TreeNode<UniqueWrapper<Object>> expandInAll(LogicStatement<S,T> rootStmt)
	{
		final boolean [] needsExpand = {false};
		
		TreeNode<UniqueWrapper<Object>> root = rootStmt.currentNode;
		
		if(root == null) return null;
		
		// determine if we need to clone the root
		new LevelOrderTraversal<UniqueWrapper<Object>>(root,new Listener<UniqueWrapper<Object>>()
		{

						public boolean onTraverse(TreeNode<UniqueWrapper<Object>> node)
						{
							if(node.getData().get() instanceof LogicStatement)
								needsExpand[0] = true;
							return true;
						}
				
					}).execute();
		
		// no nested statements
		if(!needsExpand[0])
			return root;
		
		// must unnest the statements
		root = root.clone();
		
		
		
		List<TreeNode<UniqueWrapper<Object>>>trav = new LevelOrderTraversal<UniqueWrapper<Object>>(root).execute().getTraversal();
		
		for(TreeNode<UniqueWrapper<Object>> node : trav)
		{
			Object data = node.getData().get();
			if(data instanceof LogicStatement)
			{
				LogicStatement<S,T> stmt = (LogicStatement<S,T>)data;
				
				TreeNode<UniqueWrapper<Object>> parent = node.getParent();
				if(parent != null)
				{
					// remove current logic statement and expand in expression nodes
					int index = parent.indexOfChild(node);
					parent.removeChildAt(index);
					TreeNode<UniqueWrapper<Object>> newChild = stmt.currentNode;
					// if the "root" child of the logic statement is another logic statement, then
					// must recurse through that logic statement as well
					if(newChild.getData().get() instanceof LogicStatement)
						newChild = expandInAll((LogicStatement)newChild.getData().get());
					
					parent.addChildAt(newChild,index);
				}
			}
		}
		
		return root;
		
					
	}
	// eliminate null printout nodes
	private void removeNullPrintouts(TreeNode<UniqueWrapper<Object>> root,final LogicPrinter<S,T> printer)
	{
		final List<TreeNode<UniqueWrapper<Object>>> removed = new ArrayList<TreeNode<UniqueWrapper<Object>>>();
		
			new LevelOrderTraversal<UniqueWrapper<Object>>(root,new Listener<UniqueWrapper<Object>>()
				{

								public boolean onTraverse(TreeNode<UniqueWrapper<Object>> node)
								{
									Object o = node.getData().get();
									if(o instanceof RelationalExpression)
									{
										// TODO: cache the result?
										if(printer.printExpression((RelationalExpression<S,T>)o) == null)
											removed.add(node);
									}
									return true;
								}
						
							}).execute();
		
		// remove those nodes in traversal
		for(TreeNode<UniqueWrapper<Object>> node : removed)
			node.removeFromParent();
		
		// remove empty logical nodes
		
		removed.clear();
		
//		final List<TreeNode<UniqueWrapper<Object>>> repointNodes = new ArrayList<TreeNode<UniqueWrapper<Object>>>();
		
		new LevelOrderTraversal<UniqueWrapper<Object>>(root,new Listener<UniqueWrapper<Object>>()
				{

								public boolean onTraverse(TreeNode<UniqueWrapper<Object>> node)
								{
									Object o = node.getData().get();
									if(o instanceof LogicalOperator)
									{
										if(node.isLeaf())
											removed.add(node);
									}
									return true;
								}
						
							}).execute();
		
		// remove those nodes in traversal
		for(TreeNode<UniqueWrapper<Object>> node : removed)
			node.removeFromParent();
		
		// FIXME: this should be done internally by core implementation!!!
		
		final Set<TreeNode<UniqueWrapper<Object>>> ignored = new HashSet<TreeNode<UniqueWrapper<Object>>>();
		
		do
		{
			removed.clear();
			new LevelOrderTraversal<UniqueWrapper<Object>>(root,new Listener<UniqueWrapper<Object>>()
					{
	
									public boolean onTraverse(TreeNode<UniqueWrapper<Object>> node)
									{
										Object o = node.getData().get();
										// also remove if logical operand only has a single child that is also logical operand
										if(o instanceof LogicalOperator && node.getChildCount() == 1)
										{
											TreeNode<UniqueWrapper<Object>> child = node.getChildAt(0);
											if(child.getData().get() instanceof LogicalOperator && !ignored.contains(child))
												removed.add(child);
										}
										return true;
									}
							
								}).execute();
	
			// repoint single-line nodes
			for(TreeNode<UniqueWrapper<Object>> node : removed)
			{
				TreeNode<UniqueWrapper<Object>> parent = node.getParent();
				TreeNode<UniqueWrapper<Object>> parent_parent = parent.getParent();
				if(parent_parent != null)
				{
					int index = parent_parent.indexOfChild(parent);
					parent.removeFromParent();
					parent_parent.addChildAt(node, index);
				}
				else
					ignored.add(node);
			}
		}
		while(!removed.isEmpty());
		
	}
	private TreeNode<UniqueWrapper<Object>> getFirstPrintNode(TreeNode<UniqueWrapper<Object>> root)
	{
		List<TreeNode<UniqueWrapper<Object>>> nodes = 
			new LevelOrderTraversal<UniqueWrapper<Object>>(root).execute().getTraversal();
		
		if(nodes.isEmpty())
			return null;
		
		// find first node in bottom level
		Integer lvl = null;
		TreeNode<UniqueWrapper<Object>> start = null;
		
		for(ListIterator<TreeNode<UniqueWrapper<Object>>> it = nodes.listIterator(nodes.size());it.hasPrevious();)
		{
			TreeNode<UniqueWrapper<Object>> node = it.previous();
			
			// we found our start
			if(lvl != null && node.getLevel() != lvl)
			{
				// get element before last
				it.next();
				start = it.next();
				break;
			}
			// last node
			else if(!it.hasPrevious())
				start = node;
			else
			{
				Object data = node.getData().get();
				if(lvl == null && data instanceof RelationalExpression)
					lvl = node.getLevel();
			}
			
		}
		
		return start;
	}

} //end class Constraint
