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

import java.io.*;
import java.util.*;

/**
 * CSV parser that is able to read the output from <code>CSVWriter</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: CSVReader.java 1983 2010-03-10 21:07:07Z jmontgomery $
 * @deprecated This is a non-conforming implementation and should only be used for compatibility. New code should
 *             use {@link CSVParser CSVReaderRFC} instead.
 *
 */
@Deprecated
public class CSVReader implements Closeable
{
	/**
	 * The default deliminator character.
	 * 
	 */
	public static final char DELIMINATOR = ',';
	private static final char QUOTE_CHAR = '"';
	
	private final BufferedReader in;
	private final char delim;
	
	// TODO: allow this to be passed in?
	//private final String lineSeparator = "\n";
	//private final String splitRegex;
	//private final Pattern splitPattern;
	
	/**
	 * Constructor.
	 * The default character deliminator is used.
	 * 
	 * 
	 * @param in the <code>Reader</code> from which to read the csv input
	 * @throws IOException if error occurs during stream construction
	 */
	public CSVReader(Reader in) throws IOException
	{
		this(in,DELIMINATOR);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param in the <code>Reader</code> from which to read the csv input
	 * @param delim the character deliminator to use
	 * @throws IOException if error occurs during stream construction
	 */
	public CSVReader(Reader in,char delim) throws IOException
	{
		if(in == null)
			throw new NullPointerException("in");
		
		this.in = new BufferedReader(in,1024);
		this.delim = delim;
		//this.splitRegex = "(?s)(([^\"].*?)" + 
		//	this.delim + "|(\".*?\"" + 
		//	this.delim + "?))";
		//this.splitPattern = Pattern.compile(splitRegex);
		
	}
	
	/**
	 * Returns the deliminator being used by this reader.
	 * 
	 * @return the deliminator character
	 */
	public char getDeliminator() { return delim; }
	
	/**
	 * Reads one line of csv text or <code>null</code> if there are no more non-empty lines to read.
	 * 
	 * @return a string array containing the deliminated ouput of the last read line or <code>null</code> if
	 *         there are no more non-empty lines
	 */
	public String[] readLine() throws IOException
	{
		String line = null; 
		boolean empty = true;
		do
		{
			line = in.readLine();
			empty = MiscUtils.isEmpty(line);
			if(!empty)
			{
				List<String> data = readLine0(line);
				return data.toArray(new String[data.size()]);
			}
		}
		// do while the line is only whitespace
		while(line != null && empty);
		
		return null;
			
	}
	
	/**
	 * Reads all the lines of delimitted values and stores them in each element
	 * of the returned <code>List</code>.  If the source contained no input, then an
	 * empty list is returned.
	 * 
	 * @return all the lines of delimitted values
	 * @throws IOException if error occurs during a read operation
	 */
	public List<String[]> readAll() throws IOException
	{
		List<String[]> lines = new ArrayList<String[]>(100 * 1024);
		String [] line;
		while((line = readLine()) != null)
		{
			if(line.length != 0)
				lines.add(line);
		}
		return lines;
	}
	
	/**
	 * Closes the underlying stream
	 * @throws IOException if an error occurs during the close operation
	 */
	public void close() throws IOException
	{
		in.close();
	}
	
	private List<String> readLine0(String entry)
	{
		StringBuilder str = new StringBuilder(1024);
		List<String> data = new ArrayList<String>();
		final int len = entry.length();
		
		boolean isEscaped = false;
		// denotes consecutive backslashes
		int backSlashes = 0;
		
		// if we have just isolated 
		for(int i = 0; i < len; ++i)
		{
			char c = entry.charAt(i);
			if(c == QUOTE_CHAR)
			{
				// a backslash is used to escape a character
				// all backslashes are escaped (giving even backslash count)
				// except for control character escapes and quote escapes
				// if the consec back slash count is odd and this char is a quote
				// then it must be that this quote is an escaped quote that should be 
				// printed
				
				// if previous was not a backslash then definitely ending escape sequence
				// else if even count, then this quote ends an escape sequence that was preceeded
				// by escaped backslash sequences
				if(backSlashes % 2 == 0)
					isEscaped = !isEscaped;
				else // we are escaped, so we want the quote
					str.append(c);
			}
			else if(c == '\\')
			{
				// we want to add the actual backslash character
				if(++backSlashes % 2 == 0)
					str.append(c);
			}
			else if(c == delim)
			{
				// we want the delim
				if(isEscaped)
					str.append(c);
				else // this is the deliminator, so add the input so far
				{
					data.add(str.toString());
					// clear the current value for string builder
					MiscUtils.clearStringBuilder(str);
				}			
			}
			else if(CSVWriter.isPotentialEscapedControlChar(c) && 
					backSlashes % 2 != 0)
			{
				// this is escaped control character so print it out!
				str.append(CSVWriter.controlCharForEscaped(c));
			}
			else // just add the character
				str.append(c);
			
			if(c != '\\')
				backSlashes = 0;
		} //end for
		// append last entry
		//if(str.length() != 0)
		data.add(str.toString());
		return data;
	}
}
