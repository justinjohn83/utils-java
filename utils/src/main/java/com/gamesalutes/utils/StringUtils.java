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


// FIXME: add a "normalizeLineEndings" method

/**
 * String utility methods. Note some "String utilities" are also in MiscUtils since they were placed there first and
 * remain there for compatibility.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public class StringUtils
{
	private StringUtils() {}
	
	/**
	 * Invokes the literal "replace" method of <code>s</code> until the operation has no effect.
	 * 
	 * @param s the input string
	 * @param sequence the sequence to replace
	 * @param replacement the replacement sequence
	 * @return the replaced string
	 */
	public static String literalReplaceAll(String s,CharSequence sequence,CharSequence replacement)
	{
		if(s == null)
			throw new NullPointerException("s");
		if(sequence == null)
			throw new NullPointerException("sequence");
		if(replacement == null)
			throw new NullPointerException("replacement");
		
		// nothing to do
		if(sequence.equals(replacement)) return s;
		
		String r = s;
		
		// optimization when replacement is not the same length as seqence
		if(sequence.length() != replacement.length())
		{
			int len = r.length();
			while((r = r.replace(sequence, replacement)).length() != len)
				len = r.length();
		}
		else // must do equals comparison in string to determine if something changed
		{
			String orig = r;
			while(!(r = r.replace(sequence,replacement)).equals(orig))
				orig = r;
		}
		
		return r;
		
	}	
	
	/**
	 * Behaves like <code>String.contains</code> but ignores case.
	 * 
	 * @pararm s the string
	 * @param seq the sequence to test
	 * @return <code>true</code> if <code>s</code> contains <code>seq</code> ignoring case and
	 *         <code>false</code> otherwise
	 */
	public static boolean caseInsensitiveContains(CharSequence s,CharSequence seq)
	{
		return caseInsensitiveIndexOf(s,seq,0,true) > -1;
	}
	
	/**
	 * Behaves like <code>String.contains</code> but with general <code>CharSequence</code>.
	 * 
	 * @pararm s the string
	 * @param seq the sequence to test
	 * @return <code>true</code> if <code>s</code> contains <code>seq</code>  and
	 *         <code>false</code> otherwise
	 */
	public static boolean contains(CharSequence s,CharSequence seq)
	{
		return indexOf(s,seq,0,true) > -1;
	}
	/**
	 * Behaves like <code>String.indexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveIndexOf(CharSequence s,CharSequence seq)
	{
		return caseInsensitiveIndexOf(s,seq,0,true);
	}
	
	/**
	 * Behaves like <code>String.indexOf</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int indexOf(CharSequence s,CharSequence seq)
	{
		return indexOf(s,seq,0,true);
	}
	
	/**
	 * Behaves like <code>String.indexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveIndexOf(CharSequence s,CharSequence seq,int fromIndex)
	{
		return caseInsensitiveIndexOf(s,seq,fromIndex,true);
	}
	
	/**
	 * Behaves like <code>String.indexOf</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int indexOf(CharSequence s,CharSequence seq,int fromIndex)
	{
		return indexOf(s,seq,fromIndex,true);
	}
	
	
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveLastIndexOf(CharSequence s,CharSequence seq)
	{
		return caseInsensitiveIndexOf(s,seq,s.length() - 1,false);
	}
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int lastIndexOf(CharSequence s,CharSequence seq)
	{
		return indexOf(s,seq,s.length() - 1,false);
	}
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveLastIndexOf(CharSequence s,CharSequence seq,int fromIndex)
	{
		return caseInsensitiveIndexOf(s,seq,fromIndex,false);
	}
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>seq</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int lastIndexOf(CharSequence s,CharSequence seq,int fromIndex)
	{
		return indexOf(s,seq,fromIndex,false);
	}
	
	/**
	 * Behaves like <code>String.indexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveIndexOf(CharSequence s,char c)
	{
		return caseInsensitiveIndexOf(s,c,0,true);
	}
	
	/**
	 * Behaves like <code>String.indexOf</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int indexOf(CharSequence s,char c)
	{
		return indexOf(s,c,0,true);
	}
	
	/**
	 * Behaves like <code>String.indexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveIndexOf(CharSequence s,char c,int fromIndex)
	{
		return caseInsensitiveIndexOf(s,c,fromIndex,true);
	}
	
	/**
	 * Behaves like <code>String.indexOf</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int indexOf(CharSequence s,char c,int fromIndex)
	{
		fromIndex = normalizeIndex(s,fromIndex,true);
		if(fromIndex < 0) return -1;
		
		return indexOf(s,c,fromIndex,true);
	}
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveLastIndexOf(CharSequence s,char c)
	{
		int len = s.length();
		if(len == 0) return -1;
		return caseInsensitiveIndexOf(s,c,len-1,false);
	}
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int lastIndexOf(CharSequence s,char c)
	{
		int len = s.length();
		if(len == 0) return -1;
		return indexOf(s,c,len-1,false);
	}
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but ignores case.
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int caseInsensitiveLastIndexOf(CharSequence s,char c,int fromIndex)
	{
		return caseInsensitiveIndexOf(s,c,fromIndex,false);
	}
	
	/**
	 * Behaves like <code>String.lastIndexOf</code> but for <code>CharSequence</code>
	 * 
	 * @param s the string
	 * @param c the character to test
	 * @param fromIndex the index in <code>s</code> to begin the search
	 * @return the index of <code>c</code> in <code>s</code> or <code>-1</code> if not found
	 */
	public static int lastIndexOf(CharSequence s,char c,int fromIndex)
	{
		fromIndex = normalizeIndex(s,fromIndex,false);
		if(fromIndex < 0) return -1;
		return indexOf(s,c,fromIndex,false);
	}
	
	private static int normalizeIndex(CharSequence s,int i,boolean fromStart)
	{
		int len = s.length();
		
		// behave like String.indexOf and String.lastIndexOf
		if(fromStart)
		{
			if(i < 0) i = 0;
			else if(i >= len) return -1;
		}
		else
		{
			if(i >= len) i = len - 1;
			else if(i < 0) return -1;
		}
		
		return i;
	}
	private static int caseInsensitiveIndexOf(CharSequence s,char c,int fromIndex,boolean fromStart)
	{
		fromIndex = normalizeIndex(s,fromIndex,fromStart);
		
		char lc = Character.toLowerCase(c);
		char hc = Character.toUpperCase(c);
		
		// lower case
		int index1 = fromStart ? indexOf(s,lc,fromIndex,true) : indexOf(s,lc,fromIndex,false);
		// upper case
		int index2 = fromStart ? indexOf(s,hc,fromIndex,true) : indexOf(s,hc,fromIndex,false);
						
		int index;

		// set index to be smaller of lower case and upper case match
		if(index1 == -1) index = index2;
		else if(index2 == -1) index = index1;
		else if(index1 <= index2) index = index1;
		else index = index2;
		
		return index;
	}

	private static int indexOf(CharSequence s,char c,int index,boolean fromStart)
	{
		int len = s.length();
		if(fromStart)
		{
			for(int i = index; i < len; ++i)
				if(s.charAt(i) == c)
					return i;
		}
		else
		{
			for(int i = index; i >= 0; --i)
				if(s.charAt(i) == c)
					return i;
		}
		
		return -1;
	}
	private static int indexOf(CharSequence s,CharSequence seq,int i,boolean fromStart)
	{
		// do a regional compare starting at begining of string
		if(s == null) throw new NullPointerException("s");
		if(seq == null) throw new NullPointerException("seq");
		
		int lenTotal = s.length();
		int lenSeq = seq.length();
		
		if(lenSeq > lenTotal) return -1;
		// s always contains empty string
		if(lenSeq == 0) return 0;
		
		
		// behave like String.indexOf and String.lastIndexOf
		if(fromStart)
		{
			if(i < 0) i = 0;
			else if(i >= lenTotal) return -1;
		}
		else
		{
			if(i >= lenTotal) i = lenTotal - 1;
			else if(i < 0) return -1;
		}
		
		

		// look for a match in first character
		int start = i;
		char c = seq.charAt(0);
		
		while(fromStart ? (start < lenTotal) : (start >= 0))
		{
			int index = indexOf(s,c,start,true);
			
			// no match
			if(index == -1)
				return -1;
			
			// first character matches: examine rest of sequence
			if(regionMatches(false,s,index, seq, 0,lenSeq))
				return index;
			
			start = index + (fromStart ? 1 : -1);
		}
		
		return -1;
	}
	private static int caseInsensitiveIndexOf(CharSequence s,CharSequence seq,int i,boolean fromStart)
	{
		// do a regional compare starting at beginning of string
		if(s == null) throw new NullPointerException("s");
		if(seq == null) throw new NullPointerException("seq");
		
		int lenTotal = s.length();
		int lenSeq = seq.length();
		
		if(lenSeq > lenTotal) return -1;
		// s always contains empty string
		if(lenSeq == 0) return 0;
		
		
		// behave like String.indexOf and String.lastIndexOf
		if(fromStart)
		{
			if(i < 0) i = 0;
			else if(i >= lenTotal) return -1;
		}
		else
		{
			if(i >= lenTotal) i = lenTotal - 1;
			else if(i < 0) return -1;
		}
		
		

		// look for a match in first character
		int start = i;
		char lc = Character.toLowerCase(seq.charAt(0));
		char hc = Character.toUpperCase(lc);
		
		// save values so don't compute multiple times unnecessarily
		int index1 = Integer.MIN_VALUE;
		int index2 = Integer.MIN_VALUE;
		
		while(fromStart ? (start < lenTotal) : (start >= 0))
		{
			// lower case
			if(index1 != -1 && start > index1)
				index1 = indexOf(s,lc,start,true);
			// upper case
			if(index2 != -1 && start > index2)
				index2 = indexOf(s,hc,start,true);
						
			int index;

			// set index to be smaller of lower case and upper case match
			if(index1 == -1) index = index2;
			else if(index2 == -1) index = index1;
			else if(index1 <= index2) index = index1;
			else index = index2;
			
			// no match
			if(index == -1)
				return -1;
			
			// first character matches: examine rest of sequence
			if(regionMatches(true,s,index, seq, 0,lenSeq))
				return index;
			
			start = index + (fromStart ? 1 : -1);
		}
		
		return -1;
		
	}
	
	/**
	 * Compares two sequences for equality ignoring case.  
	 * 
	 * 
	 * @param s1 first string
	 * @param s2 second string
	 * @return <code>true</code> if equal ignoring case and <code>false</code> otherwise
	 * 
	 * @see String#equalsIgnoreCase(String)
	 */
	public static boolean caseInsensitiveEquals(CharSequence s1,CharSequence s2)
	{
		if(s1 == s2) return true;
		if((s1 == null) != (s2 == null)) return false;
		
		int len1 = s1.length();
		int len2 = s2.length();
		
		if(len1 != len2) return false;
		
		return regionMatches(true,s1,0, s2, 0, len1);
	}
	
	/**
	 * Compares two sequences for equality.  
	 * 
	 * 
	 * @param s1 first string
	 * @param s2 second string
	 * @return <code>true</code> if equal and <code>false</code> otherwise
	 * 
	 * @see String#equals(Object)
	 */
	public static boolean equals(CharSequence s1,CharSequence s2)
	{
		if(s1 == s2) return true;
		if((s1 == null) != (s2 == null)) return false;
		
		int len1 = s1.length();
		int len2 = s2.length();
		
		if(len1 != len2) return false;
		
		int len = len1;
		
		for(int i = 0; i < len; ++i)
		{
			if(s1.charAt(i) != s2.charAt(i))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Compares two sequences ignoring case.
	 * 
	 * @param s1 first string
	 * @param s2 second string
	 * @return a negative integer,positive integer, or zero if 
	 * <code>s1 < s2, s1 > s2, s1 = s2</code>, respectively ignoring case
	 */
	public static int caseInsensitiveCompareTo(CharSequence s1,CharSequence s2)
	{
		return Comparators.newCaseInsensitiveCharSequenceComparator().compare(s1,s2);
	}
	
	/**
	 * Compares two sequences.
	 * 
	 * @param s1 first string
	 * @param s2 second string
	 * @return a negative integer,positive integer, or zero if 
	 * <code>s1 < s2, s1 > s2, s1 = s2</code>, respectively
	 */
	public static int compareTo(CharSequence s1,CharSequence s2)
	{
		return Comparators.newCharSequenceComparator().compare(s1,s2);
	}
	
	/**
	 * Behaves like <code>String.startsWith</code> but ignores case.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return <code>true</code> if <code>s</code> starts with <code>seq</code> ignoring case and
	 *         <code>false</code> otherwise
	 */
	public static boolean caseInsensitiveStartsWith(CharSequence s,CharSequence seq)
	{
		return regionMatches(true,s, 0, seq, 0, seq.length());
	}
	
	/**
	 * Behaves like <code>String.startsWith</code> but ignores case.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex index in <code>s</code> to begin search
	 * @return <code>true</code> if <code>s</code> starts with <code>seq</code> from <code>fromIndex</code> 
	 *         ignoring case and <code>false</code> otherwise
	 */
	public static boolean caseInsensitiveStartsWith(CharSequence s,CharSequence seq,int fromIndex) {
		return regionMatches(true,s, fromIndex, seq, 0, seq.length());
	}
	
	/**
	 * Behaves like <code>String.startsWith</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return <code>true</code> if <code>s</code> starts with <code>seq</code>  and
	 *         <code>false</code> otherwise
	 */
	public static boolean startsWith(CharSequence s,CharSequence seq)
	{
		return regionMatches(false,s, 0, seq, 0, seq.length());
	}
	
	/**
	 * Behaves like <code>String.startsWith</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex index in <code>s</code> to begin search
	 * @return <code>true</code> if <code>s</code> starts with <code>seq</code> from <code>fromIndex</code> 
	 *         and <code>false</code> otherwise
	 */
	public static boolean startsWith(CharSequence s,CharSequence seq,int fromIndex) {
		return regionMatches(false,s, fromIndex, seq, 0, seq.length());
	}
	
	/**
	 * Behaves like <code>String.endsWith</code> but ignores case.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return <code>true</code> if <code>s</code> ends with <code>seq</code> ignoring case and
	 *         <code>false</code> otherwise
	 */
	public static boolean caseInsensitiveEndsWith(CharSequence s,CharSequence seq)
	{
		return regionMatches(true,s,s.length() - seq.length(),seq,0,seq.length());
	}
	
	/**
	 * Behaves like <code>String.endsWith</code> but ignores case and starts search backward from
	 * <code>fromIndex</code>
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex index in <code>s</code> to begin search

	 * @return <code>true</code> if <code>s</code> ends with <code>seq</code> ignoring case and
	 *         <code>false</code> otherwise
	 */
	public static boolean caseInsensitiveEndsWith(CharSequence s,CharSequence seq,int fromIndex)
	{
		return regionMatches(true,s,s.length() - seq.length() - fromIndex,seq,0,seq.length());
	}
	
	/**
	 * Behaves like <code>String.endsWith</code> but for <code>CharSequence</code>.
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @return <code>true</code> if <code>s</code> ends with <code>seq</code> and
	 *         <code>false</code> otherwise
	 */
	public static boolean endsWith(CharSequence s,CharSequence seq)
	{
		return regionMatches(false,s,s.length() - seq.length(),seq,0,seq.length());
	}
	
	/**
	 * Behaves like <code>String.endsWith</code> and starts search backward from
	 * <code>fromIndex</code>
	 * 
	 * @param s the string
	 * @param seq the sequence to test
	 * @param fromIndex index in <code>s</code> to begin search

	 * @return <code>true</code> if <code>s</code> ends with <code>seq</code> and
	 *         <code>false</code> otherwise
	 */
	public static boolean endsWith(CharSequence s,CharSequence seq,int fromIndex)
	{
		return regionMatches(false,s,s.length() - seq.length() - fromIndex,seq,0,seq.length());
	}
	
	
	/**
	 * Trims whitespace on <code>s</code> if it is not <code>null</code> and returns the trimmed string; otherwise,
	 * <code>null</code> is returned.
	 * 
	 * @param s the string to trim
	 * @return trimmed string or <code>null</code>
	 */
	public static String trim(String s)
	{
		return s != null ? s.trim() : null;
	}
	/**
	 * Trims whitespace on <code>s</code> and returns an empty string if
	 * <code>s</code> is <code>null</code>.
	 * 
	 * @param s the string to trim
	 * @return trimmed string
	 */
	public static String trimEmpty(String s)
	{
		if(s == null) return "";
		return s.trim();
	}
	/**
	 * Trims whitespace on <code>s</code> and returns <code>null</code> if
	 * <code>s</code> is <code>null</code> or empty.
	 * 
	 * @param s the string to trim
	 * @return trimmed string
	 */
	public static String trimNull(String s)
	{
		if(s == null) return null;
		s = s.trim();
		if(s.length() == 0) return null;
		
		return s;
	}
	
	/**
	 * Convenience method for splitting along spaces.  Equivalent to 
	 * <code>MiscUtils.split(s,"\\s+")</code>.
	 * 
	 * @param s the string
	 * @return the split string
	 */
	public static String [] spaceSplit(String s)
	{
		return MiscUtils.split(s, "\\s+");
	}
	
	public static int indexOf(boolean ignoreCase,CharSequence thisSeq,int toffset,
			char c,int len)
	{
		int to = toffset;
		// Note: toffset, ooffset, or len might be near -1>>>1.
		if ((toffset < 0) || (toffset > (long)thisSeq.length() - len)) 
		{
			return -1;
		}
		
		if(!ignoreCase)
		{
			while (len-- > 0) 
			{
				char c1 = thisSeq.charAt(to);
				if (c1 == c)
				{
					return to;
				}
				
				++to;
			}
		}
		else
		{
			char uc = Character.toUpperCase(c);
			char lc = Character.toLowerCase(c);
			
			while (len-- > 0) 
			{
				char c1 = thisSeq.charAt(to);
				if (c1 == c)
				{
					return to;
				}
				 // If characters don't match but case may be ignored,
				 // try converting both characters to uppercase.
				 // If the results match, then the comparison scan should
				 // continue.
				 char u1 = Character.toUpperCase(c1);
				 if (u1 == uc) 
				 {
				     return to;
				 }
				 // Unfortunately, conversion to uppercase does not work properly
				 // for the Georgian alphabet, which has strange rules about case
				 // conversion.  So we need to make one last check before
				 // exiting.
				 if (Character.toLowerCase(u1) == lc) 
				 {
				     return to;
			 	 }
				 
				 ++to;
			}
		}
		
		return -1;
	}
	
    public static boolean regionMatches(boolean ignoreCase, CharSequence thisSeq,int toffset,
            CharSequence otherSeq, int ooffset, int len) 
    {
		int to = toffset;
		int po = ooffset;
		// Note: toffset, ooffset, or len might be near -1>>>1.
		if ((ooffset < 0) || (toffset < 0) || (toffset > (long)thisSeq.length() - len) ||
		 (ooffset > (long)otherSeq.length() - len)) 
		{
			return false;
		}
		while (len-- > 0) 
		{
			char c1 = thisSeq.charAt(to++);
			char c2 = otherSeq.charAt(po++);
			if (c1 == c2)
			{
			 continue;
			}
			if (ignoreCase) 
			{
				 // If characters don't match but case may be ignored,
				 // try converting both characters to uppercase.
				 // If the results match, then the comparison scan should
				 // continue.
				 char u1 = Character.toUpperCase(c1);
				 char u2 = Character.toUpperCase(c2);
				 if (u1 == u2) 
				 {
				     continue;
				 }
				 // Unfortunately, conversion to uppercase does not work properly
				 // for the Georgian alphabet, which has strange rules about case
				 // conversion.  So we need to make one last check before
				 // exiting.
				 if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) 
				 {
				     continue;
			 	 }
			} // if
			return false;
		} // while
		
		return true;
	}
	
    
    /**
     * Returns the longest common substring between <code>s1</code> and <code>s2</code>.
     * 
     * @param s1 the first sequence
     * @param s2 the second sequence
     * @return the longest common substring
     */
    public static CharSequence greatestCommonSubstring(CharSequence s1,CharSequence s2)
    {
    	for(int i = 0, len = Math.min(s1.length(), s2.length()); i < len; ++i)
    	{
    		char c1 = s1.charAt(i);
    		char c2 = s2.charAt(i);
    		if(c1 != c2)
    			return s1.subSequence(0, i);
    	}
    	return s1.length() <= s2.length() ? s1 : s2;
    }
    
    /**
     * Returns the longest common substring between <code>s1</code> and <code>s2</code>, ignoring
     * character case.
     * 
     * @param s1 the first sequence
     * @param s2 the second sequence
     * @return the longest common substring ignoring case
     */
    public static CharSequence caseInsensitiveGreatestCommonSubstring(CharSequence s1,CharSequence s2)
    {
    	for(int i = 0, len = Math.min(s1.length(), s2.length()); i < len; ++i)
    	{
    		char c1 = s1.charAt(i);
    		char c2 = s2.charAt(i);
    		if(Character.toUpperCase(c1) != Character.toUpperCase(c2) &&
    		   Character.toLowerCase(c1) != Character.toLowerCase(c2))
    		{
    			return s1.subSequence(0, i);
    		}
    	}
    	return s1.length() <= s2.length() ? s1 : s2;
    }


    /**
     * Returns <code>s</code> if it is not <code>null</code> and <code>def</code>
     * otherwise.
     *
     * @param s the String
     * @param def default value to use if <code>s</code> is <code>null</code>
     */
    public static String nvl(String s,String def)
    {
        if(s == null) return def;
        return s;
    }


    private static String substr(String s,String sub,boolean before)
    {
        if(s == null)
            throw new NullPointerException("s");
        if(sub == null)
            throw new NullPointerException("sub");

        int index = indexOf(s,sub);
        if(index == -1)
            return null;

        int sublen = sub.length();

        if(before)
        {
            return index > 0 ? s.substring(0,index) : "";
        }
        else
        {
            return index + sublen < s.length() ? s.substring(index+sublen) : "";
        }


    }

    /**
     * Returns a substring of <code>s</code> where the index of the last character
     * in that substring is the character prior to the index of the first character
     * matched by <code>sub</code>
     * @param s the string
     * @param sub the substring to find
     * @return a substring of <code>s</code> before <code>sub</code> first occurs
     *         or <code>null</code> if no match occurs
     */
    public static String substrBefore(String s,String sub)
    {
        return substr(s,sub,true);
    }

    /**
     * Returns a substring of <code>s</code> where the index of the first character
     * in that substring is the character after the index of the last character
     * matched by <code>sub</code>
     * @param s the string
     * @param sub the substring to find
     * @return a substring of <code>s</code> before <code>sub</code> first occurs
     *         or <code>null</code> if no match occurs
     */
    public static String substrAfter(String s,String sub)
    {
        return substr(s,sub,false);
    }

    /**
     * Strips non-printable ascii characters from <code>s</code>.
     *
     * @param s the <code>StringBuilder</code>
     *
     * @return <code>s</code> with only printable ascii characters
     */
    public static void stripToPrintableAscii(StringBuilder s)
    {
        for(int i = s.length() - 1; i >= 0; --i)
        {
            char c = s.charAt(i);
            if(!isPrintableAscii(c))
                s.deleteCharAt(i);
        }
    }

    private static boolean isPrintableAscii(char c)
    {
        if(c >= 32 && c <= 126) return true;
        if(c > 127) return false;

        // c < 31 : only whitespace characters
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }


    /**
     * <code>String.replace</code> for <code>StringBuilder</code>.
     *
     * @param s the <code>StringBuilder</code>
     * @param sequence the sequence to replace
     * @param replacement the replacement sequence
     */
    public static void replace(StringBuilder s,String sequence,String replacement)
    {
        if(s == null)
            throw new NullPointerException("s");
        if(sequence == null)
            throw new NullPointerException("sequence");
        if(replacement == null)
            throw new NullPointerException("replacement");

        int index = 0;

        int seqLen = sequence.length();
        int repLen = replacement.length();

        while(index != -1 && index < s.length())
        {
            index = StringUtils.indexOf(s, sequence,index);
            if(index != -1)
            {
                s.replace(index, index + seqLen, replacement);
                index += repLen;
            }
        }
    }
    
    
	/**
	 * Formats a part of a name in standard proper noun case.  If the name contains mixed case then this method simply returns the 
	 * name as is instead of guessing this link McDonald.  <b>Note: This method expects only a part of the name: so first name by itself
	 * or last name by itself and will not format a full name with spaces.  If you need to format the full name of a member, use
	 * <code>MemberInfo.getFullName()</code>.
	 *   
	 * 
	 * @param name the name
	 * @return the formatted name
	 */
	public static String formatName(String name) {
		return upperToProper(name);
	}
	
	/**
	 * Formats a whole sentence in standard proper noun case.  If the name contains mixed case then this method simply returns the 
	 * name as is instead of guessing this link McDonald. Words in the sentence are separated by spaces
	 *   
	 * 
	 * @param name the name
	 * @return the formatted name
	 */
    public static String phraseUpperToProper(String sentence) {
    	if(sentence == null || sentence.length() <= 1) {
    		return sentence;
    	}
    	StringBuilder phrase = new StringBuilder(sentence.length());
    	
    	// beginning of next word
    	int j = 0;
    	// beginning of next scan
    	int k;
    	int len = sentence.length();
    	
    	// rebuild the word
    	for(int i = 0; i < len; ++i) {
    		k = i;
    		
    		while( i < len && Character.isWhitespace(sentence.charAt(i))) {
    			++i;
    		}
    		// whitespace encountered
    		if(i > k || i == len - 1) {
    			// capitalize word
    			if(j < k) {
    				String word = upperToProper(sentence.substring(j,k+1));
    				phrase.append(word);
    				// account for spaces
    				if(i < len - 1) {
		    			for(int m = 0, n = i - k; m < n; ++m) {
		    				phrase.append(' ');
		    			}
    				}
    			}
    			j = i;
    		}
    	} // for
    	
    	return phrase.toString();
    	
    }
	/**
	 * Formats a part of a name in standard proper noun case.  If the name contains mixed case then this method simply returns the 
	 * name as is instead of guessing this link McDonald.  <b>Note: This method expects only a part of the name: so first name by itself
	 * or last name by itself and will not format a full name with spaces.
	 *   
	 * 
	 * @param name the name
	 * @return the formatted name
	 */
    public static String upperToProper(String name) {
    	
		if(name == null || name.length() <= 1)
			return name;
		
		// trim the whitespace
		name = name.trim();
		
		boolean isLowerCase = false;
		boolean isUpperCase = false;
		
		// check for all uppercase - this is only time we will need to change case
		for(int i = 0, len = name.length(); i < len; ++i) {
			char c = name.charAt(i);
			
				if(Character.isWhitespace(c)) {
					return name;
				}
				else if(Character.isUpperCase(c)) {
					isUpperCase = true;
					// already contains lower case so give up and return original name
					if(isLowerCase)
						return name;
				}
				else if(Character.isLowerCase(c)) {
					isLowerCase = true;
					// already contains upper case so give up and return original name
					if(isUpperCase)
						return name;
				}
		}
		// name is all upper case, convert first character
		StringBuilder s = new StringBuilder(name.length());
		s.append(Character.toUpperCase(name.charAt(0)));
		// convert rest of name to lower case
		s.append(name.substring(1).toLowerCase());
		
		return s.toString();	
    }



}
