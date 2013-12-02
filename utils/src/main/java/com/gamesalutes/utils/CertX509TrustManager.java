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
/* Copyright 2008 University of Chicago
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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.X509TrustManager;

/**
 * X509 trust manager that will always trust the specified imported certs.  If the imported certs
 * are not specified, then it will accept all server certificates.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id: CertX509TrustManager.java 2363 2010-10-08 23:19:02Z jmontgomery $
 */
public final class CertX509TrustManager implements X509TrustManager 
{
	private final Set<X509Certificate> certs;
	private final X509TrustManager defaultManager;
	private volatile X509Certificate[] lastChain = null;


        /**
         * Constructor.
         *
         * Creates a trust manager that trusts all certificates.
         */
        public CertX509TrustManager()
        {
            this(null);
        }

	/**
	 * Constructor.
	 * 
	 * Specify the default X509TrustManager to use in case this object cannot perform the verification
	 * or <code>null</code> to not use a default and always pass verification in that case.
	 * 
	 * @param defaultManager the <code>X509TrustManager</code>
	 */
	public CertX509TrustManager(X509TrustManager defaultManager)
	{
		certs = null;
		this.defaultManager = defaultManager;
	}
	
	/**
	 * Constructor.
	 * 
	 * 
	 * Specify the default X509TrustManager to use in case this object cannot perform the verification
	 * or <code>null</code> to not use a default and always pass verification in that case.
	 * 
	 * @param defaultManager the <code>X509TrustManager</code>
	 * @param certs the trusted certificates to use
     * @throw NullPointerException if <code>certs</code> is <code>null</code>
	 */
	public CertX509TrustManager(X509TrustManager defaultManager,X509Certificate... certs)
	{
		if(certs == null) throw new NullPointerException("certs");
		
		this.defaultManager = defaultManager;
		this.certs = new HashSet<X509Certificate>();
		
		for(X509Certificate c : certs)
		{
			if(c != null) this.certs.add(c);
		}
	}
	/* (non-Javadoc)
	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
	 */
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException 
	{
		if(defaultManager != null)
			defaultManager.checkClientTrusted(chain, authType);
	}

	/* (non-Javadoc)
	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
	 */
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException
	{
		this.lastChain = chain;
		
		// try to use default manager
		if(chain == null)
		{
			if(defaultManager != null)
				defaultManager.checkServerTrusted(chain, authType);
		}
		// check to see if one of the certificates in the chain is equal to that in 
		// trusted certs
		else if(certs != null)
		{
			boolean passed = false;
			for(X509Certificate presented : chain)
			{
				if(this.certs.contains(presented))
				{
					passed = true;
					break;
				}
			}
			// validation failed
			if(!passed)
			{
				throw new CertificateException("Presented chain certificates do not match a trusted certificate\nChain=" + 
						Arrays.toString(chain));
			}
		}
		// else accept everything
	}

	/* (non-Javadoc)
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 */
	public X509Certificate[] getAcceptedIssuers()
	{
		if(certs != null)
			return certs.toArray(new X509Certificate[certs.size()]);
		if(defaultManager != null)
			return defaultManager.getAcceptedIssuers();
		// accept all in the chain
		return lastChain;
	}

}
