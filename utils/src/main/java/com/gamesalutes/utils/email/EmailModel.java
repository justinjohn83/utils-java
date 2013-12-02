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
package com.gamesalutes.utils.email;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamesalutes.utils.MiscUtils;




public class EmailModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private String from;
	private String displayFrom;
	
	private String subject;
	private String body;
	private List<EmailAttachment> attachments;
	private boolean processed;
		
	private static final String SPLIT = "(,|;|\\s)+";
	
	public EmailModel() {}

	public List<String> getTo() {
		return to;
	}
	
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	public boolean isProcessed()
	{ return processed; }

	public void setTo(List<String> to) {
		this.to = to;
	}
	
	public void setTo(String to) {
		setTo(toList(to));
	}
	public void setCc(String cc) {
		setCc(toList(cc));
	}
	public void setBcc(String bcc) {
		setBcc(toList(bcc));
	}
	
	public List<String> toList(String value) {
		if(!MiscUtils.isEmpty(value)) {
			// split by space or comma or semicolon
			return Arrays.asList(value.split(SPLIT));
		}
		else {
			return null;
		}
		
			
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}
	
	public List<EmailAttachment> getAttachments() {
		return attachments;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public void setAttachments(List<EmailAttachment> attachments) {
		this.attachments = attachments;
	}
	
	public void setAttachment(EmailAttachment attachment) {
		if(attachment == null)
			this.attachments = null;
		else {
			this.attachments = new ArrayList<EmailAttachment>(1);
			this.attachments.add(attachment);
		}
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public String getDisplayFrom() {
		return displayFrom;
	}

	public void setDisplayFrom(String displayFrom) {
		this.displayFrom = displayFrom;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailModel [to=");
		builder.append(to);
		builder.append(", cc=");
		builder.append(cc);
		builder.append(", bcc=");
		builder.append(bcc);
		builder.append(", from=");
		builder.append(from);
		builder.append(", displayFrom=");
		builder.append(displayFrom);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", body=");
		builder.append(body);
		builder.append(", attachments=");
		builder.append(attachments);
		builder.append(", processed=");
		builder.append(processed);
		builder.append("]");
		return builder.toString();
	}
	
	
}
