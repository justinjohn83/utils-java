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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
final class CSVTestUtils
{
	
	public static final String [] HEADER = new String[] {"1","2","3" };
	
	public static final String HEADER_OUTPUT = "1,2,3";
	
	public static final String [] SIMPLE_INPUT = 
		new String[] {"Justin"};
	public static final String SIMPLE_OUTPUT = "Justin";
	
	public static final String [] SIMPLE_INPUT_2 = 
		new String [] {"ITECO_CSV_PROP","notes","\\Carpe Diem\\"};
	public static final String SIMPLE_OUTPUT_2 = 
		"ITECO_CSV_PROP,notes,\\Carpe Diem\\";
	
	public static final String [] EMPTY_INPUT = 
		new String [] {""};
	
	public static final String EMPTY_OUTPUT = "";
	
	
	public static final String [] QUOTE_INPUT = 
		new String [] {"One","Two \"three\"","F\"our"};
	
	public static final String QUOTE_OUTPUT = 
		"One,\"Two \"\"three\"\"\",\"F\"\"our\"";
	
	public static final String [] COMPLEX_QUOTE_INPUT = 
		new String [] {"One","Two \"three\"","F\"o\"\"\"ur\""};
	
	public static final String COMPLEX_QUOTE_OUTPUT = 
		"One,\"Two \"\"three\"\"\",\"F\"\"o\"\"\"\"\"\"ur\"\"\"";
	
	public static final String [] ESCAPED_INPUT = 
		new String[] {"One","Two,","Th,ree","Four,"};
	
	public static final String ESCAPED_OUTPUT = 
		"One,\"Two,\",\"Th,ree\",\"Four,\"";
	
	public static final String [] EMPTY_ENTRIES_INPUT = 
		new String[] {"","One","Two","","","Three",""};
	public static final String EMPTY_ENTRIES_OUTPUT = 
		",One,Two,,,Three,";
	
	public static final String [] NEWLINE_INPUT = 
		new String[] {"One\nTwo\n\nThree","Four\n","Five","Six"};
	
	public static final String NEWLINE_OUTPUT = 
		"\"One\nTwo\n\nThree\",\"Four\n\",Five,Six";
	
	public static final String [] COMPLEX_INPUT = 
		new String[]{"\"One\n,,\nTwo\n,Three","","Four\"",",",
		"\"Five\","};
	
	public static final String COMPLEX_OUTPUT = 
		"\"\"\"One\n,,\nTwo\n,Three\",,\"Four\"\"\",\",\"," +
		"\"\"\"Five\"\",\"";
	
	
	public static String toString(String [] line)
	{
		if(line == null) return "";
		
		StringBuilder str = new StringBuilder(512);
		for(int i = 0; i < line.length; ++i)
		{
			if(i != 0)
				str.append(',');
			str.append(line[i]);
		}
		
		if(str.length() > 0)
			str.append("\n");
		
		return str.toString();
	}
	public static String toString(List<String[]> data)
	{
		StringBuilder str = new StringBuilder(512);
		for(String [] line : data)
			str.append(toString(line));
		return str.toString();
	}
	public static List<String[]> createWholeInput(boolean input)
	{
		List<String[]> data = new ArrayList<String[]>();
		
		data.add(SIMPLE_INPUT);
		data.add(SIMPLE_INPUT_2);
		if(input)
			data.add(EMPTY_INPUT);
		data.add(QUOTE_INPUT);
		data.add(COMPLEX_QUOTE_INPUT);
		data.add(ESCAPED_INPUT);
		data.add(EMPTY_ENTRIES_INPUT);
		data.add(NEWLINE_INPUT);
		data.add(COMPLEX_INPUT);
		
		return data;
		
	}
	
	public static boolean equals(List<String[]> lhs,List<String[]>rhs)
	{
		if(lhs == rhs) return true;
		if((lhs == null) != (rhs == null)) return false;
		if(lhs.size() != rhs.size()) return false;
		
		Iterator<String[]> li = lhs.iterator();
		Iterator<String[]> ri = rhs.iterator();
		
		while(li.hasNext())
		{
			String [] la = li.next();
			String [] ra = ri.next();
			
			if(!Arrays.deepEquals(la, ra))
				return false;
		}
		return true;
		
	}
 	public static String createWholeOutput(boolean output)
	{
 		StringBuilder str = new StringBuilder(2048);
 		
		str.append(SIMPLE_OUTPUT).append("\n");
		str.append(SIMPLE_OUTPUT_2).append("\n");
//		if(output)
//			str.append(EMPTY_OUTPUT).append("\n");
		str.append(QUOTE_OUTPUT).append("\n");
		str.append(COMPLEX_QUOTE_OUTPUT).append("\n");
		str.append(ESCAPED_OUTPUT).append("\n");
		str.append(EMPTY_ENTRIES_OUTPUT).append("\n");
		str.append(NEWLINE_OUTPUT).append("\n");
		str.append(COMPLEX_OUTPUT).append("\n");
		
		return str.toString();
	}
	
	
}
