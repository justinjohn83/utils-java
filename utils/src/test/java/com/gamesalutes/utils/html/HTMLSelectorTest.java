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
package com.gamesalutes.utils.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.gamesalutes.utils.FileUtils;
import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.StringUtils;

public class HTMLSelectorTest {

	
	private static final int BUFFER_SIZE = 32;
	
	private Reader getInputResource(String name) throws IOException {
		InputStream in = getClass().getResourceAsStream("input/" + name);
		return new BufferedReader(new InputStreamReader(in,"UTF-8"));
	}
	
	private String getExpectedOutput(String inputName) throws IOException {
		InputStream in = getClass().getResourceAsStream("output/" + inputName);
		
		return FileUtils.readData(new BufferedInputStream(in));
	}
	private String getExpectedOutputListener(String inputName) throws IOException {
		InputStream in = getClass().getResourceAsStream("output/" + inputName.replace(".html", "") + ".listener.html");
		
		return FileUtils.readData(new BufferedInputStream(in));
	}
	
	@Test
	public void testSimpleTag() throws Exception {
		
		String fileName = "simpleTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutput(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, false,BUFFER_SIZE);
		
		StringWriter actual = new StringWriter();
		parser.parse(in, actual);
		
		assertEquals(expected,actual.toString());
	}
	
	@Test
	public void testSimpleTagWithTagListener() throws Exception {
		String fileName = "simpleTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutputListener(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, false,BUFFER_SIZE);

		_TagListener listener = new _TagListener("td");
		
		parser.process(in, listener);
		
		assertEquals(expected,listener.toString());
	}
	

	
	@Test
	public void testMultiTag() throws Exception {
		String fileName = "multiTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutput(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, true,BUFFER_SIZE);
		
		StringWriter actual = new StringWriter();
		parser.parse(in, actual);
		
		assertEquals(expected,actual.toString());
	}
	
	@Test
	public void testMultiTagListener() throws Exception {
		String fileName = "multiTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutputListener(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, true,BUFFER_SIZE);

		_TagListener listener = new _TagListener("td");
		
		parser.process(in, listener);
		
		assertEquals(expected,listener.toString());
	}
	
	@Test
	public void testAttributeTag() throws Exception{
		String fileName = "attributeTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutput(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", "100%", null, false,BUFFER_SIZE);
		
		StringWriter actual = new StringWriter();
		parser.parse(in, actual);
		
		assertEquals(expected,actual.toString());
	}
	
	@Test
	public void testAttributeTagListener() throws Exception {
		String fileName = "attributeTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutputListener(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, false,BUFFER_SIZE);

		_TagListener listener = new _TagListener("td");
		
		parser.process(in, listener);
		
		assertEquals(expected,listener.toString());
	}
	
	@Test
	public void testMultiAttributeTag() throws Exception {
		String fileName = "multiAttributeTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutput(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", "100%", "searchText", true,BUFFER_SIZE);
		
		StringWriter actual = new StringWriter();
		parser.parse(in, actual);
		
		assertEquals(expected,actual.toString());
	}
	
	@Test
	public void testMultiAttributeTagListener() throws Exception {
		String fileName = "multiAttributeTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutputListener(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, true,BUFFER_SIZE);

		_TagListener listener = new _TagListener("td");
		
		parser.process(in, listener);
		
		// TODO:
		assertTrue(listener.toString().length() > 0);
		
		//assertEquals(expected,listener.toString());
	}
	
	@Test
	public void testMultiNestedTag() throws Exception {
		String fileName = "multiNestedTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutput(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, true,BUFFER_SIZE);
		
		StringWriter actual = new StringWriter();
		parser.parse(in, actual);
		
		assertEquals(expected,actual.toString());
	}
	
	@Test
	public void testMultiNestedTagListener() throws Exception {
		String fileName = "multiNestedTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutputListener(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, true,BUFFER_SIZE);

		_TagListener listener = new _TagListener("td");
		
		parser.process(in, listener);

		// TODO:
		assertTrue(listener.toString().length() > 0);
		
		//assertEquals(expected,listener.toString());
	}
	
	@Test
	public void testMultiNestedAttributeTag() throws Exception {
		
		String fileName = "multiNestedAttributeTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutput(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", "100%", "searchText", true,BUFFER_SIZE);
		
		StringWriter actual = new StringWriter();
		parser.parse(in, actual);
		
		assertEquals(expected,actual.toString());
	}
	
	@Test
	public void testMultiNestedAttributeTagListener() throws Exception {
		String fileName = "multiNestedAttributeTag.html";
		
		Reader in = getInputResource(fileName);
		String expected = getExpectedOutputListener(fileName);
		
		HTMLSelector parser = new HTMLSelector("table", null, null, true,BUFFER_SIZE);

		_TagListener listener = new _TagListener("td");
		
		parser.process(in, listener);
		
		// TODO:
		assertTrue(listener.toString().length() > 0);
		
		//assertEquals(expected,listener.toString());
	}
	
	
	private static class _TagListener implements TagListener {

		private StringBuilder buf = new StringBuilder();
		
		private final String tag;
		public _TagListener(String textTag) {
			this.tag = textTag;
		}
		
		public _TagListener() {
			this.tag = null;
		}
		public TagAttributes onStartTag(String tagName) {
			if(tag == null) {
				buf.append('<').append(tagName);
			}
			
			if(tag != null) {
				if(tag.equalsIgnoreCase(tagName)) {
					return new TagAttributes(tagName).setAttributes(Arrays.asList("a1","a2","a3")).setText(tagName.equalsIgnoreCase(tag));
				}
				else {
					return null;
				}
			}
			else {
				return new TagAttributes(tagName).setAttributes(Arrays.asList("a1","a2","a3")).setText(true);

			}
			
		}


		public boolean onEndTag(TagAttributes attr) {
			if(tag == null) {
				buf.append("</").append(attr.getTagName()).append(">");
			}
			return true;
		}

		public void onText(TagAttributes attr, String text) {
			// let's ignore spaces
			for(String s : StringUtils.spaceSplit(text)) {
				buf.append(s);
			}
		}

		public boolean onAttributes(TagAttributes attr, List<String> attributeValues) {
			if(tag == null) {
				for(int i = 0; i < attr.getAttributes().size(); ++i) {
					String name = attr.getAttributes().get(i);
					String value = attributeValues.get(i);
					if(!MiscUtils.isEmpty(value)) {
						buf.append(' ').append(name).append("=").append(value);
					}
				}
			}
			
			return true;
		}
		
		public String toString() { 
			return buf.toString();
		}

		public void onStartDocument() {
			// TODO Auto-generated method stub
			
		}

		public void onEndDocument() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
