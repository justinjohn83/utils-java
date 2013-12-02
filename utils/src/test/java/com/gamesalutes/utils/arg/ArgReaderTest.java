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

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

import com.gamesalutes.utils.MiscUtils;

import static org.junit.Assert.*;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ArgReaderTest
{
        private static class DateValue implements ArgValue
        {
            public Object getValue(String value)
            {
                try
                {
                    return new SimpleDateFormat("DD/MM/yyyy").parse(value);
                }
                catch(ParseException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
	@Test
	public void testReadSimpleArgument()
		throws ArgumentException
	{
		ArgReader r = new ArgReader();
		String [] args = { "test" };
		
		r.parse(args);
		assertArrayEquals(args,r.getRemainingArguments());
	}

	
	@Test
	public void testReadMultiArguments()
		throws ArgumentException

	{
		ArgReader r = new ArgReader();
		
		String [] args = createArgs("test test2 \"--not_option\" \"test 3\"");
		String [] exp = new String[] {"test","test2","--not_option","test 3",};
		r.parse(args);
		
		assertArrayEquals(exp,r.getRemainingArguments());
	}
	
	@Test
	public void testReadOptions()
		throws ArgumentException

	{
		ArgReader r = new ArgReader();
		r.addBooleanOption("create");
		r.addBooleanOption("r");
		r.addBooleanOption("s");
		r.addBooleanOption("v");
		r.addStringOption('f',"file");
		r.addStringOption('t',"test");
                r.addObjectOption("d",new DateValue());
		
		String [] args = createArgs(
				"--create -d 08/06/2010 -r --s -f file.txt --test file2.txt -t \"file 3.txt\"");
		
		r.parse(args);
		
		assertTrue(r.getRemainingArguments().length == 0);
		
		assertTrue((Boolean)r.getOptionValue("r"));
		assertTrue((Boolean)r.getOptionValue("s"));
		assertFalse((Boolean)r.getOptionValue("v"));
		assertTrue((Boolean)r.getOptionValue("create"));
		assertEquals("file.txt",r.getOptionValue("f"));

                Object o = r.getOptionValue("d");
                assertTrue(o instanceof Date);
		
		List<Object> exp = Arrays.asList
			(new Object[] {"file2.txt","file 3.txt"});
		
		assertEquals(exp,r.getOptionValues("test"));
		
	}
	
	@Test
	public void testReadMixed()
		throws ArgumentException
	{
		ArgReader r = new ArgReader();
		r.addBooleanOption("create");
		r.addStringOption('f',"file");
		
		String [] args = createArgs(
				"--create -f file.txt --file file2.txt -f \"file 3.txt\" \"End of Line\" \"--not_option\" Dr.");
		
		r.parse(args);
		
		assertTrue((Boolean)r.getOptionValue("create"));
		
		List<Object> exp = Arrays.asList
		(new Object[] {"file.txt","file2.txt","file 3.txt"});
	
		assertEquals(exp,r.getOptionValues("file"));
		
		String [] expArgs = {"End of Line","--not_option","Dr."};
		
		assertArrayEquals(expArgs,r.getRemainingArguments());
	}
	
	private static String[] createArgs(String s)
	{
		return MiscUtils.split(s, " ");
	}
	
}
