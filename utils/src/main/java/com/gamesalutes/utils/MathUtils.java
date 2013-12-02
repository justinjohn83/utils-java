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
package com.gamesalutes.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Utility methods for mathematically operations.
 * 
 * @author Justin Montgomery
 * @version $Id: MathUtils.java 2514 2010-12-16 20:36:35Z jmontgomery $
 *
 */
public final class MathUtils 
{
	private MathUtils() {}
	
	
	/**
	 * Smallest distinguishable double value.
	 * 
	 */
	public static final double DBL_EPSILON = 1.0E-15;
	
	/**
	 * Smallest distinguishable float value.
	 * 
	 */
	public static final float FLT_EPSILON = 1.0E-6F;
	
	/**
	 * Compares two doubles in a precision-safe fashion
	 * by comparing the absolute value of the difference
	 * to {@link #DBL_EPSILON}.
	 * 
	 * @param d1 first double
	 * @param d2 second double
	 * @return <code>true</code> if absolute value of difference
	 *         is less than <code>DBL_EPSILON</code> and 
	 *         <code>false</code> otherwise
	 */
	public static boolean equals(double d1,double d2)
	{
		return Math.abs(d1-d2) < DBL_EPSILON;
	}
	
	/**
	 * Compares two floats in a precision-safe fashion
	 * by comparing the absolute value of the difference
	 * to {@link #FLT_EPSILON}.
	 * 
	 * @param f1 first float
	 * @param f2 second float
	 * @return <code>true</code> if absolute value of difference
	 *         is less than <code>FLT_EPSILON</code> and 
	 *         <code>false</code> otherwise
	 */
	public static boolean equals(float f1, float f2)
	{
		return Math.abs(f1-f2) < FLT_EPSILON;
	}
	
	/**
	 * Compares two doubles in a precision-safe fashion.
	 * Equality is determined using {@link #equals(double, double)}.
	 * 
	 * @param d1 first double
	 * @param d2 second double
	 * @return a positive integer,negative integer, or zero if
	 *         <code>d1</code> is greater than, less than,
	 *         or equal to <code>d2</code>
	 */
	public static int compareTo(double d1,double d2)
	{
		if(equals(d1,d2))
			return 0;
		double diff = d1-d2;
		if(diff < 0)
			return -1;
		else
			return 1;
	}
	
	/**
	 * Compares two floats in a precision-safe fashion.
	 * Equality is determined using {@link #equals(float, float)}.
	 * 
	 * @param f1 first float
	 * @param f2 second float
	 * @return a positive integer,negative integer, or zero if
	 *         <code>f1</code> is greater than, less than,
	 *         or equal to <code>f2</code>
	 */
	public static int compareTo(float f1,float f2)
	{
		if(equals(f1,f2))
			return 0;
		float diff = f1-f2;
		if(diff < 0)
			return -1;
		else
			return 1;
	}
	
	/**
	 * Clamps <code>value</code> so that it is in <code>[min,max]</code>.
	 * 
	 * @param value the value
	 * @param min the minimum allowed value
	 * @param max the maximum allowed value
	 * @return the clamped value
	 */
	public static int clamp(int value,int min,int max)
	{
		return Math.min(Math.max(value, min),max);
	}
	
	/**
	 * Clamps <code>value</code> so that it is in <code>[min,max]</code>.
	 * 
	 * @param value the value
	 * @param min the minimum allowed value
	 * @param max the maximum allowed value
	 * @return the clamped value
	 */
	public static long clamp(long value,long min,long max)
	{
		return Math.min(Math.max(value, min),max);
	}
	
	/**
	 * Clamps <code>value</code> so that it is in <code>[min,max]</code>.
	 * 
	 * @param value the value
	 * @param min the minimum allowed value
	 * @param max the maximum allowed value
	 * @return the clamped value
	 */
	public static float clamp(float value,float min,float max)
	{
		return Math.min(Math.max(value, min),max);
	}
	
