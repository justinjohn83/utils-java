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

import java.util.*;
import java.io.*;
import java.util.regex.*;

/**
 * Writes out csv values. The strategy is described as follows:<pre>
 * A "value" denotes a single comma-separated value. A control character is one of
 * {'\n','\r','\t','\\','"'} or the deliminator character that can appear in normal typed input.
 * A value is "escaped" by surrounding it with quote '"' characters, and each control character is escaped
 * with an additional backslash '\\' character. If a value contains at least one control character,
 * then it is escaped.
 * </pre>
 *     
 * 
 * @author Justin Montgomery
 * @version $Id: CSVWriter.java 1983 2010-03-10 21:07:07Z jmontgomery $
 * @deprecated This is a non-conforming implementation and should only be used for compatibility. New code should
 *             use {@link CSVPrinter CSVWriterRFC} instead.
 *
 */
@Deprecated
public class CSVWriter implements Closeable,Flushable
{
	/**
	 * The default deliminator character.
	 * 
	 */
	public static final char DELIMINATOR = ',';
	
	private static final char QUOTE_CHAR = '"';
	
	private final BufferedWriter out;
	private final char delim;
	private final String lineEnding;
	
	//private final String escapeRegex;
	private final Pattern escapePattern;
	private final Pattern quotePattern;
	//private final Pattern controlPattern;
	
	private static final char NEWLINE_CHAR = '\n';
	private static final char CARRAGE_RETURN_CHAR = '\r';
	private static final char TAB_CHAR = '\t';
	
	/**
	 * Constructor.
	 * The default deliminator and the default platform line separator are used.
	 * 
	 * @param out the <code>Writer</code> for which to write the csv output 
	 * @throws IOException if error occurs during stream construction
	 * @throws NullPointerException if <code>out</code> is <code>null</code>
	 */
	public CSVWriter(Writer out) throws IOException
	{
		this(out,DELIMINATOR,System.getProperty("line.separator"));
	}
	
	/**
	 * Returns whether <code>c</code> is a "control" character as defined
	 * by this class.
	 * 
	 * @param c the character
	 * @return <code>true</code> if a control character and <code>false</code> otherwise
	 */
	static boolean isControlChar(char c)
	{
		return c == NEWLINE_CHAR || c == CARRAGE_RETURN_CHAR || c == TAB_CHAR;
	}
	
	/**
	 * Returns whether <code>c</code> is a potential "control" character as defined by this class.
	 * For example 'n' is a potential control character for '\n' if the '\n' was escaped with
	 * "\\n".
	 * 
	 * @param c the character
	 * @return <code>true</code> if <code>c</code> is a potential control character and <code>false</code>
	 *         otherwise
	 */
	static boolean isPotentialEscapedControlChar(char c)
	{
		return c == 'n' || c == 'r' || c == 't';
	}
	
	/**
	 * Returns the control character for the specified potential control character as defined by 
	 * {@link #isPotentialEscapedControlChar(char)}.
	 * 
	 * @param c the potential control character
	 * @return the control character for the potential character <code>c</code>
	 * @throws IllegalArgumentException if {@link #isPotentialEscapedControlChar(char)} would return <code>false</code>
	 *                                  for <code>c</code>
	 */
	static char controlCharForEscaped(char c)
	{
		switch(c)
		{
		case 'n': return '\n';
		case 'r': return '\r';
		case 't': return '\t';
		default: throw new IllegalArgumentException("c=" + c + " not a potential escaped control char");
		}
		 
	}
	
	/**
	 * Constructor.
	 * 
	 * @param out the <code>Writer</code> for which to write the csv output 
	 * @param delim the deliminator character to use to separate values
	 * @param lineEnding the line terminator character to use
	 * @throws IOException if error occurs during stream construction
	 * @throws NullPointerException if <code>out</code> is <code>null</code>
	 * @throws IllegalArgumentException id delim is '"' or '\' or if the line
	 *         terminator is not a valid line terminating sequence
	 *         {"\n","\r","\r\n"}
	 */
	public CSVWriter(Writer out,char delim,String lineEnding) throws IOException
	{
		if(out == null)
			throw new NullPointerException("out");
		if(delim == QUOTE_CHAR || delim == '\\')
		{
			throw new IllegalArgumentException("Delim cannot be " +
					QUOTE_CHAR + " or \\");
		}
		if(lineEnding == null)
			throw new NullPointerException("lineEnding");
		else if(!MiscUtils.isLineTerminator(lineEnding))
			throw new IllegalArgumentException("Illegal lineEnding: " + lineEnding);
		
		this.out = new BufferedWriter(out,1024);
		this.delim = delim;
		this.lineEnding = lineEnding;
		String controlRegex = NEWLINE_CHAR + "|" + CARRAGE_RETURN_CHAR + "|" + 
		   					  TAB_CHAR;
		String escapeRegex =  controlRegex + "|\"|" + "\\\\|" + this.delim;
		this.escapePattern = Pattern.compile(".*?(" + escapeRegex + ")");
		this.quotePattern = Pattern.compile("\"");
		//this.controlPattern = Pattern.compile(controlRegex);
	}
	
