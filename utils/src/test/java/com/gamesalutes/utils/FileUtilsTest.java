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

import java.io.ByteArrayInputStream;
import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.junit.*;
import org.slf4j.Logger;
/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class FileUtilsTest
{
	@Test
	public void testGetPathToClass()
	{
		String codeFile = FileUtils.getPathToClass(FileUtils.class);
		assertTrue("codeFile=" + codeFile,codeFile != null && codeFile.endsWith(
				FileUtils.uriDecodeFile("edu/uchicago/nsit/iteco/utils/FileUtils.class")));
		assertTrue("codeFile=" + codeFile,new File(codeFile).exists());
		
		String jarFile = FileUtils.getPathToClass(Logger.class);
		assertTrue("jarFile=" + jarFile,jarFile != null && jarFile.endsWith(
				FileUtils.uriDecodeFile("slf4j-api-1.6.1.jar")));
		assertTrue("jarFile=" + jarFile,new File(jarFile).exists());
	}
	
	@Test
	public void testGetDirectory()
	{
		String codeDir = FileUtils.getDirectory(FileUtils.class);
		//directory should contain package name structure
		assertTrue("codeDir=" + codeDir,new File(codeDir,FileUtils.class.getPackage().getName().replace(".",FileUtils.getPlatformSeparator())).exists());
		assertTrue("codeDir=" + codeDir,new File(codeDir).isAbsolute());
		
		String libDir = FileUtils.getDirectory(Logger.class);
		assertTrue("libDir=" + libDir,libDir != null && new File(libDir,"slf4j-api-1.6.1.jar").exists());
		assertTrue("libDir=" + libDir,new File(libDir).isAbsolute());

	}

        @Test
        public void testReadDataInputStream()
                throws Exception
        {
            String data = "My name is Earl.\tI am so cool and funny.\n I have my own tv show!\r\n";

            InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

            String actual = FileUtils.readData(in);

            assertEquals(data,actual);
        }

        @Test
        public void testReadDataReader()
                throws Exception
        {
            String data = "My name is Earl.\tI am so cool and funny.\n I have my own tv show!\r\n";

            Reader r = new StringReader(data);

            String actual = FileUtils.readData(r);

            assertEquals(data,actual);
        }
}
