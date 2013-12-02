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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import org.junit.Test;

import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.StringUtils;

public class HtmlCharSequenceLexerTest {

	@Test
	public void testAttrPattern() {
		String s = "a1='test'";
		
		assertEquals("a1=test",getActual(s));

	}
	
	@Test
	public void testAttrPattern2() {
		String s = "  a1 = \"test\" b='test2' c2 =\"test3'";
		
		assertEquals("a1=test;b=test2;c2=test3",getActual(s));
	}
	
	@Test
	public void testSingleTag() {
		String s = "<td></td>";
		
		_TagListener t = new _TagListener();
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		
		assertEquals(s,t.toString());
	}
	
	@Test
	public void testSingleTagWithAttribute() {
		String s = "<td a1='test1\" a2='test2'  ></td>";
		
		_TagListener t = new _TagListener();
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		String expected = "<td a1=test1 a2=test2></td>";
		assertEquals(expected,t.toString());
	}
	
	@Test
	public void testMultiTag() {
		String s = "<td a1='test1\" a2='test2'  >column1</td>  <td a3='test3' b=\"chump\">column2</td>";
		
		_TagListener t = new _TagListener();
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		String expected = "<td a1=test1 a2=test2>column1</td><td a3=test3>column2</td>";
		assertEquals(expected,t.toString());
	}
	
	@Test
	public void testMultiNestedTag() {
		String s = "<tr><td a1='test1\" a2='test2'  >column1</td>  <td a3='test3' b=\"chump\">column2</td></tr>";
		
		_TagListener t = new _TagListener("td");
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		String expected = "<tr><td a1=test1 a2=test2>column1</td><td a3=test3>column2</td></tr>";
		assertEquals(expected,t.toString());
	}
	
	@Test
	public void testNestedTextTag() {
		String s = "<tr><td>Test <b>Today</b> study</td></tr>";
		
		TextListener t = new TextListener("td");
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		String expected = "Test Today study";
		assertEquals(expected,t.toString());
	}
	
	@Test
	public void testMultiNestedTextTag() {
		String s = "<table><tr><td>Test <b>Today</b> study</td></tr></table>";
		
		TextListener t = new TextListener("tr");
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		String expected = "Test Today study";
		assertEquals(expected,t.toString());
	}
	
	@Test
	public void testMultiNestedDeepTextTag() {
		String s = "<table><tr><td>Test <span>Today <b>Study</b></span> hard!</td></tr></table>";
		
		TextListener t = new TextListener("tr");
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		String expected = "Test Today Study hard!";
		assertEquals(expected,t.toString());
	}
	
	@Test
	public void testManyNestedTagEmpty() {
		String s = "                        <tr class=\"table-item t11 highlight-item-row\">\n" + 
				"                                <td class=\"w35 click-toggle-row expandRowDown expandRowUp\">\n" + 
				"                                    <div class=\"section-meeting\">\n" + 
				"                                        <a title=\"hide section detail\" class=\"toggle-control\" href=\"#link11\">\n" + 
				"                                            <img class=\"section-detail-control\" src=\"/cis-pac/rs/portlets/cis/img/asc.gif\" style=\"padding-bottom:5px;margin-left:-4px;\"/>\n" + 
				"                                        </a>\n" + 
				"                                    </div>\n" + 
				"                                </td>";
		TextListener t = new TextListener("td");
		HtmlCharSequenceLexer lexer = new HtmlCharSequenceLexer();
		lexer.process(s, t);
		String actual = MiscUtils.removeLineBreaks(StringUtils.trimEmpty(t.toString()));
		String expected = "";
		assertEquals(expected,actual);
	}
	
	private String getActual(String s) {
		Matcher m = HtmlCharSequenceLexer.ATTR_PATTERN.matcher(s);
		StringBuilder actual = new StringBuilder();
		while(m.find()) {
			String name = m.group(1);
			String value = m.group(3);
			
			if(actual.length() != 0) {
				actual.append(";");
			}
			actual.append(name).append('=').append(value);
		}
		
		return actual.toString();
	}
	
	private static class TextListener implements TagListener {

		private StringBuilder buf = new StringBuilder();
		private final String tag;
		
		public TextListener(String tag) {
			this.tag = tag;
		}
		public TagAttributes onStartTag(String tagName) {
			if(tagName.equalsIgnoreCase("td")) {
				return new TagAttributes(tagName).setText(true);
			}
			return null;	
		}


		public boolean onEndTag(TagAttributes tagName) {
			return true;
		}

		public void onText(TagAttributes attr, String text) {
			buf.append(text);
		}

		public boolean onAttributes(TagAttributes attr, List<String> attributeValues) {
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
			buf.append('<').append(tagName);
			
			if(tag != null) {
				return new TagAttributes(tagName).setAttributes(Arrays.asList("a1","a2","a3")).setText(tagName.equalsIgnoreCase(tag));
			}
			else {
				return new TagAttributes(tagName).setAttributes(Arrays.asList("a1","a2","a3")).setText(true);

			}
		}

		public boolean onEndTag(TagAttributes attr) {
			buf.append("</").append(attr.getTagName()).append(">");
			return true;
		}

		public void onText(TagAttributes attr, String text) {
			buf.append(text);
		}

		public boolean onAttributes(TagAttributes attr, List<String> attributeValues) {
			for(int i = 0; i < attr.getAttributes().size(); ++i) {
				String name = attr.getAttributes().get(i);
				String value = attributeValues.get(i);
				if(!MiscUtils.isEmpty(value)) {
					buf.append(' ').append(name).append("=").append(value);
				}
			}
			buf.append('>');
			
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
