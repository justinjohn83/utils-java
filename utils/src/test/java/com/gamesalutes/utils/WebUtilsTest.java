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

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class WebUtilsTest
{
	private static final String EQUAL_CODE = "%3D";
	private static final String AMP_CODE = "%26";
	private static final String SPC_CODE = "%20";
	
	private static final String SIMPLE_QUERY = 
		"first=test1&second=test2&third=test3";
	private static final Map<String,String> SIMPLE_MAP;
	
	private static final String ESCAPED_QUERY = 
		"first" + AMP_CODE + "=test" + SPC_CODE + "1" + 
        "&second=test2&" + AMP_CODE + SPC_CODE + "third" + SPC_CODE + AMP_CODE + EQUAL_CODE + AMP_CODE + EQUAL_CODE + "=test3";
	private static final Map<String,String> ESCAPED_MAP;
	
	static
	{
		Map<String,String> values = new LinkedHashMap<String,String>();
		
		values.put("first","test1");
		values.put("second", "test2");
		values.put("third", "test3");
		
		SIMPLE_MAP = Collections.unmodifiableMap(values);
		
		values = new LinkedHashMap<String,String>();
		
		values.put("first&","test 1");
		values.put("second", "test2");
		values.put("& third &=&=", "test3");
		
		ESCAPED_MAP = Collections.unmodifiableMap(values);
	}
	
	@Test
	public void testUrlEncodeSimple()
	{
		String actual = WebUtils.urlEncode(SIMPLE_MAP,false);
		assertEquals(SIMPLE_QUERY,actual);
		
	}
	
	@Test
	public void testURLEncodeEscaped()
		throws Exception
	{
		String actual = WebUtils.urlEncode(ESCAPED_MAP,false);
		assertEquals(ESCAPED_QUERY,actual);
	}
	@Test
	public void testSetQueryParameters()
		throws Exception
	{
		String uri = "http://www.example.com/test";
		String query = SIMPLE_QUERY;
		
		URI actual = WebUtils.setQueryParameters(new URI(uri), SIMPLE_MAP);
		
		String exp = uri + "?" + query;
		assertEquals(exp,actual.toString());
		
		
	}
	@Test
	public void testURLDecodeSimple()
	{
		Map<String,String> actual = WebUtils.urlDecode(SIMPLE_QUERY,false);
		
		assertEquals(SIMPLE_MAP,actual);
	}
	@Test
	public void testURLDecodeEscaped()
	{
		Map<String,String> actual = WebUtils.urlDecode(ESCAPED_QUERY,false);
		
		assertEquals(ESCAPED_MAP,actual);
	}
	
	@Test
	public void testGetQueryParameters()
		throws Exception
	{
		String uri = "http://www.example.com/test?" + SIMPLE_QUERY;
		
		Map<String,String> actual = WebUtils.getQueryParameters(new URI(uri));
		
		assertEquals(SIMPLE_MAP,actual);
	}
	
	@Test
	public void testEncodeUrlSimple()
	{
		String uri = "http://www.example.com/test/something.html";
		
		assertEquals(uri,WebUtils.urlEncode(uri));
		
		uri = "http://www.example.com/test spaces=/something&.html";
		String exp = uri.replace(" ", "%20");
		
		assertEquals(exp,WebUtils.urlEncode(uri));

	}
	@Test
	public void testEncodeUrlWithQuery()
	{
		String uri = "http://www.example.com/test/something.html?key1=value1&key2=value2";
		assertEquals(uri,WebUtils.urlEncode(uri));
		
		
		uri = "http://www.example.com/test/something.html?key1=value1&&key2=va=ue2";
		
		String exp = "http://www.example.com/test/something.html?key1=value1&" + 
			AMP_CODE + "key2=va" + EQUAL_CODE + "ue2";
		
		assertEquals(exp,WebUtils.urlEncode(uri));
		
	}
}
