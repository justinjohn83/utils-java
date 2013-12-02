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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Misc utility methods.
 * 
 * @author Justin Montgomery
 * @version $Id: MiscUtils.java 2757 2011-04-12 23:46:27Z jmontgomery $
 *
 */
public final class MiscUtils 
{

	private MiscUtils() {}
	
	public static final String LINE_BREAK_REGEX = "\n|\r\n|\r";
	public static final String HTML_LINE_BREAK_REGEX = "<br\\s*/?\\s*>";
	
	private static final String HTML_LINE_BREAK_TAG = "<br>";
	private static final String HTML_REGEX = "(<[^<>/]*?>[^<>]*?<\\s*/[^<>/]*?>)|(<[^<>/]*?/\\s*>)";
	private static final Pattern LINE_BREAK_PATTERN;
	private static final Pattern HTML_LINE_BREAK_PATTERN;
	private static final Pattern HTML_PATTERN;
	private static final Pattern HTML_TAG_PATTERN;
	private static final Pattern ALL_LINE_BREAK_PATTERN;
	private static final int LIST_PERFORMANCE_THRESHOLD = 50;
	private static final String DEFAULT_WRAP_TEXT_SEP_REGEX = 
		"\\s|,|\\.|;|!|\\?";
	private static final String HTML_TAG_REGEX = "<.*?>";
	
	private static final Object stringPoolLock = new Object();
	private static final int INIT_STRING_POOL_SIZE = 10000;
	private static WeakHashMap<String,String> stringPool;
	
	private static final PrintStream SYS_OUT = System.out;
	private static final PrintStream SYS_ERR = System.err;
	
	static
	{
		LINE_BREAK_PATTERN = Pattern.compile(LINE_BREAK_REGEX);
		HTML_LINE_BREAK_PATTERN = Pattern.compile(HTML_LINE_BREAK_REGEX);
		HTML_PATTERN = Pattern.compile(HTML_REGEX,Pattern.DOTALL);
		HTML_TAG_PATTERN = Pattern.compile(HTML_TAG_REGEX);
		ALL_LINE_BREAK_PATTERN = Pattern.compile("(" + LINE_BREAK_REGEX + ")|(" + HTML_LINE_BREAK_REGEX + ")");
	}

	/**
	 * Replaces all line breaks in <code>str</code> with single
	 * space characters.
	 * @param str the string 
	 * @return the string with line breaks replaced with a single 
	 *         white space character
	 */
	public static String removeLineBreaks(String str)
	{
		if(str == null)
			return null;
		return LINE_BREAK_PATTERN.matcher(str).replaceAll(" ");
	}
	
	/**
	 * Returns <code>true</code> is <code>str</code> is a valid line terminator sequence
	 * and <code>false</code> otherwise.
	 * 
	 * @param str the input string
	 * @return <code>true</code> is <code>str</code> is a valid line terminator sequence
	 * and <code>false</code> otherwise
	 */
	public static boolean isLineTerminator(String str)
	{
		Matcher m = LINE_BREAK_PATTERN.matcher(str);
		return m.matches();
	}
	
	/**
	 * Returns the number of matches for the given sequence.
	 * 
	 * @param str the input string
	 * @param sequence the character sequence
	 * @return the number of matches
	 */
	public static int sequenceCount(String str,String sequence)
	{
		int count = 0;
		int i = 0;
		int len = str.length();
		int seqLen = sequence.length();
		while(i < len && (i = str.indexOf(sequence, i)) != -1)
		{
			++count;
			i += seqLen;
		}
		return count;
	}
	/**
	 * Returns the number of matches for the given regular expression.
	 * 
	 * @param str the input string
	 * @param regex the regular expression
	 * @return the number of matches
	 */
	public static int matchCount(String str,String regex)
	{
		return matchCount(str,Pattern.compile(regex));

	}
	/**
	 * Returns the number of matches for the given pattern.
	 * 
	 * @param str the input string
	 * @param p the pattern
	 * @return the number of matches
	 */
	public static int matchCount(String str,Pattern p)
	{
		Matcher m = p.matcher(str);
		int count = 0;
		while(m.find())
			++count;
		return count;
	}
	
	/**
	 * Returns the longest line of text in <code>str</code>
	 * @param str the string
	 * @return the longest line
	 */
	public static String getLongestLine(String str)
	{
		String [] split = str.split(LINE_BREAK_REGEX);
		int longestLen = 0;
		String longest = null;
		
		for(String s : split)
		{
			if(!MiscUtils.isEmpty(s))
			{
				int len = s.length();
				if(len > longestLen)
				{
					longestLen = len;
					longest = s;
				}
			}
		}
		return longest;
	}

	/**
	 * Returns the count of <code>seq</code> in <code>input</code>
	 * @param input the input <code>String</code>
	 * @param seq the sequence to find counts for in <code>input</code>
	 * @return the number of occurrences of <code>seq</code> in
	 *         <code>input</code>
	 */
	public static int getCount(String input,String seq)
	{
		return sequenceCount(input,seq);
	}

	/**
	 * Returns the maximum string length of the list of strings
	 * @param list <code>List</code> of strings
	 * @return the maximum length of any string in <code>list</code>
	 */
	public static int getMaxStrWidth(List<String> list)
	{
		int max = 0;
		for(String s: list )
		{
			if(s.length() > max)
				max = s.length();
		}
		return max;
	}

	

