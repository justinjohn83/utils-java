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
/* Copyright 2008 University of Chicago
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

import java.text.ParseException;

/**
 * Allows exception chaining of <code>ParseException</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: ChainedParseException.java 1183 2008-11-14 00:14:48Z jmontgomery $
 */
public class ChainedParseException extends ParseException 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor.
	 * 
	 * @param s the message string
	 * @param errorOffset position of where error occurred during parsing 
	 * 
	 */
	public ChainedParseException(String s, int errorOffset) 
	{
		super(s, errorOffset);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param s the message string
	 * @param errorOffset position of where error occurred during parsing 
	 * @param cause the underlying cause of the exception
	 * 
	 */
	public ChainedParseException(String s,int errorOffset,Throwable cause)
	{
		super(s,errorOffset);
		initCause(cause);
	}

}
