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
import java.io.IOException;
/**
 * Wrapped version of IOException that allows exception chaining in the 
 * constructors.  This class can be used
 * in place of creating an instance of java.io.IOException in versions before Java 6
 * if exception chaining in the constructor
 * is desired since those versions of IOException do not support it.
 * 
 * @author Justin Montgomery
 * @version $Id: ChainedIOException.java 697 2008-02-20 18:29:39Z jmontgomery $
 *
 */
public class ChainedIOException extends IOException
{
	private static final long serialVersionUID = 1;
	
	public ChainedIOException() {}

	/**
	 * Constructor.
	 * 
	 * @param message the message 
	 */
	public ChainedIOException(String message)
	{
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause the underlying cause of this exception
	 */
	public ChainedIOException(Throwable cause)
	{
		initCause(cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message the message
	 * @param cause the underlying cause of this exception
	 */
	public ChainedIOException(String message, Throwable cause)
	{
		super(message);
		initCause(cause);
	}

}
