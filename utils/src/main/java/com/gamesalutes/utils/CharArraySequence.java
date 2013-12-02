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
/* Copyright 2008 - 2010 University of Chicago
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

/**
 * Wraps around raw character array to implement <code>CharSequence</code> interface.
 * Note that the underlying character array is mutable, if want to make it immutable, consider
 * creating a <code>String</code> instead.  This class is designed to avoid the overhead of copying
 * the character array internally.
 *
 * @author jmontgomery
 */
public final class CharArraySequence implements CharSequence
{

    private final char[] data;
    private final int offset;
    private final int length;

    public CharArraySequence(char [] data,int offset,int length)
    {
        if(data == null)
            throw new NullPointerException("data");
        if(offset < 0 || offset > length)
            throw new IllegalArgumentException("offset=" + offset);
        if(length < 0)
            throw new IllegalArgumentException("length=" + length);

        this.data = data;
        this.offset = offset;
        this.length = length;
    }
    public int length()
    {
        return length;
    }

    public char charAt(int index)
    {
        if(index >= offset + length)
            throw new IndexOutOfBoundsException("index=" + index);
        return data[offset + index];
    }

    public CharSequence subSequence(int start, int end)
    {
        return new CharArraySequence(data,offset + start,end-start);
    }

    @Override
    public String toString()
    {
        return new String(data,offset,length);
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof CharArraySequence))
            return false;

        return StringUtils.equals(this, (CharArraySequence)o);
    }
    @Override
    public int hashCode()
    {
        int result = 17;
        for(int i = offset; i < offset + length; ++i)
            result = 31 * result + data[i];

        return result;
    }

}
