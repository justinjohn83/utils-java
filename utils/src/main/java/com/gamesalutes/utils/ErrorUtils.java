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

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Utilities for dealing with exceptions.
 * 
 * @author Justin Montgomery
 * @version $Id: ErrorUtils.java 2749 2011-04-08 22:45:42Z jmontgomery $
 *
 */
public final class ErrorUtils
{
	private ErrorUtils() {}
	
	/**
	 * Returns the stack trace of <code>t</code> as a string with each
	 * element of the trace separated by a newline character.
	 * 
	 * @param t <code>Throwable</code> object
	 * @return String form of full stack trace of <code>t</code>
	 */
	public static String getStackTraceAsStr(Throwable t)
	{
//		StringBuilder str = new StringBuilder();
//		// get all causes
//		int count = 0;
//		do
//		{
//			if(++count > 1)
//				str.append("Caused by: ");
//			str.append(t);
//			str.append("\n");
//			str.append(getStackTraceAsStr(t.getStackTrace(),true));		
//		}
//		while((t = t.getCause()) != null);
//		
//		return str.toString();
		
		StringWriter sw = new StringWriter(2048);
		PrintWriter writer = new PrintWriter(sw);
		t.printStackTrace(writer);
                writer.flush();
		return sw.toString();

	}
	
	/**
	 * Returns the stack trace as a string with each
	 * element of the trace separated by a newline character.
	 * 
	 * @param ste the stack trace
	 * @param indent <code>true</code> to tab each stack trace element
	 * @return String form of full stack trace
	 */
	public static String getStackTraceAsStr(StackTraceElement [] ste,boolean indent)
	{
		StringBuilder str = new StringBuilder(256);
		
		for(StackTraceElement e : ste)
		{
			if(indent)
				str.append("\t");
			str.append(e);
			str.append("\n");
		}
		return str.toString();
	}
	
	/**
	 * Gets the current stack trace as a string.
	 * 
	 * @return the current stack trace as a string
	 */
	public static String getCurrentStackTraceAsStr()
	{
		return getStackTraceAsStr(
				Thread.currentThread().getStackTrace(), true);
	}
	
	/**
	 * Rethrows the caught throwable as the specified checked exception class if it 
	 * is a checked exception.  Otherwise, it is thrown as an unchecked exception.
	 * If the exception is checked but not an instance of the specified exception class,
	 *  then an <code>AssertionError</code> will be thrown.
	 * 
	 * @param t the <code>Throwable</code>
	 * @param checkedClass the <code>Class</code> of the checked exception
	 */
	public static <T extends Exception> void rethrowChecked(Throwable t, Class<T> checkedClass)
		throws T
	{
		if(t instanceof RuntimeException)
			throw (RuntimeException)t;
		if(t instanceof Error)
			throw (Error)t;
		if(checkedClass != null && checkedClass.isInstance(t))
			throw checkedClass.cast(t);
		AssertionError e = new AssertionError(
				"unexpected exception t=" + MiscUtils.getClassName(t));
		e.initCause(t);
		throw e;
	}
	
	/**
	 * Rethrows the caught throwable as an unchecked exception.  If the exception is checked, then
	 * an <code>AssertionError</code> will be thrown.
	 * 
	 * @param t
	 */
	public static void rethrowUnchecked(Throwable t)
	{
		if(t instanceof RuntimeException)
			throw (RuntimeException)t;
		if(t instanceof Error)
			throw (Error)t;
		AssertionError e = new AssertionError(
				"unexpected exception t=" + MiscUtils.getClassName(t));
		e.initCause(t);
		throw e;
	}
	
	/**
	 * Convenience method for initializing the root cause of throwable that do not accept the cause
	 * as a constructor parameter.
	 * 
	 * @param throwable the <code>Throwable</code> to throw
	 * @param cause the root cause of <code>throwable</code>
         * @return <code>throwable
	 */
	public static <T extends Throwable> T initWithCause(T throwable,Throwable cause)
	{
		throwable.initCause(cause);
		return throwable;
	}
}