	/**
	 * Returns a wrapped version of <code>text</code> where each
	 * line is at most <code>length</code> characters long as long
	 * as an individual token in <code>text</code> is not longer than
	 * <code>length</code>.
	 * 
	 * @param text the text to wrap
	 * @param length the maximum length of each line
	 * 
	 * @return the wrapped text
	 */
	public static String wrapText(String text,int length)
	{
		return wrapText(text,length,false,false,DEFAULT_WRAP_TEXT_SEP_REGEX);
	}
	/**
	 * Returns a wrapped version of <code>text</code> where each
	 * line is at most <code>length</code> characters long as long
	 * as an individual token in <code>text</code> is not longer than
	 * <code>length</code>.
	 * 
	 * @param text the text to wrap
	 * @param length the maximum length of each line
	 * @param htmlEncode <code>true</code> to encode the line breaks as html and
	 *                   <code>false</code> otherwise
	 * @param textHasHtml <code>true</code> if <code>text</code> is already html
	 *                     and <code>false</code> otherwise
	 *                     
	 * @return the wrapped text
	 */
	public static String wrapText(String text,int length,boolean htmlEncode,
			boolean textHasHtml)
	{
		return wrapText(text,length,htmlEncode,textHasHtml,DEFAULT_WRAP_TEXT_SEP_REGEX);
	}
	/**
	 * Returns a wrapped version of <code>text</code> where each
	 * line is at most <code>length</code> characters long as long
	 * as an individual token in <code>text</code> is not longer than
	 * <code>length</code>.
	 * 
	 * @param text the text to wrap
	 * @param length the maximum length of each line
	 * @param htmlEncode <code>true</code> to encode the line breaks as html and
	 *                   <code>false</code> otherwise
	 * @param textHasHtml <code>true</code> if <code>text</code> is already html
	 *                     and <code>false</code> otherwise
	 * @param sepRegex regular expression for denoting end of word characters
	 * 
	 * @return the wrapped text
	 */
	public static String wrapText(String text,int length,boolean htmlEncode,
			boolean textHasHtml,String sepRegex)
	{
		if(text == null)
			throw new NullPointerException("text");
		if(length <= 0)
			throw new IllegalArgumentException("length <= 0");
		
		Pattern pattern;
		
		// must ensure that all line breaks are replaced with <br>
		if(htmlEncode)
		{
			sepRegex += "|" + HTML_LINE_BREAK_REGEX;
			text = text.replaceAll(LINE_BREAK_REGEX,HTML_LINE_BREAK_TAG);
			pattern = HTML_LINE_BREAK_PATTERN;
		}
		else
		{
			sepRegex += "|" + LINE_BREAK_REGEX;
			pattern = LINE_BREAK_PATTERN;
		}
		// remove all the current line breaks
		//text = text.replaceAll(LINE_BREAK_REGEX, "");
		int textLen = text.length();
		StringBuilder str = new StringBuilder((int)(textLen*1.5));
		
		// prepend html tag if using html
		if(htmlEncode && !textHasHtml)
			str.append("<html>");
		
		int index = 0;
		int currLen = 0;
		int [] outHtmlIndices = new int[2];
		
		while(index < textLen)
		{
			StringBuilder token = new StringBuilder();
			int next = MiscUtils.getNextWord(text,index,token,sepRegex);
			boolean tokenIsHtml = false;
			// can't split across an html token
			if(textHasHtml)
			{
				// see if can get an html string starting at next
				String tempToken = 
					MiscUtils.getNextHtmlWord(text,next,outHtmlIndices);
				if(outHtmlIndices[0] == next)
				{
					token = new StringBuilder(tempToken);
					tokenIsHtml = true;
				}
			}
			
			int end = next + token.length();
			int len = end - index;
			String subText = text.substring(index,end);
			Matcher matcher = pattern.matcher(subText);
			
			// take into account any existing line breaks
			int lineEndingMatch = -1;
			int lineBreakCount = 0;
			while(matcher.find())
			{
				lineEndingMatch = matcher.end();
				++lineBreakCount;
			}
			// lineEndingMatch will contain index after last match
			if(lineEndingMatch > -1)
			{
				// convert from subText indices to text indices
				lineEndingMatch += index;
				// Set current length to length of characters after last line break
				currLen = end - lineEndingMatch;
				// subtract out length of html tag text since it is not displayed
				if(textHasHtml && lineEndingMatch < end)
					currLen -= getHtmlMarkupLength(text.substring(lineEndingMatch,end));
			}
			else
			{
				currLen += len;
				if(textHasHtml)
					currLen -= getHtmlMarkupLength(subText);
			}	
			
			// just append the whole text, including spaces
			// if the length is not beyond the limit
			if(currLen <= length)
			{
				str.append(subText);
			}
			else //add a line break and then the plain token
			{
				// don't split an html tag across multiple lines since will
				// encode another "\n" in the text causing misinterpretation
				// of the tag
				if(htmlEncode)
					str.append(HTML_LINE_BREAK_TAG);
				else
					str.append('\n');
				// don't use the straight token since it removes all sepRegex characters
				// but really only want to remove leading spaces when doing a line break
				// so simply remove leading spaces from the "subText" string formed from
				// index to end.  The leading spaces in an html token is the first non-html
				// part
				String strToken;
				if(tokenIsHtml)
					strToken = htmlReplaceAll(subText,"\\s","");
				else
					strToken = subText.replaceAll("\\s", "");
				str.append(strToken);
				currLen = strToken.length();
				if(textHasHtml)
					currLen -= getHtmlMarkupLength(strToken);
			}
			index += len;
		}
		
		if(htmlEncode && !textHasHtml)
		{
			// append html footer
			str.append("</html>");
		}
		return str.toString();
		
	}

	/**
	 * Retrieves the next word in <code>str</code>.
	 * 
	 * @param str the string
	 * @param index index of start search
	 * @param outToken holds new token on return if non-null
	 * @param ignoreRegex characters to skip when looking for next word 
	 * @return index in raw of start of next word
	 */
	public static int getNextWord(String str,int index,StringBuilder outToken,String ignoreRegex)
	{
		if(index >= str.length())
			return str.length();
		
		String rem = str.substring(index,str.length());
		Scanner s = new Scanner(rem);
		s.useDelimiter(ignoreRegex);
		String token = null;
		
		while(s.hasNext() && (token = s.next()).length() == 0);
		
		if(token != null && token.length() != 0)
		{
			if(outToken != null)
				outToken.append(token);
			return str.indexOf(token,index);
		}
		else
			return str.length();
		//end of string
		//if(!s.hasNext())
		//	return str.length();
		//else
		//return str.indexOf(token,index);
	}
	
	/**
	 * Finds the next html sequence (start tag to end tag) in <code>str</code> if
	 * it exists.
	 * 
	 * @param str the text string
	 * @param index the index to start search in <code>str</code>
	 * @param outRange an array of at least 2 to hold start index of returned string
	 *        in <code>str</code> in [0] and the end index of returned string
	 *        in <code>str</code> in [1]
	 * @return the html word or <code>null</code> if one is not found
	 */
	public static String getNextHtmlWord(String str,int index,int [] outRange)
	{
		if(str == null)
			throw new NullPointerException("str");
		if(index < 0 || index > str.length())
			throw new IllegalArgumentException("index="+index);
		if(outRange != null && outRange.length < 2)
			throw new IllegalArgumentException("outRange.length = " + outRange.length + " < 2");
		Pattern p = HTML_PATTERN;
		Matcher m = p.matcher(str.substring(index));
		if(m.find())
		{
			if(outRange != null)
			{
				outRange[0] = m.start() + index;
				outRange[1] = m.end() + index;
			}
			return m.group();
		}
		else
		{
			if(outRange != null)
				outRange[0] = outRange[1] = -1;
			return null;
		}
			
		
	}

	/**
	 * Checks two objects for equality in a "null-safe" fashion. This 
	 * method <b><i>cannot</i></b> be used as a substitute for overriding equals
	 * since first.equals(second) may be called by this method.
	 *  
	 * @param first first object
	 * @param second second object
	 * @return <code> true</code> if <code>first</code> equals second
	 *         and <code>false</code> otherwise
	 */
	public static boolean safeEquals(Object first,Object second)
	{
		return first == null ? second == null : first.equals(second);
	}

	/**
	 * Compares two objects in an exception-safe fashion.  Although in general a
	 * <code>NullPointerException</code> should be thrown if at least one object involved
	 * in comparison is <code>null</code>, some implementations may not desire this for
	 * their constituent parts.  In this implementation, <code>null</code> is considered
	 * less than all <code>non-null</code> objects if <code>nullLessThanAll</code> is 
	 * <code>true</code>, and it will be greater than all <code>non-null</code> objects 
	 * otherwise.  If <code>first</code> and <code>second</code> do not implement 
	 * <code>Comparable</code>, then equality is first checked and if false their hashcodes are compared as
	 * returned by <code>System.identityHashCode</code>.
	 * Since equal objects have equal hash codes and hash codes must be consistent, this method
	 * is deterministic.
	 * 
	 * This method <b><i>cannot</i></b> be used as a substitute for implementing <code>CompareTo</code>
	 * since first.compareTo(second) may be called by this method.
	 * 
	 * @param first first object to compare
	 * @param second second object to compare
	 * @return negative integer,zero, positive integer if <code>first</code> is 
	 *         less than, equal to, or greater than <code>second</code>, respectively
	 */
	public static int safeCompareTo(Object first,Object second,boolean nullLessThanAll)
	{
		return new GeneralComparator(nullLessThanAll).compare(first, second);
	}
	
	
	/**
	 * Equivalent to <code>safeCompareTo(first,second,true)</code>.
	 * 
	 * @param first first object to compare
	 * @param second second object to compare
	 * @return negative integer,zero, positive integer if <code>first</code> is 
	 *         less than, equal to, or greater than <code>second</code>, respectively
	 */
	public static int safeCompareTo(Object first,Object second)
	{
		return safeCompareTo(first,second,true);
	}
	