	/**
	 * Returns the sequence being used for line termination.
	 * 
	 * @return the line termination sequence
	 */
	public String getLineTerminator() { return lineEnding; }
	
	/**
	 * Returns the character used to delimit values.
	 * 
	 * @return the deliminator character
	 */
	public char getDeliminator() { return delim; }
	
	/**
	 * Prints a single line of csv output to the underlying stream.
	 * 
	 * @param line the delimited values to print
	 * @throws IOException if error occurs during the write operation
	 */
	public void print(String [] line) throws IOException
	{
		if(line != null)
			out.write(print0(line,true));
	}
	
	/**
	 * Prints each line of input in <code>lines</code> to the underlying
	 * stream. The output is then flushed.
	 * 
	 * @param lines the lines to print
	 * @throws IOException if error occurs during the write operation
	 */
	public void printAll(List<String[]> lines) throws IOException
	{
		if(lines != null)
		{
			for(String[] line : lines)
				print(line);
			flush();
		}
	}
	
	/**
	 * Prints each line of input in <code>lines</code> to the underlying
	 * stream. The output is then flushed.
	 * 
	 * @param lines the lines to print
	 * @throws IOException if error occurs during the write operation
	 */
	public void printAll(String[][] lines) throws IOException
	{
		if(lines != null)
		{
			for(String [] line : lines)
				print(line);
			flush();
		}
	}
	/**
	 * Flushes the underlying stream.
	 * 
	 * @throws IOException if error occurs
	 */
	public void flush() throws IOException
	{
		out.flush();
	}
	
	/**
	 * Closes the underlying stream.
	 * 
	 * @throws IOException if error occurs
	 */
	public void close() throws IOException
	{
		out.close();
	}
	
	private String print0(String[] data,boolean addLineSep)
	{
		if(data == null)
			return "";
		
		StringBuilder str = new StringBuilder(1024);
		for(int i = 0, len = data.length; i < len; ++i)
		{
			str.append(makeEntry(data[i]));
			if(i < len - 1)
				str.append(delim);
		}
		if(addLineSep)
			str.append(lineEnding);
		return str.toString();
	}
	
	private String makeEntry(String entry)
	{
		if(entry == null) return "";
		
		// determine whether entry contains at least one control character or a quote
		boolean escape = MiscUtils.matchCount(entry, escapePattern) > 0;
		boolean hasQuote = MiscUtils.matchCount(entry, quotePattern) > 0;
		if(escape || hasQuote) //add an extra backslash
		{
			// don't double escape the quotes
			if(escape)
			{
				entry = entry.replace("\\", "\\\\");
				// also be sure to actually escape the control characters
				//$0 matches the whole control pattern group
				// we want to prepend all control characters with a literal backslash
				//doesn't work since prints out literal backslash followed by 
				// control character instead of an escaped control character
				//entry = controlPattern.matcher(entry).replaceAll("\\\\$0");
				// fallback solution is to implement manually since not that many cases
				entry = entry.replace("\n","\\n");
				entry = entry.replace("\r","\\r");
				entry = entry.replace("\t","\\t");
				
			}
			if(hasQuote)
				entry = entry.replace("\"", "\\\"");
			StringBuilder str = new StringBuilder(entry.length() + 2);
			// now add the escape quoting
			str.append(QUOTE_CHAR);
			str.append(entry);
			str.append(QUOTE_CHAR);
			entry = str.toString();
		}
		
		return entry;
		
		
	}

	/**
	 * Generates a single line of csv.  No line terminator is included in the output.
	 * 
	 * @param line the line data
	 * @return the csv string
	 */
	public static String createCSV(String [] line)
	{
		CSVWriter csv = null;
		StringWriter data = new StringWriter(1024);
		try
		{
			csv = new CSVWriter(data);
			data.write(csv.print0(line,false));
			csv.flush();
		}
		catch(IOException e)
		{
			throw new AssertionError(e);
		}
		
		return data.toString();
	}
}
