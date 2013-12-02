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

import static com.gamesalutes.utils.CSVTestUtils.COMPLEX_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.COMPLEX_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.COMPLEX_QUOTE_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.COMPLEX_QUOTE_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.EMPTY_ENTRIES_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.EMPTY_ENTRIES_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.EMPTY_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.EMPTY_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.ESCAPED_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.ESCAPED_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.NEWLINE_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.NEWLINE_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.QUOTE_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.QUOTE_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.SIMPLE_INPUT;
import static com.gamesalutes.utils.CSVTestUtils.SIMPLE_INPUT_2;
import static com.gamesalutes.utils.CSVTestUtils.SIMPLE_OUTPUT;
import static com.gamesalutes.utils.CSVTestUtils.SIMPLE_OUTPUT_2;
import static com.gamesalutes.utils.CSVTestUtils.HEADER;
import static com.gamesalutes.utils.CSVTestUtils.HEADER_OUTPUT;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class CSVPrinterTest
{
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception 
	{
		
	}
	
	private void test(String expectedOutput,String[] input)
		throws Exception
	{
		test(null,expectedOutput,input);
		
		if(!expectedOutput.endsWith("\n"))
			expectedOutput += "\n";
		
		test(HEADER,HEADER_OUTPUT + "\n" + expectedOutput,input);
	}
	
	private void test(String [] header,String expectedOutput,String [] input)
		throws Exception
	{
		StringWriter w = new StringWriter(512);
		CSVPrinter p = new CSVPrinter(w,header,CSVPrinter.DELIMINATOR,"\n",false);
		
		p.print(input);
		
		p.flush();
		
		if(!expectedOutput.endsWith("\n"))
			expectedOutput += "\n";
		assertEquals(expectedOutput,w.toString());
	}
	
	private void test(String expectedOutput,List<String[]> input)
	throws Exception
	{
		test(null,expectedOutput,input);
		
		if(!expectedOutput.endsWith("\n"))
			expectedOutput += "\n";
		
		test(HEADER,HEADER_OUTPUT + "\n" + expectedOutput,input);
	}
	private void test(String [] header,String expectedOutput,List<String[]> input)
		throws Exception
	{
		StringWriter w = new StringWriter(2048);
		CSVPrinter p = new CSVPrinter(w,header,CSVPrinter.DELIMINATOR,"\n",false);
		
		p.printAll(input);
		
		if(!expectedOutput.endsWith("\n"))
			expectedOutput += "\n";
		
		assertEquals(expectedOutput,w.toString());
	}
	
	@Test
	public void testSimple()
		throws Exception
	{
		test(SIMPLE_OUTPUT,SIMPLE_INPUT);
	}
	
	@Test
	public void testSimple2()
		throws Exception
	{
		test(SIMPLE_OUTPUT_2,SIMPLE_INPUT_2);
	}
	
	@Test
	public void testEmpty()
		throws Exception
	{
		test(EMPTY_OUTPUT,EMPTY_INPUT);
	}
	
	@Test
	public void testQuote()
		throws Exception
	{
		test(QUOTE_OUTPUT,QUOTE_INPUT);
	}
	
	@Test
	public void testComplexQuote()
		throws Exception
	{
		test(COMPLEX_QUOTE_OUTPUT,COMPLEX_QUOTE_INPUT);
	}
	
	@Test
	public void testEscaped()
		throws Exception
	{
		test(ESCAPED_OUTPUT,ESCAPED_INPUT);
	}
	
	@Test
	public void testEmptyEntries()
		throws Exception
	{
		test(EMPTY_ENTRIES_OUTPUT,EMPTY_ENTRIES_INPUT);
	}
	
	@Test
	public void testNewline()
		throws Exception
	{
		test(NEWLINE_OUTPUT,NEWLINE_INPUT);
	}
	
	@Test
	public void testComplex()
		throws Exception
	{
		test(COMPLEX_OUTPUT,COMPLEX_INPUT);
	}
	
	@Test
	public void testComplete()
		throws Exception
	{
		test(CSVTestUtils.createWholeOutput(true),
				CSVTestUtils.createWholeInput(false));
	}
}