	/**
	 * Compares two objects in an exception-safe fashion.  Although in general a
	 * <code>NullPointerException</code> should be thrown if at least one object involved
	 * in comparison is <code>null</code>, some implementations may not desire this for
	 * their constituent parts.  In this implementation, <code>null</code> is considered
	 * less than all <code>non-null</code> objects if <code>nullLessThanAll</code> is 
	 * <code>true</code>.
	 * 
	 * @param first first object to compare
	 * @param second second object to compare
	 * @param comp the <code>Comparator</code> to use
	 * @return negative integer,zero, positive integer if <code>first</code> is 
	 *         less than, equal to, or greater than <code>second</code>, respectively
	 */
	public static <T> int safeCompare(T first,T second,Comparator<? super T> comp,boolean nullLessThanAll)
	{
		if(first == second) return 0;
		if(first == null) return nullLessThanAll ? -1 : 1;
		if(second == null) return nullLessThanAll ? 1 : -1;
		
		return comp.compare(first, second);
	}
	
	/**
	 * Equivalent to <code>safeCompare(first,second,comp,true)</code>.
	 * 
	 * @param first first object to compare
	 * @param second second object to compare
	 * @param comp the <code>Comparator</code> to use
	 * @return negative integer,zero, positive integer if <code>first</code> is 
	 *         less than, equal to, or greater than <code>second</code>, respectively
	 */
	public static <T> int safeCompare(T first,T second,Comparator<? super T> comp)
	{
		return safeCompare(first,second,comp,true);
	}
	
	/**
	 * Returns the hashcode of <code>obj</code> by invoking <code>obj.hashCode</code> if
	 * <code>obj</code> is not <code>null</code> and returning <code>0</code> otherwise.
	 * 
	 * @param obj the <code>Object</code>
	 * @return a hashcode for <code>obj</code>
	 */
	public static int safeHashCode(Object obj)
	{
		return obj != null ? obj.hashCode() : 0;
	}




        /**
         * Returns 0 if <code>isEmpty(s)</code> returns <code>true</code> and
         * the length of <code>s</code> otherwise.
         * 
         * @param s the character sequence.
         * @return the size
         */
        public static int getSize(CharSequence s)
        {
            return MiscUtils.isEmpty(s) ? 0 : s.length();
        }
        /**
         * Returns 0 if <code>collection</code> is <code>null</code> or empty and
         * <code>collection.size()</code> otherwise.
         *
         * @param collection the <code>Collection</code>
         * @return the size
         */
        public static int getSize(Collection<?> collection)
        {
            return collection != null ? collection.size() : 0;
        }

        /**
         * Returns 0 if <code>array</code> is <code>null</code> or empty and
         * <code>array.length</code> otherwise.
         *
         * @param collection the <code>Collection</code>
         * @return the size
         */
        public static int getSize(Object[] array)
        {
            return array != null ? array.length : 0;
        }

        /**
         * Returns 0 if <code>m</code> is <code>null</code> or empty and
         * <code>m.size()</code> otherwise.
         *
         * @param m the <code>Map</code>
         * @return the size
         */
        public static int getSize(Map<?,?> m)
        {
            return m != null ? m.size() : 0;
        }

       /**
	 * Returns <code>true</code> if <code>value</code> is <code>null</code> or
	 * its trimmed length is zero.
	 *
	 * @param value the <code>String</code>
	 * @return <code>true</code> if empty and <code>false</code> otherwise
	 */
        // RETAINED FOR COMPATABILITY
	public static boolean isEmpty(String value)
        {
            return isEmpty((CharSequence)value);
        }
    /**
	 * Returns <code>true</code> if <code>value</code> is <code>null</code> or
	 * its trimmed length is zero.
	 *
	 * @param value the <code>String</code>
	 * @return <code>true</code> if empty and <code>false</code> otherwise
	 */
	public static boolean isEmpty(CharSequence value)
	{
		if(value == null)
			return true;
		for(int i = 0, len = value.length(); i < len; ++i)
			if(value.charAt(i) > ' ')
				return false;
		return true;
	}

	/**
	 * Returns <code>true</code> if <code>collection</code> is <code>null</code>
	 * or empty.
	 * 
	 * @param collection the <code>Collection</code>
	 * @return <code>true</code> if empty and <code>false</code> otherwise
	 */
	public static boolean isEmpty(Collection<?> collection)
	{
		return collection == null || collection.isEmpty();
	}
	
	/**
	 * Returns <code>true</code> if <code>m</code> is <code>null</code> or
	 * empty.
	 * 
	 * @param m the <code>Map</code>
	 * @return <code>true</code> if empty and <code>false</code> otherwise
	 */
	public static boolean isEmpty(Map<?,?> m)
	{
		return m == null || m.isEmpty();
	}
	
	/**
	 * Returns <code>true</code> if <code>arr</code> is <code>null</code> or
	 * has length 0.
	 * 
	 * @param arr the array
	 * @return <code>true</code> if empty and <code>false</code> otherwise
	 */
	public static boolean isEmpty(Object [] arr)
	{
		return arr == null || arr.length == 0;
	}

	/**
	 * Returns the name of the class of <code>obj</code> in null-safe fashion.
	 * If <code>obj</code> is <code>null</code> the string "null" is returned.
	 * 
	 * @param obj the <code>Object</code>
	 * @return the class name of <code>obj</code> or "null" if <code>obj</code>
	 *         is <code>null</code>
	 */
	public static String getClassName(Object obj)
	{
		return obj != null ? obj.getClass().getName() : "null";
	}

	/**
	 * Makes a deep copy of the specified <code>col</code>.
	 * If the elements in the collection have a publicly accessible clone
	 * method or are <code>Serializable</code>, then that method is called and a "deep copy" of that element
	 * is added to the collection; otherwise, the original element in 
	 * <code>col</code> is simply added.
	 * 
	 * <b><i> Warning: invocation of this method may be slow since it relies on 
	 *        reflection</i></b>.
	 * 
	 * @param col the input collection
	 * @Param outputType the class of the copy
	 * @return the copied <code>Collection</code>
	 */
	public static <T,U extends Collection<T>,V extends Collection<T>> V
		deepCollectionCopy(U col,Class<V> outputType)
	{
		return deepCollectionCopy(col,outputType,null);
	}
	
	
	private static Map<Object,Object> createOrigToCopyMap(int size)
	{
		return new IdentityHashMap<Object,Object>(size);
	}
	@SuppressWarnings("unchecked")
	private static <T,U extends Collection<T>,V extends Collection<T>> V
		deepCollectionCopy(U col,Class<V> outputType,Map<Object,Object> origToCopy)
	{
		if(col == null)
			return null;
		if(outputType == null)
			throw new NullPointerException("outputType");
		if(origToCopy == null)
			origToCopy = createOrigToCopyMap(col.size());
		
		V output = null;
		boolean unmodifiableList = false;
		boolean unmodifiableSet = false;
		boolean unmodifiableCollection  = false;
		
		// make copy have same class as original
		// all collection classes should have no argument constructors
		try
		{
			output = outputType.newInstance();
		}
		catch(Exception e) 
		{ 
			if(col.getClass() != outputType)
			{
				throw new IllegalArgumentException("outputType = " 
						+ outputType + ";col: " + col);
			}
			// attempt to use regular copy method that includes cloning and serialization
			output = (V)copy(col);
			if(output == null)
			{
				throw new IllegalArgumentException("outputType = " 
						+ outputType + ";col: " + col);
			}
			// clear the collection
			try
			{
				output.clear();
			}
			// unmodifiable collection
			catch(UnsupportedOperationException uoe)
			{
				if(output instanceof List)
				{
					unmodifiableList = true;
					output = (V)new ArrayList<T>(col.size());
				}
				else if(output instanceof Set)
				{
					unmodifiableSet = true;
					output = (V)CollectionUtils.createLinkedHashSet(col.size(), 
									CollectionUtils.LOAD_FACTOR);
				}
				else
				{
					unmodifiableCollection = true;
					output = (V)new ArrayList<T>(col.size());
				}
			}
		}
		
		for(T elm : col)
		{
			T copy = null;
			if(elm != null)
			{
				if(!origToCopy.containsKey(elm))
				{
					copy = copyContainerElement(col,output,origToCopy,elm);
					origToCopy.put(elm, copy);
				}
				else
					copy = (T)origToCopy.get(elm);
			}
			if(copy != null)
				output.add(copy);
			else
				output.add(elm);
		}
		if(unmodifiableList)
			output = (V)Collections.unmodifiableList((List)output);
		else if(unmodifiableSet)
			output = (V)Collections.unmodifiableSet((Set)output);
		else if(unmodifiableCollection)
			output = (V)Collections.unmodifiableCollection(output);
		
		return output;
	}
	
