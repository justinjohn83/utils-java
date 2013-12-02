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
 * Captures output written from a ByteArrayOutputStream into a List of Strings.
 *  
 * @author Justin Montgomery
 * @version $Id: ByteArrayReader.java 697 2008-02-20 18:29:39Z jmontgomery $
 *
 */
public class ByteArrayReader 
{
	private ByteArrayOutputStream rawOut;
	/**
	 * Constructor.
	 * 
	 * @param out <code>ByteArrayOutputStream</code> to capture the output of
	 */
	public ByteArrayReader(ByteArrayOutputStream out)
	{
		rawOut = out;
	}
	/**
	 * Gets data written to stream since last call to this method and
	 * puts results in a <code>List</code> of strings from the line-separated
	 * output.
	 * 
	 * @return <code>List</code> of data strings
	 */
	public List<String> readData()
	{
		byte [] bytes = rawOut.toByteArray();
		String str = new String(bytes);
		String [] strs = str.split(MiscUtils.LINE_BREAK_REGEX);
		List<String> data = new ArrayList<String>();
		for(String s : strs)
			data.add(s);
		rawOut.reset();
		return data;
	}
}
