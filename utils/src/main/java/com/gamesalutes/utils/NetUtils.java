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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.gamesalutes.utils.Cidr.LocalAddressType;

/**
 * Utilities for working with network entities in IPV4.
 * 
 * @author Justin Montgomery
 * @version $Id: NetUtils.java 1860 2010-01-20 23:53:31Z jmontgomery $
 */
public final class NetUtils
{
	private NetUtils() {}
	
	/**
	 * Sort the domains so that subset domains listed after domains.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: NetUtils.java 1860 2010-01-20 23:53:31Z jmontgomery $
	 */
	public static final class DomainComparator implements Comparator<String>
	{
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(String o1, String o2)
		{
			int rc = o1.compareToIgnoreCase(o2);
			
			// if equal then just return 0 now
			if(rc == 0) return 0;
			
			// list subsets after
			if(StringUtils.caseInsensitiveContains(o1,o2))
				return -1;
			if(StringUtils.caseInsensitiveContains(o2, o1))
				return 1;
			
			// default to regular compare to
			
			return rc;
		}
		
	}
	public static class NetException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		public NetException() { super(); }
		public NetException(String message, Throwable cause) { super(message, cause); }
		public NetException(String message) { super(message); }
		public NetException(Throwable cause) { super(cause); }
	}
	public static class AmbiguousHostException extends NetException
	{
		private static final long serialVersionUID = 1L;
		private String hostname;
		private List<String> domains;
		private List<? extends Collection<String>> ips;
		
		public AmbiguousHostException(String hostname,List<String> domains,List<? extends Collection<String>> ips)
		{
			this.hostname = hostname;
			this.domains = domains;
			this.ips = ips;
		}
		public String getHostName() { return hostname; }
		public List<String> getDomains() { return domains; }
		public List<? extends Collection<String>> getIps() { return ips; }
	}
	
	
	/**
	 * Converts a valid ip address into an integer.
	 * 
	 * @param s the ip address
	 * @return the address as an unsigned int
	 */
	public static int addressToInt(String s)
	{
		if(s == null)
			throw new NullPointerException("s");
		if(!NetUtils.isValidAddress(s))
			throw new IllegalArgumentException("s=" + s + " not in valid a.b.c.d form");
		
		int dot1 = s.indexOf(".");
		int dot2 = s.indexOf('.',dot1+1);
		int dot3 = s.indexOf('.',dot2+1);
		
		byte octet1 = (byte)Integer.parseInt(s.substring(0,dot1));
		byte octet2 = (byte)Integer.parseInt(s.substring(dot1+1,dot2));
		byte octet3 = (byte)Integer.parseInt(s.substring(dot2+1,dot3));
		byte octet4 = (byte)Integer.parseInt(s.substring(dot3+1));
		
		return ByteUtils.toInteger(octet1,octet2,octet3,octet4);
	}
	
	/**
	 * Converts an unsigned ip address to its dotted decimal string form.
	 * 
	 * @param address the address
	 * @return the dotted decimal string form or <code>address</code>
	 */
	public static String intToAddress(int address)
	{

		int octet1 = (address & 0xFF000000) >>> 24;
		int octet2 = (address & 0xFF0000) >>> 16;
		int octet3 = (address & 0xFF00) >>> 8;
		int octet4 = address & 0xFF;
		
		return new StringBuilder(15
		 ).append(octet1).append('.'
		 ).append(octet2).append('.'
	     ).append(octet3).append('.'
	     ).append(octet4
	     ).toString();
	}
	/**
	 * Returns whether <code>s</code> is a valid IPV4 address in 
	 * standard octet decimal form.
	 * 
	 * @param s the input string
	 * @return <code>true</code> if valid address and <code>false</code> otherwise
	 * @throws NullPointerException if <code>s</code> is <code>null</code>
	 */
	public static boolean isValidAddress(String s)
	{
		if(s == null) throw new NullPointerException("s");
		
		// find the dots
		int dot1,dot2,dot3;
		dot1 = s.indexOf('.');
		if(dot1 == -1) 
			return false;
		dot2 = s.indexOf('.',dot1+1);
		if(dot2 == -1) 
			return false;
		dot3 = s.indexOf('.',dot2+1);
		if(dot3 == -1) 
			return false;
		
		// find the values
		int [] sep = {-1,dot1,dot2,dot3,s.length()};
		
		for(int i = 0, end = sep.length - 1; i < end; ++i)
		{
			int sb = sep[i] + 1;
			int se = sep[i+1];
			
			// empty octet or too big
			if(sb >= se || se - sb > 3) 
				return false;
			
			String sub = s.substring(sb,se);
			
			int octet = 0;
			for(int j = 0, len = sub.length(); j < len; ++j)
			{
				char c = sub.charAt(j);
				if(c < '0' || c > '9')
					return false;
				int place = len - j;
				int mult;
				switch(place)
				{
				case 1: mult = 1; break;
				case 2: mult = 10; break;
				case 3: mult = 100; break;
				default: throw new AssertionError();
				}
				octet += mult*(c - '0');
			}
			// out of range
			if(octet > 255)
				return false;
		}
		
		return true;
		
	}
	
	
	/**
	 * Returns whether the specified IPV4 address is a local address on the network according
	 * to <code>localType</code>
     *
	 * @param address the input address
	 * @param localType <code>Set</code> of local types to use in evaluation
	 * @return <code>true</code> if network local and <code>false</code> otherwise
	 * @throw NullPointerException if <code>address</code> is <code>null</code>
	 * @throw IllegalArgumentException if <code>address</code> is not a valid IPV4 address in 
	 *        standard decimal octet form
	 * 
	 */
	public static boolean isNetworkLocal(String address,Set<LocalAddressType> localType)
	{
		if(!isValidAddress(address))
			throw new IllegalArgumentException("address=" + address);
		if(localType == null)
			throw new NullPointerException("localType");
		
		for(LocalAddressType t : localType)
		{
			if(t.containsAddress(address))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns whether the specified IPV4 address is a local address on the network.  These 
	 * include </br>
	 * <ul>127.0.0.1</ul>
	 * <ul>10.0.0.0/8</ul>
	 * <ul>172.16.0.0/12</ul>
	 * <ul>192.168.0.0/16</ul>
	 * 
	 * @param address the input address
	 * @return <code>true</code> if network local and <code>false</code> otherwise
	 * @throw NullPointerException if <code>address</code> is <code>null</code>
	 * @throw IllegalArgumentException if <code>address</code> is not a valid IPV4 address in 
	 *        standard decimal octet form
	 * 
	 */
	public static boolean isNetworkLocal(String address)
	{
		return isNetworkLocal(address,EnumSet.allOf(LocalAddressType.class));
	}
	
	/**
	 * Attempts to get an <code>InetAddress</code> object given the input
	 * <code>adddress</code>.  Only public ip addresses are queried.  All private
	 * addresses return <code>null</code>.
	 * 
	 * @param address the address to search
	 * @return the <code>InetAddress</code> associated with the <code>address</code> or
	 *         <code>null</code> if not found
	 */
	public static InetAddress getAddressByName(String address)
	{
		return getAddressByName(address,EnumSet.noneOf(LocalAddressType.class));
	}
	
	/**
	 * Attempts to get an <code>InetAddress</code> object given the input
	 * <code>adddress</code>.
	 * 
	 * @param address the address to search or <code>null</code> for localhost
	 * @param allowedLocalSearches <code>Set</code> of {@link LocalAddressType} allowed
	 *                               to being queried
	 * @return the <code>InetAddress</code> associated with the <code>address</code> or
	 *         <code>null</code> if not found
	 */
	public static InetAddress getAddressByName(String address,
			Set<LocalAddressType> allowedLocalSearches)
	{
		if(allowedLocalSearches == null)
			allowedLocalSearches = EnumSet.noneOf(LocalAddressType.class);
		
		boolean search = true;
		
		if(address == null && !allowedLocalSearches.contains(LocalAddressType.LOCALHOST))
			search = false;
		else if(address != null && isValidAddress(address))
		{
			for(LocalAddressType t : EnumSet.allOf(LocalAddressType.class))
			{
				if(t.containsAddress(address) && !allowedLocalSearches.contains(t))
				{
					search = false;
					break;
				}
			}
				
		}
		if(search)
		{
			try
			{
				return InetAddress.getByName(address);
			}
			catch(UnknownHostException e) { return null; }
		}
		else
			return null;
	}
	
	
	/**
	 * Converts the hexadecimal mac to 
	 * xx:xx:xx:xx:xx:xx format where all hexadecimal letters are capitalized.
	 * 
	 * @param mac the input mac
	 * @return canonicalized mac
	 */
	public static String canonicalizeMac(String mac)
	{
		if(mac == null) throw new NullPointerException("mac");
		
		if(!isValidMacAddress(mac))
			throw new IllegalArgumentException("mac=" + mac);
		// clear all but hexidecimal digits
		//String raw = mac.replaceAll("[^0-9A-Fa-f]","").toUpperCase();
		StringBuilder buf = new StringBuilder(18);
		int count = 0;
		
		final char offset = 'a' - 'A';
		for(int i = 0, len = mac.length(); i < len; ++i)
		{
			char c = mac.charAt(i);
			boolean pass = false;
			// only add hexidecimal digits
			if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F'))
			{
				buf.append(c);
				pass = true;
				++count;
			}
			else if(c >= 'a' && c <= 'f')
			{
				// convert to uppercase
				buf.append((char)(c - offset));
				pass = true;
				++count;
			}
			// every two digits include a ":" except for last
			if(pass && count % 2 == 0 && i < len - 1)
				buf.append(':');
		}
		// check output
		if(buf.length() != 17)
			throw new IllegalArgumentException("Could not resolve mac=" + mac);
		
		return buf.toString();
	}
	
	/**
	 * Tests whether input string is a valid mac address.  Test will pass if 
	 * the input string contains exactly 12 hexadecimal digits.
	 * 
	 * @param s the input string
	 * @return <code>true</code> if valid mac and <code>false</code> otherwise
	 * @throw NullPointerException if <code>s</code> is <code>null</code>
	 */
	public static boolean isValidMacAddress(String s)
	{
		if(s == null) throw new NullPointerException("s");
		
		int count = 0;
		for(int i = 0, len = s.length(); i < len; ++i)
		{
			char c = s.charAt(i);
			
			if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') ||
			   (c >= 'a' && c <= 'f'))
			   {
				   ++count;
			   }
		}
		// mac addresses have 12 hexadecimal digits
		return count == 12;
	}
	
	/**
	 * Returns whether input <code>address</code> is an external mac address.  
	 * 
	 * @param address the input mac address
	 * @return <code>true</code> if external address and <code>false</code> otherwise
	 * @throws NullPointerException if <code>address</code> is <code>null</code>
	 * @throws IllegalArgumentException if <code>address</code> is not a valid mac address as 
	 *         per {@link #isValidMacAddress(String)}
	 */
	public static boolean isExternalMacAddress(String address)
	{
		if(address == null) throw new NullPointerException("address");
		if(!isValidMacAddress(address)) throw new IllegalArgumentException("address=" + address);
		address = canonicalizeMac(address);
		return !address.equals("00:00:00:00:00:00");
	}
	
	/**
	 * Url-encodes the path part of an url according to <code>rfc1738</code>. If an url form-encoding
	 * is required, use <code>java.net.URLEncoder</code>.  <b><i>Note: <code>path</code> should 
	 * not include other parts of the url such as the scheme,userInfo,host,query, or fragment
	 * components as the resulting behavior is undefined</i></b>.
	 * 
	 * @param path the path string
	 * @return the url-encoded path string
	 */
	public static String urlEncodePath(String path)
	{
		return WebUtils.urlEncode(path);
	}
	
	
	public static boolean isDNSHost(String fullHostName)
	{
		return getIpAddresses(fullHostName) != null;
	}
	public static boolean isDNSHost(String name,Collection<String> domains,boolean checkAll)
	{
		try
		{
			return getIpAddresses(name,domains,checkAll) != null;
		}
		catch(AmbiguousHostException e)
		{
			return false;
		}
	}
	
	/**
	 * Returns the ip addresses of the given full host name or <code>null</code> if <code>fullHostName</code>
	 * cannot be resolved.
	 * 
	 * @param fullHostName the full host name
	 * @return the ip addresses or <code>null</code>
	 */
	public static Collection<String> getIpAddresses(String fullHostName)
	{
		if(MiscUtils.isEmpty(fullHostName))
			return null;
		// no info after dot, no dice!
		int index = fullHostName.indexOf('.');
		if(index <= 0 || index == fullHostName.length() - 1)
			return null;
		
		try
		{
			InetAddress[] ias = InetAddress.getAllByName(fullHostName);
			Set<String> addresses = new LinkedHashSet<String>();
			
			for(InetAddress ia : ias)
				addresses.add(ia.getHostAddress());
			
			return addresses;
		}
		catch(UnknownHostException e)
		{
			return null;
		}
	}
	
	/**
	 * Returns the ip addresses of the given host name using one of the search domains provided
	 * or <code>null</code> if <code>partialHostName</code>
	 * cannot be resolved. If <code>checkAll</code> is <code>true</code>, then all domains are tested and if
	 * different ip addresses are resolved between two or more of these domains, then an <code>AmbiguousHostException</code>
	 * is thrown.
	 * 
	 * @param partialHostName the partial host name
	 * @param checkAll <code>true</code> to check all search domains and <code>false</code> to stop at first match
	 * @return the ip addresses or <code>null</code>
	 */
	public static Map<String,Collection<String>> resolveHost(String partialHostName,Collection<String> searchDomains,
			boolean checkAll)
		throws AmbiguousHostException
	{
		if(partialHostName == null)
			throw new NullPointerException("partialHostName");
		if(searchDomains == null)
			throw new NullPointerException("searchDomains");
		
		if(MiscUtils.isEmpty(partialHostName) || MiscUtils.isEmpty(searchDomains))
			return null;
		
		Map<String,Collection<String>> domainIpMap = null;
		
		Set<Set<String>> matchedIpSets = null;
		for(String domain : searchDomains)
		{
			String hostname;
			if(!partialHostName.endsWith(domain))
				hostname = partialHostName + (!partialHostName.endsWith(".") && !domain.startsWith(".") ? "." : "") + domain;
			else
				hostname = partialHostName;
			
			Collection<String> ips = getIpAddresses(hostname);
			
			// must try all domains to ensure that two domains don't map to different ips meaning that there is an ambiguity
			if(ips != null)
			{
				if(domainIpMap == null)
					domainIpMap = new LinkedHashMap<String,Collection<String>>();
				domainIpMap.put(hostname, ips);
				if(!checkAll)
					return domainIpMap;
				
				if(matchedIpSets == null)
					matchedIpSets = new LinkedHashSet<Set<String>>();
				matchedIpSets.add(new LinkedHashSet<String>(ips));
				// ambiguity detected
				if(matchedIpSets.size() > 1)
				{
					throw new AmbiguousHostException(partialHostName,new ArrayList<String>(domainIpMap.keySet()),
							new ArrayList<Set<String>>(matchedIpSets));
				}
			}
		}
		return domainIpMap;
	}
	/**
	 * Returns the ip addresses of the given host name using one of the search domains provided
	 * or <code>null</code> if <code>partialHostName</code>
	 * cannot be resolved. If <code>checkAll</code> is <code>true</code>, then all domains are tested and if
	 * different ip addresses are resolved between two or more of these domains, then an <code>AmbiguousHostException</code>
	 * is thrown.
	 * 
	 * @param partialHostName the partial host name
	 * @param checkAll <code>true</code> to check all search domains and <code>false</code> to stop at first match
	 * @return the ip addresses or <code>null</code>
	 */
	public static Collection<String> getIpAddresses(String partialHostName,Collection<String> searchDomains,
			boolean checkAll)
		throws AmbiguousHostException
	{
		Map<String,Collection<String>> m = resolveHost(partialHostName,searchDomains,checkAll);
		if(m == null) return null;
		
		Set<String> ips = new LinkedHashSet<String>();
		
		for(Collection<String> values : m.values())
			ips.addAll(values);
		
		return ips;
	}
	
	/**
	 * Returns the dns names for the given ip address or <code>null</code> if it could not be 
	 * resolved.  If more than one name is found (i.e. cname), the first one is the FQDN.
	 * 
	 * @param ipAddress the ip address
	 * @return the host names or <code>null</code>
	 */
	public static List<String> getDNSNames(String ipAddress)
	{
		if(ipAddress == null) 
			throw new NullPointerException("ipAddress");
		if(!isValidAddress(ipAddress)) 
			throw new IllegalArgumentException("ipAddress=" + ipAddress);
		
		// convert ipAddress into byte representation
		byte [] byteAddr = ByteUtils.getIntegerBytes(addressToInt(ipAddress));
		try
		{
			InetAddress addr = InetAddress.getByAddress(byteAddr);
			// FIXME: getHostName or getCanonicalHostName() ?
			List<String> names = new ArrayList<String>();
			
			String name = addr.getCanonicalHostName();
			if(name != null)
				names.add(name);
			
			name = addr.getHostName();
			// get hostname could return an ip address
			if(name != null && !names.contains(name) /*&& !isValidAddress(name)*/)
				names.add(name);

			return names;
			
		}
		catch(UnknownHostException e)
		{
			return null;
		}
		
				
	}
	

	/**
	 * Normalizes the input <code>host</code> so that the domain is stripped off it.
	 * 
	 * @param host the input host name
	 * @param domains collection of the domains to strip off <code>host</code>
	 * @return the normalized host name
	 */
	public static String normalizeHostName(String host,Collection<String> domains)
	{
		if(host == null) throw new NullPointerException("host");
		if(MiscUtils.isEmpty(domains)) return host;
		
		Set<String> domainSet = new TreeSet<String>(new DomainComparator());
		
		// canonicalize the domains and store in correct order
		for(String domain : domains)
		{
			if(!MiscUtils.isEmpty(domain))
			{
				domain = domain.trim();
				// make sure we start with "."
				if(domain.charAt(0) != '.')
					domain = '.' + domain;
				
				domainSet.add(domain);
			}
		}
		
		// process the host
		for(String domain : domainSet)
		{
			int index = host.lastIndexOf(domain);
			if(index != -1)
				return host.substring(0,index);
		}
		return host;
		
	}
}
