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

import java.util.List;

public interface TagListener {

	
	/**
	 * Called when parsing first begins.
	 * 
	 */
	void onStartDocument();
	
	/**
	 * Called when parsing ends.
	 */
	void onEndDocument();
	
	/**
	 * Tag event.  Return information on how to process this tag or <code>null</code> to skip processing
	 * altogether.
	 * 
	 * @param tagName the tag name
	 * @return <code>true</code> to process and <code>false</code> to skip
	 */
	TagAttributes onStartTag(String tagName);
	
	
	/**
	 * Returns whether to continue processing after processing this tag.
	 * 
	 * @param tagAttributes the tag attributes 
	 * @return <code>true</code> to continue and <code>false</code> otherwise
	 */
	boolean onEndTag(TagAttributes tagAttributes);
	
	/**
	 * Text event.
	 * 
	 * @param tagAttributes the tag attributes identifying this element
	 * @param text the text for this tag
	 */
	void onText(TagAttributes tagAttributes,String text);
	
	/**
	 * Attributes event.
	 * 
	 * @param tagAttributes the tag attributes identifying this element
	 * @param attributeValues the attribute values for the requested names
	 * 
	 * @return <code>true</code> to continue processing this tag and <code>false</code> otherwise
	 */
	boolean onAttributes(TagAttributes tagAttributes,List<String> attributeValues);
}
