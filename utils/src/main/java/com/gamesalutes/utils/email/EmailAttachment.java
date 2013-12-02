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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import com.gamesalutes.utils.ByteUtils;


public class EmailAttachment implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
    private String name;
    private String contentType;
    
    // can't serialize stream
    private byte [] data;
    


    public EmailAttachment(String name,String contentType,InputStream data)
    {
        if(name == null)
            throw new NullPointerException("name");
        if(data == null)
            throw new NullPointerException("data");
        if(contentType == null)
            contentType = "application/octet-stream";
        this.name = name;
        this.contentType = contentType;
        try {
        	this.data = ByteUtils.readBytes(data);
        }
        catch(IOException e) {
        	throw new RuntimeException(e);
        }
    }

    public String getName() { return name; }
    public String getContentType() { return contentType; }
    public InputStream getInputStream() { 
    	if(data != null)
    		return new ByteArrayInputStream(data); 
    	return null;
    }
    
    @Override
    public String toString() {
    	StringBuilder s = new StringBuilder(1024);
    	s.append("name=").append(name).append(";contentType=").append(contentType).append("dataLength=").append(data != null ? data.length : "NULL");
    	return s.toString();
    }
    // custom read/write for InputStream
//    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
//    	
//    	// read in default state
//    	in.defaultReadObject();
//    	
//    	byte [] data = Utils.readBytes(in);
//    	if(data != null && data.length > 0)
//    		stream = new ByteArrayInputStream(data);
//    	
//    }
//    
//    private void writeObject(ObjectOutputStream out) throws IOException {
//    	// write out default state
//    	out.defaultWriteObject();
//    	
//    	// write out stream as byte array
//    	if(stream != null)
//    		out.write(Utils.readBytes(stream));
//    }
    
}
