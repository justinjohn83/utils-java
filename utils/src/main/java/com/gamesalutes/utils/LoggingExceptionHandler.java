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

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// could put this in com.gamesalutes.utils but then add new coupling
// to log4j
/**
 * Exception handler that logs the uncaught exception using log4j.
 * 
 * @author Justin Montgomery
 * @version $Id: LoggingExceptionHandler.java 2015 2010-04-02 18:41:05Z jmontgomery $
 *
 */
public final class LoggingExceptionHandler implements UncaughtExceptionHandler
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public LoggingExceptionHandler() {}
	
	public void uncaughtException(Thread t, Throwable e)
	{
		logger.error("Uncaught exception in " + t,e);
	}
	
	/**
	 * Convenience method for registering this handler as the default exception
	 * handler.
	 * 
	 */
	public static void registerAsDefaultHandler()
	{
		if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof LoggingExceptionHandler))
		{
			UncaughtExceptionHandler h = new LoggingExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(h);
		}
	}

}
