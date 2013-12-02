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

import java.security.Key;

import javax.crypto.Cipher;

/**
 * Default implementation for a <code>dataCypher</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: DefaultDataCypher.java 697 2008-02-20 18:29:39Z jmontgomery $
 */
public class DefaultDataCypher implements DataCypher 
{
	private final Cipher cipher;
	private final Key key;
	
	
	public static final String AES = "AES";
	public static final String RSA = "RSA";

	
	public DefaultDataCypher(String alg,Key key)
		throws Exception
	{
		if(alg == null)
			throw new NullPointerException("alg");
		if(key == null)
			throw new NullPointerException("key");
		cipher = Cipher.getInstance(alg);
		this.key = key;
	}
	public byte[] decrypt(byte[] inBytes)
		throws Exception
	{
		return crypt(Cipher.DECRYPT_MODE,inBytes);
	}

	public byte[] encrypt(byte[] inBytes)
		throws Exception
	{
		return crypt(Cipher.ENCRYPT_MODE,inBytes);
	}
	
	private byte [] crypt(int mode,byte [] in)
		throws Exception
	{
		  // init the cipher
		  cipher.init(mode, key);
		  return cipher.doFinal(in);
		  // unnecessary, since have input bytes can simply
		  // do encrypt/decrypt in single operation
		  // and return those bytes
		  /*
	      final int blockSize = cipher.getBlockSize();
	      final int outputSize = cipher.getOutputSize(blockSize);
	      final byte[] outBytes = new byte[outputSize];
	      final ByteArrayOutputStream out = 
	    	  new ByteArrayOutputStream(in.length * 2);
	      
	      // only read direct increments of block size so don't get buffer overflow
	      final int extra = in.length % blockSize;
	      for(int i = 0, len = in.length - extra;
	              i < len;i+=blockSize)
	      {
	           int outLength = cipher.update(in, i, blockSize, outBytes);
	           out.write(outBytes, 0, outLength);         
	      }
	      if (extra > 0)
	         out.write(cipher.doFinal(in, in.length - extra, extra));
	      else
	         out.write(cipher.doFinal());
	      return out.toByteArray();
	     */
	}

}
