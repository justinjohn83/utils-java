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
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.gamesalutes.utils.MiscUtils;

/**
 * Test suite for all iteco components.
 * 
 * @author Justin Montgomery
 * @version $Id: ItecoAPITestSuite.java 1841 2010-01-07 22:12:11Z jmontgomery $
 *
 */
@RunWith(Suite.class)
@SuiteClasses({com.gamesalutes.utils.TestSuite.class,
			   com.gamesalutes.utils.logic.TestSuite.class,
			   com.gamesalutes.utils.arg.TestSuite.class,
			   com.gamesalutes.utils.graph.TestSuite.class}
		       )
		       
public class UtilsTestSuite
{
	private static final String tempDir = 
		UtilsTestSuite.class.getPackage().getName().replace(
				'.', '/') + "/temp";
		
		
	
	/**
	 * Gets a temporary directory for writing to temp files.
	 * 
	 * @return the temporary directory
	 */
	public static File getTempDirectory()
	{
		File f = new File(tempDir);
		f.mkdir();
		return f;
	}
	
	/**
	 * Creates a temporary file in the temp directory.
	 * 
	 * @return a new temp file
	 */
	public static File createTempFile()
		throws IOException
	{
		File f = File.createTempFile("tmp", ".tmp", getTempDirectory());
		f.deleteOnExit();
		return f;
	}
	/**
	 * Asserts that two objects are equal but not the same
	 * @param obj1 <code>Object</code> one
	 * @param obj2 <code>Object</code> two
	 */
	public static void assertEqualsButNotSame(Object obj1,Object obj2)
	{
		assertEquals(obj1,obj2);
		assertNotSame(obj1,obj2);
	}
	/**
	 * Suppresses the output of System.out and System.err.
	 *
	 */
	public static void suppressConsole()
	{
		MiscUtils.suppressConsole();
	}
	
	/**
	 * Restores System.out and System.err after a call to {@link #suppressConsole()}.
	 *
	 */
	public static void restoreConsole()
	{
		MiscUtils.restoreConsole();
	}
}
