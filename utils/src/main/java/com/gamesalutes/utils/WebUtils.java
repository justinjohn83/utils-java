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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class WebUtils
{
	private WebUtils() {}
	
	private static final String ENC = "UTF-8";
	
	private static final boolean [] toEscape = new boolean[256];
	private static final boolean [] reserved = new boolean[256];
	
	static
	{
		// http://www.blooberry.com/indexdot/html/topics/urlencoding.htm
		// http://www.faqs.org/rfcs/rfc1738.html
				
		// iso control
		for(char c = 0; c <= 0x1F; ++c)
			toEscape[c] = true;
		toEscape[0x7F] = true;
		
		// non-ascii
		for(char c = 0x80; c <= 0xFF; ++c)
			toEscape[c] = true;
		
		// reserved

		//res.add((char)0x24); // $
		reserved[0x26] = true; // &
		//res.add((char)0x2B); // +
		//res.add((char)0x2C); // ,
		reserved[0x2F] = true; // / 
		reserved[0x3A] = true; // :
		reserved[0x3B] = true; // ;
		reserved[0x3D] = true; // =
		reserved[0x3F] = true; // ?
		reserved[0x40] = true; // @
		
		// unsafe
		toEscape[0x20] = true;
		toEscape[0x22] = true;
		toEscape[0x3C] = true;
		toEscape[0x3E] = true;
		toEscape[0x23] = true;
		toEscape[0x25] = true;
		toEscape[0x7B] = true;
		toEscape[0x7D] = true;
		toEscape[0x7C] = true;
		toEscape[0x5C] = true;
		toEscape[0x5E] = true;
		toEscape[0x7E] = true;
		toEscape[0x5B] = true;
		toEscape[0x5D] = true;
		toEscape[0x60] = true;
		
	}
	public static Map<String,String> getQueryParameters(URI uri)
	{
		if(uri == null)
			throw new NullPointerException("uri");
		return urlDecode(uri.getRawQuery(),false);
	}
	
	/**
	 * Decodes the parameters of a query string using either url encoding (rfc1738) or form encoding and returns
	 * them as a <code>Map</code>.
	 * 
	 * @param query the parameters to encode
	 * @param formDecode <code>true</code> to form-encode and <code>false</code> to url encode
	 * @return the decoded parameters
	 */
	public static Map<String,String> urlDecode(String query,boolean formDecode)
	{
		if(query == null) return null;
		
		// split along the "&"
		String [] entries = query.split("&");
		Map<String,String> params = new LinkedHashMap<String,String>();
		
		for(String entry : entries)
		{
			// split along the "="
			String [] keyValue = entry.split("=");
			if(keyValue.length != 2)
				throw new IllegalArgumentException("query=" + query + "; invalid entry=" + entry);
			
			params.put(decode(keyValue[0],formDecode), decode(keyValue[1],formDecode));
		}
		
		return params;
	}
	
	/**
	 * Adds the query parameters to the uri <code>path</code>.
	 * 
	 * @param path the uri
	 * @param parameters the query parameters to set for the uri
	 * @return <code>path</code> with the query parameters added
	 */
	public static URI setQueryParameters(URI path,Map<String,String> parameters)
	{
		try
		{
			return new URI(path.getScheme(),
					       path.getRawUserInfo(),
					       path.getHost(),
					       path.getPort(),
					       path.getRawPath(),
					       urlEncode(parameters,false),
					       path.getRawFragment());
		}
		catch(URISyntaxException e)
		{
			// shouldn't happen
			throw new AssertionError(e);
		}
				       
	}
	
	/**
	 * Encodes the given map of parameters using either url encoding (rfc1738) or form encoding.
	 * 
	 * @param parameters the parameters to encode
	 * @param formEncode <code>true</code> to form-encode and <code>false</code> to url encode
	 * @return the encoded string form of the parameters
	 */
	public static String urlEncode(Map<String,String> parameters,boolean formEncode)
	{
		if(MiscUtils.isEmpty(parameters)) 
			return null;
		
		StringBuilder queryStr = new StringBuilder(2048);
		
		for(Iterator<Map.Entry<String, String>> it = parameters.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry<String, String> E = it.next();
			
			String key = E.getKey();
			String value = E.getValue();

			// url decode the key
			
			if(key == null)
				throw new IllegalArgumentException("null key in parameters: " + parameters);
			
			key = encode(key,formEncode);
			value = encode(value,formEncode);
			
			queryStr.append(key).append("=").append(value);
			
			if(it.hasNext())
				queryStr.append("&");
		} // for
		
		return queryStr.toString();
	}
	
	private static String encode(String s,boolean formEncode)
	{
		if(s == null) return null;
		
		if(formEncode)
			return formEncode(s);
		return urlEncode(s,true);
	}
	private static String decode(String s,boolean formDecode)
	{
		if(s == null) return null;
		if(formDecode)
			return formDecode(s);
		return urlDecode(s);
	}
	
	/**
	 * Encodes <code>s</code> using the 
	 * MIME <code>application/x-www-form-urlencoded</code> type.
	 * 
	 * @param s the string
	 * @return the encoded string
	 */
	public static String formEncode(String s)
	{
		if(s == null) return null;
		try
		{
			return URLEncoder.encode(s,ENC);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new AssertionError(e);
		}
	}
	
	/**
	 * Decodes <code>s</code> that has been form-encoded using the 
	 * MIME <code>application/x-www-form-urlencoded</code> type.
	 * 
	 * @param s the form-encoded string
	 * @return the decoded string
	 */
	public static String formDecode(String s)
	{
		if(s == null) return null;
		
		try
		{
			return URLDecoder.decode(s,ENC);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new AssertionError(e);
		}
	}
	
	
	private static boolean encodeReserved(CharSequence s,int index,boolean isLast)
	{
		// FIXME: this does not work properly for &
		int len = s.length();
		// reserved chars
		// & encode within a given query parameter 
		// / don't encode unless in query
		// : after the first / following the hostname
		// ; always?
		// = always outside a query name-value separation
		// ? after the first appearance in a path - denoting the start of the query string
		// @ always?
		if(index < 0 || index >= len) return false;
		
		char c = s.charAt(index);
		switch(c)
		{
			case '&':
			{
				if(isLast) return false;
				
				int qi = StringUtils.lastIndexOf(s,'?',index - 1);
				if(qi == -1) 
					return false;
				// find prev one
				int start = StringUtils.lastIndexOf(s, c,index - 1);
				if(start == -1)
					start = qi + 1;
				if(start >= index - 1)
					return true;
				// check to see if we name=value
				return StringUtils.indexOf(false, s, start,'=', index - start) == -1;
			}
			case '?':
			{
				return StringUtils.lastIndexOf(s,c,index - 1) != -1;
			}
			case '/':
			{
				return StringUtils.lastIndexOf(s, '?',index - 1) != -1;
			}
			case ':':
			{
				return StringUtils.indexOf(s, "//",index - 1) != -1 &&
				   StringUtils.indexOf(s,c,index - 1) != -1;
			}
			case ';': case '@':
			{
				return true;
			}
			case '=':
			{
				if(isLast) return false;
				
				int qi = StringUtils.lastIndexOf(s,'?',index - 1);
				if(qi == -1) return false;
				// find current token
				int start = StringUtils.lastIndexOf(s, '&',index - 1);
				if(start == -1)
					start = qi + 1;
				if(start >= index - 1)
					return true;
				
				// if previous character was first a & and not a = then we are good
				
				// check to see if we name=value
				return StringUtils.indexOf(false, s, start,'=', index - start) != -1;
			}
			default: return false;
		}
	}
	
//	private void fixQueryEncoding(StringBuilder s)
//	{
//		int queryIndex = StringUtils.indexOf(s, '?');
//		if(queryIndex != -1)
//		{
//			// do &
//			for(int i = 0; i < s.length() - 1; ++i)
//			{
//				char c = s.charAt(i);
//				if(c == '&')
//				{
//					// find next =
//					int ei = StringUtils.indexOf(s, '=',i + 1);
//					if(ei == -1)
//					{
//						
//					}
//					
//				}
//			}
//			// do =
//			for(int i = 0; i < s.length(); ++i)
//			{
//				char c = s.charAt(i);
//				if(c == '=')
//				{
//					
//				}
//			}
//		}
//	}


        /*
         * Convenience method for making a URI from a possibly unencoded path string.
         *
         * @param s the raw form of a relative or absolute hierarchial uri.
         *
         * @returns the URI object
         * @throws IllegalArgumentException if the URI cannot be formed
         */
        public static URI createUri(String s)
        {
            if(s == null)
                throw new NullPointerException("s");

            try
            {
                return new URI(WebUtils.urlDecode(s));
            }
            catch(Exception e)
            {
                try
                {
                    return new URI(WebUtils.urlEncode(s));
                }
                catch(Exception e2)
                {
                   try
                   {
                       return new URI(s);
                   }
                   catch(Exception e3)
                   {
                       throw new IllegalArgumentException("s=" + s);
                   }
                }
            }
        }

        /*
         * Convenience method for making a URI from a possibly unencoded path string.
         *
         * @param s the raw form of a relative or absolute hierarchial uri.
         *
         * @returns the URI object
         * @throws IllegalArgumentException if the URI cannot be formed
         */
        public static URL createUrl(String s)
        {
            try
            {
                return createUri(s).toURL();
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("s=" + s,e);
            }
        }
	/**
	 * Url-encodes an http url according to <code>rfc1738</code>. If an url form-encoding
	 * is required, use <code>formEncode</code>. 
	 * 
	 * @param s the path string
	 * @return the url-encoded path string
	 */
	public static String urlEncode(String s)
	{
		return urlEncode(s,false);
	}
	/**
	 * Url-encodes an http url according to <code>rfc1738</code>. If an url form-encoding
	 * is required, use <code>formEncode</code>. 
	 * 
	 * @param s the path string
	 * @return the url-encoded path string
	 */
	private static String urlEncode(String s,boolean alwaysEncodeReserved)
	{
		if(s == null) return null;
		
		StringBuilder enc = new StringBuilder(s.length()*2);
		
		for(int i = 0, slen = s.length(); i < slen; ++i)
		{
			char c = s.charAt(i);
			// only UTF-8 characters are allowed
			if(c > 0xFF)
			{
				throw new IllegalArgumentException("" +
						"s=[" + s + "]; illegal char: " + c + " at index=" + i);
			}
			// first start by adding regular character
			enc.append(c);
			int len = enc.length();
			
			if(toEscape[c] || 
					((alwaysEncodeReserved && reserved[c]) || 
							encodeReserved(enc,len-1,i == slen - 1)))
			{
				enc.setCharAt(len - 1,'%');
				
				// to hex
				int c1 = c / 16;
				int c2 = c % 16;
				
				if(c1 < 10)
					enc.append(c1);
				else
					enc.append((char)('A' + c1 - 10));
				
				if(c2 < 10)
					enc.append(c2);
				else
					enc.append((char)('A' + c2 - 10));
			}
		}
		
		// must make second pass to fix the % and = 
		
		
		return enc.toString();
	}
	
	/**
	 * Url-decodes the path part of a http url according to <code>rfc1738</code>. If an url form-decoding
	 * is required, use <code>formDecode</code>.
	 * 
	 * @param s the path string
	 * @return the url-decoded path string
	 */
	public static String urlDecode(String s)
	{
		if(s == null) return null;
		
		StringBuilder dec = new StringBuilder(s.length()*2);
		
		for(int i = 0; i < s.length(); ++i)
		{
			char c = s.charAt(i);
			if(c != '%')
				dec.append(c);
			else // decode
			{
				if(i + 2 >= s.length())
					throw new IllegalArgumentException("s=" + s);
				
				int c1 = s.charAt(++i);
				int c2 = s.charAt(++i);
				// convert to hex int
				if(c1 >= 'A') c1 = c1 - 'A' + 10;
				else c1 -= '0';
				
				if(c2 >= 'A') c2 = c2 - 'A' + 10;
				else c2 -= '0';
				
				// convert to dec
				char d = (char)(c1 * 16 + c2);
				
				dec.append(d);
				
			}
		} // for
		
		return dec.toString();
	}

        /**
         * Creates a Base64 encoded version of <code>s</code>.
         *
         * @param s the string to encode
         * @return the Base64 encoded string
         */
        public static String base64Encode(String s)
        {
            if(s == null)
               return null;
            try
            {
                return new String(Base64.encodeBase64(s.getBytes("UTF-8")),"UTF-8");
            }
            catch(UnsupportedEncodingException e)
            {
                throw new AssertionError(e);
            }
        }

       /**
         * Creates a Base64 decoded version of <code>s</code>.
         *
         * @param s the string to decode
         * @return the Base64 decoded string
         */
        public static String base64Decode(String s)
        {
            if(s == null)
                return null;
            try
            {
                return new String(Base64.decodeBase64(s.getBytes("UTF-8")),"UTF-8");
            }
            catch(UnsupportedEncodingException e)
            {
                throw new AssertionError(e);
            }
        }
        
        
        // only do &nbsp;&amp;&lt;&gt;
        private static final String[][] entities = {{"&nbsp;"," "},{"&lt;","<"},{"&gt;",">"},{"&amp;","&"}};
        
        /**
         * Decodes each html entity present in <code>buf</code> and replaces it inline in <code>buf</code>.
         * 
         * @param buf the text
         */
        public static void decodeHtmlEntities(StringBuilder buf) {
        	if(buf == null) {
        		throw new NullPointerException("buf");
        	}
        	
        	if(!MiscUtils.isEmpty(buf)) {
        	
	        	for(String [] entity : entities) {
	        		StringUtils.replace(buf,entity[0],entity[1]);
	        	}
        	}
        }
       
        /**
         * Decodes each html entity present in <code>s</code> and replaces it and returns decoded result.
         * 
         * @param buf the text
         */
        public static String decodeHtmlEntities(String s) {
        	if(s == null) {
        		throw new NullPointerException("s");
        	}
        	if(MiscUtils.isEmpty(s)) {
        		return s;
        	}
        	
        	for(String [] entity : entities) {
        		s = s.replace(entity[0],entity[1]);
        	}
        	
        	return s;
        }
	 
}
