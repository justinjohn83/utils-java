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
package com.gamesalutes.utils;

import com.gamesalutes.utils.EmailUtil.EmailAttachment;
import com.gamesalutes.utils.EmailUtil.MessageReaderCallback;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.mail.internet.MimeMessage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmontgomery
 */
public  class EmailUtilTest
{
    @Test
    public void testSerializeAndDeserialize() throws Exception
    {
        final String subject = "Test";
        final String body = "body text";
        final String attachmentName = "test.txt";
        final String attachment = "attachment text";

        InputStream in = new ByteArrayInputStream(attachment.getBytes("UTF-8"));

        EmailAttachment ea = new EmailAttachment(attachmentName,"text/plain",in);

        // create the message
        MimeMessage input = EmailUtil.createEmail(EmailUtil.createEmailSession(),
                "test@test.com",
                new String [] {"test2@test.2"}, null, null, subject,body,
                Arrays.asList(ea));

        // serialize
        String serialized =
                EmailUtil.serializeMessage(input);

        MimeMessage output = EmailUtil.deserializeMessage(serialized);


        EmailUtil.readMessage(output, new MessageReaderCallback()
        {

            public boolean onContent(String contentType, String disposition, String name, InputStream content) throws Exception
            {
                String data = FileUtils.readData(new BufferedReader(new InputStreamReader(content,"UTF-8")));
                if(name == null)
                    assertEquals("body",body,trim(data));
                else
                    assertEquals("attachment",attachment,trim(data));

                return true;
            }

        });

    }

    private static String trim(String s)
    {
        return StringUtils.trim(MiscUtils.removeLineBreaks(s));
    }
}
