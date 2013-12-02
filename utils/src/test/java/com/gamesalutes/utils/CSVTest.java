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


import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

/**
 * Tests CSVReader in conjunction with CSVWriter.
 * 
 * @author Justin Montgomery
 * @version $Id: CSVTest.java 1228 2008-12-12 21:54:25Z jmontgomery $
 */
public class CSVTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception 
	{
		
	}
	
	private void test(List<String[]> input,boolean assertEmpty) throws IOException
	{
		StringWriter strOut = new StringWriter(1024);
		CSVWriter writer = new CSVWriter(strOut,',',"\n");
		writer.printAll(input);
		StringReader strIn = new StringReader(strOut.toString());
		CSVReader reader = new CSVReader(strIn,',');
		List<String[]> output = reader.readAll();
		
		// convert for formatting and comparison purposes
		List<List<String>> listInput = makeList(input);
		List<List<String>> listOutput = makeList(output);
		
		if(!assertEmpty)
			assertEquals("\nInput: " + listInput + "\nOutput: " + listOutput,listInput,listOutput);
		else
			assertTrue("Empty : listOutput = " + listOutput,listOutput.isEmpty());
		
		
	}
	
	private List<List<String>> makeList(List<String[]> input)
	{
		List<List<String>> list = new ArrayList<List<String>>(input.size());
		for(String[] entry : input)
			list.add(Arrays.asList(entry));
		return list;
	}
	@Test
	public void testSimpleInput() throws IOException
	{
		test(Arrays.<String[]>asList(new String[] {"Justin"}),false);
	}
	@Test
	public void testSimpleInput2() throws IOException
	{
		test(Arrays.<String[]>asList(
				new String[] {
						"ITECO_CSV_PROP","notes","\\Carpe Diem\\"}),false);
	}
	
	@Test
	public void testEmptyInput() throws IOException
	{
		test(Arrays.<String[]>asList(new String[]{""},new String[]{""}),true);
	}
	
	@Test
	public void testQuoteInput() throws IOException
	{
		// purposely don't close the quote to try to break it
		test(Arrays.<String[]>asList(
				new String[] {"One","\"Two three\"","\"Four"}),false);
				
	}
	@Test
	public void testEscapedInput() throws IOException
	{
		test(Arrays.<String[]>asList(
				new String[] {"One\t\nTwo","three\nfour"}),false);
	}

	@Test
	public void testDelimInput() throws IOException
	{
		test(Arrays.<String[]>asList(
				new String[] {",One","two,three,"}),false);
	}
	@Test
	public void testUserNewLines() throws IOException
	{
		test(Arrays.<String[]>asList(
				new String[] {"\\nOne\\n","Two\\n","\\nThree\\n"}),false);
	}
	@Test
	public void testComplexInput() throws IOException
	{
		test(Arrays.<String[]>asList(
				new String[] {"\n\nOne","\"Two\\n\n\nthree\\n,\"four\",five\n\n"}),false);
	}
	@Test
	public void testComplexInput2() throws IOException
	{
		// test with delimiters and an unclosed quote in middle
		test(Arrays.<String[]>asList(
				new String[] {"ITECO_CSV_PROP","notes",
						"\"\"\\Hello\"\" I have \\\\1,2,\\\\3,\"4, ,\\dollars\\"}),false);
				
	}
	
	@Test
	public void testMultiLineSimple() throws IOException
	{
		test(Arrays.<String[]>asList(
				new String[] {"Jon"},
				new String[] {"Doe"},
				new String[] {"Mr."},
				new String[] {"Smith"}),false);
	}
	@Test
	public void testMultiLineComplex() throws IOException
	{
		test(Arrays.<String[]>asList(
				new String[] {"One","\"Two\\n\nthree\\n,\"four\n\n\"\n,five\n"},
				new String[] {"One","two\nthree\n"},
				new String[] {"\\nOne\\n","Two\\n","\\nThree\\n"},
				new String[] {",One","two,three,"},
				new String[] {"\n\nOne","\"Two\\n\nthree\\n\n,\"four\",five\n\n"},
				new String[] {"\nOne\nTwo","three\nfour"},
				new String[] {"\nOne","\"Two three\""},
				new String[] {"Justin"},
				new String[] {"ITECO_CSV_PROP","notes",
								"\"\"Hello\"\" I have 1,2,3,\"4\" dollars"}),
			false);
				


	}
	
	@Test
	public void testTrailingEmpty() throws IOException
	{
		test(Arrays.<String[]>asList(new String[] {"1","2","",""}),false);
		
	}

}
