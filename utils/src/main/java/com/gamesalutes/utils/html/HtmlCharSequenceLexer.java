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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gamesalutes.utils.ArrayStack;
import com.gamesalutes.utils.CollectionUtils;
import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.StringUtils;
import com.gamesalutes.utils.WebUtils;

public class HtmlCharSequenceLexer {

	private static final class ParserEvent {
		private  final StringBuilder data;
		private final TagAttributes tagAttributes;
		private int index;
		
		public ParserEvent(TagAttributes tagAttributes,int index,boolean isText) {
			this.tagAttributes = tagAttributes;
			this.index = index;
			if(isText) {
				this.data = new StringBuilder(128);
			}
			else {
				this.data = null;
			}
		}
		
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder(256);
			buf.append("tagName=").append(tagAttributes.getTagName()
					).append(";index=").append(index).append(";data=").append(data);
			return buf.toString();
		}
	}
	
	// name is in group(1) and value is in group(3)
	static final Pattern ATTR_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*('|\")(.*?)('|\")");
	
	public void process(CharSequence sequence,TagListener tagListener) {
		
		final int len = sequence.length();
		int i = 0;
		ArrayStack<ParserEvent> events = new ArrayStack<ParserEvent>();
		StringBuilder tagBuffer = new StringBuilder(64);
		Map<String,String> attrMap = CollectionUtils.createHashMap(4, CollectionUtils.LOAD_FACTOR);

		tagListener.onStartDocument();
		
		while(i < len) {
			// find a tag
			i = StringUtils.indexOf(sequence, '<',i);
			if(i == -1 || i == len - 1 ) {
				break;
			}
			int j = StringUtils.indexOf(sequence,'>',i+1);
			if(j == -1) {
				break;
			}
			// ensure this is not a comment
			if(!StringUtils.startsWith(sequence, "<!--",i+1)) {
				// tag
				// check for end tag
				int endTagIndex = isEndTag(sequence,i,j);
				if(endTagIndex != -1) {
					if(!this.doEndTag(sequence, events, tagListener, tagBuffer, i, j, endTagIndex)) {
						break;
					}
				} // end tag
				// start tag
				else {
					if(!this.doStartTag(sequence, events, tagListener, tagBuffer, attrMap, i, j)) {
						break;
					}
				} // start tag
			} // not a comment
			
			i = j + 1;
		} // while
		
		// close any remaining tags
		this.finishTags(tagListener, events);
		
		tagListener.onEndDocument();
		
	} // process
	
	private boolean doStartTag(final CharSequence sequence,
			final ArrayStack<ParserEvent> events,
			final TagListener tagListener,
			final StringBuilder tagBuffer,
			final Map<String,String> attrMap,
			final int i,
			final int j
			) {
		
		int endIndex = this.isInlineTag(sequence, i, j);
		boolean isInline = endIndex != -1;
		if(endIndex == -1) {
			endIndex = j;
		}
		boolean push = false;
		boolean isText = false;
		boolean continueProcessing = true;

		String tagName = getTagName(sequence,tagBuffer,i,endIndex);
		// check to see if we are missing a closed tag that we can infer
		if(!isInline) {
			isInline = this.isImplicitlyInline(tagName);
		}
		this.checkTags(tagListener, events, tagName);
		
		TagAttributes tagAttr = null;
		if((tagAttr = tagListener.onStartTag(tagName)) != null) {
			
			push = this.doAttributes(sequence, tagListener, tagAttr, tagBuffer, attrMap, i, endIndex);			
			// only push non-inline
			push &= !isInline;
			
			if(push) {
				isText = tagAttr.isText();
			}
		}
		else if(!events.isEmpty() && !isInline) {
			push = true;
			tagAttr = new TagAttributes(tagName);
		}
		
		
		for(int k = events.size() - 1; k >= 0; --k) {
			ParserEvent event = events.get(k);
			
			if(event.data != null) {
				for(int x = event.index; x < i; ++x) {
					event.data.append(sequence.charAt(x));
				}
			}
			event.index = j + 1;
		}
		// if this tag is a br then must add a "\n" to the tokens
		if("br".equalsIgnoreCase(tagName)) {
			for(int k = events.size() - 1; k >= 0; --k) {
				ParserEvent event = events.get(k);
				
				if(event.data != null) {
					event.data.append('\n');
				}
			}
		}
		
		
		
		if(push) {
			ParserEvent event = new ParserEvent(tagAttr,j+1,isText);
			events.push(event);
		}
		else if(isInline) {
			// end tag event
			if(tagAttr != null && !tagListener.onEndTag(tagAttr)) {
				continueProcessing = false;
			}
		}
		
		return continueProcessing;
	}
	
	private boolean doEndTag(final CharSequence sequence,
			final ArrayStack<ParserEvent> events,
			final TagListener tagListener,
			final StringBuilder tagBuffer,
			final int i,
			final int j,
			final int endTagIndex) {
		
		if(!events.isEmpty()) {
			String tagName = getTagName(sequence,tagBuffer,endTagIndex,j);
			
			int pushStart = -1;
			// notify of text if we are configured for text
			for(int k = events.size() - 1; k >= 0; --k) {
				ParserEvent event = events.get(k);
				
				if(pushStart == -1) {
					pushStart = event.index;
				}
				if(event.data != null) {
					for(int x = pushStart; x < i; ++x) {
						event.data.append(sequence.charAt(x));
					}
				}
				event.index = j + 1;
			}
			
			if(!this.doText(tagListener, events, tagName,false)) {
				return false;
			}
		} // events exist
		
		return true;
	}
		
	private void checkTags(final TagListener tagListener,
			final ArrayStack<ParserEvent> events,
			final String tagName) {
		if(!events.isEmpty()) {
			// currently check for consistency in "td" and "tr"
			if(tagName.equalsIgnoreCase("tr")) {
				// make sure last td is closed
				this.doText(tagListener, events, "td",true);
			}
			else if(tagName.equalsIgnoreCase("table")) {
				// make sure last "tr" is closed
				this.doText(tagListener, events, "tr",true);
			}
			else if(tagName.equalsIgnoreCase("td")) {
				// make sure that previous td is closed
				this.doText(tagListener, events, "td",true);
			}
			else if(tagName.equalsIgnoreCase("option")) {
				this.doText(tagListener, events, "option",true);
			}
			else if(tagName.equalsIgnoreCase("select")) {
				this.doText(tagListener, events, "option",true);
			}
			else if(tagName.equalsIgnoreCase("li")) {
				this.doText(tagListener, events, "li",true);
			}
			else if(tagName.equalsIgnoreCase("ul")) {
				this.doText(tagListener, events, "li",true);
			}
			else if(tagName.equalsIgnoreCase("ol")) {
				this.doText(tagListener, events, "li",true);
			}
			// these are already covered and are implicitly inline
			/*
			else {
				// make sure "img","br" tags are closed
				this.doText(tagListener,events,"img",false);
				this.doText(tagListener,events,"br",false);
			}
			*/
		}
	}
	
	private boolean isImplicitlyInline(String tagName) {
		
		/* Implicitly inline
		 * <area >
			<base>
			<br>
			<col>
			<command>
			<embed>
			<hr>
			<img>
			<input>
		 */
		/*
		 * These also can be inline but are NOT required:
		 * <body> </body>
			<colgroup> </colgroup>
			<dd> </dd>
			<dt> </dt>
			<head> </head>
			<html> </html>
			<li> </li>
			<optgroup> </optgroup>
			<option> </option>
			<p> </p>
			<tbody> </tbody>
			<td> </td>
			<tfoot> </tfoot>
			<th> </th>
			<thead> </thead>
			<tr> </tr>
		 */
		return "img".equalsIgnoreCase(tagName) ||
			   "br".equalsIgnoreCase(tagName) || 
			   "hr".equalsIgnoreCase(tagName) || 
			   "input".equalsIgnoreCase(tagName);
	}
	
	private void finishTags(final TagListener tagListener,
			final ArrayStack<ParserEvent> events) {
			while(!events.isEmpty()) {
				ParserEvent event = events.peek();
				this.doText(tagListener, events, event.tagAttributes.getTagName(),false);
			}
	}
	
	private int findIndex(final ArrayStack<ParserEvent> events,String tagName,boolean recursive) {
		
			if(recursive && "tr".equalsIgnoreCase(tagName)) {
				for(int i = events.size() - 1; i >= 0; --i) {
					String name = events.get(i).tagAttributes.getTagName();
					
					if("tr".equalsIgnoreCase(name)/* || "table".equalsIgnoreCase(name)*/) {
						return i;
					}
					// don't cross structure boundaries
					else if("table".equalsIgnoreCase(name)) {
						break;
					}
				} // for
			} // if
			else if(recursive && "td".equalsIgnoreCase(tagName)) {
				for(int i = events.size() - 1; i >= 0; --i) {
					String name = events.get(i).tagAttributes.getTagName();
					
					if("td".equalsIgnoreCase(name) /* || "tr".equalsIgnoreCase(name) */) {
						return i;
					}
					// don't cross structure boundaries
					else if("table".equalsIgnoreCase(name)) {
						break;
					}
				} // for
			}
			else if(recursive && "option".equalsIgnoreCase(tagName)) {
				for(int i = events.size() - 1; i >= 0; --i) {
					String name = events.get(i).tagAttributes.getTagName();
					
					if("option".equalsIgnoreCase(name) /* || "tr".equalsIgnoreCase(name) */) {
						return i;
					}
					// don't cross structure boundaries
					else if("select".equalsIgnoreCase(name)) {
						break;
					}
				} // for
			}
			else if(recursive && "li".equalsIgnoreCase(tagName)) {
				for(int i = events.size() - 1; i >= 0; --i) {
					String name = events.get(i).tagAttributes.getTagName();
					
					if("li".equalsIgnoreCase(name) /* || "tr".equalsIgnoreCase(name) */) {
						return i;
					}
					// don't cross structure boundaries
					else if("ol".equalsIgnoreCase(name) || "ul".equalsIgnoreCase(name)) {
						break;
					}
				} // for
			}
			else {
				ParserEvent event = events.peek();
				if(event.tagAttributes.getTagName().equalsIgnoreCase(tagName)) {
					return events.size() - 1;
				}
			}
		
		return -1;
	}
	private boolean doText(
			final TagListener tagListener,
			final ArrayStack<ParserEvent> events,
			final String tagName,boolean recursive) {
		
		int popIndex = this.findIndex(events, tagName,recursive);
		boolean continueProcessing = true;
		
		if(popIndex != -1) {
			for(int i = events.size() - 1; i >= popIndex; --i) {
				
				ParserEvent event = events.get(i);
				// text event
				if(event.data != null) {
					// replace html entities
					if(!MiscUtils.isEmpty(event.data)) {
						WebUtils.decodeHtmlEntities(event.data);
					}
					tagListener.onText(event.tagAttributes,event.data.toString());
				}
				// end tag event
				if(!tagListener.onEndTag(event.tagAttributes)) {
					continueProcessing = false;
					break;
				}
			} // for
			int popCount = events.size() - popIndex;
			while(popCount-- > 0) {
				events.pop();
			}
		}
		
		return continueProcessing;
	}
	
	private boolean doAttributes(
			final CharSequence sequence,
			final TagListener tagListener,
			final TagAttributes attr,
			final StringBuilder tagBuffer,
			final Map<String,String> attrMap,
			final int i,
			final int endIndex
			) {
		List<String> attrNames = attr.getAttributes();
		final String tagName = attr.getTagName();
		
		boolean process = false;
		
		if(!MiscUtils.isEmpty(attrNames)) {
			
			List<String> attrValues = new ArrayList<String>(attrNames.size());
			
			MiscUtils.clearStringBuilder(tagBuffer);
			// copy to buffer
			for(int x = i + tagName.length() + 1; x < endIndex; ++x) {
				tagBuffer.append(sequence.charAt(x));
			}
			// regex
			Matcher m = ATTR_PATTERN.matcher(tagBuffer);
			attrMap.clear();
			
			while(m.find()) {
				// group(1) is name
				// group(2) is value
				String name = m.group(1);
				String value = m.group(3);
				
				attrMap.put(name.toLowerCase(),value);
				
			} // while matches
			for(String attrName : attrNames) {
				attrName = attrName.toLowerCase();
				String value = attrMap.get(attrName);
				if(!MiscUtils.isEmpty(value)) {
					// replace html entities
					value = WebUtils.decodeHtmlEntities(value);
				}
				attrValues.add(value);
			}
			
			// event
			if(tagListener.onAttributes(attr, attrValues)) {
				process = true;
			}
		} // attribute names defined
		else {
			process = true;
		}
		
		return process;
	}
	
	private String getTagName(CharSequence seq,StringBuilder buf,int start,int end) {
		MiscUtils.clearStringBuilder(buf);
		
		boolean firstSpace = true;
		
		for(int i = start+1; i < end; ++i) {
			char c = seq.charAt(i);
			
			if(!Character.isWhitespace(c)) {
				buf.append(c);
				firstSpace = false;
			}
			else if(!firstSpace) {
				break;
			}
		}
		
		return buf.toString();
	}
	
	private int isInlineTag(CharSequence seq,int start,int end) {
		if(seq.charAt(end-1) == '/') {
			return end - 1;
		}
		return -1;
	}
	private int isEndTag(CharSequence seq,int start,int end) {
		
		if(seq.charAt(start+1) == '/') {
			return start+1;
		}
//		int index = end;
//		
//		while(--index > 0) {
//			char c = seq.charAt(index);
//			if(c == '/') {
//				return c - 1;
//			}
//			else if(!Character.isWhitespace(c)) {
//				break;
//			}
//		}
		
		return -1;
	}
}
