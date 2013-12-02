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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;


/**
 * @author Justin Montgomery
 * @deprecated
 * @version $Id:$
 */
@Deprecated
public final class LoggingUtils
{
	private static boolean loggingInit;

	
	private LoggingUtils() {}


        public static synchronized void initializeLogging(String logConfFile)
        {
            if(logConfFile == null)
                throw new NullPointerException("logConfFile");
            
            if(loggingInit) return;

            try
            {
                initializeLogging(new BufferedInputStream(new FileInputStream(logConfFile)));
            }
            catch(Exception e)
            {
                System.err.println("Unable to load logging config ; using default logging config");
                e.printStackTrace(System.err);
                initializeLogging();
            }

            loggingInit = true;
        }

        public static synchronized void initializeLogging(InputStream in)
        {
            if(in == null)
                throw new NullPointerException("in");

            if(loggingInit) return;

            try
            {
                initializeLogging(FileUtils.loadPropertiesFile(in));
            }
            catch(Exception e)
            {
                System.err.println("Unable to load logging config ; using default logging config");
                e.printStackTrace(System.err);
                initializeLogging();
            }

            loggingInit = true;
        }
	/**
	 * Initializes the log4j subsystem.
	 * 
	 * @param logConf the logging conf <code>Properties</code>
	 */
	public static synchronized void initializeLogging(Properties logConf)
	{
            if(logConf == null)
                    throw new NullPointerException("logConf");

		if(loggingInit) return;
		
		try
		{
			PropertyConfigurator.configure(logConf);
		}
		catch(Exception e)
		{
			System.err.println("Unable to load logging config ; using default logging config");
			e.printStackTrace(System.err);
			BasicConfigurator.configure();
		}
		// register Log4j as the default exception handler
		LoggingExceptionHandler.registerAsDefaultHandler();
		
		loggingInit = true;
		
	}

	/**
	 * Default initializes the log4j subsystem.
	 * 
	 */
	public static synchronized void initializeLogging()
	{
            if(loggingInit) return;

            BasicConfigurator.configure();

            // register Log4j as the default exception handler
            LoggingExceptionHandler.registerAsDefaultHandler();

            loggingInit = true;

	}
	
	
}
