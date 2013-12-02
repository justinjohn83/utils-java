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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.gamesalutes.utils.UtilsTestSuite;

public class FileProxyRetrieverStoreTest extends FileProxyRetrieverTest {

	@Before
	public void setUp() throws Exception
	{
		retriever = newRetriever();
		retriever.clear();
	}
	
	
	private FileProxyRetriever<String,String> newRetriever() throws IOException {
		return  new FileProxyRetriever<String,String>(
				getDir(),"file_proxy_test",true);
	}
	
	private File getDir() throws IOException
	{
//		File f = ItecoUtilsTestSuite.getTempDirectory();
		File f = File.createTempFile("tst",null);
		f = new File(f.getParentFile(),"FileProxyRetriever");
		f.mkdirs();
		return f;
	}
	
	private <S,T> Date getCreateDate(ProxyRetriever<S,T> retriever) {
		return ((FileProxyRetriever<S,T>)retriever).getCreateDate();
	}
	
	@Test
	public void testReload() throws IOException
	{
		retriever.put("test1", "test2");
		retriever.put("test3", "test4");
		
		Date createDate = getCreateDate(retriever);
		assertNotNull(createDate);
		
		retriever.dispose();
		retriever = newRetriever();
		
		assertEquals("test2",retriever.lookup("test1").second);
		assertEquals("test4",retriever.lookup("test3").second);
		
		assertEquals(createDate,getCreateDate(retriever));
		
		retriever.remove("test3");
		retriever.dispose();
		
		retriever = newRetriever();
		assertEquals("test2",retriever.lookup("test1").second);
		assertFalse(retriever.lookup("test3").first);
		
		retriever.put("blah","blah");
		
		assertEquals(createDate,getCreateDate(retriever));
		
		retriever.clear();
		
		// should update create date
		assertTrue(createDate.compareTo(getCreateDate(retriever)) < 0);
		
		createDate = getCreateDate(retriever);
		
		retriever = newRetriever();
		
		assertFalse(retriever.lookup("blah").first);
		assertFalse(retriever.lookup("test1").first);
		
		assertEquals(createDate,getCreateDate(retriever));

		
		
		
		
	}
	
	@Test
	public void testCloseOnClear() throws Exception
	{
		retriever.put("test1", "test2");
		retriever.put("test3", "test4");
		
		// now delete backing file try to clear - should be successful
		File dir = getDir();
		boolean deleted = dir.delete();
//		assertFalse(dir.canWrite());
		//assertTrue(deleted);
		
		retriever.clear();
		
		assertFalse(retriever.lookup("test1").first);
		assertFalse(retriever.lookup("test2").first);
		
	}
}
