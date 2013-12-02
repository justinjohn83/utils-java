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
import static org.junit.Assert.*;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ArgUtilsTest
{
	@Test
	public void testQuoteStringValue()
	{
		// no spaces
		String s = "test";
		assertEquals(s,ArgUtils.quoteStringValue(s));
		
		// null
		assertNull(ArgUtils.quoteStringValue(null));
		
		// needs quoting
		s = "test test2 test3";
		String exp = "\"" + s + "\"";
		assertEquals(exp,ArgUtils.quoteStringValue(s));
		
	}
	
	@Test
	public void testExtractStringValues()
	{
		String [] args = new String[] {"test"};
		String [] exp = args;
		
		// simple case
		assertArrayEquals(exp,ArgUtils.extractStringValues(args));
		
		// null case
		assertArrayEquals(new String[0],ArgUtils.extractStringValues(null));
		
		// multi case
		args = new String[] {"test","test2","test3"};
		exp = args;
		assertArrayEquals(exp,ArgUtils.extractStringValues(args));
		
		// all quoted case
		args = new String[] {"\"test","test2","test3\""};
		exp = new String[] {"test test2 test3"};
		
		assertArrayEquals(exp,ArgUtils.extractStringValues(args));
		
		// mixed quoted
		args = new String[] {"-c -f","\"some","file.txt\"","\"some","other","file.txt\"","-n"};
		exp = new String[] {"-c -f","some file.txt","some other file.txt","-n"};
		
		assertArrayEquals(exp,ArgUtils.extractStringValues(args));

                args = new String[] {"test","test2","\"--not_option\"","\"test","3\""};
                exp = new String[] {"test","test2","--not_option","test 3"};

                assertArrayEquals(exp,ArgUtils.extractStringValues(args));

		
	}

}