	/**
	 * Clamps <code>value</code> so that it is in <code>[min,max]</code>.
	 * 
	 * @param value the value
	 * @param min the minimum allowed value
	 * @param max the maximum allowed value
	 * @return the clamped value
	 */
	public static double clamp(double value,double min,double max)
	{
		return Math.min(Math.max(value, min),max);
	}
	
	/**
	 * Computes <code>n!</code>.
	 * 
	 * @param n the number
	 * @return <code>n!</code>
	 */
	public static long factorial(int n)
	{
		if(n == 0) return 1;
		
		long fact = n;
		for(int i = n - 1; i > 1; --i)
			fact *= i;
		return fact;
	}
	
	/**
	 * Computes <code>P(n,r)</code>.
	 * 
	 * @param n the number
	 * @param r the number of selections
	 * @return <code>P(n,r)</code>
	 */
	public static long permutation(int n,int r)
	{
		return factorial(n) / factorial(n - r);
	}
	
	/**
	 * Computes <code>C(n,r)</code>.
	 * 
	 * @param n the number
	 * @param r the number of selections
	 * @return <code>C(n,r)</code>
	 */
	public static long combination(int n,int r)
	{
		return factorial(n) / (factorial(n-r) * factorial(r));
	}
	
	public static double mean(double [] values)
	{
		double sum = 0;
		for(int i = 0; i < values.length; ++i)
			sum += values[i];
		return sum / values.length;
	}
	public static double standardDeviation(double [] values)
	{
		return standardDeviation(values,mean(values));
	}
	public static double standardDeviation(double [] values,double mean)
	{
		double mean_sums = 0;
		for(int i = 0; i < values.length; ++i)
		{
			double v = values[i];
			mean_sums += (v - mean) * (v - mean);
		}
		
		return Math.sqrt(mean_sums / values.length);
	}

        /**
         * Returns whether <code>[x1,y1]</code> intersects <code>[x2,y2]</code>.
         *
         * @param x1 start of first range
         * @Param y1 end of first range
         * @param x2 start of second range
         * @param y2 end of second range
         * @param comp the <code>Comparator</code>  to use or <code>null</code> to use natural ordering
         *
         * @return <codE>true</code> if ranges intersect and <code>false</code> otherwise
         */
        public static <T> boolean intersects(T x1,T y1,T x2,T y2,Comparator<? super T> comp)
        {
            if(comp == null)
            {
                comp = CollectionUtils.createComparatorFromComparable((Comparable)x1);
            }

            if(comp.compare(x1,y1) > 0)
            {
                return false;
            }
            if(comp.compare(x2,y2) > 0)
            {
                return false;
            }
            // have to consider six cases: all combos of overlap
            // case 1: both x1 and y1 before x2: return false
            // case 2: x1 before x2 and y1 in [x2,y2]: return true
            // case 3: both x1 and y1 after y2: return false
            // case 4: x1 and y1 within [x2,y2]: return true
            // case 5: x1 before x2 and y1 after y2: return true
            // case 6: x1 within [x2,y2] but y2 outside rangeEnd: return true
            if(comp.compare(x1,x2) >= 0)
            {
                if(comp.compare(x1,y2) <= 0)
                    return true; // cases 4,6
                else
                    return false; // case 3
            }
            else //x1 comes before x2
            {
                if(comp.compare(y1,x2) >= 0)
                    return true; // cases 2,5
                else
                    return false; // case 1
            }
        }
        /**
         * Returns whether <code>[x1,y1]</code> intersects <code>[x2,y2]</code>.
         *
         * @param x1 start of first range
         * @Param y1 end of first range
         * @param x2 start of second range
         * @param y2 end of second range
         *
         * @return <codE>true</code> if ranges intersect and <code>false</code> otherwise
         */
        public static boolean intersects(long x1,long y1,long x2,long y2)
        {
            if(x1 > y1)
            {
                return false;
            }
            if(x2 > y2)
            {
                return false;
            }
            // have to consider six cases: all combos of overlap
            // case 1: both x1 and y1 before x2: return false
            // case 2: x1 before x2 and y1 in [x2,y2]: return true
            // case 3: both x1 and y1 after y2: return false
            // case 4: x1 and y1 within [x2,y2]: return true
            // case 5: x1 before x2 and y1 after y2: return true
            // case 6: x1 within [x2,y2] but y2 outside rangeEnd: return true
            if(x1 >= x2)
            {
                if(x1 <= y2)
                    return true; // cases 4,6
                else
                    return false; // case 3
            }
            else //x1 comes before x2
            {
                if(y1 >= x2)
                    return true; // cases 2,5
                else
                    return false; // case 1
            }
        }

