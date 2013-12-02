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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="http://tools.ietf.org/html/rfc4180#page-2">RFC 4180</a> compliant csv parser 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class CSVParser implements Closeable
{

	/**
	 * The default deliminator character.
	 * 
	 */
	public static final char DELIMINATOR = CSVPrinter.DELIMINATOR;
	private static final char QUOTE_CHAR = CSVPrinter.QUOTE_CHAR;
	
	private final BufferedReader in;
	private final char delim;
	private final char quoteChar;
	private final String lineTerminator;
	private final boolean hasHeader;
	
	private boolean readHeader = false;
	
	private static final Logger logger = LoggerFactory.getLogger(CSVParser.class.getSimpleName());
	
	// TODO: allow this to be passed in?
	//private final String lineSeparator = "\n";
	//private final String splitRegex;
	//private final Pattern splitPattern;
	
	/**
	 * Constructor.
	 * The default character deliminator is used and it is assumed all csv is data and there is no header.
	 * 
	 * 
	 * @param in the <code>Reader</code> from which to read the csv input
	 * @throws IOException if error occurs during stream construction
	 */
	public CSVParser(Reader in) throws IOException
	{
		this(in,false);
	}
	
	/**
	 * Constructor.
	 * The default character deliminator is used.
	 * 
	 * 
	 * @param in the <code>Reader</code> from which to read the csv input
	 * @param hasHeader <code>true</code> if first line of stream is header and <code>false</code> otherwise
	 * @throws IOException if error occurs during stream construction
	 */
	public CSVParser(Reader in,boolean hasHeader) throws IOException
	{
		this(in,hasHeader,DELIMINATOR,System.getProperty("line.separator"));
	}


       /**
	 * Constructor.
	 *
	 * @param in the <code>Reader</code> from which to read the csv input
	 * @param hasHeader <code>true</code> if first line of stream is header and <code>false</code> otherwise
	 * @param delim the character deliminator to use
	 * @throws IOException if error occurs during stream construction
	 */
	public CSVParser(Reader in,boolean hasHeader,char delim,String lineTerminator) throws IOException
	{
            this(in,hasHeader,delim,lineTerminator,QUOTE_CHAR);
        }
	/**
	 * Constructor.
	 * 
	 * @param in the <code>Reader</code> from which to read the csv input
	 * @param hasHeader <code>true</code> if first line of stream is header and <code>false</code> otherwise
	 * @param delim the character deliminator to use
         * @param quoteChar the character to use to escape csv
	 * @throws IOException if error occurs during stream construction
	 */
	public CSVParser(Reader in,boolean hasHeader,char delim,String lineTerminator,char quoteChar) throws IOException
	{
		if(in == null)
			throw new NullPointerException("in");
		
		this.in = new BufferedReader(in,1024);
		this.hasHeader = hasHeader;
		this.delim = delim;
                this.quoteChar = quoteChar;

                if(delim == quoteChar || delim == '\\')
		{
			throw new IllegalArgumentException("Delim cannot be " +
					quoteChar + " or \\");
		}
		if(lineTerminator == null)
			throw new NullPointerException("lineTerminator");
		else if(!MiscUtils.isLineTerminator(lineTerminator))
			throw new IllegalArgumentException("lineTerminator=" + lineTerminator);
		this.lineTerminator = lineTerminator;
		
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
		List<String> data = new ArrayList<String>();
		StringBuilder buf = null;
		boolean skipped = false;
		
		do
		{
			line = in.readLine();
			empty = MiscUtils.isEmpty(line);
			if(!empty || (buf != null && line != null))
			{
				if(this.hasHeader && !readHeader)
				{
					skipped = readHeader = true;
					continue;
				}
				else
					skipped = false;
				
				buf = readLine0(line,data,buf);
				if(buf == null)
					return data.toArray(new String[data.size()]);
			}
		}
		// do while the line is only whitespace or we are parsing multi-line element
		while(line != null && (skipped || (empty || buf != null)));
		
		// ran out of input before we finished quoting
		if(buf != null)
			logger.warn("Unexpected EOF: Unterminated quoted entry: " + buf);
		
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
	
	/**
	 * Reads a single "line" of csv input and returns whether the current "line"
	 * is complete.    
	 * @param entry the line
	 * @param data the storage for the delimited input of this line
	 * @param prev return result of last call to this method
	 * @return current buffer if more input exists on next line and <code>null</code>
	 *         otherwise
	 */
	private StringBuilder readLine0(String entry,List<String> data,StringBuilder prev)
	{
		boolean continued = prev != null;
		
		StringBuilder str;
		if(!continued)
		{
			str = new StringBuilder(1024);
			data.clear();
		}
		else
		{
			str = prev;
			str.append(lineTerminator);
		}
				
		// denotes consecutive quotes
		int quotes = continued ? 1 : 0;
		
		// if we have just isolated 
		for(int i = 0, len = entry.length(); i < len; ++i)
		{
			char c = entry.charAt(i);

			if(c == quoteChar)
			{
				// since quotes are escaped with another quote only add the 
				// even counted quotes
				if(++quotes % 2 == 0)
				{
					// but don't add the final quote character that determines end
					// of entry
					if(i != len - 1 && entry.charAt(i + 1) != delim)
						str.append(c);
					else
						quotes = 0;
				}
			}
			else if(c == delim)
			{
				// we want the delim
				if(quotes > 0)
					str.append(c);
				else // this is the deliminator, so add the input so far
				{
					data.add(str.toString());
					// clear the current value for string builder
					MiscUtils.clearStringBuilder(str);
				}	
			}
			else
			{
				str.append(c);
			}
		} //end for

		
		// return current buffer if more input for this logical line exists on 
		if(quotes > 0)
		{
			return str;
		}
		// next logical line
		else
		{
			// append last entry
			data.add(str.toString());
			return null;
		}
	}

}
