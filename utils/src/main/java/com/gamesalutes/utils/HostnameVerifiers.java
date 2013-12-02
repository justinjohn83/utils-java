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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Contains static factory methods for creating different kinds of <code>HostnameVerifier</code>
 * instances to use with <code>SSL</code> when the url's hostname and the hostname on the 
 * presented server certificate mismatch during the ssl handshaking procedure.
 * 
 * @author Justin Montgomery
 * @version $Id: HostnameVerifiers.java 1212 2008-12-02 23:47:28Z jmontgomery $
 */
public final class HostnameVerifiers
{
	private HostnameVerifiers() {}
	
	/**
	 * Creates a <code>HostnameVerifier</code> that accepts all hostnames.
	 * @return the <code>HostnameVerifier</code>
	 */
	public static HostnameVerifier getNullVerifier()
	{
		return new HostnameVerifier()
		{

			public boolean verify(String hostname, SSLSession session)
			{
				return true;
			}
			
		};
	}
	
}
