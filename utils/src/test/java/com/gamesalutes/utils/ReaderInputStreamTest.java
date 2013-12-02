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
package com.gamesalutes.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.InputStream;
import org.junit.*;
import static org.junit.Assert.*;


// FIXME: there is a bug in this class that shows up in dmca project with all xml input and can't figure out what is causing it!

/**
 *
 * @author jmontgomery
 */
public class ReaderInputStreamTest
{
    @Test
    public void testBasicRead() throws IOException
    {
        testString("Test",null);
    }

    private void testString(String s,Integer bufSize) throws IOException
    {
        InputStream in;
        if(bufSize != null)
            in = new ReaderInputStream(new StringReader(s),"UTF-8",bufSize);
        else
            in = new ReaderInputStream(new StringReader(s),"UTF-8");
        
        String actual = new String(ByteUtils.readBytes(in),"UTF-8");

        char [] s1 = s.toCharArray();
        char [] s2 = actual.toCharArray();
        
        assertEquals(s,actual);
    }

    @Test
    public void testNewlineRead() throws IOException
    {
        String s = "Test\nSome\rLines\r\n";
        testString(s,null);
    }

    @Test
    public void testBufferedRead() throws IOException
    {
        String s = "Jack and Jill went up the hill.\nSomebody must"
                + " have come running after them.\nThen Humpty Dumpty did something stupid!\n";

        testString(s,16);
    }

    @Test
    public void testLongRead() throws IOException
    {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<result success=\"true\">\n"+
        "<user inRange=\"true\">\n"+
        "<cnetid>bwaldrep</cnetid>\n"+
        "<login>2011-02-04T03:13:42-06:00</login>\n"+
        "<logout type=\"normal\">2011-02-04T05:56:26-06:00</logout>\n"+
        "<ip>10.150.37.71</ip>\n"+
        "<mac>00:21:6A:52:08:36</mac>\n"+
        "<source>radius</source>\n"+
        "</user>\n"+
        "</result>\n";

        testString(s,16);
    }


}
