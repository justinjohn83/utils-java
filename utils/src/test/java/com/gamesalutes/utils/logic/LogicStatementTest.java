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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gamesalutes.utils.logic.RelationalExpression;
import com.gamesalutes.utils.logic.LogicStatement;


// FIXME: since improved the LogicStatement interface can test more aggressively with different relational operators now!

@SuppressWarnings("unchecked")
/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public class LogicStatementTest
{

	private static final int TIMEOUT = 3000;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	
	@Test(timeout=TIMEOUT)
	public void testSingleConstraint()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(new RelationalExpression(0),new Checker());
		assertTrue(c.evaluateOverrideRight(1));
		assertFalse(c.evaluateOverrideRight(-1));
		
		String expected = "0 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
		
	}

	@Test(timeout=TIMEOUT)
	public void testRemove()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(new RelationalExpression(0),new Checker()).and(new RelationalExpression(5));
		assertTrue(c.remove(new RelationalExpression(5)));
		
		assertTrue(c.evaluateOverrideRight(1));
		
		String expected = "0 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	@Test(timeout=TIMEOUT)
	public void testConcatANDObject()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(new RelationalExpression(0),new Checker()).and(new RelationalExpression(5));
		
		assertFalse(c.evaluateOverrideRight(3));
		assertTrue(c.evaluateOverrideRight(10));
		
		String expected = "0 == null && 5 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	@Test(timeout=TIMEOUT)
	public void testConcatANDObjectArray()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0),new Checker()).and(
					new RelationalExpression[]{
							new RelationalExpression(5),
							new RelationalExpression(10),
							new RelationalExpression(15)});
		
		assertFalse(c.evaluateOverrideRight(1));
		assertFalse(c.evaluateOverrideRight(7));
		assertFalse(c.evaluateOverrideRight(12));
		assertTrue(c.evaluateOverrideRight(20));
		
		String expected = "0 == null && 5 == null && 10 == null && 15 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.logic.LogicStatement#and(java.util.Collection)}.
	 */
	@Test(timeout=TIMEOUT)
	public void testConcatANDCollectionOfQ()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(new RelationalExpression(0),new Checker()).and(
				Arrays.<RelationalExpression<Integer,Integer>>asList(
						new RelationalExpression(5),
						new RelationalExpression(10),
						new RelationalExpression(15)));
		
		assertFalse(c.evaluateOverrideRight(1));
		assertFalse(c.evaluateOverrideRight(7));
		assertFalse(c.evaluateOverrideRight(12));
		assertTrue(c.evaluateOverrideRight(20));
		
		String expected = "0 == null && 5 == null && 10 == null && 15 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.logic.LogicStatement#and()}.
	 */
	@Test(timeout=TIMEOUT)
	public void testConcatAND()
	{
		// FIXME: what is no argument version for again?
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(new RelationalExpression(0),new Checker()).and();
		
		assertTrue(c.evaluateOverrideRight(1));
		assertFalse(c.evaluateOverrideRight(-1));
		
		String expected = "0 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.logic.LogicStatement#and(com.gamesalutes.utils.logic.LogicStatement)}.
	 */
	@Test(timeout=TIMEOUT)
	public void testConcatANDConstraint()
	{
		LogicStatement<Integer,Integer> c1= new LogicStatement<Integer,Integer>(new RelationalExpression(0),new Checker());
		LogicStatement<Integer,Integer> c2 = new LogicStatement<Integer,Integer>(new RelationalExpression(5),new Checker());
		
		
		LogicStatement<Integer,Integer> result = c1.and(c2);
		assertFalse(result.evaluateOverrideRight(3));
		assertTrue(result.evaluateOverrideRight(10));
		
		String expected = "0 == null && 5 == null";
		assertEquals(expected,result.print(new DefaultLogicPrinter()));
	}


	@Test(timeout=TIMEOUT)
	public void testConcatORObject()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(new RelationalExpression(5),new Checker()).or(new RelationalExpression(0));
		
		assertFalse(c.evaluateOverrideRight(-1));
		assertTrue(c.evaluateOverrideRight(10));
		assertTrue(c.evaluateOverrideRight(3));
		
		String expected = "5 == null || 0 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	
	@Test(timeout=TIMEOUT)
	public void testConcatORObjectArray()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(new RelationalExpression(20),new Checker()).or(
				new RelationalExpression[]
				 {
						new RelationalExpression(5),
						new RelationalExpression(10),
						new RelationalExpression(15)
				});
		
		assertFalse(c.evaluateOverrideRight(0));
		assertTrue(c.evaluateOverrideRight(7));
		assertTrue(c.evaluateOverrideRight(12));
		assertTrue(c.evaluateOverrideRight(20));
		
		String expected = "20 == null || 5 == null || 10 == null || 15 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.logic.LogicStatement#or(java.util.Collection)}.
	 */
	@Test(timeout=TIMEOUT)
	public void testConcatORCollectionOfQ()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(
				new RelationalExpression(20),
				new Checker()).or(
						Arrays.<RelationalExpression<Integer,Integer>>asList(
								new RelationalExpression(5),
								new RelationalExpression(10),
								new RelationalExpression(15)));
		
		assertFalse(c.evaluateOverrideRight(0));
		assertTrue(c.evaluateOverrideRight(7));
		assertTrue(c.evaluateOverrideRight(12));
		assertTrue(c.evaluateOverrideRight(20));
		
		String expected = "20 == null || 5 == null || 10 == null || 15 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.logic.LogicStatement#or()}.
	 */
	@Test(timeout=TIMEOUT)
	public void testConcatOR()
	{
		// FIXME: what is no argument version for again?
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0),
				new Checker()).or();
		
		assertTrue(c.evaluateOverrideRight(1));
		assertFalse(c.evaluateOverrideRight(-1));
		
		String expected = "0 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.logic.LogicStatement#or(com.gamesalutes.utils.logic.LogicStatement)}.
	 */
	@Test(timeout=TIMEOUT)
	public void testConcatORConstraint()
	{
		LogicStatement<Integer,Integer> c1= new LogicStatement<Integer,Integer>(
				new RelationalExpression(5),new Checker());
		LogicStatement<Integer,Integer> c2 = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0),
				new Checker());
		
		
		LogicStatement<Integer,Integer> result = c1.or(c2);
		assertFalse(result.evaluateOverrideRight(-1));
		assertTrue(result.evaluateOverrideRight(7));
		assertTrue(result.evaluateOverrideRight(3));
		
		String expected = "5 == null || 0 == null";
		assertEquals(expected,result.print(new DefaultLogicPrinter()));
	}
	
	
	@Test(timeout=TIMEOUT)
	public void testComplex1()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(
				new RelationalExpression(1),
				new Checker()).or(
						new RelationalExpression(2)).and(new RelationalExpression(5));
		
		assertTrue(c.evaluateOverrideRight(6));
		assertFalse(c.evaluateOverrideRight(0));
		assertFalse(c.evaluateOverrideRight(4));
		assertFalse(c.evaluateOverrideRight(2));
		
		String expected = "(1 == null || 2 == null) && 5 == null";
		assertEquals(expected,c.print(new DefaultLogicPrinter()));
	}
	
	@Test(timeout=TIMEOUT)
	public void testComplex2()
	{
		// test 
		// ((x > -20 || x > 5) && (x > -10 || x > 10)) || x > 20;
		
		LogicStatement<Integer,Integer> lhs = new LogicStatement<Integer,Integer>(
				new RelationalExpression(-20),
				new Checker()).or(
						new RelationalExpression(5));
		LogicStatement<Integer,Integer> mid = new LogicStatement<Integer,Integer>(
				new RelationalExpression(-10),new Checker()).or(
						new RelationalExpression(10));
		LogicStatement<Integer,Integer> rhs = new LogicStatement<Integer,Integer>(
				new RelationalExpression(20),
				new Checker());
		
		LogicStatement<Integer,Integer> combo = new LogicStatement<Integer,Integer>(
				new Checker()).and(
										lhs,mid).or(rhs);
		
		assertTrue(combo.evaluateOverrideRight(30));
		assertTrue(combo.evaluateOverrideRight(15));
		assertFalse(combo.evaluateOverrideRight(-50));
		assertTrue(combo.evaluateOverrideRight(7));
		
		String expected = "((-20 == null || 5 == null) && (-10 == null || 10 == null)) || 20 == null";
		assertEquals(expected,combo.print(new DefaultLogicPrinter()));
	}
	
	@Test(timeout=TIMEOUT)
	public void testComplex3()
	{
		// test
		// (((x > 0 && x > 5) || (x > 20 && x > 35)) && (x > 10 || x > 15) && x > -10;
		
		LogicStatement<Integer,Integer> first = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0),
				new Checker()).and(
						new RelationalExpression(5));
		LogicStatement<Integer,Integer> second = new LogicStatement<Integer,Integer>(
				new RelationalExpression(20),
				new Checker()).and(
						new RelationalExpression(35));
		LogicStatement<Integer,Integer> third = new LogicStatement<Integer,Integer>(
				new RelationalExpression(10),
				new Checker()).or(
						new RelationalExpression(15));
		LogicStatement<Integer,Integer> last = new LogicStatement<Integer,Integer>(
				new RelationalExpression(-10),
				new Checker());
		
		LogicStatement<Integer,Integer> combo = new LogicStatement<Integer,Integer>(new Checker()).or(first,second).and(third,last);
		
		assertTrue(combo.evaluateOverrideRight(100));
		assertTrue(combo.evaluateOverrideRight(12));
		assertFalse(combo.evaluateOverrideRight(8));
		assertFalse(combo.evaluateOverrideRight(0));
		assertFalse(combo.evaluateOverrideRight(-5));
		assertFalse(combo.evaluateOverrideRight(-15));
		
		String expected = "((0 == null && 5 == null) || (20 == null && 35 == null)) && (10 == null || 15 == null) && -10 == null";
		assertEquals(expected,combo.print(new DefaultLogicPrinter()));


	}
	
	
	@Test(timeout=TIMEOUT)
	public void testComplex4()
	{
		// test
		// (((x > 0 && x > 5) || (x > 20 && x > 35)) && (x > 10 || x > 15) && x > -10;
		
		LogicStatement<Integer,Integer> first = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0),
				new Checker()).and(
						new RelationalExpression(5));
