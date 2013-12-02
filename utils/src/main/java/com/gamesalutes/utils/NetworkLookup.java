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

import java.util.Collection;

/**
 * Interface for looking up ip addresses and names of hosts.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public interface NetworkLookup
{
	/**
	 * Gets all the ip addresses associated with the given partial host name.
	 * 
	 * @param name the partial host name
	 * @return all the ip addresses or <code>null</code> if they could not be determined
	 */
	Collection<String> getIpAddresses(String name)
		throws NetUtils.AmbiguousHostException;
	
	/**
	 * Returns the dns names for the given ip address or <code>null</code> if it could not be 
	 * resolved.  If more than one name is found (i.e. cname), the first one is the FQDN.
	 * 
	 * @param ipAddress the ip address
	 * @return the host names or <code>null</code>
	 */
	Collection<String> getHostNames(String ipAddress);
	
	
	/**
	 * Returns a canonical identifier for the given host.  This does not necessarily require
	 * an actual network-based
	 * query, but instead could rely on application-specific knowledge. 
	 * 
	 * @param host the host
	 * @return a canonical identifier for <code>host</code>
	 */
	String normalizeName(String host);
	
}