	/**
	 * Makes a deep copy of the specified <code>arr</code>.
	 * If the elements in the array have a publicly accessible clone
	 * method or are <code>Serializable</code>, then that method is called and a "deep copy" of that element
	 * is added to the collection; otherwise, the original element in 
	 * <code>arr</code> is simply added.
	 * 
	 * <b><i> Warning: invocation of this method may be slow since it relies on 
	 *        reflection</i></b>.
	 * 
	 * @param arr the input array
	 * @return the copied array
	 */
	public static <T> T[] deepArrayCopy(T[] arr)
	{
		return deepArrayCopy(arr,null);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] deepArrayCopy(T[] arr,Map<Object,Object> origToCopy)
	{
		if(arr == null) return null;
		if(origToCopy == null)
			origToCopy = createOrigToCopyMap(arr.length);
		// create copy of array
		T [] output = (T[])Array.newInstance(arr.getClass().getComponentType(), arr.length);
		
		// now create deep copies of its elements
		for(int i = 0; i < output.length; ++i)
		{
			T elm = arr[i];
			T elmCopy = null;
			
			if(elm != null)
			{
				if(!origToCopy.containsKey(elm))
				{
					elmCopy = copyContainerElement(arr,output,origToCopy,elm);
					origToCopy.put(elm, elmCopy);
				}
				else
					elmCopy = (T)origToCopy.get(elm);
			}
			output[i] = elmCopy != null ? elmCopy : elm;
		}
		return output;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private static <T> T copyContainerElement(Object origCont,Object newCont,
			Map<Object,Object> origToCopy,T elm)
	{
		T copy;
		// make deep recursive copy unless elm is the collection itself
		if(elm != origCont)
		{
			
			if(elm instanceof Collection)
			{
				copy = (T)deepCollectionCopy((Collection)elm,
						(Class<? extends Collection>)elm.getClass(),origToCopy);
			}
			else if(elm instanceof Map)
			{
				copy = (T)deepMapCopy((Map)elm,(Class<? extends Map>)elm.getClass(),
						origToCopy);
			}
			else if(elm instanceof Object[])
			{
				copy = (T)deepArrayCopy((T[])elm,origToCopy);
			}
			else 
				copy = MiscUtils.copy(elm);
		}
		else // since original elm pointed to origCont, copy of elm will point to copy of container
			return (T)newCont;
		
		return copy;
	}
	
	/**
	 * Makes a deep copy of the specified <code>col</code>.
	 * If the elements in the collection have a publicly accessible clone
	 * method or are <code>Serializable</code>, then that method is called and a "deep copy" 
	 * of that element is added to the collection; otherwise, the original element in 
	 * <code>col</code> is simply added.
	 * 
	 * <b><i> Warning: invocation of this method may be slow since it relies on 
	 *        reflection</i></b>.
	 * 
	 * @param col the input collection
	 * @return the copied <code>Collection</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T,U extends Collection<T>> U deepCollectionCopy(U col)
	{
		return (U)deepCollectionCopy(col,col != null ? col.getClass() : null);
	}

	/**
	 * Attempts to make a deep copy of the specified <code>obj</code> by invoking
	 * that object's clone method. If <code>obj</code> does not have a publicly
	 * accessible clone method or the invocation of that method throws an
	 * exception, then if <code>obj</code> is <code>Serializable</code> a copy using 
	 * serialization is attempted; otherwise, <code>null</code> is returned.  Also,
	 * if the <code>obj</code> is immutable as designated by implementing
	 * {@link Mutability} and having <code>Mutability.isMutable()</code> return
	 * <code>false</code>, or if it is a <code>String</code>,<code>Number</code>,or
	 * <code>Class</code>, then <code>null</code> is also returned because these objects
	 * should not be copied.
	 * 
	 * <b><i> Warning: invocation of this method may be slow since it relies on 
	 *        reflection</i></b>.
	 *        
	 * @param obj the object to copy
	 * @return a copy of the object or <code>null</code> if the object was not
	 *         be copied
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(T obj)
	{
		boolean tryAgain = true;
	    if(obj == null)
	    	return null; //throw new NullPointerException("obj");
		Class<?> clazz = obj.getClass();
		// don't even try to deep copy immutable Strings,numbers,or classes, or
		// classes designated as immutable
		if(obj instanceof Mutability)
		{
			// make a deep copy of the wrapper
			// unlike the shallow copy made by its clone method
			if(obj instanceof Wrapper)
			{
				Wrapper<?> w = (Wrapper<?>)obj;
				Object wo = w.get();
				Object wc = copy(wo);
				if(wc != null)
					return (T)new Wrapper(wc,w.isMutable());
				else
					return (T)new Wrapper(wo,w.isMutable());
			}
			else if(!((Mutability)obj).isMutable())
				return null;
		}
		else if(
		   clazz == Class.class  ||
		   clazz == String.class || obj instanceof Number)
			
		{
			// don't copy these objects
			return null;
		}
		
		// if element if cloneable, then clone it
		if(Cloneable.class.isInstance(obj))
		{
			Method m;
			try
			{
				m = clazz.getMethod("clone",(Class[])null);
				return (T)m.invoke(obj, (Object[])null);
			}
			// CloneNotSupportedException
			catch(Exception e){}
		}
		// if element is serializable, then serialize it and then reconstruct it to get a copy
		if(tryAgain && obj instanceof Serializable)
		{
			try
			{
				return ByteUtils.serializationClone(obj);
			}
			catch(Exception e){}
		}
		//return copy0(obj);
		return null;
	}

//	@SuppressWarnings("unchecked")
//	private static <T> T copy0(T obj)
//	{
//		Class<?> clazz = obj.getClass();
//		try
//		{
			// this will only call copy constructors that take the specific implementation
			// class as an argument.  
			// if a copy constructor takes an interface or superclass type then that
			// method won't be found
//			Constructor<?> c = clazz.getConstructor(clazz);
//			return (T)c.newInstance(obj);
//		}
//		catch(Exception e)
//		{
//			return null;
//		}
//	}

	
	@SuppressWarnings("unchecked")
	private static <U,V,S extends Map<U,V>, T extends Map<U,V>> T deepMapCopy(S map,
			Class<T> outputClass,Map<Object,Object> origToCopy)
	{
		if(map == null)
			return null;
		if(outputClass == null)
			throw new NullPointerException("outputClass");
		if(origToCopy == null)
			origToCopy = createOrigToCopyMap(map.size());
		T output = null;
		// make copy have same class as original
		// all collection classes should have no argument constructors
		boolean unmodifiableMap = false;
		
		try
		{
			output = outputClass.newInstance();
		}
		catch(Exception e) 
		{
			if(map.getClass() != outputClass)
				throw new IllegalArgumentException("outputClass=" + outputClass + ";map: " + map); 
			// attempt to use regular copy method that includes cloning and serialization
			output = (T)copy(map);
			if(output == null)
				throw new IllegalArgumentException("outputClass=" + outputClass + ";map: " + map);
			// clear the map
			try
			{
				output.clear();
			}
			// unmodifiable map
			catch(UnsupportedOperationException uoe)
			{
				unmodifiableMap = true;
				output = (T)CollectionUtils.createLinkedHashMap(map.size(), 
						CollectionUtils.LOAD_FACTOR);
			}
		}
		
		for(Map.Entry<U,V> E : map.entrySet())
		{
		    // attempt to copy the key and value and insert the copies
			U key = E.getKey();
			V value = E.getValue();
			U keyCopy = null;
			V valueCopy = null;
			if(key != null)
			{
				if(!origToCopy.containsKey(key))
				{
					keyCopy = copyContainerElement(map,output,origToCopy,key);
					origToCopy.put(key, keyCopy);
				}
				else
					keyCopy = (U)origToCopy.get(key);
			}
			if(value != null)
			{
				if(!origToCopy.containsKey(value))
				{
					valueCopy = copyContainerElement(map,output,origToCopy,value);
					origToCopy.put(value, valueCopy);
				}
				else
					valueCopy = (V)origToCopy.get(value);
			}
			
			output.put(keyCopy != null ? keyCopy : key,
					valueCopy != null ? valueCopy : value);
		}
			
		if(unmodifiableMap)
			output = (T)Collections.unmodifiableMap(output);
		
		return output;
	}
	
	/**
	 * Makes a deep copy of the specified <code>map</code>.
	 * If the keys and values in the map have a publicly accessible clone
	 * method or are <code>Serializable</code>, then that method is called and a "deep copy" of that element
	 * is added to the map; otherwise, the original element in 
	 * <code>map</code> is simply added.
	 * 
	 * <b><i> Warning: invocation of this method may be slow since it relies on 
	 *        reflection</i></b>.
	 * 
	 * @param map the input map
	 * @param outputClass the type of the output map
	 * @return the copied <code>map</code>
	 */
	public static <U,V,S extends Map<U,V>, T extends Map<U,V>> T deepMapCopy(S map,
			Class<T> outputClass)
	{
		return deepMapCopy(map,outputClass,null);
	}
	
	/**
	 * Makes a deep copy of the specified <code>map</code>.
	 * If the keys and values in the map have a publicly accessible clone
	 * method or are <code>Serializable</code>, then that method is called and 
	 * a "deep copy" of that element is added to the map; otherwise,
	 *  the original element in <code>map</code> is simply added.
	 * 
	 * <b><i> Warning: invocation of this method may be slow since it relies on 
	 *        reflection</i></b>.
	 * 
	 * @param map the input map
	 * @return the copied <code>map</code>
	 */
	@SuppressWarnings("unchecked")
	public static <U,V,T extends Map<U,V>> T deepMapCopy(T map)
	{
		return (T)deepMapCopy(map,map != null ? map.getClass() : null);
	}

	/**
	 * Convenience method for closing streams that swallows the <code>IOException</code>
	 * that could be thrown during closing.
	 * 
	 * @param closeable the <code>Closeable</code>: 
	 *                  <code>InputStream,OutputStream,Reader,Writer</code>
	 */
	public static void closeStream(Closeable closeable)
	{
		if(closeable != null)
		{
			try { closeable.close(); }
			catch(IOException e) {}
		}
	}
	
	/**
	 * Retained split splits the string about whitespace, but keeps the whitespace
	 * in the returned results.  The whitespace is added to the previous token in the list.
	 * If there is no previous token, then the whitespace is pre-pended to the first
	 * token in the list.  If <code>s</code> contains only whitespace, then <code>s</code>
	 * is simply returned in a list of size 1.
	 * 
	 * @param str the input string
	 * @return a <code>List</code> of tokens with their whitespace retained
	 * @throws NullPointerException if <code>str</code> is <code>null</code>
	 */
	public static List<String> retainedSpaceSplit(String str)
	{
		if(str == null)
			throw new NullPointerException("str");
		if(isEmpty(str))
			return Arrays.asList(str);
		
		Matcher m = Pattern.compile("\\s").matcher(str);
		List<String> matches = new ArrayList<String>();
		
		// create the initial match list
		int index = 0;
		while(m.find())
		{
			matches.add(str.substring(index,m.end()));
			index = m.end();
		}
		// must add rest of input after match
		if(index < str.length())
			matches.add(str.substring(index,str.length()));
		
		// copy into linked list for efficient removal operations
		if(matches.size() > LIST_PERFORMANCE_THRESHOLD)
			matches = new LinkedList<String>(matches);
		
		// fix up 
		for(ListIterator<String> it = matches.listIterator(); it.hasNext(); )
		{
			String s = it.next();
			// all whitespace
			if(s.trim().length() == 0)
			{
				it.previous();
				if(it.hasPrevious())
				{
					s = it.previous() + s; 
					it.set(s);
					// first next returns previous again
					it.next();
					it.next();
					// remove the entry pointed to by original call to next
					it.remove();
				}
				else // whitespace is first match, so prepend it to next instead of previous
				{
					// get original next since called previous before
					it.next();
					if(it.hasNext())
					{
						// remove this entry since all whitespace
						it.remove();
						it.set(s + it.next());
						// allow the next entry to be processed in next iteration
						// in case it is all whitespace and must be moved to 
						// a future entry
						it.previous();
					}
					// else only input will be the single whitespace
					// shouldn't happen since return at method start in this case
				}
			} //end if
		} //end for
		
		return matches;
	}
	
	/**
	 * Trims <code>s</code> and returns the start and end index in <code>s</code>
	 * of the trimmed substring.  {@code outIndices[0]} holds the start index and 
	 * {@code outIndices[1]} holds the end index, such that the returned string 
	 * is equal to {@code s.substring(outIndices[0],outIndices[1])}.
	 * 
	 * @param s the input string
	 * @param outIndices array of length at least two to hold start and end indices
	 * @return the trimmed string
	 */
	public static String getTrimmedIndices(String s,int [] outIndices)
	{
		if(s == null)
			throw new NullPointerException("s");
		if(outIndices == null)
			throw new NullPointerException("outIndices");
		if(outIndices.length < 2)
		{
			throw new IllegalArgumentException("outIndices.length = " +
					outIndices.length + " < 2");
		}
		String trim = s.trim();
		int trimLen = trim.length();
		
		// get the start index
		if(trimLen > 0)
		{
			char c = trim.charAt(0);
			outIndices[0] = s.indexOf(c);
			
			// get the end index
			if(trimLen > 1)
			{
				c = trim.charAt(trimLen - 1);
				outIndices[1] = s.lastIndexOf(c) + 1;
			}
			else
				outIndices[1] = outIndices[0] + 1;
		}
		else
			outIndices[0] = outIndices[1] =  0;
		

		return trim;
	}
	
	
	/**
	 * Returns a hash code for a boolean.
	 * 
	 * @param b the boolean
	 * @return the hashcode for <code>b</code>
	 */
	public static int hashBoolean(boolean b)
	{
		return b ? 1 : 0;
	}
	/**
	 * Returns a hash code for a long.
	 * 
	 * @param l the long
	 * @return the hashcode for <code>l</code>
	 */
	public static int hashLong(long l)
	{
		return (int)(l ^ (l >>> 32));
	}
	/**
	 * Returns a hash code for a float.
	 * 
	 * @param f the float
	 * @return the hashcode for <code>f</code>
	 */
	public static int hashFloat(float f)
	{
		return Float.floatToIntBits(f);
	}
	
	/**
	 * Returns a has code for a double.
	 * 
	 * @param d the double
	 * @return the hashcode for <code>d</code>
	 */
	public static int hashDouble(double d)
	{
		return hashLong(Double.doubleToLongBits(d));
	}

	/**
	 * Returns the total length of all the html markup in 
	 * <code>str</code>.
	 * @param str the input string
	 * @return the number of markup characters
	 */
	public static int getHtmlMarkupLength(String str)
	{
		// match a start or end tag
		Matcher m = HTML_TAG_PATTERN.matcher(str);
		int count = 0;
		while(m.find())
			count += m.group().length();
		return count;
		
	}
	
	/**
	 * Replaces only the whole words in <code>str</code> equal to the literal
	 * <code>target</code> with <code>replacement</code>.  A whole word is defined by the
	 * tokens present in a call to <code>String.split(String)</code> with an argument of
	 * <code>"\\s"</code>.
	 * 
	 * @param str the string on which to operate
	 * @param target the literal sequence to replace
	 * @param replacement the replacement sequence
	 * @return the resulting string
	 */
	public static String replaceWholeWordOnly(String str,String target,String replacement)
	{
		// get the list of individual words
		List<String> words = retainedSpaceSplit(str);
		int [] trimIndices = new int[2];
		StringBuilder tmp = new StringBuilder(32);
		for(ListIterator<String> it = words.listIterator(); it.hasNext();)
		{
			String word = it.next();
			// replace only whole words
			String trimmedWord = getTrimmedIndices(word,trimIndices);
			int trimBegin = trimIndices[0];
			int trimEnd = trimIndices[1];
			
			if(trimmedWord.equals(target))
			{
				// add any beginning spaces
				if(trimBegin > 0)
					tmp.append(word.substring(0,trimBegin));
				// replace target sequence with replacement sequence
				tmp.append(replacement);
				// add any trailing spaces
				if(trimEnd < word.length())
					tmp.append(word.substring(trimEnd));
				// set the replacement word
				it.set(tmp.toString());
				// clear the temporary string
				clearStringBuilder(tmp);					
			}
		}
		// rebuild the result string
		StringBuilder results = new StringBuilder(512);
		for(String s : words)
			results.append(s);
		return results.toString();
	}
	/**
	 * Replaces all non-html character sequences in <code>str</code> matching the given
	 * <code>regex</code> with the given <code>replacement</code>
	 * @param str the input <code>String</code>
	 * @param regex the regular expression <code>String</code>
	 * @param replacement the replacement <code>String</code>
	 * @return the replaced version of <code>str</code>
	 */
	public static String htmlReplaceAll(String str,String regex,String replacement)
	{
		StringBuilder result = new StringBuilder((int)(str.length() * 1.5));
		Matcher m = HTML_TAG_PATTERN.matcher(str);
		int prevMatchEnd = 0;
		while(m.find())
		{
			int matchStart = m.start();
			int matchEnd = m.end();
			// current match had to skip over some characters
			// that do not match the html tag pattern, and hence
			// are not part of an html tag's text
			// we want to run replaceAll on these characters
			if(matchStart != prevMatchEnd)
			{
				result.append(str.substring(prevMatchEnd,matchStart).replaceAll(
						regex, Matcher.quoteReplacement(replacement)));
			}
			// add the unmodified html text
			result.append(m.group());
			
			prevMatchEnd = matchEnd;
		}
		
		// must run replaceAll on remaining unmatched text
		if(prevMatchEnd < str.length())
		{
			result.append(str.substring(prevMatchEnd,str.length()).replaceAll(
					regex, Matcher.quoteReplacement(replacement)));
		}
		
		return result.toString();
			
	}

	/**
	 * Returns whether current platform is Mac OS.
	 * 
	 * @return <code>true</code> if current platform is Mac OS and
	 * <code>false</code> otherwise
	 */
	public static boolean isMacOs()
	{
		String osName = System.getProperty("os.name");
		return osName != null && osName.startsWith("Mac OS");
	}
	
	/**
	 * Returns whether current platform is Windows.
	 * 
	 * @return <code>true</code> if current platform is Windows and
	 * <code>false</code> otherwise
	 */
	public static boolean isWindows()
	{
		String osName = System.getProperty("os.name");
		return osName != null && osName.toUpperCase().startsWith("WINDOWS");
	}
	
	/**
	 * Trims non-printable characters for <code>input</code>.  Non-printable chars are all
	 * the ASCII chars between 0-31, except for 9 (tab), 10 (newline), 13 (carrage return).
	 * 
	 * @param input the input string
	 * @return the trimmed input string with only printable characters
	 */
	public static String trimNonPrintableChars(String input)
	{
		if(input == null) return null;
		
		StringBuilder out = null; 
		
		for(int i = 0, len = input.length(); i < len ; ++i)
		{
			char c = input.charAt(i);
			// input characters must be ascii
			if(isPrintableCharacter(c))
			{
				// if we have ignored a previous character then must use the stringbuilder to append
				if(out != null)
					out.append(c);
			}
			// we are ignoring this character so must create string builder to hold output
			else if(out == null)
			{
				out = new StringBuilder(input.length());
				// append previous input to the string, ignoring this character
				if(i > 0)
					out.append(input.substring(0,i));
			}
		}
		if(out != null)
			return out.toString();
		return input;
	}

        /**
         * Checks to see if a character is printable.
         *
         * @param c the <code>char</code>
         *
         * @return <code>true</code> if printable and <code>false</code> otherwise
         */
        public static boolean isPrintableCharacter(char c)
        {
            // input characters must be ascii
            return c <= 0x7F && (!Character.isISOControl(c) || c == '\n' || c == '\t' || c == '\r');
        }
	
	/**
	 * Changes the delimiter of <code>input</code> from <code>oldDelim</code> to
	 * <code>newDelim</code>, trimming any empty entries.  If straight replacement is desired then 
	 * use <code>input.replace(oldDelim,newDelim)</code>.
	 * 
	 * @param input the input string
	 * @param oldDelim the old delimiter regex
	 * @param newDelim the new delimter regex
	 * @return the modified string
	 */
	public static String replaceDelim(String input,String oldDelim,String newDelim)
	{
		if(input == null) return null;
		if(oldDelim == null) throw new NullPointerException("oldDelim");
		if(newDelim == null) throw new NullPointerException("newDelim");
		
		// add the "1 or more" quantifier so that the matches method will 
		// match multiple consecutive oldDelim values
		// This prevents all occurrences of the oldDelim from appearing in output
		/*
		Pattern p = Pattern.compile(!oldDelim.endsWith("+") ? 
				new StringBuilder(oldDelim.length() + 3).append('(').append(oldDelim).append('+').toString() :
			    oldDelim);
		String [] parts = input.split(oldDelim);
		*/
		String [] parts = split(input,oldDelim);
		StringBuilder str = new StringBuilder((int)(input.length() * 1.5));
		boolean first = true;
		for(String part : parts)
		{
			if(first) first = false;
			else str.append(newDelim);
			str.append(part);
		}
		return str.toString();
	}
	/**
	 * Version of <code>String.split</code> that discards empty entries and guarantees that the returned
	 * data does not contain any <code>regex</code> sequences.
	 * 
	 * @param input the input string
	 * @param regex the regular expression on which to perform the split
	 * @return the trimmed split string
	 */
	public static String [] split(String input,String regex)
	{
		if(input == null)
			throw new NullPointerException("input");
		if(regex == null)
			throw new NullPointerException("regex");
		
		return trimSplit(input.split(canonicalizeSplitRegex(regex)));
	}
	
	private static String [] trimSplit(String[] s)
	{
		// it is possible that first entry is empty, so remove from results if this is the case
		if(s.length > 0 && s[0].length() == 0)
		{
			String [] ss = new String[s.length - 1];
			if(s.length > 1)
				System.arraycopy(s, 1, ss, 0, ss.length);
			s = ss;
		}
		return s;
	}
	
	/**
	 * Version of <code>Pattern.split</code> that discards empty entries and guarantees that the returned
	 * data does not contain any <code>regex</code> sequences.
	 * 
	 * @param input the input string
	 * @param p the <code>Pattern</code> on which to perform the split
	 * @return the trimmed split string
	 */
	public static String [] split(String input,Pattern p)
	{
		if(input == null)
			throw new NullPointerException("input");
		if(p == null)
			throw new NullPointerException("p");
		
		return trimSplit(p.split(input));
	}
	
	
	/**
	 * Creates a <code>Pattern</code> for use with <code>split</code>.
	 * 
	 * @param regex the regular expression
	 * @param flags the pattern flags
	 * @return the created pattern
	 */
	public static Pattern createSplitPattern(String regex,int flags)
	{
		return Pattern.compile(canonicalizeSplitRegex(regex),flags);
	}
	/**
	 * Prepares the regular expression for use with <code>split</code>.
	 * 
	 * @param regex the regular expression
	 * @return the canonicalized regular expression
	 */
	private static String canonicalizeSplitRegex(String regex)
	{
		// add the "1 or more" quantifier so that the matches method will match multiple consecutive delims
		return !regex.endsWith("+") ? 
				new StringBuilder(regex.length() + 3).append('(').append(regex).append(")+").toString() :
				regex;
	}
	
	/**
	 * Splits an input string in same manner as {@link #split(String, String)} except that
	 * text enclosed between an <code>escapeSequence</code> pair is not considered during the
	 * split.  The <code>escapeSequence</code> in <code>input</code> is trimmed out of the returned
	 * result.
	 * 
	 * @param input the input string
	 * @param regex the regular expression on which to perform the split
	 * @return the trimmed split string 
	 */
	public static String [] escapedSplit(String input,String regex,String escapeSequence)
	{
		if(input == null)
			throw new NullPointerException("input");
		if(regex == null)
			throw new NullPointerException("regex");
		if(escapeSequence == null)
			return split(input,regex);
		
		regex = canonicalizeSplitRegex(regex);
		
		List<String> split = new ArrayList<String>();
		
		Pattern escapePattern = Pattern.compile(escapeSequence);
		Pattern splitPattern = Pattern.compile(regex);
		Matcher escapeMatcher = escapePattern.matcher(input);
		Matcher splitMatcher = splitPattern.matcher(input);
		
		// index in input of end of current token: start of current regex match
		int end = 0;
		// index in input of end of previous token: start of previous regex match
		int prevEnd = end;
		boolean lastToken = false;
		StringBuilder buf = new StringBuilder(512);
		
		int count = 0;
		while(splitMatcher.find() || (lastToken = end < input.length()))
		{
			boolean drainBuffer = false;
			// index in input of start of current token: end of previous regex match
			int start = end;
			if(!lastToken)
				end = splitMatcher.start();
			else
				end = input.length();
			
			if(!lastToken)
			{
				// look for an escape sequence within the value
				int newCount = 0;
				escapeMatcher.region(start, end);
				while(escapeMatcher.find())
					++newCount;
				// we are closing a previous open split
				if(count % 2 != 0)
				{
					if(newCount > 0)
						drainBuffer = true;
				}
				else // even count
				{
					// no new pending sequences
					if(newCount % 2 == 0)
						drainBuffer = true;
				}
					
				// update the count
				count += newCount;
				
			}
			else // add end of input
			{
				drainBuffer = true;
			}
			
			
			// no previous text in the buffer so don't include any of the escaped regex text
			if(buf.length() == 0)
				buf.append(input.substring(start,end));
			else // append the buffer from end of last match
				buf.append(input.substring(prevEnd,end));
			
			if(drainBuffer)
			{
				// remove the escape sequences from the buffer
				String s = escapePattern.matcher(buf.toString()).replaceAll("");
				// only add non-empty sequences
				if(s.length() != 0)
					split.add(s);
				MiscUtils.clearStringBuilder(buf);
			}
			
			
			if(!lastToken)
			{
				prevEnd = end;
				end = splitMatcher.end();
			}
			else // loop finished, we must break
			{
				//end = input.length();
				break;
			}
		}
		
		return split.toArray(new String[split.size()]);
	}
	
	/**
	 * Returns a formatted time difference string
	 * @param dt the time difference
	 * @param input the unit of <code>dt</code>
	 * @param output the desired output unit of <code>dt</code>
	 * @param decimalPlaces the number of decimal places to
	 *         print or -1 to print all in precision
	 * @return the formatted time string
	 */
	public static String formatTime(long dt,
			TimeUnit input,TimeUnit output,int decimalPlaces)
	{
		if(input == null)
			throw new NullPointerException("unit");
		if(output == null)
			throw new NullPointerException("output");
		
		double inputMult;
		double outputMult;
		String strOutUnit;
		
		switch(input)
		{
			case MICROSECONDS: inputMult = 1.0e-6; break;
			case MILLISECONDS: inputMult = 1.0e-3; break;
			case NANOSECONDS:  inputMult = 1.0e-9; break;
			case SECONDS: inputMult = 1.0; break;
		    default: throw new IllegalArgumentException("input=" + input);
			
		}
		switch(output)
		{
			case MICROSECONDS: outputMult = 1.0e-6; strOutUnit = "us"; break;
			case MILLISECONDS: outputMult = 1.0e-3; strOutUnit = "ms"; break;
			case NANOSECONDS: outputMult = 1.0e-9; strOutUnit = "ns"; break;
			case SECONDS: outputMult = 1.0; strOutUnit = "s"; break;
			default: throw new IllegalArgumentException("output=" + output);
		
		}
		
		StringBuilder str = new StringBuilder(64);
		double result = dt * inputMult / outputMult;
		String strResult = String.format("%" + 
				(decimalPlaces >= 0 ? "." + decimalPlaces : "") + "f",
				result);
		
		str.append(strResult).append(' ').append(strOutUnit);
		return str.toString();
		
	}
	
	/**
	 * Generates a unique identifier.
	 * 
	 * @return the unique identifier as a <code>BigInteger</code>
	 */
	public static BigInteger generateID()
	{
		UUID id = UUID.randomUUID();
		
		long hi = id.getMostSignificantBits();
		long lo = id.getLeastSignificantBits();
		
		final int len = 16;
		byte [] data = new byte[len];	
		for( int i=0, hlen = len >> 1; i<len; ++i )
		{
			int offset = (hlen-i-1)*8;
			data[i] = (byte)(((i < hlen ? hi : lo) & (0xffL << offset)) >>> offset);
		}
		
		return new BigInteger(1,data);
	}
	
	/**
	 * Interns a <code>String</code> value to avoid memory duplication.  If 
	 * a reference to <code>s</code> already exists in the pool, then the pool
	 * reference is returned; otherwise, <code>s</code> is added to the pool
	 * and then returned.  The pool maintains <code>WeakReference</code> symmantics.
	 * 
	 * @param s the string to intern
	 * @return the interned string reference
	 */
	public static String intern(String s)
	{
		if(s == null) return null;
		synchronized(stringPoolLock)
		{
			// lazy init, so don't pay for pool cost unless app actually
			// uses it
			if(stringPool == null)
			{
				stringPool = 
					new WeakHashMap<String,String>(
							CollectionUtils.calcHashCapacity(INIT_STRING_POOL_SIZE,
							CollectionUtils.LOAD_FACTOR),
							CollectionUtils.LOAD_FACTOR);
			}
			String pooled = stringPool.get(s);
			if(pooled != null) return pooled;
			stringPool.put(s, s);
			return s;
		}
	}
	
	/**
	 * Interns a <code>List</code> of strings
	 * 
	 * @param strings the strings
	 */
	public static void internList(List<String> strings)
	{
		if(strings == null) return;
		
		for(ListIterator<String> it = strings.listIterator(); it.hasNext();)
		{
			String s = it.next();
			it.set(MiscUtils.intern(s));
		}
		
	}
	
	/**
	 * Interns an <code>ArrayList</code> of strings.
	 * 
	 * @param strings the strings
	 */
	public static void internList(ArrayList<String> strings)
	{
		if(strings == null) return;
		for(int i = 0, len = strings.size(); i < len; ++i)
			strings.set(i,MiscUtils.intern(strings.get(i)));
	}
	
	/**
	 * Returns the <code>String</code> form of <code>o</code>.  Unlike
	 * <code>String.valueOf(Object)</code>, if <code>o</code> is <code>null</code>
	 * an empty <code>String</code> will be returned.
	 * 
	 * @param o the <code>Object</code>
	 * @return string form of <code>o</code>
	 */
	public static String toString(Object o)
	{
		return o != null ? o.toString() : "";
	}
	
	/**
	 * Removes the specified characters from string <code>s</code>.  This may be 
	 * faster than <code>String.replace(s,"")</code>.
	 * 
	 * @param s the input string
	 * @param chars the characters to remove
	 * @return the new string with the characters removed
	 */
	public static String removeChars(String s,char... chars)
	{
		if(s == null || chars == null) return s;
		
		int [] toRemove = new int[s.length()];
		int count = 0;
		for(int i = 0, len = s.length(); i < len; ++i)
		{
			for(int j = 0; j < chars.length; ++j)
			{
				if(s.charAt(i) == chars[j])
					toRemove[count++] = i;
			}
				
		}
		StringBuilder temp = new StringBuilder(s);
		for(int i = count - 1; i >=0;--i)
			temp.deleteCharAt(toRemove[i]);
		
		return temp.toString();
	}
	
	/**
	 * Clears the contents of the <code>StringBuilder</code>.
	 * 
	 * @param buf the <code>StringBuilder</code>
	 * @return <code>buf</code>
	 */
	public static StringBuilder clearStringBuilder(StringBuilder buf)
	{
		//return buf.delete(0, buf.length());
		buf.setLength(0);
		return buf;
	}
	
	
	/**
	 * Returns whether the two given url's point to the same host process.
	 * 
	 * @param first the first url
	 * @param second the second url
	 * @return <code>true</code> if same host process and <code>false</code> otherwise
	 */
	public static boolean isSameHostProcess(String first,String second)
	{
		URI authURI;
		URI dataURI;
		try
		{
			authURI = new URI(first);
			
		}
		catch(URISyntaxException e)
		{
			throw new IllegalArgumentException("first=" + first);
		}
		
		try
		{
			dataURI = new URI(second);
		}
		catch(URISyntaxException e)
		{
			
			throw new IllegalArgumentException("second=" + second);
		}
		
		String authHost = authURI.getHost();
		String authProtocol = authURI.getScheme();
		int authPort = authURI.getPort();
		
		String dataHost = dataURI.getHost();
		String dataProtocol = dataURI.getScheme();
		int dataPort = dataURI.getPort();
		
		return authPort == dataPort &&
		       MiscUtils.safeEquals(authHost, dataHost) &&
		       MiscUtils.safeEquals(authProtocol, dataProtocol);
	}
	
	/**
	 * Releases <code>l</code> before sleeping and then acquires <code>l</code> after
	 * sleeping.
	 * 
	 * @param l the <code>Lock</code>
	 * @param time the time to sleep
	 */
	public static void lockedSleep(Lock l,long time)
	{
    	// release the lock before sleeping
    	l.unlock();
    	try { Thread.sleep(time); }
    	catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
    	finally
    	{
    		// regain the lock after sleeping
    		l.lock();
    	}
	}
	
	/**
	 * Determines whether <code>s</code> is newline terminated.
	 * 
	 * @param s the string
	 * @return <code>true</code> if new line terminated and <code>false</code> otherwise
	 */
	public static boolean isLineTerminated(String s)
	{
		Matcher m = ALL_LINE_BREAK_PATTERN.matcher(s);
		while(m.find())
		{
			if(m.end() == s.length())
				return true;
		}
		
		return false;
		
		
	}
	

	
	// This turned out to be a false assumption. Benchmarks put the BigInteger
	// comparisons 2x SLOWER than just using the string comparions
	// Must be because of the long mask that is applied to the ints during the 
	// BigInteger comparison
	///**
	// * Converts a <code>String</code> to a <code>BigInteger</code> for faster
	// * string comparisons. The max size of any possible string being compared must
	// * be known in order for the comparisons to be correct.  The resulting comparisons
	// * may be 2x as fast or more.
	// * 
	// * @param s the string to convert
	// * @param isAscii <code>true</code> is string is ascii and <code>false</code> otherwise
	// * @param maxSize the max size of any string to be converted
	// * 
	// * @return the <code>BigInteger</code> equivalent of <code>s</code>
	// */
	//public static BigInteger stringToInt(String s,boolean isAscii,int maxSize)
	//{
	//	byte [] data = new byte[maxSize];
	//	for(int i = 0,j=0,len = s.length(); i < len; ++i)
	//	{
	//		char c = s.charAt(i);
	//		byte lo = (byte)(c & 0xFF);
	//		if(!isAscii)
	//		{
	//			byte hi = (byte)(((c & 0xFF00)) >> 8);
	//			data[j++] = hi;
	//		}
	//		data[j++] = lo;
	//	}
	//	// make sure returned value is positive
	//	return new BigInteger(1,data);
	//}
	
	/**
	 * Suppresses the output of System.out and System.err.
	 *
	 */
	public static void suppressConsole()
	{
		PrintStream stream = new PrintStream(new ByteArrayOutputStream(1024)
		{
			// override flush so that it resets the buffer so that the buffer
			// does not just keep increasing with each written line
			// flush in ByteArrayOutputStream by default does nothing
			public void flush()
			{
				reset();
			}
		},true);
         //redirect System.out and System.err to empty stream destination
		System.setOut(stream);
		System.setErr(stream);
	}
	
	/**
	 * Restores System.out and System.err after a call to {@link #suppressConsole()}.
	 *
	 */
	public static void restoreConsole()
	{
		System.setOut(SYS_OUT);
		System.setErr(SYS_ERR);
	}

                /**
         * Encodes any characters classified as html entities that occur in <code>s</code> and
         * returns the encoded string.
         *
         * @param s the raw string
         * @return the encoded string
         */
        public static String encodeHtml(String s)
        {
            // only doing ascii entities - '<','>','&'
            // see http://www.w3schools.com/html/html_entities.asp for full list
            StringBuilder result = new StringBuilder(s.length() * 2);
            result.append(s);
            
            StringUtils.replace(result,"&","&amp;");
            StringUtils.replace(result,"<","&lt;");
            StringUtils.replace(result,">","&gt;");

            return result.toString();
        }
	
}
