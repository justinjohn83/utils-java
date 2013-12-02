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
import org.junit.Test;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public class StringUtilsTest
{
	@Test(timeout = 3000)
	public void testLiteralReplaceAll()
	{
		String s = "((()))";
		String seq = "()";
		String r = "";
		String expected = "";
		
		assertEquals(expected,StringUtils.literalReplaceAll(s, seq, r));
		
		s = "nothing";
		
		assertEquals(s,StringUtils.literalReplaceAll(s, seq, r));
		
		s = "aaa";
		seq = "a";
		r = "a";
		expected = s;
		
		assertEquals(expected,StringUtils.literalReplaceAll(s, seq, r));
		
		s = "aaa";
		seq = "aa";
		r = "a";
		expected = "a";
		
		assertEquals(expected,StringUtils.literalReplaceAll(s, seq, r));
		
		s = "aaa";
		seq = "aa";
		r = "";
		expected = "a";
		
		assertEquals(expected,StringUtils.literalReplaceAll(s, seq, r));

	}
	
	@Test
	public void testIndexOf() {
		String s = "Jack and Jill jumped over the hill and the dog came running after them";
		
		assertEquals(0,StringUtils.indexOf(s, "Jack"));
		assertEquals(-1,StringUtils.indexOf(s,"JACK"));
		assertEquals(-1,StringUtils.indexOf(s, "jack"));
		
		assertEquals(0,StringUtils.indexOf(s,'J'));
		assertEquals(s.indexOf("Jill"),StringUtils.indexOf(s,"Jill"));
		
		assertEquals(-1,StringUtils.indexOf(s, "poo"));
		assertEquals(-1,StringUtils.indexOf(s, 'z'));
		
	}
	
	@Test
	public void testCaseInsensitiveIndexOf() {
		String s = "Jack and Jill jumped over the hill and the dog came running after them";

		assertEquals(0,StringUtils.caseInsensitiveIndexOf(s, "Jack"));
		assertEquals(0,StringUtils.caseInsensitiveIndexOf(s,"JACK"));
		assertEquals(0,StringUtils.caseInsensitiveIndexOf(s, "jack"));
		
		assertEquals(0,StringUtils.caseInsensitiveIndexOf(s,'j'));
		assertEquals(s.indexOf("Jill"),StringUtils.caseInsensitiveIndexOf(s,"JILL"));
		
		assertEquals(-1,StringUtils.indexOf(s, "poo"));
		assertEquals(-1,StringUtils.indexOf(s, 'z'));	
	}
	
	@Test
	public void testCaseInsensitiveContains()
	{
		String s = "Jack and Jill jumped over the hill and the dog came running after them";
		String seq;
		
		// assumptions
		assertTrue("".contains(""));
		assertTrue(s.contains(""));
		assertFalse("".contains("Test"));
		
		
		seq = "Jack";
		assertTrue(StringUtils.caseInsensitiveContains(s, seq));
		seq = "jILL";
		assertTrue(StringUtils.caseInsensitiveContains(s, seq));
		seq = "THEM";
		assertTrue(StringUtils.caseInsensitiveContains(s, seq));
		seq = s;
		assertTrue(StringUtils.caseInsensitiveContains(s, seq));
		seq = "Over THe HIll";
		assertTrue(StringUtils.caseInsensitiveContains(s, seq));
		seq = "blah blah";
		assertFalse(StringUtils.caseInsensitiveContains(s, seq));
		seq = s + s;
		assertFalse(StringUtils.caseInsensitiveContains(s, seq));
		seq = "";
		assertTrue(StringUtils.caseInsensitiveContains(s, seq));
		
	}
	
	@Test
	public void testCaseInsensitiveEquals()
	{
		String s = "Humpty Dumpty";
		String s2;
		
		s2 = new String(s);
		assertTrue(StringUtils.caseInsensitiveEquals(s, s2));
		s2 = "humpTY dUMPty";
		assertTrue(StringUtils.caseInsensitiveEquals(s, s2));
		s2 = "bob";
		assertFalse(StringUtils.caseInsensitiveEquals(s, s2));
		s2 = "Fumpty Humpty";
		assertFalse(StringUtils.caseInsensitiveEquals(s, s2));
		
		

		
	}
	@Test
	public void testCaseInsensitiveStartsWith()
	{		
		String s = "Humpty Dumpty";
		
		
		// assumptions
		assertTrue(s.startsWith(""));
		
		String seq = "Hum";
		assertTrue(StringUtils.caseInsensitiveStartsWith(s, seq));
		seq = "HuM";
		assertTrue(StringUtils.caseInsensitiveStartsWith(s, seq));
		seq = "umpty";
		assertFalse(StringUtils.caseInsensitiveStartsWith(s, seq));
		
		// all strings start with empty sequence : must behave like String.startsWith
		seq = "";
		assertTrue(StringUtils.caseInsensitiveStartsWith(s, seq));

		
	}
	@Test
	public void testCaseInsensitiveEndsWith()
	{
		String s = "Humpty Dumpty";
		
		// assumptions
		assertTrue(s.endsWith(""));
		
		String seq = "pty";
		assertTrue(StringUtils.caseInsensitiveEndsWith(s, seq));
		seq = "PtY";
		assertTrue(StringUtils.caseInsensitiveEndsWith(s, seq));
		seq = "Humpty";
		assertFalse(StringUtils.caseInsensitiveEndsWith(s, seq));
		// all strings end with empty sequence : must behave like String.endsWith
		seq = "";
		assertTrue(StringUtils.caseInsensitiveEndsWith(s, seq));
	}
	
	@Test
	public void testGreatestCommonSubstring()
	{
		String s1 = "Big test should pass";
		String s2 = "Big test with a vengeance";
		String exp = "Big test ";
		assertEquals(exp,StringUtils.greatestCommonSubstring(s1, s2));
		
		s1 = "Big test";
		s2 = "Big test will pass";
		exp = "Big test";
		assertEquals(exp,StringUtils.greatestCommonSubstring(s1, s2));

		
		s1 = "No common";
		s2 = "blah blah";
		assertEquals("",StringUtils.greatestCommonSubstring(s1, s2));

		exp = s1 = s2 = "same";
		assertEquals(exp,StringUtils.greatestCommonSubstring(s1, s2));
		
	}
	@Test
	public void testCaseInsensitiveGreatestCommonSubstring()
	{
		String s1 = "BIg tesT should pass";
		String s2 = "Big tESt with a vengeance";
		String exp = "big test ";
		assertEquals(exp,((String)StringUtils.caseInsensitiveGreatestCommonSubstring(s1, s2)).toLowerCase());
		
		s1 = "biG test";
		s2 = "Big tEsT will pass";
		exp = "big test";
		assertEquals(exp,((String)StringUtils.caseInsensitiveGreatestCommonSubstring(s1, s2)).toLowerCase());

		
		s1 = "No common";
		s2 = "blah blah";
		assertEquals("",StringUtils.caseInsensitiveGreatestCommonSubstring(s1, s2));

		exp = s1 = s2 = "same";
		assertEquals(exp,StringUtils.caseInsensitiveGreatestCommonSubstring(s1, s2));
	}

        @Test
        public void testSubstrBefore()
        {
            String s = "underground";

            // no match
            assertNull(StringUtils.substrBefore(s, "blah"));

            // match in middle
            assertEquals("under",StringUtils.substrBefore(s, "gr"));

            // match in beginning
            assertEquals("",StringUtils.substrBefore(s, "under"));

            // match at end
            assertEquals("under",StringUtils.substrBefore(s, "ground"));
        }

        @Test
        public void testSubstrAfter()
        {
            String s = "underground";

            // no match
            assertNull(StringUtils.substrAfter(s, "blah"));

            // match in middle
            assertEquals("ground",StringUtils.substrAfter(s, "er"));

            // match in beginning
            assertEquals("ground",StringUtils.substrAfter(s, "under"));

            // match at end
            assertEquals("",StringUtils.substrAfter(s, "ground"));
        }

        @Test
        public void testReplace()
        {
            String orig = "Jack and Jill ran up the hill.";
            StringBuilder s = new StringBuilder(orig);


            // simple same length replacement
            String exp = "Jack and John ran up the hill.";
            StringUtils.replace(s, "Jill","John");
            assertEquals(exp,s.toString());

            // shorter replacement
            s = new StringBuilder(orig);
            exp = "Jack and Pat ran up the hill.";
            StringUtils.replace(s,"Jill","Pat");
            assertEquals(exp,s.toString());

            // long replacement
            s = new StringBuilder(orig);
            exp = "Jack and Justin ran up the hill.";
            StringUtils.replace(s,"Jill","Justin");
            assertEquals(exp,s.toString());

            // empty replacement
            s = new StringBuilder(orig);
            exp = "Jack ran up the hill.";
            StringUtils.replace(s," and Jill","");
            assertEquals(exp,s.toString());
        }
        
    	@Test
    	public void testFormatNameString() {

    		String exp = "James";
    		String name = "James";
    		
    		assertEquals(exp,StringUtils.formatName(name));
    		
    		name = "JAMES";
    		assertEquals(exp,StringUtils.formatName(name));
    		
    		name = "JAMES SPADER";
    		assertEquals("JAMES SPADER",StringUtils.formatName(name));
    		
    		name = "james";
    		assertEquals("James",StringUtils.formatName(name));
    		
    		name = "james spader";
    		assertEquals("james spader",StringUtils.formatName(name));
    		
    		name = "McDonald";
    		assertEquals("McDonald",StringUtils.formatName(name));

    	}
    	
    	@Test
    	public void testPhraseUpperToProper() {
    		
    		
    		String exp = "James";
    		String phrase = "JAMES";
    		
    		assertEquals(exp,StringUtils.phraseUpperToProper(phrase));
    		
    		
    		exp = "James Spader";
    		phrase = "JAMES SPADER";
    		
    		assertEquals(exp,StringUtils.phraseUpperToProper(phrase));
    		
    		exp = "This Cool Provider";
    		phrase = "THIS COOL PROVIDER";
    		
    		assertEquals(exp,StringUtils.phraseUpperToProper(phrase));
    		
    		// leading whitespace
    		
    		exp = "This Cool Provider";
    		phrase = "  THIS COOL PROVIDER";
    		assertEquals(exp,StringUtils.phraseUpperToProper(phrase));

    		
    		// trailing whitespace
    		exp = "This Cool Provider";
    		phrase = "THIS COOL PROVIDER   ";
    		assertEquals(exp,StringUtils.phraseUpperToProper(phrase));
    		
    		exp = "Fall 2012";
    		phrase = "fall 2012";
    		assertEquals(exp,StringUtils.phraseUpperToProper(phrase));
    	}
}
