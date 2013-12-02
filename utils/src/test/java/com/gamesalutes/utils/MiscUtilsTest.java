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
/* Copyright 2008 University of Chicago
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 * @author Justin Montgomery
 * @version $Id: MiscUtilsTest.java 2709 2011-03-17 22:34:37Z jmontgomery $
 */
public class MiscUtilsTest
{
	private static final long TIMEOUT = 3000; //ms
	
	private static abstract class BaseCopy implements Serializable
	{
		private final String value;
		
		private static final long serialVersionUID = 1L;
		
		protected BaseCopy(String value)
		{
			this.value = value;
		}
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(!(o instanceof BaseCopy)) return false;
			
			return this.value.equals(((BaseCopy)o).value);
		}
		
		@Override
		public int hashCode() 
		{
			int result = 17;
			result = 31 * result + MiscUtils.safeHashCode(this.value);
			
			return result;
		}
		
		@Override
		public String toString()
		{
			return value;
		}
	}
	private static class CloneableCopy extends BaseCopy implements Cloneable
	{
		public CloneableCopy(String value)
		{
			super(value);
		}
		public CloneableCopy clone()
		{
			try
			{
				return (CloneableCopy)super.clone();
			}
			catch(CloneNotSupportedException e)
			{
				throw new AssertionError(e);
			}
		}

	}
	
	private static class SerializableCopy extends BaseCopy implements Serializable
	{
		private static final long serialVersionUID = 1L;
				
		public SerializableCopy(String value)
		{
			super(value);
		}
	}
	/**
	 * Tests {@link MiscUtils#copy(Object)}
	 */
	@Test
	public void testCopy()
	{
		CloneableCopy e = new CloneableCopy("elm");
		CloneableCopy copy = MiscUtils.copy(e);
		assertNotNull(copy);
		assertNotSame(e,copy);
		assertEquals(e,copy);
		assertNull(MiscUtils.copy(null));
	}
	
	@Test
	public void testCopySerialize()
	{
		// serializable but not cloneable
		SerializableCopy exp = new SerializableCopy("test");
		SerializableCopy copy = MiscUtils.copy(exp);
		assertNotSame(exp,copy);
		assertEquals(exp,copy);
		
	}
	/**
	 * Tests {@link MiscUtils#deepCollectionCopy(Collection)}.
	 */
	@Test
	public void testDeepCopyCollection()
	{
		ArrayList<CloneableCopy> elms = 
			new ArrayList<CloneableCopy>(3);
		CloneableCopy elm1 = new CloneableCopy("elm1");
		CloneableCopy elm2 = new CloneableCopy("elm2");
		elms.add(elm1);
		elms.add(elm2);
		elms.add(null);
		
		ArrayList<CloneableCopy> copy = 
			MiscUtils.deepCollectionCopy(elms);
		assertNotNull(copy);
		assertNotSame(elms,copy);
		assertEquals(elms,copy);
		assertNotSame(elms.get(0),copy.get(0));
		assertNotSame(elms.get(1),copy.get(1));
		assertSame(elms.get(2),copy.get(2));
		assertNull(copy.get(2));
		
					
	}
	
	@Test(timeout=TIMEOUT)
	public void testDeepCopyNestedCollection()
	{
		ArrayList<ArrayList<?>> elmMatrix = 
			new ArrayList<ArrayList<?>>();
		CloneableCopy elm1 = new CloneableCopy("elm1");
		CloneableCopy elm2 = new CloneableCopy("elm2");
		CloneableCopy elm3 = null;
		ArrayList<CloneableCopy> nested = new ArrayList<CloneableCopy>(2);
		Collections.addAll(nested, elm1,elm2,elm3);
		elmMatrix.add(nested);
		// try adding itself as element
		//elmMatrix.add(elmMatrix);
		ArrayList<ArrayList<?>> copy = 
			MiscUtils.deepCollectionCopy(elmMatrix);
		assertNotNull(copy);
		assertNotSame(elmMatrix,copy);
		assertEquals(elmMatrix,copy);
		assertNotSame(elmMatrix.get(0),copy.get(0));
		//assertNotSame(elmMatrix.get(1),copy.get(1));
		// make sure nested was also copied
		ArrayList<?> nestedCopy = copy.get(0);
		for(int i = 0; i < nestedCopy.size(); ++i)
		{
			if(nested.get(i) != null)
				assertNotSame(nestedCopy.get(i),nested.get(i));
			else
				assertNull(nestedCopy.get(i));
			//assertEquals(nestedCopy.get(i),nested.get(i));
		}
	}
	
	@Test
	public void testDeepCopyArray()
	{
		CloneableCopy elm1 = new CloneableCopy("elm1");
		CloneableCopy elm2 = new CloneableCopy("elm2");
		CloneableCopy elm3 = null;
		CloneableCopy [] elms = {elm1,elm2,elm3};
		CloneableCopy [] copy = MiscUtils.deepArrayCopy(elms);
		assertNotNull(copy);
		assertNotSame(elms,copy);
		assertArrayEquals(elms,copy);
		for(int i = 0; i < copy.length; ++i)
		{
			if(elms[i] != null)
				assertNotSame(elms[i],copy[i]);
			else
				assertNull(copy[i]);
		}
		
	}
	
	
	/**
	 * Tests {@link MiscUtils#deepMapCopy(Map)}.
	 */
	@Test
	public void testDeepCopyMap()
	{
		Map<String,CloneableCopy> map = 
			new HashMap<String,CloneableCopy>();
		CloneableCopy elm1 = new CloneableCopy("elm1");
		CloneableCopy elm2 = new CloneableCopy("elm2");
		CloneableCopy elm3 = null;
		String key1 = "elm1";
		String key2 = "elm2";
		String key3 = null;
		map.put(key1,elm1);
		map.put(key2, elm2);
		map.put(key3, elm3);
		
		Map<String,CloneableCopy> copy = 
			MiscUtils.deepMapCopy(map);
		assertNotNull(copy);
		assertNotSame(map,copy);
		assertEquals(map,copy);
		assertNotSame(map.get(key1),copy.get(key1));
		assertNotSame(map.get(key2),copy.get(key2));
	}
	
	@Test//(timeout=2000)
	public void testGetNextWord() 
	{
		List<String> expected = Arrays.asList("word1","word2","word3","word4");
		List<String> actual = new ArrayList<String>();
		
		String text = "\t" + expected.get(0) + "  " + expected.get(1) + " \n\n" + expected.get(2) +
		"\n \t" + expected.get(3) + "\n";
		
		int index = 0;
		StringBuilder str = new StringBuilder();
		
		while((index = MiscUtils.getNextWord(text,index,str,"\\s")) < text.length())
		{
			//index = ItecoUtil.getNextWord(text, index, str, "\\s");
			String word = str.toString();
			index += word.length();
			actual.add(word);
			str = new StringBuilder();
		}
		
		assertEquals(expected,actual);
		
	}
	
	@Test
	public void testWrapText()
	{
		String word5 = "apple";
		String word4 = "keys";
		String word3 = "yes";
		
		String text = word5 + " " + word4 + " " + word3 + "\n" + word5 + " " + word4;
		
		String expected = word5 + " " + word4 + "\n" + word3 + "\n" + word5 + " " + word4;
		
		String actual = MiscUtils.wrapText(text, 10);
		
		assertEquals(expected,actual);
		
		
		
	}
	@Test
	public void testWrapTextUsingHtml()
	{
		String word4 = "test";
		String word3 = "<text>she</text>";
		String word0 = "<text />";
		String word2 = "he";
		String word6 = "<text >desire</text>";
		String text = word4 + " " + word3 + " " + word0 + word2 + " " + word6;
		String exp = word4 + " " + word3 + " " + word0 + "<br>" + word2 + " " + word6;
		String act = MiscUtils.wrapText(text, 10,true,true);
		assertEquals(exp,act);
	}
	
	// add timeout in case method degenerates into infinite loop
	@Test(timeout=10000)
	public void testRetainedWhitespaceSplit()
	{
		// test no spaces
		String expected = "NoSpaces";
		assertEquals("No spaces",expected,testRetainedWhiteSpaceSplit(expected));
		
		// test whitespace only
		expected = " \n \t \r\n";
		assertEquals("White space only",expected,testRetainedWhiteSpaceSplit(expected));
		
		// test normal
		expected = "Hello, today is\nMonday\t";
		assertEquals("Single spaces",expected,testRetainedWhiteSpaceSplit(expected));
		
		// test multiple
		expected = "Hello, today\n\t\r\n is  Monday\n\n ";
		assertEquals("Multiple spaces",expected,testRetainedWhiteSpaceSplit(expected));
		
		// test white space start
		expected = " \n\r\n \t " + expected;
		assertEquals("White space start",expected,testRetainedWhiteSpaceSplit(expected));
	}
	
	private String testRetainedWhiteSpaceSplit(String expected)
	{
		StringBuilder str = new StringBuilder(1024);
		List<String> results;
		
		results = MiscUtils.retainedSpaceSplit(expected);
		for(String s : results)
			str.append(s);
		return str.toString();
	}
	
	/**
	 * Tests {@link MiscUtils#sequenceCount(String, String)}.
	 */
	@Test(timeout=5000)
	public void testSequenceCount()
	{
		int count = MiscUtils.sequenceCount("code", "code");
		assertEquals(1,count);
		count = MiscUtils.sequenceCount("code", "de");
		assertEquals(1,count);
		count = MiscUtils.sequenceCount("code", "no");
		assertEquals(0,count);
		count = MiscUtils.sequenceCount("aaaaa", "a");
		assertEquals(5,count);
	}
	
	/**
	 * Tests {@link MiscUtils#getLongestLine(String)}.
	 */
	@Test(timeout=5000)
	public void testGetLongestLine()
	{
		String text = "Single line";
		String line = text;
		assertEquals("Single Line",line,MiscUtils.getLongestLine(text));
		
		text = "12345\n123\n1234";
		line = "12345";
		assertEquals("Mult line",line,MiscUtils.getLongestLine(text));
		
		text = "123\n1234\n12345";
		line = "12345";
		assertEquals("Multi line last",line,MiscUtils.getLongestLine(text));
		
		text = "\n\n\n123\n\n\n12345\n\n1234\n\n\n";
		line = "12345";
		assertEquals("Multi line padded",line,MiscUtils.getLongestLine(text));
	}
	
	@Test
	public void testTrimmedIndices()
	{
		int [] outIndices = new int[2];
		
		String s = "";
		test(s,outIndices);
		assertArrayEquals("Empty",new int [] {0,0},outIndices);
		

		s = "hello";
		test(s,outIndices);
		assertArrayEquals("No spaces",new int [] {0,s.length()},outIndices);
		
		s = "\n\t hello";
		test(s,outIndices);
		assertArrayEquals("Start spaces",new int[]{3,s.length()},outIndices);
		
		s = "hello  \n ";
		test(s,outIndices);
		assertArrayEquals("End spaces",new int[] {0,5},outIndices);
		
		s = "\n\t hello  \n ";
		test(s,outIndices);
		assertArrayEquals("Spaces at both ends",new int[] {3,8},outIndices);
		
		s = "\n \t ";
		test(s,outIndices);
		assertArrayEquals("All spaces",new int[] {0,0},outIndices);
		
	}
	
	private void test(String s,int [] outIndices)
	{
		MiscUtils.getTrimmedIndices(s, outIndices);
	}
	
	/**
	 * Test method for {@link com.gamesalutes.utils.MiscUtils#getHtmlMarkupLength(java.lang.String)}.
	 */
	@Test
	public void testGetHtmlMarkupLength()
	{
		String str0 = "<a href=\"test\">";
		String str1 = "test";
		String str2 = "</a>";
		String text = str0 + str1 + str2;
		int expected = str0.length() + str2.length();
		int actual = MiscUtils.getHtmlMarkupLength(text);
		assertEquals("markup length",expected,actual);
	}
	
	
	@Test
	public void testHtmlReplaceAll()
	{
		String regex = "\\s";
		String replacement  = "";
		String expected,actual;
		// test all tags
		String text = "<html tag></html>";
		assertEquals(text,MiscUtils.htmlReplaceAll(text, regex, replacement));
		
		text = "<html tag>Text</html>";
		// test no replacement
		assertEquals(text,MiscUtils.htmlReplaceAll(text, regex, replacement));
		
		// test replacement
		text = "<html tag>  Text with spaces  </html>";
		expected = "<html tag>Textwithspaces</html>";
		actual = MiscUtils.htmlReplaceAll(text, regex, replacement);
		assertEquals(expected,actual);
		
		// test replacement with trailing characters
		text += "  not html  ";
		expected += "nothtml";
		actual = MiscUtils.htmlReplaceAll(text, regex, replacement);
		assertEquals(expected,actual);
		
	}
	
	@Test
	public void testReplaceWholeWordsOnly()
	{
		String text = "This is";
		String exp = "This is";
		String actual = MiscUtils.replaceWholeWordOnly(text, "test", "TEST");
		assertEquals("No replace",exp,actual);
		
		
		text = "This is a test";
		exp = "This is a TEST";
		actual = MiscUtils.replaceWholeWordOnly(text, "test", "TEST");
		assertEquals("Single replace",exp,actual);
		
		text = "This is a\ntesting\ttested  \n test";
		exp = "This is a\ntesting\ttested  \n TEST";
		actual = MiscUtils.replaceWholeWordOnly(text, "test", "TEST");
		assertEquals("Only whole word",exp,actual);
		
		text = "  \n\t ";
		exp =  "  \n\t ";
		actual = MiscUtils.replaceWholeWordOnly(text, "test", "TEST");
		assertEquals("No replace (spaces)",exp,actual);
		
		text = "";
		exp =  "";
		actual = MiscUtils.replaceWholeWordOnly(text, "test", "TEST");
		assertEquals("Empty",exp,actual);
		
		
	}
	@Test
	public void testGetNextHtmlWord()
	{
		String text = "This is < html >text</html>";
		String exp = "< html >text</html>";
		
		int [] expIndices = new int[2];
		expIndices[0] = text.indexOf('<');
		expIndices[1] = text.lastIndexOf('>') + 1;
		int [] actIndices = new int[2];
		String token;
		token = MiscUtils.getNextHtmlWord(text, 0, actIndices);
		
		assertEquals("Html word",exp,token);
		assertTrue("Html indicies: expIndices=" + Arrays.toString(expIndices) + 
				"; actIndices=" + Arrays.toString(actIndices) ,
				Arrays.equals(expIndices, actIndices));
		
		text = "This is <html />";
		exp = "<html />";
		expIndices[0] = text.indexOf('<');
		expIndices[1] = text.lastIndexOf('>') + 1;
		
		token = MiscUtils.getNextHtmlWord(text, 0,actIndices);
		
		assertEquals("Html word",exp,token);
		assertTrue("Html indicies: expIndices=" + Arrays.toString(expIndices) + 
				"; actIndices=" + Arrays.toString(actIndices) ,
				Arrays.equals(expIndices, actIndices));
		
		text = "This is not html\n";
		exp = null;
		expIndices[0] = expIndices[1] = -1;
		
		token = MiscUtils.getNextHtmlWord(text, 0,actIndices);
		
		assertEquals("Html word",exp,token);
		assertTrue("Html indicies: expIndices=" + Arrays.toString(expIndices) + 
				"; actIndices=" + Arrays.toString(actIndices) ,
				Arrays.equals(expIndices, actIndices));
		
		
	}
	
	@Test
	public void testTrimNonPrintableChars()
	{
		String input;
		String expected;
		String actual;
		
		// test non-trimming
		input =  "Hello\n \t \r\n Justin";
		expected = input;
		actual = MiscUtils.trimNonPrintableChars(input);
		assertEquals(expected,actual);
		
		//test trimming
		char [] data = {0,'H',8,'e','l','l',3,'o','\r','\n'};
		input = new String(data);
		expected = "Hello\r\n";
		actual = MiscUtils.trimNonPrintableChars(input);
		assertEquals(expected,actual);
		
		char [] data2 = {'H','e','l','l',0,3,'o','\r','\n',8};
		input = new String(data2);
		expected = "Hello\r\n";
		actual = MiscUtils.trimNonPrintableChars(input);
		assertEquals(expected,actual);
	}
	
	@Test
	public void testReplaceDelim()
	{
		String input = "SimpleTest";
		String oldDelim = "\\s";
		String newDelim = ",";
		
		assertEquals(input,MiscUtils.replaceDelim(input, oldDelim, newDelim));
		
		input = "This \n \t is \r\n a  \t sentence  ";
		String exp = "This,is,a,sentence";
		assertEquals(exp,MiscUtils.replaceDelim(input, oldDelim, newDelim));
		
		input = "";
		assertEquals(input,MiscUtils.replaceDelim(input, oldDelim, newDelim));
		
	}
	
	@Test
	public void testSplit()
	{
		String input = "SimpleTest";
		String regex = "\\s";
		assertArrayEquals(new String[] {input},MiscUtils.split(input,regex));
		
		input = "\t  \nThis \n \t is \r\n a  \t sentence  ";
		String [] exp = {"This","is","a","sentence"};
		assertArrayEquals(exp,MiscUtils.split(input, regex));
		assertArrayEquals(exp,MiscUtils.split(input, regex+"+"));
		
		input = "";
		assertArrayEquals(new String[0],MiscUtils.split(input, regex));
		
		input = "  ";
		assertArrayEquals(new String[0],MiscUtils.split(input, regex));
	}
	
	@Test
	public void testFormatTime()
	{
		long dt;
		String exp,actual;
		
		dt = 5;
		
		exp = "5 s";
		actual = MiscUtils.formatTime(dt, TimeUnit.SECONDS,
				TimeUnit.SECONDS, 0);
		assertEquals(exp,actual);
		
		dt = (long)1e6;
		exp = "1.000 ms";
		
		actual = MiscUtils.formatTime(dt,
				TimeUnit.NANOSECONDS,TimeUnit.MILLISECONDS,3);
		assertEquals(exp,actual);
		
	}
	
	@Test
	public void testIntern()
	{
		String s1 = new String("Test");
		String s2 = new String("Test");
		
		assertNotSame(s1,s2);
		assertEquals(s1,s2);
		
		s1 = MiscUtils.intern(s1);
		s2 = MiscUtils.intern(s2);
		assertSame(s1,s2);
	}
	
	@Test
	public void testRemoveChars()
	{
		String s = "Jack.and/Jill.ran-over/the.hill";
		String expected = s.replaceAll("\\.|/","");
		assertEquals(expected,MiscUtils.removeChars(s, '.','/'));
	}
	
	@Test
	public void testClearStringBuilder()
	{
		StringBuilder s = new StringBuilder();
		s.append("Testing clear");
		MiscUtils.clearStringBuilder(s);
		assertEquals(0,s.length());
		assertEquals("",s.toString());
	}
	
	@Test
	public void testSplitEscaped()
	{
		String escape = "\"";
		String regex = "\\s";
		String test = "Simple";
		// simple test
		assertArrayEquals(new String[] {test},MiscUtils.escapedSplit(test, regex, escape));
		// regular unescaped
		test = "One Two";
		assertArrayEquals(new String[] {"One","Two"},MiscUtils.escapedSplit(test,regex,escape));
		// simple escaped
		test = "\"One Two\"";
		assertArrayEquals(new String[] {"One Two"},MiscUtils.escapedSplit(test,regex,escape));
		// complex escaped
		test = "\"One  Two\" Three  Four";
		assertArrayEquals(new String[] {"One  Two","Three","Four"},MiscUtils.escapedSplit(test, regex, escape));
		
		// more complex escaped
		test = "One\t\"Two Three  Four\nFive\" \"Six Seven\"\" Eight Nine\" Ten  Eleven";
		assertArrayEquals(new String[] {"One","Two Three  Four\nFive","Six Seven","Eight Nine","Ten","Eleven"},
				MiscUtils.escapedSplit(test, regex, escape));
		
		
	}
	
	@Test
	public void testIsSameHostProcess()
	{
		String host1 = "http://localhost:8080/iteco";
		String host2 = "http://localhost:8080/iteco/auth";
		
		assertTrue(MiscUtils.isSameHostProcess(host1, host1));
		
		assertTrue(MiscUtils.isSameHostProcess(host1, host2));
		
		host2 = "http://localhost:9100/iteco";
		assertFalse(MiscUtils.isSameHostProcess(host1, host2));
		
		host2 = "https://localhost:8080/iteco";
		assertFalse(MiscUtils.isSameHostProcess(host1, host2));
		
		host2 = "http://localhost/iteco";
		assertFalse(MiscUtils.isSameHostProcess(host1, host2));
		
		host2 = "http://someserver:8080/iteco";
		assertFalse(MiscUtils.isSameHostProcess(host1, host2));
		
		
	}
	
	@Test
	public void testIsLineTerminated()
	{
		assertTrue(MiscUtils.isLineTerminated("Justin\n"));
		assertTrue(MiscUtils.isLineTerminated("Justin\r\n"));
		assertTrue(MiscUtils.isLineTerminated("Justin\r"));
		assertTrue(MiscUtils.isLineTerminated("Justin<br>"));
		assertTrue(MiscUtils.isLineTerminated("Justin<br/>"));
		assertFalse(MiscUtils.isLineTerminated("Justin"));
		assertFalse(MiscUtils.isLineTerminated("Jus\ntin"));
		assertFalse(MiscUtils.isLineTerminated("\nJustin"));
		assertFalse(MiscUtils.isLineTerminated("Justin\n "));
		
	}

        @Test
        public void testEncodeHtml()
        {
            String input = "<html><body>&hitme!</body></html>";
            String exp = "&lt;html&gt;&lt;body&gt;&amp;hitme!&lt;/body&gt;&lt;/html&gt;";

            String act = MiscUtils.encodeHtml(input);

            assertEquals(exp,act);

        }
	

}