        /**
         * Returns whether <code>[x1,y1]</code> intersects <code>[x2,y2]</code>.
         *
         * @param x1 start of first range
         * @Param y1 end of first range
         * @param x2 start of second range
         * @param y2 end of second range
         *
         * @return <codE>true</code> if ranges intersect and <code>false</code> otherwise
         */
        public static boolean intersects(double x1,double y1,double x2,double y2)
        {
            if(x1 > y1)
            {
                return false;
            }
            if(x2 > y2)
            {
                return false;
            }
            // have to consider six cases: all combos of overlap
            // case 1: both x1 and y1 before x2: return false
            // case 2: x1 before x2 and y1 in [x2,y2]: return true
            // case 3: both x1 and y1 after y2: return false
            // case 4: x1 and y1 within [x2,y2]: return true
            // case 5: x1 before x2 and y1 after y2: return true
            // case 6: x1 within [x2,y2] but y2 outside rangeEnd: return true
            if(x1 >= x2)
            {
                if(x1 <= y2)
                    return true; // cases 4,6
                else
                    return false; // case 3
            }
            else //x1 comes before x2
            {
                if(y1 >= x2)
                    return true; // cases 2,5
                else
                    return false; // case 1
            }
        }

       /**
         * Returns whether <code>[x1,y1]</code> intersects <code>[x2,y2]</code>.
         *
         * @param x1 start of first range
         * @Param y1 end of first range
         * @param x2 start of second range
         * @param y2 end of second range
         *
         * @return <codE>true</code> if ranges intersect and <code>false</code> otherwise
         */
        public static boolean intersects(Number x1,Number y1, Number x2, Number y2)
        {
            if(isLongConvertable(x1.doubleValue()) && isLongConvertable(y1.doubleValue()) &&
               isLongConvertable(x2.doubleValue()) && isLongConvertable(y2.doubleValue()))
            {
                return intersects(x1.longValue(),y1.longValue(),x2.longValue(),y2.longValue());
            }

            return intersects(x1.doubleValue(),y1.doubleValue(),x2.doubleValue(),y2.doubleValue());
        }

        /**
         * Returns whether <code>d</code> can be converted to a <code>long</code> without
         * loss of any precision.
         *
         * @param d the <code>double</code> value
         *
         * @return <code>true</code> is <code>long</code> convertible and
         *         <code>false</code> otherwise
         */
        public static boolean isLongConvertable(double d)
        {
            if(Double.isInfinite(d) || Double.isNaN(d)) return false;
            if(d > Long.MAX_VALUE || d < Long.MIN_VALUE) return false;

            // use fmod : returns fractional part of a decimal if second argument is 1.0
            return equals(d % 1.0, 0.0);
        }



        /**
         * Condenses an input list of range values (x1,y1,x2,y2,...) so that
         * overlaps are replaced with single start and end values.
         *
         * @param numbers a list of ranges (x1,y1,x2,y2,...)
         *
         * @return the condensed range list
         */
        public static <T extends Comparable<? super T>> List<T> condenseComparableRanges(List<T> values)
        {
            if(!MiscUtils.isEmpty(values))
            {
                return condenseRanges(values,CollectionUtils.createComparatorFromComparable(values.get(0)));
            }
            return new ArrayList<T>(0);
        }


