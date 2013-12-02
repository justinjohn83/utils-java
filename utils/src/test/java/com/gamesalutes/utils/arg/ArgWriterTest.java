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

import org.junit.*;

import com.gamesalutes.utils.MiscUtils;
import static org.junit.Assert.*;
/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ArgWriterTest
{
	@Test
	public void testSimpleWrite()
	{
		ArgWriter writer = new ArgWriter();
		writer.addArgument("test");
		
		testEquals("test",writer);
	}
	
	@Test
	public void testMultiArgs()
	{
		ArgWriter writer = new ArgWriter();
		writer.addArgument("test1");
		writer.addArgument("test2");
		writer.addArgument("test 3");
		
		testEquals("test1 test2 \"test 3\"",writer);
	}
	
	@Test
	public void testOptions()
	{
		ArgWriter writer = new ArgWriter();
		writer.addOption("f", "file.txt");
		writer.addOption("create",null);
		
		testEquals("-f file.txt --create",writer);
	}
	
	@Test
	public void testMixed()
	{
		ArgWriter writer = new ArgWriter();
		
		writer.addOption("f","file.txt");
		writer.addArgument("The big cheese");
		writer.addArgument("stinks");
		
		testEquals("-f file.txt \"The big cheese\" stinks",writer);
	}
	
	private void testEquals(String exp,ArgWriter writer)
	{
		// string version
		assertEquals(exp,writer.toString());
		// args version
		assertArrayEquals(MiscUtils.split(exp, " "),writer.generate());
	}
}
