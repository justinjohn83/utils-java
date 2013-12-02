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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.StringUtils;

/**
 * Parses input HTML data and only preserves desired data.  Resulting partial html can then be cleaned up by 
 * JTidy or equivalent html parser implementation and turned into a valid XML document.  This is useful for 
 * cases where only a small piece of html is required and parsing the entire HTML document and turning it into XML is
 * prohibitive to performance.  This class is <b>NOT</b> thread safe.
 * 
 * @author jmontgomery
 *
 */
public class HTMLSelector implements Lexer {

	private final String tag;
	private final String tagAttribute;
	private final String searchText;
	private final boolean selectMultiple;
	private final CharBufferList dataBuffer;
	private final BufferWrapper buffer;
	private final CharBuffer tagBuffer;
	
	private static final int BUFFER_SIZE = 2048;
	
	private static final Logger logger = LoggerFactory.getLogger(HTMLSelector.class.getSimpleName());
	
	public final static class Builder {
		
		private String tag;
		private String attribute;
		private String searchText;
		private boolean selectMultiple;
		private int bufferSize = BUFFER_SIZE;
		
		public int getBufferSize() {
			return bufferSize;
		}
		public void setBufferSize(int bufferSize) {
			this.bufferSize = bufferSize;
		}
		
		public String getTag() {
			return tag;
		}
		public Builder setTag(String tag) {
			this.tag = tag;
			return this;
		}
		public String getAttribute() {
			return attribute;
		}
		public Builder setAttribute(String attribute) {
			this.attribute = attribute;
			return this;
		}
		public String getSearchText() {
			return searchText;
		}
		public Builder setSearchText(String searchText) {
			this.searchText = searchText;
			return this;
		}
		public boolean isSelectMultiple() {
			return selectMultiple;
		}
		public Builder setSelectMultiple(boolean selectMultiple) {
			this.selectMultiple = selectMultiple;
			return this;
		}
				
		public HTMLSelector build() {
			
			return new HTMLSelector(getTag(),getAttribute(),getSearchText(),isSelectMultiple(),this.getBufferSize());
		}
		
	}
	
	private enum TagType {
		BEGIN,
		END;
	}
	private static class BufferWrapper {
		
		private int startPosition;
		private final CharBuffer buffer;
		
		public BufferWrapper(CharBuffer buffer) {
			this.buffer = buffer;
		}

		public int getStartPosition() {
			return startPosition;
		}

		public BufferWrapper setStartPosition(int startPosition) {
			this.startPosition = startPosition;
			return this;
		}
		public BufferWrapper setEndPosition(int end) {
			buffer.position(end);
			return this;
		}
		
		public CharBuffer setPosition() {
			this.buffer.position(startPosition);
			return this.buffer;
		}
		
		public int getEndPosition() {
			return buffer.position();
		}
		
		public CharBuffer asBuffer() { return buffer; }
		
		public boolean hasRemaining() {
			return getRemaining() > 0;
		}
		public int getRemaining() {
			return Math.max(0,getEndPosition() - getStartPosition());
		}
		public void clear() {
			this.startPosition = -1;
			buffer.clear();
		}
		public int position() {
			return buffer.position();
		}
		public int limit() {
			return buffer.limit();
		}
		
		public CharBuffer partialSlice() {
			CharBuffer b;
			if(getStartPosition() >= 0 ) { //&& getEndPosition() != buffer.limit()) {
				b = CharBuffer.wrap(buffer.array(), 
						buffer.arrayOffset() + getStartPosition(),
						getEndPosition() - getStartPosition());
				
				if(logger.isDebugEnabled()) {
					logger.debug(b.toString());
				}
				
//				setStartPosition(0);

			}
			else {
				b = this.buffer.duplicate();
				b.position(buffer.limit());
			}
			
			return b;
		}
		
		
	}
	
	
	public HTMLSelector(String tag,String tagAttribute,String searchText,boolean selectMultiple) {
		this(tag,tagAttribute,searchText,selectMultiple,BUFFER_SIZE);
	}
	public HTMLSelector(String tag,String tagAttribute,String searchText,boolean selectMultiple,int bufferSize) {
		this.tag = tag;
		this.tagAttribute = tagAttribute;
		this.searchText = searchText;
		this.selectMultiple = selectMultiple;
		dataBuffer = new CharBufferList(bufferSize);
		buffer = new BufferWrapper(CharBuffer.allocate(bufferSize));
		tagBuffer = CharBuffer.allocate(bufferSize);
	}
	
	public void parse(InputStream in,OutputStream out) throws IOException {
		parse(new InputStreamReader(in,"UTF-8"),
			  new OutputStreamWriter(out,"UTF-8"));
	}
	
	private void parse(InputStream in,OutputType out) throws IOException {
		parse(new InputStreamReader(in,"UTF-8"),out);
	}
	

