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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;

import java.util.List;
import static org.junit.Assert.*;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public class ReverseFileTest
{
	private static final List<String> EXPECTED = 
		Arrays.asList("",
					  "tired",
					  "is",
					  "dumpty",
					  "humpty",
					  "and",
					  "hill",
					  "the",
					  "up",
					  "went",
					  "Jill",
					  "and",
					  "Jack");
	
	private static final String TEST_FILE = FileUtils.getFile(ReverseFileTest.class, "ReverseFileTest.txt");
	
	@Test(timeout = 5000)
	public void testRead()
		throws IOException
	{
		ReverseFileReader r = new ReverseFileReader(TEST_FILE);
		List<String> data = new ArrayList<String>();
		String line = null;
		while((line = r.readLine()) != null)
			data.add(line);
		r.close();
		assertEquals(EXPECTED,data);
	}
}
