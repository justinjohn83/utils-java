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
import java.io.OutputStream;
import java.io.Writer;

/**
 * <code>OutputStream</code> wrapper around a <code>Writer</code>.
 *
 * @author jmontgomery
 */
public class WriterOutputStream extends OutputStream
{

    private final Writer writer;
    private final String charset;
    private final byte [] buf;
    private int ptr;

    public WriterOutputStream(Writer w,String charset)
    {
        this(w,charset,1024);
    }
    public WriterOutputStream(Writer w,String charset,int bufferSize)
    {
        if(w == null)
            throw new NullPointerException("w");
        if(charset == null)
            throw new NullPointerException("charset");
        this.writer = w;
        this.charset = charset;
        this.buf = new byte[bufferSize];
    }
    @Override
    public void close() throws IOException
    {
        flush();
        writer.close();
    }

    @Override
    public void flush() throws IOException
    {
        flush0();
        writer.flush();
    }

    private void flush0() throws IOException
    {
        if(ptr > 0)
        {
            writer.append(new String(buf,0,ptr,charset));
            ptr = 0;
        }
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        write(b,0,b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        int avail = buf.length - ptr;
        int count = 0;
        int num = len - off;

        while(count < num)
        {
            int n = Math.min(avail,num-count);
            System.arraycopy(b, off+count, buf, ptr, n);
            ptr += n;
            count += n;

            if(ptr == buf.length)
                flush0();
        }
    }

    @Override
    public void write(int b) throws IOException
    {
        if(ptr == buf.length)
            flush0();
        buf[ptr++] = (byte)b;
    }

}
