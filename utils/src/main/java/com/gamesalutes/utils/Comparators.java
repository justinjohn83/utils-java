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
package com.gamesalutes.utils;

import java.util.Comparator;

/**
 * Common comparator implementations.
 * 
 * @author Justin Montgomery
 * @version $Id: Comparators.java 2241 2010-07-29 19:03:34Z jmontgomery $
 */
public final class Comparators
{
	private Comparators() {}
	
	/**
	 * Returns a comparator that sorts lists of strings in increasing order by length
	 * (shortest to longest).
	 * 
	 * @return the comparator
	 */
	public static Comparator<String> newStringLengthComparator()
	{
		return new Comparator<String>()
		{
			public int compare(String s1, String s2)
			{
				int diff =  s1.length() - s2.length();
				if(diff != 0)
					return diff;
				return s1.compareTo(s2);
			}
		};
	}
	
	/**
	 * Returns a <code>Comparator</code> that compares pairs of strings in a case insensitive
	 * way. This comparator will compare the pair in the same manner as that defined by 
	 * {@link Pair#compareTo(Pair)}, comparing the seconds first and then the firsts if the
	 * seconds are equal.
	 * 
	 * @return the comparator
	 */
	public static Comparator<Pair<String,String>> newCaseInsensitiveStringPairComparator()
	{
		return new Comparator<Pair<String,String>>()
		{
			public int compare(Pair<String,String> p1,Pair<String,String> p2)
			{
				int result = newCaseInsentiveStringComparator().compare(p1.second, p2.second);
				if(result != 0) return result;
				return newCaseInsentiveStringComparator().compare(p1.first,p2.first);
			}
		};
	}
	
	/**
	 * Returns a comparator that sorts strings, ignoring case.
	 * 
	 * @return the comparator
	 */
	@SuppressWarnings("unchecked")
	public static Comparator<String> newCaseInsentiveStringComparator()
	{
		return (Comparator<String>)caseInsensitiveCharSeqComp;
	}
	
	/**
	 * Returns a comparator for general character sequences.
	 * 
	 * @return the comparator
	 */
	public static Comparator<CharSequence> newCharSequenceComparator()
	{
		return charSeqComp;
	}
	
	/**
	 * Returns a comparator for general character sequences, ignoring case.
	 * 
	 * @return the comparator
	 */
	@SuppressWarnings("unchecked")
	public static Comparator<CharSequence> newCaseInsensitiveCharSequenceComparator()
	{
		return (Comparator<CharSequence>)caseInsensitiveCharSeqComp;
	}

        /**
         * Returns a comparator for comparing generic <code>Number</code> elements.
         *
         * @return the comparator
         */
        public static Comparator<Number> newNumberComparator()
        {
            return numberComparator;
        }
	
	private static final Comparator<? extends CharSequence> caseInsensitiveCharSeqComp = 
		new Comparator<CharSequence>()
	{
		public int compare(CharSequence s1,CharSequence s2)
		{
	        int n1=s1.length(), n2=s2.length();
	        for (int i1=0, i2=0; i1<n1 && i2<n2; i1++, i2++)
	        {
	            char c1 = s1.charAt(i1);
	            char c2 = s2.charAt(i2);
	            if (c1 != c2) 
	            {
	                c1 = Character.toUpperCase(c1);
	                c2 = Character.toUpperCase(c2);
	                if (c1 != c2) 
	                {
	                    c1 = Character.toLowerCase(c1);
	                    c2 = Character.toLowerCase(c2);
	                    if (c1 != c2) 
	                    {
	                        return c1 - c2;
	                    }
	                }
	            }
	        }
	        int result = n1 - n2;
	        // make order definite by doing regular comparison in case where case insensitive is equal
	        if(result != 0) return result;
	        return charSeqComp.compare(s1, s2);
		}
	};
	
	private static final Comparator<CharSequence> charSeqComp = 
		new Comparator<CharSequence>()
		{
			public int compare(CharSequence s1,CharSequence s2)
			{
		        int n1=s1.length(), n2=s2.length();
		        for (int i1=0, i2=0; i1<n1 && i2<n2; i1++, i2++)
		        {
		            char c1 = s1.charAt(i1);
		            char c2 = s2.charAt(i2);
		            if (c1 != c2) 
		            	return c1 - c2;
		        }
		        return n1 - n2;
			}
	};

        private static final Comparator<Number> numberComparator =
                new Comparator<Number>()
                {
                    public int compare(Number n1,Number n2)
                    {
                        if(MathUtils.isLongConvertable(n1.doubleValue()) && MathUtils.isLongConvertable(n2.doubleValue()))
                        {
                            long v1 = n1.longValue();
                            long v2 = n2.longValue();

                            if(v1 < v2) return -1;
                            if(v1 > v2) return 1;
                            return 0;
                        }
                        double v1 = n1.doubleValue();
                        double v2 = n2.doubleValue();

                        if(v1 < v2) return -1;
                        if(v1 > v2) return 1;
                        return 0;
                    }
                };
}
