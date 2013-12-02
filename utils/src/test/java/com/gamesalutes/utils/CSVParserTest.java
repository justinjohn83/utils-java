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

import static org.junit.Assert.assertEquals;
import java.io.StringReader;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.gamesalutes.utils.CSVTestUtils.*;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class CSVParserTest
{
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception 
	{
		
	}
	
	private void test(String input,String [] expOutput)
		throws Exception
	{
		test(false,input,expOutput);
		test(true,HEADER_OUTPUT + "\n" + input,expOutput);
	}
	private void test(boolean hasHeader,String input,String[] expOutput)
		throws Exception
	{
		StringReader r = new StringReader(input);
		CSVParser p = new CSVParser(r,hasHeader,CSVParser.DELIMINATOR,"\n");
		
		String [] actual = p.readLine();
		
		assertEquals(CSVTestUtils.toString(expOutput),CSVTestUtils.toString(actual));
	}
	
	
	private void test(String input,List<String[]> expOutput)
		throws Exception
	{
		test(false,input,expOutput);
		test(true,HEADER_OUTPUT + "\n" + input,expOutput);
	}
	private void test(boolean hasHeader,String input,List<String[]> expOutput)
		throws Exception
	{
		StringReader r = new StringReader(input);
		CSVParser p = new CSVParser(r,hasHeader,CSVParser.DELIMINATOR,"\n");
		
		List<String[]> actual = p.readAll();
		
		assertEquals(CSVTestUtils.toString(expOutput),CSVTestUtils.toString(actual));
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
		test(CSVTestUtils.createWholeOutput(false),
				CSVTestUtils.createWholeInput(true));
	}
}
