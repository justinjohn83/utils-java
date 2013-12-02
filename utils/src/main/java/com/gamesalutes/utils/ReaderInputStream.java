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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * <code>InputStream</code> wrapper around a <code>Reader</code>.
 *
 * @author jmontgomery
 */
public class ReaderInputStream extends InputStream
{

    private final Reader reader;
    private final String charset;
    private char [] char_buf_;
    private byte [] byte_buf_;
    private int ptr;

    public ReaderInputStream(Reader r,String charset)
    {
        this(r,charset,1024);
    }
    public ReaderInputStream(Reader r,String charset,int bufSize)
    {
        if(r == null)
            throw new NullPointerException("r");
        this.reader = r;
        this.charset = charset;
        char_buf_ = new char[bufSize];
        byte_buf_ = null;
        ptr = -1;
    }

    @Override
    public int available() throws IOException 
    {
        return byte_buf_ != null ? byte_buf_.length : 0;
    }

    @Override
    public void close() throws IOException
    {
        reader.close();
        ptr = -1;
        byte_buf_ = null;
    }

    @Override
    public synchronized void mark(int i)
    {
        try
        {
            reader.mark(i);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean markSupported() 
    {
        return reader.markSupported();
    }

    @Override
    public int read(byte[] bytes) throws IOException 
    {
        return read(bytes,0,bytes.length);
    }

    @Override
    public int read(byte[] bytes, int i, int len) throws IOException
    {
        int num = len - i;
        int read = 0;
        while(read < num)
        {
            if(ptr == -1 || ptr >= byte_buf_.length)
            {
                if(fillBuffer() < 0)
                    return read > 0 ? read : -1;

            }

            int n = Math.min(byte_buf_.length, num - read);
            System.arraycopy(byte_buf_, 0, bytes, i + read, n);
            read += n;
            ptr += n;
        }

        return read;
    }

    @Override
    public synchronized void reset() throws IOException 
    {
        reader.reset();
        clearBuffer();
    }

    @Override
    public long skip(long l) throws IOException 
    {
        long rem;
        long skipped = 0;

        if(ptr == -1)
            rem = l;
        else
        {
            long bufRem = byte_buf_.length - ptr;
            if(bufRem <= l)
            {
                ptr += l;
                rem = 0;
                skipped = l;
            }
            else
            {
                rem = l - bufRem;
                clearBuffer();

            }
        }
        if(rem > 0)
        {
            long _skipped = reader.skip(rem);
            if(_skipped > 0)
                skipped += _skipped;
        }

        return skipped;

    }

    private void clearBuffer()
    {
        ptr = -1;
        byte_buf_ = null;
    }

    @Override
    public int read() throws IOException
    {
        if(ptr == -1 || ptr >= byte_buf_.length)
        {
            if(fillBuffer() < 0)
                return -1;
        }
        return byte_buf_[ptr++];

    }



    private int fillBuffer() throws IOException
    {
        int read = reader.read(char_buf_,0,char_buf_.length);
        if(read > 0)
        {
            // TODO: optimize - this is only way I know how to convert for now
            byte_buf_ = new String(char_buf_,0,read).getBytes(charset);
            ptr = 0;
            return byte_buf_.length;
        }
        else
        {
            clearBuffer();
            return -1;
        }
    }

}
