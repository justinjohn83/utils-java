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
package com.gamesalutes.utils.arg;

import java.util.ArrayList;
import java.util.List;

import com.gamesalutes.utils.MiscUtils;

/**
 * Utility class for working with common command line argument functions.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ArgUtils
{
	private static final char QUOTE = '\"';
	private static final char SEP = ' ';
	
	public static String [] extractStringValues(String [] rawValues)
	{
		if(rawValues == null) return new String[0];
		
		// uses quotes to get the real arguments
		List<String> values = new ArrayList<String>(rawValues.length);
		
		boolean inQuote = false;
		
		StringBuilder valueBuf = new StringBuilder();
		
		int quoteValueBeginIndex = 0;
		int quoteStringBeginIndex = 0;
		
		for(int i = 0; i < rawValues.length; ++i)
		{
			String rawValue = rawValues[i];
			if(MiscUtils.isEmpty(rawValue)) 
				continue;
			
			int j = 0;
			int len = rawValue.length();
			
			// valid quotes only at beginning or end of a token
			j = rawValue.indexOf(QUOTE);
                        int k = rawValue.lastIndexOf(QUOTE);
			if((inQuote && j == len - 1) || (j == 0 && k == len - 1))
			{
				// grab previous data
                                String value;
                                if(j != 0)
                                {
                                    value = appendStringValues(
						rawValues,
						quoteValueBeginIndex,i,
						quoteStringBeginIndex,j,
						valueBuf);
                                }
                                else
                                {
                                    value = rawValue.substring(1,len - 1);
                                }

				
				if(!MiscUtils.isEmpty(value))
				{
					inQuote = false;

					values.add(value);
				}
			}
			else if(!inQuote)
			{
				if(j == 0)
				{
					inQuote = true;
					
					// set the state vars
					quoteValueBeginIndex = i;
					quoteStringBeginIndex = j + 1;
				}
				else // just append as normal
				{
					values.add(rawValue);
				}
			}
		} // for
		
		// add trailing output
		if(inQuote)
		{
			String value = appendStringValues(
					rawValues,
					quoteValueBeginIndex,-1,
					-1,-1,valueBuf);
			if(!MiscUtils.isEmpty(value))
				values.add(value);
		}
		
		return values.toArray(new String[values.size()]);
	}
	
	private static String appendStringValues(String [] data,int begin,int end,int valueBegin,int valueEnd,StringBuilder store)
	{
		if(store == null)
			store = new StringBuilder();
		
		MiscUtils.clearStringBuilder(store);
		
		if(end < 0 || end >= data.length)
			end = data.length - 1;
		if(valueBegin < 0)
			valueBegin = 0;
		if(valueEnd < 0)
			valueEnd = data[end].length();
		
		// grab previous data
		for(int k = begin; k <= end; ++k)
		{
			String quoteValue = data[k];
			
			// don't append empty values
			if(MiscUtils.isEmpty(quoteValue))
				continue;
			
			// grab substring
			if(k == begin)
			{
				if(begin != end)
					store.append(quoteValue.substring(valueBegin));
				// make sure string is not empty
				else if(valueEnd > valueBegin)
					store.append(quoteValue.substring(valueBegin,valueEnd));
					
			}
			else if(k == end)
			{
				store.append(quoteValue.substring(0,valueEnd));
			}
			// append whole string
			else
			{
				store.append(quoteValue);
			}
			
			// manually append a space if more stuff to process
			if(k < end)
				store.append(SEP);
		} // for
		
		return store.toString();
	}
	
	/**
	 * Quotes the string value if it contains spaces.
	 * 
	 * @param value the value
	 * @return the quote-escaped value
	 */
	public static String quoteStringValue(String value)
	{
		if(value == null) return null;
		
		boolean isSpc = false;
		for(int i = 0, len = value.length(); i < len; ++i)
		{
			if(Character.isSpaceChar(value.charAt(i)))
			{
				isSpc = true;
				break;
			}
		}
		
		if(isSpc)
		{
			StringBuilder buf = new StringBuilder(value.length() + 2);
			buf.append(QUOTE).append(value).append(QUOTE);
			return buf.toString();
		}
		return value;
	}
}