	// true if start tag
	// false if end tag
	// null if no tag
	private TagType findTag(Reader in,int bracketCount) throws IOException {
		
		int startTagCopyPosition = -1;
		int endTagCopyPosition = -1;
		
		CharBuffer buffer = this.buffer.asBuffer().duplicate();
		
		int index = StringUtils.indexOf(buffer, '<');
		if(index == -1) {
			this.buffer.asBuffer().position(this.buffer.asBuffer().limit());
			return null;
		}
		// find end tag
		int endIndex = -1;
		if(index < buffer.length() - 1) {
			endIndex = StringUtils.indexOf(buffer, '>',index+1);
		}
		
		
		// buffer out of data - must get some more
		if(endIndex == -1) {
			

			if(logger.isDebugEnabled()) {
				logger.debug("Grabbing additional end tag data...");
			}
			
			buffer = this.tagBuffer;
			buffer.clear();
//			int position = this.buffer.asBuffer().position();
			
			buffer.put(this.buffer.asBuffer());
			buffer.flip();
			
//			this.buffer.asBuffer().position(position);
			this.buffer.asBuffer().position(this.buffer.asBuffer().limit());
			
//			buffer.limit(this.buffer.asBuffer().limit());
			
			startTagCopyPosition = index;
			endTagCopyPosition = buffer.limit();
			
			if(bracketCount > 0) {
				
//				CharBuffer toAdd = this.buffer.setPosition();
				if(logger.isDebugEnabled()) {
					logger.debug("Adding remaining accumulation input...");
//					logger.debug(toAdd.toString());
				}
				
				dataBuffer.add(this.buffer.partialSlice());
				this.buffer.setStartPosition(0);
			}
			else {
				// will be copying from buffer

				this.buffer.setStartPosition(-1);

			}
			
						
			buffer.position(buffer.position() + index);
			buffer.compact();
			index = 0;
			
			this.buffer.asBuffer().clear();
			
			if(!read(in)) {
				return null;
			}
			
			CharBuffer newBuffer = this.buffer.asBuffer();
			
			buffer.put(newBuffer.array(),newBuffer.arrayOffset(),Math.min(buffer.remaining(), newBuffer.remaining()));
			buffer.flip();
			
			endIndex = StringUtils.indexOf(buffer, '>',index+1);
			if(endIndex == -1) {
//				this.buffer.position(this.buffer.limit());
				return null;
			}
			else {
				buffer.limit(endIndex+1);
				int copied = buffer.limit() - (endTagCopyPosition - startTagCopyPosition);
				this.buffer.asBuffer().position(copied);
				
				if(bracketCount == 0) {
					this.buffer.setStartPosition(copied);
				}
			}
			
			//buffer.rewind();
		}
		// endIndex found in original input
		else {
			buffer.position(buffer.position() + index);
			buffer.limit(buffer.position() + endIndex+1 - index);
			//if(bracketCount == 0) {
				this.buffer.asBuffer().position(this.buffer.asBuffer().position() + endIndex+1);
			//}
		}
		
		// make sure this is not a comment
		if(StringUtils.startsWith(buffer, "<!--")) {
			return null;
		}
		
		TagType tagType = isTagMatches(buffer,tag);
		
		if(tagType == null) {
			return null;
		}
		if(tagType == TagType.END) {
			return tagType;
		}
		
		// no end tag
		// if tag attribute is defined search for it now
		if(bracketCount == 0 && !MiscUtils.isEmpty(tagAttribute)) {
			boolean contains = StringUtils.caseInsensitiveContains(buffer,tagAttribute);
			if(!contains) {
//				this.buffer.position(endIndex+1);
				
				if(logger.isDebugEnabled()) {
					logger.debug("tag: " + buffer + " failed to match attribute: " + tagAttribute);
				}
				return null;
			}
		}
		
		if(bracketCount == 0) {
			
			if(startTagCopyPosition == -1) {
				this.buffer.setStartPosition(index + this.buffer.asBuffer().position() - (endIndex+1));
			}
			else {
								
//				CharBuffer slice = buffer.slice();
//				slice.position(0);
//				slice.limit(endTagCopyPosition);
				CharBuffer slice = buffer;
				slice.rewind();
				
				if(logger.isDebugEnabled()) {
					logger.debug("Adding new buffer contents to new match...");
					logger.debug(slice.toString());
				}
				dataBuffer.add(slice);
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Found START tag: " + buffer);
		}
		
		return TagType.BEGIN;
	}
	
	private TagType isTagMatches(CharSequence buffer,CharSequence tag) {
		int index3 = StringUtils.caseInsensitiveIndexOf(buffer, tag);
		
		if(index3 == -1) {
			return null;
		}
		
		// make sure match is not a substring match
		char cl = buffer.charAt(index3 - 1);
		char cr = buffer.charAt(index3 + tag.length());
		
		if((cl != '<' && cl != '/' && !Character.isWhitespace(cl)) ||
		   (cr != '>' && cr != '/' && !Character.isWhitespace(cr))) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("Skipping incorrect matched tag: " + buffer);
			}
			return null;
		}
		  
		boolean foundEndTag = false;
		