//		LogicStatement<Integer,Integer> second = new LogicStatement<Integer,Integer>(
//				new RelationalExpression(20),
//				new Checker()).and(
//						new RelationalExpression(35));
		LogicStatement<Integer,Integer> third = new LogicStatement<Integer,Integer>(
				new RelationalExpression(10),
				new Checker()).or(
						new RelationalExpression(15));
//		LogicStatement<Integer,Integer> last = new LogicStatement<Integer,Integer>(
//				new RelationalExpression(-10),
//				new Checker());
		
		LogicStatement<Integer,Integer> combo = new LogicStatement<Integer,Integer>(new Checker()).and(first).and(third);
		
		assertTrue(combo.evaluateOverrideRight(100));
		assertTrue(combo.evaluateOverrideRight(12));
		assertFalse(combo.evaluateOverrideRight(8));
		assertFalse(combo.evaluateOverrideRight(0));
		assertFalse(combo.evaluateOverrideRight(-5));
		assertFalse(combo.evaluateOverrideRight(-15));
		
		String expected = "(0 == null && 5 == null) && (10 == null || 15 == null)";
		assertEquals(expected,combo.print(new DefaultLogicPrinter()));


	}
	
	@Test(timeout=TIMEOUT)
	public void testNullPrint()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0,RelationalOperator.GREATER_THAN,10),new Checker()).and(
					new RelationalExpression[]{
							new RelationalExpression(5,RelationalOperator.EQUAL,6),
							new RelationalExpression(10,RelationalOperator.NOT_EQUAL,null),
							new RelationalExpression(15,RelationalOperator.LESS_THAN_OR_EQUAL,20)});
		
		
		String expected = "0 > 10 && 5 == 6 && 15 <= 20";
		assertEquals(expected,c.print(new DefaultLogicPrinter()
		{
			@Override
			public String printNull()
			{
				return null;
			}
		}));
	}
	
	@Test(timeout=TIMEOUT)
	public void testNullPrintComplex()
	{
		// test
		// (((x > 0 && x > 5) || (x > 20 && x > 35)) && (x > 10 || x > 15) && x > -10;
		
		LogicStatement<Integer,Integer> first = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0,RelationalOperator.GREATER_THAN_OR_EQUAL,-5),
				new Checker()).and(
						new RelationalExpression(5,RelationalOperator.LESS_THAN,10));
		LogicStatement<Integer,Integer> second = new LogicStatement<Integer,Integer>(
				new RelationalExpression(20),
				new Checker()).and(
						new RelationalExpression(35));
		LogicStatement<Integer,Integer> third = new LogicStatement<Integer,Integer>(
				new RelationalExpression(10,RelationalOperator.EQUAL,10),
				new Checker()).or(
						new RelationalExpression(15,RelationalOperator.NOT_EQUAL,10));
		LogicStatement<Integer,Integer> last = new LogicStatement<Integer,Integer>(
				new RelationalExpression(-10),
				new Checker());
		
		LogicStatement<Integer,Integer> combo = new LogicStatement<Integer,Integer>(new Checker()).or(first,second).and(third,last);

		String expected = "(0 >= -5 && 5 < 10) && (10 == 10 || 15 != 10)";
		assertEquals(expected,combo.print(new DefaultLogicPrinter()
		{
			@Override
			public String printNull()
			{
				return null;
			}
		}));
	}
	
	@Test(timeout=TIMEOUT)
	public void testNullPrintAll()
	{
		LogicStatement<Integer,Integer> c = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0),new Checker()).and(
					new RelationalExpression[]{
							new RelationalExpression(5),
							new RelationalExpression(10),
							new RelationalExpression(15)});
		
		
		String expected = "";
		assertEquals(expected,c.print(new DefaultLogicPrinter()
		{
			@Override
			public String printNull()
			{
				return null;
			}
		}));
	}
	@Test(timeout=TIMEOUT)
	public void testNullPrintAllComplex()
	{
		// test
		// (((x > 0 && x > 5) || (x > 20 && x > 35)) && (x > 10 || x > 15) && x > -10;
		
		LogicStatement<Integer,Integer> first = new LogicStatement<Integer,Integer>(
				new RelationalExpression(0),
				new Checker()).and(
						new RelationalExpression(5));
		LogicStatement<Integer,Integer> second = new LogicStatement<Integer,Integer>(
				new RelationalExpression(20),
				new Checker()).and(
						new RelationalExpression(35));
		LogicStatement<Integer,Integer> third = new LogicStatement<Integer,Integer>(
				new RelationalExpression(10),
				new Checker()).or(
						new RelationalExpression(15));
		LogicStatement<Integer,Integer> last = new LogicStatement<Integer,Integer>(
				new RelationalExpression(-10),
				new Checker());
		
		
		LogicStatement<Integer,Integer> combo = new LogicStatement<Integer,Integer>(new Checker()).or(first,second).and(third,last);

		String expected = "";

		assertEquals(expected,combo.print(new DefaultLogicPrinter()
		{
			@Override
			public String printNull()
			{
				return null;
			}
		}));
	}
	
	// FIXME: doing this out of laziness for now
	private static class Checker extends EqualsRelationalEvaluator
	{

		@Override
		public boolean equal(Object lhs, Object rhs)
		{
			// TODO Auto-generated method stub
			return ((Integer)lhs).compareTo((Integer)rhs) < 0;
		}
		
	}

}
