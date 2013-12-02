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

import com.gamesalutes.utils.MiscUtils;

public final class TagAttributes {



	private boolean isText;
	private List<String> attributes;
	private final String tagName;
	private Object userData;
	
	public TagAttributes(String tagName) {
		if(MiscUtils.isEmpty(tagName)) {
			throw new IllegalArgumentException("tagName=" + tagName);
		}
		this.tagName = tagName;
	}
	public boolean isProcessable() {
		return isText() || !MiscUtils.isEmpty(getAttributes());
	}
	public boolean isText() {
		return isText;
	}
	public TagAttributes setText(boolean isText) {
		this.isText = isText;
		return this;
	}
	public List<String> getAttributes() {
		return attributes;
	}
	public TagAttributes setAttributes(List<String> attributes) {
		this.attributes = attributes;
		return this;
	}
	
	public String getTagName() { return tagName; }
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagAttributes [isText=");
		builder.append(isText);
		builder.append(", attributes=");
		builder.append(attributes);
		builder.append(", tagName=");
		builder.append(tagName);
		builder.append("]");
		return builder.toString();
	}
	public Object getUserData() {
		return userData;
	}
	public TagAttributes setUserData(Object userData) {
		this.userData = userData;
		return this;
	}
}
