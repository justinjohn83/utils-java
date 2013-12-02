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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class CSVPrinter implements Closeable, Flushable
{

	/**
	 * The default deliminator character.
	 * 
	 */
	public static final char DELIMINATOR = ',';
	
	/**
	 * The character used for escaping the csv.
	 * 
	 */
	static final char QUOTE_CHAR = '"';
	
	private final BufferedWriter out;
	private final char delim;
	private final String lineEnding;
	private final boolean alwaysQuote;
	private final char quoteChar;
	
	private static final char NEWLINE_CHAR = '\n';
	private static final char CARRAGE_RETURN_CHAR = '\r';
	private static final char TAB_CHAR = '\t';
	
	
	/**
	 * Constructor.
	 * The default deliminator,the default platform line separator, and no header are used.
	 * 
	 * @param out the <code>Writer</code> for which to write the csv output 
	 * @throws IOException if error occurs during stream construction
	 * @throws NullPointerException if <code>out</code> is <code>null</code>
	 */
	public CSVPrinter(Writer out) throws IOException
	{
		this(out,null);
	}
	
	/**
	 * Constructor.
	 * The default deliminator and the default platform line separator are used.
	 * 
	 * @param out the <code>Writer</code> for which to write the csv output 
	 * @param header the header to print to the csv or <code>null</code> to print no header
	 * @throws IOException if error occurs during stream construction
	 * @throws NullPointerException if <code>out</code> is <code>null</code>
	 */
	
	public CSVPrinter(Writer out,String [] header)
		throws IOException
	{
		this(out,header,DELIMINATOR,System.getProperty("line.separator"),false);
	}
	
	private boolean requiresQuoting(String line)
	{
		if(alwaysQuote) return true;
		
		// also quote if we start with or end with spaces to try to preserve
		// this in implementations that incorrectly ignore these
		if(line.length() > 0)
		{
			if(Character.isSpaceChar(line.charAt(0)))
				return true;
			if(line.length() > 1)
			{
				if(Character.isSpaceChar(line.charAt(line.length() - 1)))
					return true;
			}
		}
		// must quote if we contain a delimiter, line terminator or quote character
		for(int i = 0, len = line.length(); i < len; ++i)
		{
			char c = line.charAt(i);
			if(c == delim || c == NEWLINE_CHAR || c == CARRAGE_RETURN_CHAR || c == quoteChar)
				return true;
		}
		return false;
	}


       /**
	 * Constructor.
	 *
	 * @param out the <code>Writer</code> for which to write the csv output
	 * @param header the header to print to the csv or <code>null</code> to print no header
	 * @param delim the deliminator character to use to separate values
	 * @param lineEnding the line terminator character to use
	 * @param alwaysQuote <code>true</code> to always quote values and <code>false</code> otherwise
	 * @throws IOException if error occurs during stream construction
	 * @throws NullPointerException if <code>out</code> is <code>null</code>
	 * @throws IllegalArgumentException id delim is '"' or '\' or if the line
	 *         terminator is not a valid line terminating sequence
	 *         {"\n","\r","\r\n"}
	 */
	public CSVPrinter(Writer out,String [] header,char delim,String lineEnding,boolean alwaysQuote) throws IOException
        {
            this(out,header,delim,lineEnding,QUOTE_CHAR,alwaysQuote);
        }
	/**
	 * Constructor.
	 * 
	 * @param out the <code>Writer</code> for which to write the csv output 
	 * @param header the header to print to the csv or <code>null</code> to print no header
	 * @param delim the deliminator character to use to separate values
	 * @param lineEnding the line terminator character to use
         * @param quoteChar the char to use to escape csv
	 * @param alwaysQuote <code>true</code> to always quote values and <code>false</code> otherwise
	 * @throws IOException if error occurs during stream construction
	 * @throws NullPointerException if <code>out</code> is <code>null</code>
	 * @throws IllegalArgumentException id delim is '"' or '\' or if the line
	 *         terminator is not a valid line terminating sequence
	 *         {"\n","\r","\r\n"}
	 */
	public CSVPrinter(Writer out,String [] header,char delim,String lineEnding,char quoteChar,boolean alwaysQuote) throws IOException
	{
		if(out == null)
			throw new NullPointerException("out");
                this.quoteChar = quoteChar;

		if(delim == quoteChar || delim == '\\')
		{
			throw new IllegalArgumentException("Delim cannot be " +
					quoteChar + " or \\");
		}
		if(lineEnding == null)
			throw new NullPointerException("lineEnding");
		else if(!MiscUtils.isLineTerminator(lineEnding))
			throw new IllegalArgumentException("Illegal lineEnding: " + lineEnding);
		
		this.out = new BufferedWriter(out,1024);
		this.delim = delim;
		this.lineEnding = lineEnding;
		this.alwaysQuote = alwaysQuote;
		
		if(header != null)
			this.print(header);
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
	 * Generates a single line of csv output without a terminal line separator.
	 * 
	 * @param line the input line
	 * @return the single line of csv output
	 */
	public static String generate(String [] line)
	{
		if(line == null)
			return null;
		
		CSVPrinter csv = null;
		StringWriter data = new StringWriter(512);
		try
		{
			csv = new CSVPrinter(data);
			data.write(csv.print0(line,false));
			csv.flush();
		}
		catch(IOException e)
		{
			throw new AssertionError(e);
		}
		
		return data.toString();
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
		
		
		// see if we need to quote
		boolean quote = requiresQuoting(entry);
		StringBuilder str = new StringBuilder(entry.length() * 2);
		if(quote)
			str.append(quoteChar);
		for(int i = 0,len = entry.length(); i < len; ++i)
		{
			char c = entry.charAt(i);
			str.append(c);
			// escape quote characters by quoting again
			if(c == quoteChar)
				str.append(quoteChar);
		}
		if(quote)
			str.append(quoteChar);
		
		return str.toString();
	}

}
