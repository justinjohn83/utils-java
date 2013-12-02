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

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author jmontgomery
 */
public class WriterOutputStreamTest
{
    @Test
    public void testBasicWrite() throws IOException
    {
        testString("Test");
    }

    private void testString(String s) throws IOException
    {
        StringWriter w = new StringWriter(1024);

        OutputStream out = new WriterOutputStream(w,"UTF-8",16);
        out.write(s.getBytes("UTF-8"));
        out.flush();

        String actual = w.toString();
        
        assertEquals(s,actual);
    }

    @Test
    public void testNewlineWrite() throws IOException
    {
        String s = "Test\nSome\rLines\r\n";
        testString(s);
    }

    @Test
    public void testBufferedWrite() throws IOException
    {
        String s = "Jack and Jill went up the hill.\nSomebody must"
                + " have come running after them.\nThen Humpty Dumpty did something stupid!\n";

        testString(s);
    }


}
