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
package com.gamesalutes.utils;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for {@link MathUtils}.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public class MathUtilsTest
{

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.MathUtils#clamp(int, int, int)}.
	 */
	@Test
	public void testClampIntIntInt()
	{
		int min = 1;
		int max = 10;
		assertEquals((min + max)/2,MathUtils.clamp((min + max)/2,min,max));
		assertEquals(min,MathUtils.clamp(-min, min, max));
		assertEquals(max,MathUtils.clamp(max*max, min, max));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.MathUtils#clamp(long, long, long)}.
	 */
	@Test
	public void testClampLongLongLong()
	{
		long min = 1;
		long max = 10;
		assertEquals((min + max)/2,MathUtils.clamp((min + max)/2,min,max));
		assertEquals(min,MathUtils.clamp(-min, min, max));
		assertEquals(max,MathUtils.clamp(max*max, min, max));
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.MathUtils#clamp(float, float, float)}.
	 */
	@Test
	public void testClampFloatFloatFloat()
	{
		float min = 1;
		float max = 10;
		assertEquals((min + max)/2,MathUtils.clamp((min + max)/2,min,max),MathUtils.FLT_EPSILON);
		assertEquals(min,MathUtils.clamp(-min, min, max),MathUtils.FLT_EPSILON);
		assertEquals(max,MathUtils.clamp(max*max, min, max),MathUtils.FLT_EPSILON);
	}

	/**
	 * Test method for {@link com.gamesalutes.utils.MathUtils#clamp(double, double, double)}.
	 */
	@Test
	public void testClampDoubleDoubleDouble()
	{
		float min = 1;
		float max = 10;
		assertEquals((min + max)/2,MathUtils.clamp((min + max)/2,min,max),MathUtils.DBL_EPSILON);
		assertEquals(min,MathUtils.clamp(-min, min, max),MathUtils.DBL_EPSILON);
		assertEquals(max,MathUtils.clamp(max*max, min, max),MathUtils.DBL_EPSILON);
	}
	
	@Test
	public void testFactorial()
	{
		assertEquals(1,MathUtils.factorial(0));
		assertEquals(1,MathUtils.factorial(1));
		assertEquals(24,MathUtils.factorial(4));
	}
	@Test
	public void testCombination()
	{
		assertEquals(3,MathUtils.combination(3, 1));
		assertEquals(6,MathUtils.combination(4, 2));
	}
	
	@Test
	public void testPermutation()
	{
		assertEquals(3,MathUtils.permutation(3, 1));
		assertEquals(12,MathUtils.permutation(4, 2));
	}

        @Test
        public void testIntersects()
        {
            // have to consider six cases: all combos of overlap
            // case 1: both x1 and y1 before x2: return false
            // case 2: x1 before x2 and y1 in [x2,y2]: return true
            // case 3: both x1 and y1 after y2: return false
            // case 4: x1 and y1 within [x2,y2]: return true
            // case 5: x1 before x2 and y1 after y2: return true
            // case 6: x1 within [x2,y2] but y2 outside y1: return true
            assertFalse(MathUtils.intersects(0,1,2,3));
            assertTrue(MathUtils.intersects(0,2,1,3));
            assertFalse(MathUtils.intersects(2,3,0,1));
            assertTrue(MathUtils.intersects(1,3,0,4));
            assertTrue(MathUtils.intersects(0, 5, 3, 4));
            assertTrue(MathUtils.intersects(1,2,0,5));
        }

        @Test
        public void testCondenseRanges()
        {
            // no changes
            assertEquals(Arrays.asList(1,2,3,4,5,6,7,8,9,10),
                    MathUtils.condenseRanges(Arrays.asList(1,2,3,4,5,6,7,8,9,10)));
            // all condensed
            assertEquals(Arrays.asList(1,10),MathUtils.condenseRanges(Arrays.asList(1,10,2,3,4,5,6,7,8,9)));

            // mixed
            assertEquals(Arrays.asList(1,12,13,20,21,25),MathUtils.condenseRanges(Arrays.asList(1,3,2,4,3,7,5,10,8,12,13,20,21,24,23,25)));

            // condense to single
            assertEquals(Arrays.asList(1,10),
                    MathUtils.condenseRanges(
                    Arrays.asList(1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10)));

            // condense some
            assertEquals(Arrays.asList(1,5,6,8,9,10),
                    MathUtils.condenseRanges(Arrays.asList(1,2,2,3,3,4,4,5,6,8,9,10)));
        }

        @Test
        public void testCreateRange()
        {
            // single value
            assertEquals(Arrays.asList(1,1),MathUtils.createRange(Arrays.asList(1)));

            assertEquals(Arrays.asList(1,2),MathUtils.createRange(Arrays.asList(1,2)));

            assertEquals(Arrays.asList(1,2,2,3),MathUtils.createRange(Arrays.asList(1,2,3)));

            assertEquals(Arrays.asList(1,2,2,3,3,4),MathUtils.createRange(Arrays.asList(1,2,3,4)));
        }

}
