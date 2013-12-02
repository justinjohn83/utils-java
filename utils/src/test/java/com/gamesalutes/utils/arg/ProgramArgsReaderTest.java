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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.*;

import com.gamesalutes.utils.MiscUtils;
import static org.junit.Assert.*;
/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ProgramArgsReaderTest
{
	public static class ClassA {}
	public static class ClassB {}
	public static class ClassC {}
	
	private static final URL TEST_DATA = 
		ProgramArgsReaderTest.class.getResource("test_args.xml");
	
	@Test
	public void test()
		throws Exception
		{
		
		List<ProgramArgsReader.Program> expected = 
			new ArrayList<ProgramArgsReader.Program>();
		
		Collections.addAll(expected,
				createProgram(ClassA.class,"",1),
				createProgram(ClassB.class,"-a \"test 1\" test \"test 2\" test3",2),
				createProgram(ClassA.class,"-f dns_zones.dat",3),
				createProgram(ClassC.class,"-O logs/hi.log data/him.csv",4),
				createProgram(ClassB.class,
						"--dn --cluster_sep : --datacenters consoles.dat data/vi_console.txt logs/vi.log",
						6));
		
		List<ProgramArgsReader.Program> actual = new ProgramArgsReader(
				TEST_DATA.openStream()).getPrograms();
		
		assertEquals(expected,actual);
	}
	
	private ProgramArgsReader.Program createProgram(
			Class<?> clazz,String args,int order)
	{
		return new ProgramArgsReader.Program(
			clazz,MiscUtils.split(args, " "),order);
	}
}
