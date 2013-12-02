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
/* Copyright 2008 - 2009 University of Chicago
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email utility methods using <a href="http://java.sun.com/products/javamail/">JavaMail API</a>.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class EmailUtil 
{
	private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class.getSimpleName());
	
        public static class EmailAttachment
        {
            private final String name;
            private final String contentType;
            private final InputStream data;


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
                this.data = data;
            }

            public String getName() { return name; }
            public String getContentType() { return contentType; }
            public InputStream getInputStream() { return data; }

        }
        /**
         * Callback for serializing email messages.
         */
        public interface MessageSerializationCallback
        {
            /**
             * Creates a new <code>Writer</code> for receiving the data in <code>msg</code>.
             *
             * @param msg the <code>MimeMessage</code> that will be serialized
             *
             * @throws IOException if the stream cannot be created
             *
             * @return the initialized <code>Writer</code>
             */
            Writer newMessageWriter(MimeMessage msg) throws IOException;

            /**
             * Called when the message has been completely written to <code>w</code>.
             * <code>w</code> is still open at this point but will be automatically closed
             * by the caller.
             *
             * @param msg the written <code>MimeMessage</code>
             * @param w the <code>Writer</code> used to store the content
             */
            void messageWritten(MimeMessage msg,Writer w);
        }

        /**
         * Callback for reading a <code>MimeMessage</code>.
         *
         */
        public interface MessageReaderCallback
        {
            /**
             * Callback for each piece of content in a <code>MimeMessage</code>.  Should return
             * <code>true</code> to continue parsing more content parts if available and <code>false</code> to
             * stop parsing.
             *
             * @param contentType the Content-Type header for the part
             * @param disposition the disposition of the content.  Either <code>Part.INLINE</code>,
             *                     <code>Part.ATTACHMENT</code>, or <code>null</code> if unknown
             * @param name the name of the attachment if <code>disposition</code> is <code>Part.ATTACHMENT</code> and
             *           <code>null</code> otherwise
             * @param content the <code>InputStream</code> containing the content of the part
             * @return <code>true</code> to continue parsing more content and <code>false</code> otherwise
             * @throws Exception if error occurs processing the content
             */
            boolean onContent(String contentType,String disposition,String name,InputStream content) throws Exception;
        }

	private EmailUtil() {}

        private static final Pattern BOUNDARY_PATTERN = Pattern.compile("boundary=\"");

        private static final int SOCKET_CONNECT_TIMEOUT_MS = 60 * 1000;
        private static final int SOCKET_IO_TIMEOUT_MS = 60 * 1000;
	
	/**
	 * Sends an email message.
	 *  
	 * @param relayHost the email relay server
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @throws Exception if an error occurs during the send operation
	 */
	public static void sendEmail(
			String relayHost,String from,String [] to,
			String [] cc,String subject,String body)
		throws Exception
	{
		sendEmail(relayHost,from,to,cc,null,subject,body);
	}
	/**
	 * Sends an email message.
	 *  
	 * @param relayHost the email relay server
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @throws Exception if an error occurs during the send operation
	 */
	public static void sendEmail(String relayHost,String from,String [] to,
			String [] cc,String [] bcc,String subject,String body)
		throws Exception
	{
		sendEmail(relayHost,from,to,cc,bcc,subject,body,null);
	}



       /**
         * Convenience method for creating a MIME mail message.
         *
         * @param mailSession the <code>Session</code> to use for the email message
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @param attachments attachments to send in the email or <code>null</code> to send no attachments
	 * @throws Exception if an error occurs during the send operation
         *
         * @return the created <code>MimeMessage</code>
	 */
        public static MimeMessage createEmail(Session mailSession,String from,String [] to,String [] cc,
                String [] bcc,String subject,String body,
                Map<String,InputStream> attachments)
                throws Exception
        {
            List<EmailAttachment> list = null;
            if(attachments != null)
            {
                list = new ArrayList<EmailAttachment>(attachments.size());
                for(Map.Entry<String,InputStream> E : attachments.entrySet())
                    list.add(new EmailAttachment(E.getKey(),null,E.getValue()));
            }

            return createEmail(mailSession,from,to,cc,bcc,subject,body,list);
        }

        /**
         * Convenience method for creating a MIME mail message.
         *
         * @param mailSession the <code>Session</code> to use for the email message
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @param attachments attachments to send in the email or <code>null</code> to send no attachments
	 * @throws Exception if an error occurs during the send operation
         *
         * @return the created <code>MimeMessage</code>
	 */
        public static MimeMessage createEmail(Session mailSession,String from,String [] to,String [] cc,
                String [] bcc,String subject,String body,
                List<EmailAttachment> attachments)
                throws Exception
        {
            return createEmail(mailSession,from,to,cc,bcc,subject,body,"text/html",attachments);
        }
        /**
         * Convenience method for creating a MIME mail message.
         *
         * @param mailSession the <code>Session</code> to use for the email message
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param subject email subject
	 * @param body email body
         * @param bodyContentType the content type for the body
	 * @param attachments attachments to send in the email or <code>null</code> to send no attachments
	 * @throws Exception if an error occurs during the send operation
         *
         * @return the created <code>MimeMessage</code>
	 */
        public static MimeMessage createEmail(Session mailSession,String from,String [] to,String [] cc,
                String [] bcc,String subject,String body,
                String bodyContentType,
                List<EmailAttachment> attachments)
                throws Exception
        {
            if(from == null)
                    throw new NullPointerException("from");
            // must have either a to,cc,or bcc
            if(MiscUtils.isEmpty(to) && MiscUtils.isEmpty(cc) && MiscUtils.isEmpty(bcc))
                    throw new IllegalArgumentException("at least one of to,cc,bcc must be non-empty");


	    MimeMessage       emailMessage;
            InternetAddress   emailAddressFrom;
	    InternetAddress[] emailAddressTo;
	    InternetAddress[] emailAddressCc;
	    InternetAddress[] emailAddressBcc;


            // create an email message from the current session
            emailMessage = new MimeMessage(mailSession);

            // set the from
            emailAddressFrom = new InternetAddress(from);
            emailMessage.setFrom(emailAddressFrom);

            // set the to list if defined
            if(to != null && to.length != 0)
            {
                    emailAddressTo = new InternetAddress[to.length];
                    for(int i = 0, size = to.length; i < size; ++i)
                            emailAddressTo[i] = new InternetAddress(to[i]);
                    emailMessage.setRecipients(Message.RecipientType.TO, emailAddressTo);
            }

            // set the cc list if it is defined
            if(cc != null && cc.length != 0)
            {
                emailAddressCc = new InternetAddress[cc.length];
                for(int i = 0, size = cc.length; i < size; ++i)
                    emailAddressCc[i] = new InternetAddress(cc[i]);
                emailMessage.setRecipients(Message.RecipientType.CC, emailAddressCc);
            }

            // set the bcc list if it is defined
            if(bcc != null && bcc.length != 0)
            {
                emailAddressBcc = new InternetAddress[bcc.length];
                for(int i = 0, size = bcc.length; i < size; ++i)
                    emailAddressBcc[i] = new InternetAddress(bcc[i]);
                emailMessage.setRecipients(Message.RecipientType.BCC, emailAddressBcc);
            }

            if(subject != null)
                    emailMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            if(body != null)
            {
                // create the message part
                MimeBodyPart messageBodyPart =
                  new MimeBodyPart();

                //fill message
                body = WebUtils.base64Encode(body);
                if(bodyContentType == null)
                    bodyContentType = "text/plain";
                if(bodyContentType.startsWith("text/"))
                    bodyContentType += "; charset=UTF-8";

                messageBodyPart.setContent(body, bodyContentType);
                //Content-Transfer-Encoding : base64
                messageBodyPart.addHeader("Content-Transfer-Encoding", "base64");
                multipart.addBodyPart(messageBodyPart);
            }
            // Part two is attachment
            if(!MiscUtils.isEmpty(attachments))
            {
                    try
                    {
                            for(EmailAttachment a : attachments)
                            {
                                MimeBodyPart attachBodyPart =
                                    new MimeBodyPart();
                                DataSource source =
                                    new StreamDataSource(new StreamDataSource.DefaultStreamFactory(a.getInputStream(),true),a.getName(),a.getContentType());
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

            return emailMessage;
        }
	/**
	 * Sends an email message.
	 *  
	 * @param relayHost the email relay server
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @param attachments attachments to send in the email or <code>null</code> to send no attachments
	 * @throws Exception if an error occurs during the send operation
         *
	 */
	public static void sendEmail(
			String relayHost,String from,String [] to,
			String [] cc,String [] bcc,String subject,String body,
			Map<String,InputStream> attachments)
		throws Exception
	{

            if(relayHost == null)
                throw new NullPointerException("relayHost");
            
	    Session           emailSession;
	    Message           emailMessage;  

            emailSession = createEmailSession(relayHost,null);

            // create the message from the given info
            emailMessage = createEmail(emailSession,from,to,cc,bcc,subject,body,attachments);

            // send the message
            sendEmail(emailMessage);
        }
        
        /**
         * Sends the given mail message
         * 
         * @param msg the <code>Message</code>
         * @throws Exception if error occurs during sending
         */
        public static void sendEmail(Message msg)
                throws Exception
        {
            if(msg == null)
                throw new NullPointerException("msg");

            // send the message
            try
            {
                Transport.send(msg);
            }
            catch(Throwable t)
            {
                logger.error("Unable to send message [" +
                        messageToString(msg) + "]",t);

                ErrorUtils.rethrowChecked(t, Exception.class);
            }
        }

        /**
         * Creates a string form of <code>msg</code>.
         *
         * @param msg the <code>Message</code>
         * @return the string form
         */
        public static String messageToString(Message msg)
        {
            if(msg == null)
                return null;

            StringBuilder s = new StringBuilder(1024);

            try
            {
                s.append("from=").append(Arrays.toString(msg.getFrom())
                 ).append(";to=").append(Arrays.toString(msg.getRecipients(RecipientType.TO))
                 ).append(";cc=").append(Arrays.toString(msg.getRecipients(RecipientType.CC))
                 ).append(";bcc=").append(Arrays.toString(msg.getRecipients(RecipientType.BCC))
                 ).append(";subject=").append(msg.getSubject());
                
                return s.toString();

            }
            catch(Exception e)
            {
                logger.warn("Unable to format message: " + msg,e);
                return msg.toString();
            }
        }

        private static String messageToDebugString(Message msg)
        {
            StringBuilder s = new StringBuilder(32 * 1024);

            try
            {
                s.append(messageToString(msg));
                if(msg instanceof MimeMessage)
                    s.append(";content=\n").append(EmailUtil.serializeMessage((MimeMessage)msg));

                return s.toString();
            }
            catch(Exception e)
            {
               logger.warn("Unable to format message: " + msg,e);
                return msg.toString();
            }

        }

        /**
         * Creates an email <code>Session</code> given the relay host.
         *
         * @param relayHost
         *
         * @return the <code>Session</code>
         */
        public static Session createEmailSession(String relayHost,Integer port)
        {
            if(relayHost == null)
                throw new NullPointerException("relayHost");

            Properties        emailProps;
	    Session           emailSession;

	    // set the relay host as a property of the email session
            emailProps = new Properties();
            emailProps.setProperty("mail.transport.protocol", "smtp");
            emailProps.put("mail.smtp.host", relayHost);
            if(port != null)
                emailProps.setProperty("mail.smtp.port",String.valueOf(port));

            // set the timeouts
            emailProps.setProperty("mail.smtp.connectiontimeout",String.valueOf(SOCKET_CONNECT_TIMEOUT_MS));
            emailProps.setProperty("mail.smtp.timeout",String.valueOf(SOCKET_IO_TIMEOUT_MS));
            

            emailSession = Session.getInstance(emailProps, null);
            emailSession.setDebug(false);

            return emailSession;
        }

        /**
         * Creates a default email session without any properties set.
         *
         * @return  the <code>Session</code>
         */
        public static Session createEmailSession()
        {
            Session emailSession = Session.getInstance(new Properties(), null);
            emailSession.setDebug(false);

            return emailSession;
        }
	
	
	/**
	 * Sends an email message replacing identifier sequences with their
	 * mapped values.
	 * 
	 * @param strReplaceMap map of replacement identifiers to the values that
	 *        should be replaced
	 * @param relayHost the email relay server
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @throws Exception if an error occurs during the send operation
	 */
	public static void sendEmailDecoded(
			Map<String,String> strReplaceMap,
			String relayHost,String from,String [] to,
			String [] cc,String subject,String body) 
		throws Exception
	{
		sendEmailDecoded(strReplaceMap,relayHost,from,to,cc,null,subject,body);
	}


        	/**
	 * Creates an email message replacing identifier sequences with their
	 * mapped values.
	 *
         * @param emailSession the <code>Session</code> to use for the message
	 * @param strReplaceMap map of replacement identifiers to the values that
	 *        should be replaced
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param bcc blind carbon copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @throws Exception if an error occurs during the send operation
	 */
        public static MimeMessage createEmailDecoded(Session mailSession,
                Map<String,String> strReplaceMap,
                String from,String [] to,
		String [] cc,String [] bcc,String subject,String body,
                Map<String,InputStream> attachments)
                    throws Exception
        {
            List<EmailAttachment> list = null;

            if(attachments != null)
            {
                list = new ArrayList<EmailAttachment>(attachments.size());
                for(Map.Entry<String,InputStream> E : attachments.entrySet())
                    list.add(new EmailAttachment(E.getKey(),null,E.getValue()));
            }

            return createEmailDecoded(mailSession,strReplaceMap,from,to,cc,bcc,subject,body,list);
        }
	/**
	 * Creates an email message replacing identifier sequences with their
	 * mapped values.
	 *
         * @param emailSession the <code>Session</code> to use for the message
	 * @param strReplaceMap map of replacement identifiers to the values that
	 *        should be replaced
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param bcc blind carbon copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @throws Exception if an error occurs during the send operation
	 */
        public static MimeMessage createEmailDecoded(Session mailSession,
                Map<String,String> strReplaceMap,
                String from,String [] to,
		String [] cc,String [] bcc,String subject,String body,
                List<EmailAttachment> attachments)
                    throws Exception
        {
		if(from == null)
			throw new NullPointerException("from");

		// must have either a to,cc,or bcc
		if(MiscUtils.isEmpty(to) && MiscUtils.isEmpty(cc) && MiscUtils.isEmpty(bcc))
			throw new IllegalArgumentException("at least one of to,cc,bcc must be non-empty");

		// deep copy the array arguments so that they are not modified
		String [] tmp;

		if(to != null && to.length != 0)
		{
			tmp = new String[to.length];
			System.arraycopy(to, 0, tmp, 0, tmp.length);
			to = tmp;
		}
		if(cc != null && cc.length != 0)
		{
			tmp = new String[cc.length];
			System.arraycopy(cc, 0, tmp, 0, tmp.length);
			cc = tmp;
		}

		if(bcc != null && bcc.length != 0)
		{
			tmp = new String[bcc.length];
			System.arraycopy(bcc, 0, tmp, 0, tmp.length);
			bcc = tmp;
		}

		// replace all the identifier characters with their actual values
		for(Map.Entry<String, String> E : strReplaceMap.entrySet())
		{
			String idKey = E.getKey();
			String idValue = E.getValue();

			from = from.replace(idKey, idValue);
			if(to != null)
			{
				for(int i = 0; i < to.length; ++i)
					to[i] = to[i].replace(idKey,idValue);
			}

			if(cc != null)
			{
				for(int i = 0; i < cc.length; ++i)
					cc[i] = cc[i].replace(idKey,idValue);
			}

			if(bcc != null)
			{
				for(int i = 0; i < bcc.length; ++i)
					bcc[i] = bcc[i].replace(idKey,idValue);
			}

			if(subject != null)
				subject = subject.replace(idKey, idValue);
			if(body != null)
				body = body.replace(idKey, idValue);
		}

		// send the email
		return createEmail(mailSession,from,to,cc,bcc,subject,body,attachments);
        }

	/**
	 * Sends an email message replacing identifier sequences with their
	 * mapped values.
	 * 
	 * @param strReplaceMap map of replacement identifiers to the values that
	 *        should be replaced
	 * @param relayHost the email relay server
	 * @param from sender address string
	 * @param to recipient address strings
	 * @param cc copied recipient address string
	 * @param bcc blind carbon copied recipient address string
	 * @param subject email subject
	 * @param body email body
	 * @throws Exception if an error occurs during the send operation
	 */
	public static void sendEmailDecoded(
			Map<String,String> strReplaceMap,
			String relayHost,String from,String [] to,
			String [] cc,String [] bcc,String subject,String body) 
		throws Exception
	{
		if(relayHost == null)
			throw new NullPointerException("relayHost");

                Session emailSession;
                Message emailMessage;

                emailSession = createEmailSession(relayHost,null);


                emailMessage = createEmailDecoded(emailSession,strReplaceMap,
                        from,to,cc,bcc,subject,body,(List)null);

                sendEmail(emailMessage);
	}

        /**
         * Serializes the given <code>msg</code> to its raw encoded string form using the given
         * <code>cb</code> to receive the output.
         *
         * @throws Exception if error occurs processing the message
         */
        public static void serializeMessage(MimeMessage msg,MessageSerializationCallback cb)
                throws Exception
        {
            Writer writer = cb.newMessageWriter(msg);

            try
            {

                // write out the headers
                Enumeration headers = msg.getAllHeaderLines();
                String boundary = null;
                while(headers.hasMoreElements())
                {
                    String header = (String)headers.nextElement();
                    if(boundary == null)
                    {
                        Matcher m = BOUNDARY_PATTERN.matcher(header);
                        if(m.find())
                        {
                            int start = m.end();
                            int end = header.indexOf('"',start+1);
                            boundary = header.substring(start,end);
                        }
                    }
                    writer.write(header);
                    writer.write("\r\n");
                }
                writer.write("\r\n");

                // read in raw input stream
                Object part = msg.getContent();
                if(part instanceof MimeMultipart)
                {
                    writeMultiPart((MimeMultipart)part,boundary,writer);
                }
                // write it inline
                else
                {
                    try
                    {
                        writeInputStream(msg.getRawInputStream(),writer);
                    }
                    catch(MessagingException e)
                    {
                        writeInputStream(msg.getInputStream(),writer);
                    }
                }
                writer.flush();
                cb.messageWritten(msg, writer);
            }
            finally
            {
                MiscUtils.closeStream(writer);
            }
        }

        private static boolean isBase64Content(Part p) throws MessagingException
        {
            // Content-Transfer-Encoding: base64
            Enumeration headers = p.getAllHeaders();
            while(headers.hasMoreElements())
            {
                Header next = (Header)headers.nextElement();
                if(StringUtils.caseInsensitiveEquals("Content-Transfer-Encoding", next.getName()))
                {
                    return StringUtils.caseInsensitiveEquals("base64", next.getValue());
                }
            }
            return false;
        }

        private static void writeMultiPart(MimeMultipart part,String boundary,Writer writer)
                throws Exception
        {
            for(int i = 0; i < part.getCount(); ++i)
            {
                if(boundary != null)
                {
                    writer.write("--");
                    writer.write(boundary);
                    writer.write("\r\n");
                }
                writeBodyPart((MimeBodyPart) part.getBodyPart(i),writer);
                if(i <= part.getCount() - 1)
                    writer.write("\r\n");
            }
            
            if(boundary != null)
            {
                writer.write("--");
                writer.write(boundary);
                writer.write("\r\n");
            }

        }

        private static void writeBodyPart(MimeBodyPart part,Writer writer)
                throws Exception
        {
             // write out the body header
             Enumeration headers = part.getAllHeaderLines();
             while(headers.hasMoreElements())
             {
                 writer.write((String)headers.nextElement());
                 writer.write("\r\n");
             }

             writer.write("\r\n");
             try
             {
                writeInputStream(part.getRawInputStream(),writer);
             }
             catch(MessagingException e)
             {
                /* MimeMessage.getRawInputStream() may throw a "no content"
                 * MessagingException. In JavaMail v1.3, when you initially
                 * create a message using MimeMessage APIs, there is no raw
                 * content available. getInputStream() works, but
                 * getRawInputStream() throws an exception. If we catch that
                 * exception, throw the UDTE. It should mean that someone
                 * has locally constructed a message part for which JavaMail
                 * doesn't have a DataHandler.
                 */
                 writeInputStream(part.getInputStream(),writer);

             }

        }

        private static void writeInputStream(InputStream in,Writer writer)
                throws Exception
        {
             // write out the content
             // write out the content
             char [] buf = new char[512];

             Reader r = new BufferedReader(FileUtils.createInputStreamReader(in));

             int cnt;
             while((cnt = r.read(buf,0,buf.length)) > 0)
                writer.write(buf,0,cnt);
        }

        /**
         * Serializes the given <code>msg</code> to its raw encoded string form.
         *
         * @throws Exception if error occurs processing the message
         *
         * @return the raw encoded string form of the message
         */
        public static String serializeMessage(MimeMessage msg)
                throws Exception
        {
            final Writer writer = new StringWriter(16 * 1024);


            MessageSerializationCallback cb = new MessageSerializationCallback()
            {

                public Writer newMessageWriter(MimeMessage msg)
                {
                    return writer;
                }

                public void messageWritten(MimeMessage msg, Writer w)
                {

                }

            };
            serializeMessage(msg,cb);
            return writer.toString();
        }

        /*
         * Deserializes a message written by {@link #serializeMessage(MimeMessage)}.
         *
         * @param msg the raw message content
         *
         * @return the read <code>MimeMessage</code>
         */
        public static MimeMessage deserializeMessage(String msg)
                throws Exception
        {
            return deserializeMessage(null,msg);
        }

         /*
         * Deserializes a message written by {@link #serializeMessage(MimeMessage)}.
         *
         * @param session the <code>Session</code> to use for the read message
         * @param msg the raw message content
         *
         * @return the read <code>MimeMessage</code>
         */
        public static MimeMessage deserializeMessage(Session session,String msg)
                throws Exception
        {
            try
            {
                return deserializeMessage(session,
                        new ByteArrayInputStream(msg.getBytes("UTF-8")));
            }
            catch(UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }

        /*
         * Deserializes a message written by {@link #serializeMessage(MimeMessage)}.
         *
         * @param msg the raw message content
         *
         * @return the read <code>MimeMessage</code>
         */
        public static MimeMessage deserializeMessage(InputStream is)
                throws Exception
        {
            return deserializeMessage(null,is);
        }

        /*
         * Deserializes a message written by {@link #serializeMessage(MimeMessage)}.
         *
         * @param s the <code>Session</code> to use for the read message
         * @param msg the raw message content
         *
         * @return the read <code>MimeMessage</code>
         */
        public static MimeMessage deserializeMessage(Session s,InputStream is)
                throws Exception
        {
            try
            {
                return new MimeMessage(s != null ? s : EmailUtil.createEmailSession(), is);
            }
            finally
            {
                 MiscUtils.closeStream(is);
            }
        }



    public static void readMessage(MimeMessage msg,MessageReaderCallback cb)
            throws Exception
    {
        Object content = msg.getContent();

        if(content instanceof Multipart)
            readMessageMultipart((Multipart)content,cb);
        else if(content instanceof Part)
            readMessagePart((Part)content,cb);
        else
            cb.onContent("text/plain",Part.INLINE,null,new ByteArrayInputStream(MiscUtils.toString(content).getBytes("UTF-8")));

    }


    private static boolean readMessageMultipart(Multipart content,MessageReaderCallback cb)
            throws Exception
    {
        for(int i = 0; i < content.getCount(); ++i)
        {
            if(!readMessagePart(content.getBodyPart(i),cb))
                return false;
        }

        return true;
    }

    private static boolean readMessagePart(Part part,MessageReaderCallback cb)
            throws Exception
    {

        String disposition = part.getDisposition();
        String contentType = part.getContentType();
        String attachmentName = null;

        ByteArrayOutputStream dout = new ByteArrayOutputStream(1024*8);

        // just plain body message
        if(disposition == null)
        {
//            part.writeTo(dout);
             ByteUtils.transferData(part.getInputStream(),dout);
        }
        else if(disposition.equalsIgnoreCase(Part.ATTACHMENT))
        {
            attachmentName = part.getFileName();
            ByteUtils.transferData(part.getInputStream(),dout);
        }
        else if(disposition.equalsIgnoreCase(Part.INLINE))
        {
            ByteUtils.transferData(part.getInputStream(),dout);
        }
        else if(StringUtils.caseInsensitiveStartsWith(disposition,"multipart/"))
        {
            return readMessageMultipart((Multipart)part.getContent(),cb);
        }
        else
        {
            throw new MessagingException("Unknown disposition: " + disposition + ";contentType=" + contentType);
        }

        return cb.onContent(contentType,disposition,attachmentName, new ByteArrayInputStream(dout.toByteArray()));

    }
}
