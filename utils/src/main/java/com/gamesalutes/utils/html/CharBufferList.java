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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import com.gamesalutes.utils.ArrayStack;
import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.StringUtils;

public class CharBufferList implements CharSequence {


	private final int bufferSize;
	private final List<CharBuffer> bufferList;
	private int size;
	//private final char [] temp = new char[2048];
	
	public CharBufferList() {
		this(2048);
	}
	public CharBufferList(int bufferSize) {
		this.bufferSize = bufferSize;
		bufferList = new ArrayList<CharBuffer>();
		
	}
	
	private CharBufferList(CharBufferList other,int start,int end) {
		
		List<CharBuffer> bufferList = other.bufferList;
		int size = other.size;
		
		int x = start;
		int len = bufferList.get(0).limit();
		int i = 0;
		while(x >= len) {
			++i;
			x -= len;

			if(i >= size) {
				throw new IllegalArgumentException("start=" + start);
			}
			len = bufferList.get(i).limit();
		}
		start = x;
		int startIndex = i;
		
		x = end;
		len = bufferList.get(0).limit();
		i = 0;
		while(x > len) {
			++i;
			x -= len;

			if(i >= size) {
				throw new IllegalArgumentException("start=" + start);
			}
			len = bufferList.get(i).limit();
		}
		
		end = x;
		int endIndex = i;
		
		this.bufferList = new ArrayList<CharBuffer>(endIndex - startIndex + 1);
		for(i = startIndex; i <= endIndex; ++i) {
			this.bufferList.add(bufferList.get(i).duplicate());
		}
		this.size = endIndex - startIndex + 1;
		
		this.bufferList.get(0).position(start);
		this.bufferList.get(this.size-1).limit(end);
		this.bufferSize = other.bufferSize;
		
		
	}
	
	
	public void add(CharBuffer buffer) {
		while(buffer.hasRemaining()) {
			CharBuffer storage = getCurrentBuffer();
			int copied = Math.min(storage.remaining(), buffer.remaining());
			buffer.get(storage.array(), storage.arrayOffset() + storage.position(),
					copied);
			storage.position(storage.position() + copied);
		}
	}
	
	private CharBuffer getCurrentBuffer() {
		if(!bufferList.isEmpty()) {

			while(size < bufferList.size()) {
				if(size == 0) {
					size = 1;
				}
				CharBuffer buffer = bufferList.get(size - 1);
				if(buffer.hasRemaining()) {
					return buffer;
				}
				++size;
			}
		}
		// allocate new buffer
		CharBuffer buffer = newBuffer();
		bufferList.add(buffer);
		size = bufferList.size();
		
		return buffer;
		
	}
	private CharBuffer newBuffer() {
		return CharBuffer.allocate(bufferSize);
	}
	
	public void write(OutputStream out) throws IOException {
		write(new OutputStreamWriter(out,"UTF-8"));
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	public void write(Writer out) throws IOException {
		for(int i = 0; i < size; ++i) {
			CharBuffer buffer = bufferList.get(i);
			buffer.flip();
			out.write(buffer.array(),buffer.arrayOffset(),buffer.remaining());
		}
		clear();
	}
	
	public void clear() {
		for(int i = 0; i < size; ++i) {
			bufferList.get(i).clear();
		}
		size = 0;
	}
	
	public boolean contains(CharSequence sequence) {
		return contains(sequence,true);
	}
	
	public boolean caseInsensitiveContains(CharSequence sequence) {
		return contains(sequence,false);
	}
	private boolean contains(CharSequence sequence,boolean caseSensitive) {
		
		if(sequence.length() > this.bufferSize) {
			throw new IllegalArgumentException("sequence=" + sequence + ";sequenceLength=" + sequence.length() + ";bufferSize=" + this.bufferSize);
			
		}
		
		for(int i = 0; i < size; ++i) {
			CharBuffer buffer = bufferList.get(i);
			int position = buffer.position();
			buffer.rewind();
			
			boolean found = false;
			if(contains(buffer,sequence,caseSensitive)) {
				found = true;
			}
			buffer.position(position);
			if(found) {
				return true;
			}
		}
		
		StringBuilder temp = new StringBuilder(64);

		// check edge conditions
		int charLen = sequence.length();
		
		for(int i = 0; i < size - 1; i++) {
			CharBuffer first = bufferList.get(i);
			CharBuffer second = bufferList.get(i+1);
			
			int firstPosition = first.position();
			first.rewind();
			int secondPosition = second.position();
			second.rewind();
			
			MiscUtils.clearStringBuilder(temp);
			appendLast(temp,first,charLen);
			appendFirst(temp,second,charLen);
			
			boolean found = false;
			if(contains(temp,sequence,caseSensitive)) {
				found = true;
			}
			first.position(firstPosition);
			second.position(secondPosition);
			
			if(found) {
				return true;
			}

			
		}
		
		return false;
		
		
	}
	private void appendLast(StringBuilder temp,CharBuffer buffer,int sequenceLen) {
		int len = Math.min(sequenceLen, buffer.length());
		
		temp.append(buffer.array(),buffer.arrayOffset() + buffer.limit() - len,len);
	}
	private void appendFirst(StringBuilder temp,CharBuffer buffer,int sequenceLen) {
		int len = Math.min(sequenceLen, buffer.length());
		
		temp.append(buffer.array(),buffer.arrayOffset() + buffer.position(),len);
	}
	private boolean contains(CharSequence string,CharSequence sequence,boolean caseSensitive) {
		if(caseSensitive) {
			return StringUtils.contains(string, sequence);
		}
		else {
			return StringUtils.caseInsensitiveContains(string, sequence);
		}
	}
	
	public void finish() {
		for(int i = 0; i < size; ++i) {
			CharBuffer buffer = bufferList.get(i);
			buffer.flip();
		}
	}
	public char charAt(int p) {
		int x = p;
		int len = bufferList.get(0).limit();
		int i = 0;
		while(x >= len) {
			++i;
			x -= len;
			
			if(i >= size) {
				throw new IllegalArgumentException("p=" + p);
			}
			len = bufferList.get(i).limit();
		}
		return bufferList.get(i).charAt(x);
		
		
	}
	public int length() {
		int len = 0;
		for(int i = 0; i < size; ++i) {
			len += bufferList.get(i).limit();
		}
		return len;
	}
	public CharSequence subSequence(int start, int end) {
		return new CharBufferList(this,start,end);
	}
	
	@Override
	public String toString() {
		return toStringBuilder().toString();
	}
	
	public StringBuilder toStringBuilder() {
		StringBuilder buf = new StringBuilder(size * bufferSize);
		for(int i = 0; i < size; ++i) {
			CharBuffer buffer = bufferList.get(i);
//			buffer.rewind();
			buf.append(buffer);
		}
		return buf;
	}
}
