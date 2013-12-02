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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.ByteBufferInputStream;
import com.gamesalutes.utils.ByteUtils;
import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.StreamDataSource;
import com.gamesalutes.utils.StreamDataSource.DefaultStreamFactory;
import com.gamesalutes.utils.StreamDataSource.StreamFactory;



public class DefaultEmailServiceImpl implements EmailService {

    private static final int SOCKET_CONNECT_TIMEOUT_MS = 60 * 1000;
    private static final int SOCKET_IO_TIMEOUT_MS = 60 * 1000;
    
    
    private String host = "localhost";
    private int port = 25;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultEmailServiceImpl.class.getName());

    public void setPort(Integer port) {
    	this.port = port;
    }
    
    public void setHost(String host) {
    	this.host = host;
    }
    
    private void setEmailRecipients(MimeMessage msg,List<String> addresses,RecipientType type) throws EmailException {
		InternetAddress[] 	inetAddr = new InternetAddress[addresses.size()];
    	try {
	        for(int i = 0; i < addresses.size(); ++i) {
	        	String addr = addresses.get(i);
	        	if(MiscUtils.isEmpty(addr))
	        		throw new IllegalArgumentException("model.get" + type + " has empty address at index:" + i);
	        	addr = addr.trim();
	        	doValidateEmailAddress(addr);
	    		inetAddr[i] = new InternetAddress(addr);
	        }
        
    		msg.setRecipients(type, inetAddr);
    	}
    	catch(MessagingException e) {
    		throw new EmailAddressException(e);
    	}

    }
    
    private void doValidateEmailAddress(String address) throws EmailAddressException {
    	if(address == null)
    		throw new NullPointerException("address");
    	
    	int index = address.indexOf('@');
    	if(index == -1)
    		throw new EmailAddressException("address=" + address + ";missing '@'");
    	if(index == 0)
    		throw new EmailAddressException("address=" + address + ";missing user");
    	if(index == address.length() - 1)
    		throw new EmailAddressException("address=" + address + ";missing domain");
    	int domainIndex = address.indexOf('.',index);
    	if(domainIndex <= index + 1 || domainIndex == address.length() - 1)
    		throw new EmailAddressException("address=" + address + ";bad domain");
    }
	public void send(EmailModel model) throws EmailException {
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Sending email: " + model);
		
		if(model == null)
			throw new NullPointerException("model");

        Properties        emailProps;
        Session           emailSession;

        // set the relay host as a property of the email session
        emailProps = new Properties();
        emailProps.setProperty("mail.transport.protocol", "smtp");
        emailProps.put("mail.smtp.host", host);
        emailProps.setProperty("mail.smtp.port",String.valueOf(port));
        // set the timeouts
        emailProps.setProperty("mail.smtp.connectiontimeout",String.valueOf(SOCKET_CONNECT_TIMEOUT_MS));
        emailProps.setProperty("mail.smtp.timeout",String.valueOf(SOCKET_IO_TIMEOUT_MS));
        
        if(LOGGER.isDebugEnabled())
        	LOGGER.debug("Email properties: " + emailProps);

        // set up email session
        emailSession = Session.getInstance(emailProps, null);
        emailSession.setDebug(false);
        
        
        String from;
        String displayFrom;
        
        String body;
        String subject;
        List<EmailAttachment> attachments;
        
        if(model.getFrom() == null)
        	throw new NullPointerException("from");
        if(MiscUtils.isEmpty(model.getTo()) && MiscUtils.isEmpty(model.getBcc()) && MiscUtils.isEmpty(model.getCc()))
        	throw new IllegalArgumentException("model has no addresses");
        
        from = model.getFrom();
        displayFrom = model.getDisplayFrom();
        body = model.getBody();
        subject = model.getSubject();
        attachments = model.getAttachments();
        
		MimeMessage       emailMessage;
		InternetAddress   emailAddressFrom;


    // create an email message from the current session
    emailMessage = new MimeMessage(emailSession);

    // set the from
    try {
    	emailAddressFrom = new InternetAddress(from,displayFrom);
    	emailMessage.setFrom(emailAddressFrom);
    }
    catch(Exception e) {
    	throw new IllegalStateException(e);
    }
    
    if(!MiscUtils.isEmpty(model.getTo()))
    	setEmailRecipients(emailMessage,model.getTo(),RecipientType.TO);
    if(!MiscUtils.isEmpty(model.getCc()))
    	setEmailRecipients(emailMessage,model.getCc(),RecipientType.CC);
    if(!MiscUtils.isEmpty(model.getBcc()))
    	setEmailRecipients(emailMessage,model.getBcc(),RecipientType.BCC);

    try {
    	
    if(!MiscUtils.isEmpty(subject))
        emailMessage.setSubject(subject);

    Multipart multipart = new MimeMultipart();

    if(body != null)
    {
        // create the message part
        MimeBodyPart messageBodyPart =
          new MimeBodyPart();

        //fill message
        String bodyContentType;
//        body = Utils.base64Encode(body);
        bodyContentType = "text/html; charset=UTF-8";

        messageBodyPart.setContent(body, bodyContentType);
        //Content-Transfer-Encoding : base64
//        messageBodyPart.addHeader("Content-Transfer-Encoding", "base64");
        multipart.addBodyPart(messageBodyPart);
    }
    // Part two is attachment
    if(attachments != null && !attachments.isEmpty())
    {
            try
            {
                    for(EmailAttachment a : attachments)
                    {
                        MimeBodyPart attachBodyPart =
                            new MimeBodyPart();
                        // don't base 64 encode
                        DataSource source =
                            new StreamDataSource(new DefaultStreamFactory(a.getInputStream(),false),a.getName(),a.getContentType());
                        attachBodyPart.setDataHandler(
                            new DataHandler(source));
                        attachBodyPart.setFileName(a.getName());
                        attachBodyPart.setHeader("Content-Type", a.getContentType());
                        attachBodyPart.addHeader("Content-Transfer-Encoding", "base64");

                        // add the attachment to the message
                        multipart.addBodyPart(attachBodyPart);

                    }
            }
            // close all the input streams
            finally
            {
                    for(EmailAttachment a : attachments)
                            MiscUtils.closeStream(a.getInputStream());
            }
    }

    // set the content
    emailMessage.setContent(multipart);
    emailMessage.saveChanges();
    
    if(LOGGER.isDebugEnabled())
    	LOGGER.debug("Sending email message: " + emailMessage);
    
    Transport.send(emailMessage);
    
    if(LOGGER.isDebugEnabled())
    	LOGGER.debug("Sending email complete.");
    
    }
    catch(Exception e) {
    	throw new EmailException(e);
    }

	}
	
	
	/////////////////// INNER CLASSES /////////////////////////////////////////////////////////
	/**
	 * Creates a new input stream and new output stream on demand that
	 * encapsulates the desired content.
	 * 
	 * @author Justin Montgomery
	 */
	private interface StreamFactory
	{
		InputStream newInputStream(String name) throws IOException;
		OutputStream newOutputStream(String name) throws IOException;
	}
	
	/**
	 * <code>DataSource</code> for an arbitrary stream.  MIME type will be "application/octet-stream" unless
	 * indicated otherwise.
	 * 
	 * @author Justin Montgomery
	 */
	private static class StreamDataSource implements DataSource
	{

		private final StreamFactory creator;
		private final String name;
		private final String contentType;
		

		
		public StreamDataSource(StreamFactory creator,String name)
		{
			this(creator,name,null);
		}
		public StreamDataSource(StreamFactory creator,String name,String contentType)
		{
			if(creator == null)
				throw new NullPointerException("creator");
			this.creator = creator;
			this.name = name;
			if(contentType != null)
				this.contentType = contentType;
			else
				this.contentType = "application/octet-stream";
		}
		/* (non-Javadoc)
		 * @see javax.activation.DataSource#getContentType()
		 */
		public String getContentType()
		{
			return contentType;
		}

		/* (non-Javadoc)
		 * @see javax.activation.DataSource#getInputStream()
		 */
		public InputStream getInputStream() throws IOException
		{
			return creator.newInputStream(name);
		}

		/* (non-Javadoc)
		 * @see javax.activation.DataSource#getName()
		 */
		public String getName()
		{
			return name;
		}

		/* (non-Javadoc)
		 * @see javax.activation.DataSource#getOutputStream()
		 */
		public OutputStream getOutputStream() throws IOException
		{
			return creator.newOutputStream(name);
		}
	}
		/**
		 * Default implementation of stream creator that only supports input streams and stores its content in
		 * memory so that {@link #newInputStream(String)} can return an input stream that points to the position of the
		 * content at the time this object was constructed.
		 * 
		 * @author Justin Montgomery
		 */
		private static class DefaultStreamFactory implements StreamFactory
		{
			private ByteBuffer data;
			private InputStream in;
			
			public DefaultStreamFactory(InputStream in,boolean base64Encode)
				throws IOException
			{
				if(in == null)
					throw new NullPointerException("in");
				
                if(base64Encode) {
                	this.data = createBuffer(in,true);
                }
                else if(!(in instanceof ByteArrayInputStream) || !in.markSupported()){
                	this.data = createBuffer(in,false);
                }
                // ByteArrayInputStream do not create another copy
                else {
                	this.in = in;
                	in.mark(in.available());
          
                }
			}
			
			private ByteBuffer createBuffer(InputStream in,boolean base64) throws IOException {
            	
				byte [] data = ByteUtils.readBytes(in);
				
				if(base64) {
                	try {
                		data = Base64.encodeBase64(data);
                	}
                	catch(Exception e) {
                		IOException ioe = new IOException(e.getMessage());
                		ioe.initCause(e);
                		throw ioe;
                	}
				}
				
                return ByteBuffer.wrap(data);
			}
			
			private ByteBuffer getBuffer() throws IOException {
                ByteBuffer b = data.duplicate();
                b.rewind();
                return b;
			}
			/* (non-Javadoc)
			 * @see com.gamesalutes.utils.StreamDataSource.StreamCreator#newInputStream(java.lang.String)
			 */
			public synchronized InputStream newInputStream(String name) throws IOException
			{
				if(in != null) {
					in.reset();
					return in;
				}
				return new ByteBufferInputStream(getBuffer());
			}

			/* (non-Javadoc)
			 * @see com.gamesalutes.utils.StreamDataSource.StreamCreator#newOutputStream(java.lang.String)
			 */
			public OutputStream newOutputStream(String name)
			{
				throw new UnsupportedOperationException();
			}
			
		}
		public void validate(EmailModel model) throws EmailException {
			if(model == null)
				throw new NullPointerException("model");

			doValidateEmailAddress(model.getFrom());
	        if(MiscUtils.isEmpty(model.getTo()) && MiscUtils.isEmpty(model.getBcc()) && MiscUtils.isEmpty(model.getCc()))
	        	throw new IllegalArgumentException("model has no addresses");
	        
	        doValidateEmailAddresses(model.getTo());
	        doValidateEmailAddresses(model.getCc());
	        doValidateEmailAddresses(model.getBcc());
		}
		
		private void doValidateEmailAddresses(List<String> addresses) throws EmailAddressException {
			if(!MiscUtils.isEmpty(addresses)) {
				for(String addr : addresses)
					doValidateEmailAddress(addr);
			}
			
		}

		public boolean validateEmailAddress(String address) {
			try {
				doValidateEmailAddress(address);
				return true;
			}
			catch(EmailAddressException e) {
				return false;
			}
		}

}