        /**
         * Creates a <code>List</code> of ranges from the input values.
         * If <code>values</code> contains {1,2,3,4,5} then on output
         * the list contains {1,2,2,3,3,4,4,5}.
         *
         * @return the values as a continuous range
         */
        public static <T> List<T> createRange(List<T> values)
        {
            if(MiscUtils.isEmpty(values))
                return values;

            if(values.size() == 1)
            {
                return new ArrayList<T>(Arrays.asList(values.get(0),values.get(0)));
            }

            List<T> range = new ArrayList<T>((values.size() << 1) - 2);

            for(ListIterator<T> it = values.listIterator(); it.hasNext();)
            {
                T first = it.next();
                T second;
                if(it.hasNext())
                {
                    second = it.next();
                    it.previous();
                }
                else
                    second = null;

                if(second != null)
                {
                    range.add(first);
                    range.add(second);
                }
            }

            return range;
        }

        /**
         * Condenses an input list of range values (x1,y1,x2,y2,...) so that
         * overlaps are replaced with single start and end values.
         *
         * @param numbers a list of ranges (x1,y1,x2,y2,...)
         *
         * @return the condensed range list
         */
        public static <T extends Number> List<T> condenseRanges(List<T> numbers)
        {
            return condenseRanges(numbers,Comparators.newNumberComparator());
        }
        /**
         * Condenses an input list of range values (x1,y1,x2,y2,...) so that
         * overlaps are replaced with single start and end values.
         *
         * @param numbers a list of ranges (x1,y1,x2,y2,...)
         * @param comp <code>Comparator</code> to use for list elements
         *
         * @return the condensed range list
         */
        public static <T> List<T> condenseRanges(List<T> numbers,Comparator<? super T> comp)
        {
            if(numbers == null)
                throw new NullPointerException("numbers");
            if(numbers.size() % 2 != 0)
                throw new IllegalArgumentException("numbers=" + numbers);

            List<Pair<T,T>> workingList = new ArrayList<Pair<T,T>>(numbers.size() >>> 2);

            // convert to range pairs for ease of sorting
            for(int i = 0, len = numbers.size(); i < len; i+=2)
                workingList.add(Pair.makePair(numbers.get(i),numbers.get(i+1)));

            // sort the ranges
            Collections.sort(workingList,new RangeComparator<T>(comp));

            ListIterator<Pair<T,T>> it = workingList.listIterator();

            while(it.hasNext())
            {
                Pair<T,T> r1 = it.next();
                if(!it.hasNext()) break;

                Pair<T,T> r2 = it.next();

                T x1 = r1.first;
                T y1 = r1.second;
                T x2 = r2.first;
                T y2 = r2.second;

                // take whole range
                if(intersects(x1,y1,x2,y2,comp))
                {
                    T n1,n2;

                    int result = comp.compare(x1, x2);
                    if(result <= 0) n1 = x1;
                    else n1 = x2;

                    result = comp.compare(y1, y2);
                    if(result <= 0) n2 = y2;
                    else n2 = y1;

                    it.remove();
                    it.previous();
                    it.set(Pair.makePair(n1, n2));
                }
                else
                    it.previous();
            } // while

            // return newly compiled ranges
            List<T> results = new ArrayList<T>(workingList.size() << 2);
            for(Pair<T,T> range : workingList)
            {
                results.add(range.first);
                results.add(range.second);
            }

            return results;
        }

        private static class RangeComparator<T> implements Comparator<Pair<T,T>>
        {

            private final Comparator<? super T> comp;

            public RangeComparator(Comparator<? super T> comp)
            {
                this.comp = comp;
            }

            public int compare(Pair<T, T> s, Pair<T, T> t)
            {
                return comp.compare(s.first, t.first);
            }

        }
}