		while(--index3 > 0) {
			char c = buffer.charAt(index3);
			if(c == '/') {
				foundEndTag = true;
				break;
			}
			else if(!Character.isWhitespace(c)) {
				break;
			}
		}
		
		if(foundEndTag) {
			if(logger.isDebugEnabled()) {
				logger.debug("Found END tag: " + buffer);
			}
			
			return TagType.END;
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug("Found potential START tag: " + buffer);
			}
			
			return TagType.BEGIN;
		}
	}
	
	private boolean read(Reader in) throws IOException {
		if(in.read(buffer.asBuffer()) <= 0) {
			return false;
		}
		buffer.asBuffer().flip();
		return true;
	}
	
	private static class OutputType implements Flushable,Closeable {
		private final Writer writer;
		private final TagListener tagListener;
		
		public OutputType(Writer writer) {
			this.writer = writer;
			this.tagListener = null;
		}
		public OutputType(TagListener listener) {
			this.tagListener = listener;
			this.writer = null;
		}
		public void close() throws IOException {
			if(writer != null) {
				writer.close();
			}
			
		}
		public void flush() throws IOException {
			if(writer != null) {
				writer.flush();
			}
		}
	}
	
	public void parse(Reader in,Writer out) throws IOException {
		parse(in,new OutputType(out));
	}
	public void process(InputStream in, TagListener tokenizer)
			throws IOException {
		parse(in,new OutputType(tokenizer));
	}
	
	public void process(Reader in,TagListener tokenizer) throws IOException {
		parse(in,new OutputType(tokenizer));
	}
	
	public void parse(Reader in,OutputType out) throws IOException {
		
		buffer.clear();
		int bracketCount = 0;
		boolean written = false;
		try {
			while((!written || this.selectMultiple) && (read(in))) {
//				if(bracketCount > 0 && buffer.getEndPosition() == 0) {
//					buffer.setStartPosition(0);
//				}
				while(buffer.asBuffer().hasRemaining()) {
	
					TagType result = findTag(in,bracketCount);
					
					// must count all the tags in the buffer
					if(TagType.BEGIN.equals(result)) {
						++bracketCount;
						
						if(logger.isDebugEnabled()) {
							logger.debug("bracketCount=" + bracketCount);
						}
					}
					else if(TagType.END.equals(result) && bracketCount > 0) {
						--bracketCount;
						
						if(logger.isDebugEnabled()) {
							logger.debug("bracketCount=" + bracketCount);
						}
						
						if(bracketCount <= 0) {
							bracketCount = 0;
							
							if(logger.isDebugEnabled()) {
								logger.debug("bracket count < 0; resetting to 0");
							}
						}
						

					}
					else {
						if(result != null && logger.isDebugEnabled()) {
							logger.debug("Ignoring tag: " + result);
						}
						result = null;
					}
					
					if(bracketCount == 0 && TagType.END.equals(result)) {
						

						//buffer.rewind();
//						int limit = buffer.limit();
//						buffer.flip();
//						buffer.position(bufferStart);
						if(logger.isDebugEnabled()) {
							logger.debug("Adding finished input...");
						}
						
						dataBuffer.add(buffer.partialSlice());
						buffer.setStartPosition(-1);
//						bufferStart = 0;
//						
//						buffer.position(buffer.limit());
//						buffer.limit(limit);
						
						written = writeData(out);
						
						if(written && !selectMultiple) {
							if(logger.isDebugEnabled()) {
								logger.debug("selectMultiple=false;breaking...");
							}
							break;
						}
					}
//					else if(bracketCount > 0) {
//						logger.debug("Adding accumulated input...");
//						dataBuffer.add(buffer.partialSlice());
//						buffer.setStartPosition(buffer.asBuffer().position());
//					}
				} // buffer has remaining
				if(bracketCount > 0) {
					
					if(logger.isDebugEnabled()) {
						logger.debug("Adding remaining buffer input...");
					}
					dataBuffer.add(buffer.partialSlice());
					buffer.setStartPosition(0);
					
				}
				buffer.asBuffer().clear();
			} // all data read
			
			writeData(out);
			

		}
		finally {
			MiscUtils.closeStream(in);
			try {
				if(written) {
					out.flush();
				}
			}
			finally {
				MiscUtils.closeStream(out);
			}
		}
	}
	
	private boolean writeData(OutputType out) throws IOException {
		
		boolean add = false;
		if(!dataBuffer.isEmpty()) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("Writing output data...");
			}
			add = true;
			// if search text defined look for it now
			if(!MiscUtils.isEmpty(this.searchText)) {
				add = dataBuffer.caseInsensitiveContains(searchText);
			}
		
			if(add) {
				if(out.writer != null) {
					dataBuffer.write(out.writer);
				}
				else {
					dataBuffer.finish();
					new HtmlCharSequenceLexer().process(dataBuffer, out.tagListener);
					dataBuffer.clear();
				}
			}
			else {
				dataBuffer.clear();
			}
		}
		
		return add;
	}

}
