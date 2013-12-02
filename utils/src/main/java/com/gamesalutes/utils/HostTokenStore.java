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
/* Copyright 2008 - 2010 University of Chicago
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;


/**
 * Storage class for host,ip,mac tokens.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id: HostTokenStore.java 2507 2010-12-10 17:14:44Z jmontgomery $
 */
public final class HostTokenStore<T> implements Iterable<HostTokenStore.HostToken<T>>,Cloneable
{
	
	public interface TokenIterator<T> extends Iterator<HostToken<T>>
	{
		void setMac(String mac);
		void setIp(String ip);
		void setName(String name);
		boolean add(HostToken<T> t);
	}
	
	public interface HostTokenCallback<T>		
	{
		/**
		 * Converts a <code>String</code> to <code>T</code>.
		 * 
		 * @param s the string
		 * @return the converted value
		 */
		T valueOf(String s);
		
		/**
		 * Returns the string form of <code>data</code> as required by
		 * <code>valueOf</code>.
		 * 
		 * @param data the data
		 * @return the string conversion of data
		 */
		String toString(T data);
		
		/**
		 * Attempts to merge two data segments together to form a combo data segment.
		 * If a merge is not possible, then <code>null</code> should be returned.
		 * 
		 * @param first
		 * @param second
		 * @return the merge result or <code>null</code>
		 */
		T merge(T first,T second);
	}

        public interface TokenNormalizer
        {
            String normalizeName(String name);
            String normalizeIp(String ip);
            String normalizeMac(String mac);
        }
        
	private HostTokenCallback<T> callback;
 	
	public void setHostTokenCallback(HostTokenCallback<T> cb)
	{
		this.callback = cb;
	}
	
	public HostTokenCallback<T> getHostTokenCallback()
	{
		return this.callback;
	}
	
	
	/**
	 * Key entry of the data sorted.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: HostTokenStore.java 2507 2010-12-10 17:14:44Z jmontgomery $
	 */
	private enum KeyType
	{
		MAC
		{
			public String toString()
			{
				return "Mac";
			}
		},
		IP
		{
			public String toString()
			{
				return "Ip";
			}
		},
		HOST
		{
			public String toString()
			{
				return "Name";
			}
		};
	}
	
	/**
	 * Token consisting of a host name,ip, and mac.
	 * 
	 */
	public static final class HostToken<T> implements Cloneable
	{
		public String mac;
		public String ip;
		public String name;
		public T data;		
		
		public T getData()
		{
			return data;
		}
		
		public HostToken() 
		{ 
		}
		
		private static String makeName(String name)
		{
			return !MiscUtils.isEmpty(name) ? name : null;
		}
		private static String makeIp(String ip)
		{
			return !MiscUtils.isEmpty(ip) ? ip : null;
		}
		private static String makeMac(String mac)
		{
			return !MiscUtils.isEmpty(mac) ? mac : null;
		}
		
		public HostToken(String name,String ip,String mac)
		{
			this(name,ip,mac,null);
		}
		public HostToken(String name,String ip, String mac,T data)
		{
			this.name = makeName(name);
			this.ip = makeIp(ip);
			this.mac = makeMac(mac);
			this.data = data;
		}
		
		@Override
		public HostToken<T> clone()
		{
			try
			{
				return (HostToken<T>)super.clone();
			}
			catch(CloneNotSupportedException e)
			{
				throw new AssertionError(e);
			}
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(!(o instanceof HostToken))
				return false;
			HostToken<T> that = (HostToken<T>)o;
			return MiscUtils.safeEquals(this.mac, that.mac) &&
			       MiscUtils.safeEquals(this.ip, that.ip) &&
			       MiscUtils.safeEquals(this.name, that.name);
		}
		
		/**
		 * Returns whether this token contains all the same non-null information as <code>t</code>.
		 * 
		 * @param t another <code>HostToken<T></code>
		 * @return <code>true</code> if contains and <code>false</code> otherwise
		 */
		public boolean contains(HostToken<T> t)
		{
			return (MiscUtils.safeEquals(this.mac, t.mac) || MiscUtils.isEmpty(t.mac)) &&
				   (MiscUtils.safeEquals(this.ip, t.ip) || MiscUtils.isEmpty(t.ip)) &&
				   (MiscUtils.safeEquals(this.name, t.name) || MiscUtils.isEmpty(t.name));
		}
		
		@Override
		public int hashCode()
		{
			int result = 17;
			final int mult = 31;
			
			result = mult * result + MiscUtils.safeHashCode(this.mac);
			result = mult * result + MiscUtils.safeHashCode(this.ip);
			result = mult * result + MiscUtils.safeHashCode(this.name);
			
			return result;
		}
		@Override
		public String toString()
		{
			return new StringBuilder(128).append("name=").append(name
					).append(";ip=").append(ip
					).append(";mac=").append(mac
					).append(";data=").append(data
					).toString();
		}
		
		public boolean isEmpty()
		{
			return name == null &&
			       ip   == null &&
			       mac  == null;
		}

	}
	
	// TODO: remove "exists" parameter and put in a base class in HostImporter
	public static final class HostData<T> implements Cloneable
	{
		private final String name;
		private final boolean exists;
		private Collection<String> ipAddresses;
		private Collection<String> macAddresses;
		private T data;
		
		
		public HostData(String name,boolean exists)
		{
			this(name,exists,null);
		}
		public HostData(String name,boolean exists,T data)
		{
			if(name == null)
				throw new NullPointerException("name");
			this.name = name;
			this.exists = exists;
			this.ipAddresses = new LinkedHashSet<String>();
			this.macAddresses = new LinkedHashSet<String>();
			this.data = data;
		}
		
		public T getData() { return data; }
		
		public HostData(String name,Collection<String> ipAddresses,Collection<String> macAddresses,boolean exists)
		{
			this(name,ipAddresses,macAddresses,exists,null);
		}
		public HostData(String name,Collection<String> ipAddresses,Collection<String> macAddresses,boolean exists,T data)
		{
			this(name,exists,data);
			
			if(ipAddresses != null)
				this.ipAddresses.addAll(ipAddresses);
			if(macAddresses != null)
				this.macAddresses.addAll(macAddresses);
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(!(o instanceof HostData)) return false;
			return name.equals(((HostData)o).getHostName());
		}
		
		@Override
		public int hashCode()
		{
			int result = 17;
			result = 31 * result + name.hashCode();
			
			return result;
		}
		
		@Override
		public HostData<T> clone()
		{
			HostData<T> copy;
			try
			{
				copy = (HostData<T>)super.clone();
			}
			catch(CloneNotSupportedException e)
			{
				throw new AssertionError(e);
				
			}
			
			copy.ipAddresses = MiscUtils.deepCollectionCopy(copy.ipAddresses);
			copy.macAddresses = MiscUtils.deepCollectionCopy(copy.macAddresses);
			
			return copy;
			
		}
		public String getHostName() { return name; }
		
		public boolean exists() { return exists; }
		
		/**
		 * Returns a modifiable collection of ips.
		 * 
		 * @return the ips
		 */
		public Collection<String> getIpAddresses() { return ipAddresses; }
		
		/**
		 * Returns a modifiable collection of macs
		 * @return the macs
		 */
		public Collection<String> getMacAddresses() { return macAddresses; }
		
		public String toString()
		{
			return name;
		}
		
		public List<HostToken<T>> toHostTokens()
		{
			List<HostToken<T>> tokens = new ArrayList<HostToken<T>>();
			
			Iterator<String> ipIt = ipAddresses.iterator();
			Iterator<String> macIt = macAddresses.iterator();
			
			do
			{
				String ip = ipIt.hasNext() ? ipIt.next() : null;
				String mac = macIt.hasNext() ? macIt.next() : null;
				
				tokens.add(new HostToken<T>(name,ip,mac,data));
				
			}
			while(ipIt.hasNext() || macIt.hasNext());
			
			return tokens;
		}
	}
	
	
	/**
	 * Orders host tokens so that host tokens that are subsets of each other are ordered together.
	 * The token with most info is ordered first.
	 * 
	 * 
	 * @author Justin Montgomery
	 * @version $Id: HostTokenStore.java 2507 2010-12-10 17:14:44Z jmontgomery $
	 */
	public static class HostTokenSubsetComparator<T> implements Comparator<HostToken<T>>
	{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(HostToken<T> o1, HostToken<T> o2)
		{
			if(o1.equals(o2)) return 0;
			if(o1.contains(o2)) return -1;
			return 1;
			
		}
		
	}
	private Map<String,Set<HostToken<T>>> byMac,byIp,byName;
	private Set<HostToken<T>> allTokens;
	public HostTokenStore()
	{
		byMac = new HashMap<String,Set<HostToken<T>>>();
		byIp = new HashMap<String,Set<HostToken<T>>>();
		byName = new HashMap<String,Set<HostToken<T>>>();
		allTokens = new LinkedHashSet<HostToken<T>>();
		
	}
	
	public HostTokenStore<T> clone()
	{
		try
		{
			HostTokenStore<T> copy = (HostTokenStore<T>)super.clone();
			copy.allTokens = MiscUtils.deepCollectionCopy(copy.allTokens);
			copy.byMac = MiscUtils.deepMapCopy(copy.byMac);
			copy.byIp = MiscUtils.deepMapCopy(copy.byIp);
			copy.byName = MiscUtils.deepMapCopy(copy.byName);
			
			return copy;
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError(e);
		}
	}
	
	private boolean addToMap(Map<String,Set<HostToken<T>>> m,
			String key,HostToken<T> t)
	{
		Set<HostToken<T>> s = m.get(key);
		if(s == null)
		{
			s = new ArraySet<HostToken<T>>(1);
			m.put(key,s);
		}
		return s.add(t);
	}
	
	private boolean removeFromMap(Map<String,Set<HostToken<T>>> m,
			String key,HostToken<T> t)
	{
		Set<HostToken<T>> s = m.get(key);
		if(s != null) 
		{
			boolean removed = s.remove(t);
			// if set is empty then remove it from the map
			if(s.isEmpty())
				m.remove(key);
			return removed;
		}
		return false;
	}
	
	private Set<HostToken<T>> get(Map<String,Set<HostToken<T>>> m,String key)
	{
		Set<HostToken<T>> s = m.get(key);
		if(s != null) return Collections.unmodifiableSet(s);
		return Collections.<HostToken<T>>emptySet();
	}
	
	
	public boolean addAll(HostTokenStore<T> store)
	{
		if(store == null) throw new NullPointerException("store");
		
		return addAllTokens(store.allTokens);
	}
	
	public boolean addAllTokens(Collection<HostToken<T>> tokens)
	{
		boolean modified = false;
		if(tokens == null) throw new NullPointerException("tokens");
		for(HostToken<T> ht : tokens)
			modified |= addToken(ht);
		
		return modified;
	}
	public boolean addToken(HostToken<T> t)
	{
		if(t == null) throw new NullPointerException("t");
		
		boolean added = !t.isEmpty() && this.allTokens.add(t);
		
		if(added)
		{
			if(t.mac != null)
				addToMap(byMac,t.mac,t);
			if(t.ip != null)
				addToMap(byIp,t.ip,t);
			if(t.name != null)
				addToMap(byName,t.name,t);
		}
		
		return added;
	}
	public boolean removeToken(HostToken<T> t)
	{
		if(t == null) throw new NullPointerException("t");
		
		boolean removed = !t.isEmpty() && this.allTokens.remove(t);
		
		if(removed)
		{
			if(t.mac != null)
				removeFromMap(byMac,t.mac,t);
			if(t.ip != null)
				removeFromMap(byIp,t.ip,t);
			if(t.name != null)
				removeFromMap(byName,t.name,t);
		}
		
		return removed;
		
	}
	
	public void clear()
	{
		this.allTokens.clear();
		this.byIp.clear();
		this.byMac.clear();
		this.byName.clear();
		
	}
	public boolean isEmpty()
	{
		return allTokens.isEmpty();
	}
	public int size()
	{
		return allTokens.size();
	}
	public Set<HostToken<T>> getTokensByMac(String mac)
	{
		return get(byMac,mac);
	}
	public Set<HostToken<T>> getTokensByIP(String ip)
	{
		return get(byIp,ip);
	}
	public Set<HostToken<T>> getTokensByName(String name)
	{
		return get(byName,name);
	}
	
	public Set<HostToken<T>> getAllTokens() 
	{ 
		return Collections.unmodifiableSet(allTokens); 
	}
	
	public Map<String,Set<HostToken<T>>> getAllByMac()
	{
		return getAllByMap(byMac);
	}
	public Map<String,Set<HostToken<T>>> getAllByIp()
	{
		return getAllByMap(byIp);
	}
	public Map<String,Set<HostToken<T>>> getAllByName()
	{
		return getAllByMap(byName);
	}
	private Map<String,Set<HostToken<T>>> getAllByMap(Map<String,Set<HostToken<T>>> m)
	{
		Map<String,Set<HostToken<T>>> copy = 
			CollectionUtils.createHashMap(m.size(), CollectionUtils.LOAD_FACTOR);
		for(Map.Entry<String, Set<HostToken<T>>> e : m.entrySet())
			copy.put(e.getKey(), Collections.unmodifiableSet(e.getValue()));
		return Collections.unmodifiableMap(copy);
	}

	public String toString()
	{
		StringBuilder str = new StringBuilder(1024 * 1024);
		boolean first = true;
		for(HostToken<T> t : allTokens)
		{
			if(!first)
				str.append(";");
			else
				first = false;
			str.append(t);
		}
		return str.toString();
			
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof HostTokenStore)) return false;
		HostTokenStore<T> other = (HostTokenStore<T>)o;
		return this.allTokens.equals(other.allTokens);
	}
	@Override
	public int hashCode()
	{
		int result = 17;
		final int mult = 31;
		
		result = mult * result + allTokens.hashCode();
		
		return result;
	}
	
	/**
	 * Saves the data in native mac,ip,name csv format.
	 * The writer is always closed as a result of invoking this operation.
	 * 
	 * @param out the <code>Writer</code>
	 * 
	 */
	public void save(Writer out)
		throws IOException
	{
		CSVWriter writer = null;
		try
		{
			
			if(!(out instanceof BufferedWriter))
				out = new BufferedWriter(out);
			writer = new CSVWriter(out);
			for(HostToken<T> t : allTokens)
			{
				// write out as [Host],[IP],[MAC]
				writer.print(new String[] {t.name,t.ip,t.mac,
						callback != null ? callback.toString(t.data) : MiscUtils.toString(t.data)});
				
			}
			writer.flush();
		}
		finally
		{
			MiscUtils.closeStream(writer);
		}
	}

        /**
         * Normalizes this store using the given normalizer
         *
         * @param n the <code>TokenNormalizer</code>
         */
        public void normalize(TokenNormalizer n)
        {
            for(TokenIterator<T> it = this.iterator(); it.hasNext();)
            {
                HostToken<T> token = it.next();
                it.setName(n.normalizeName(token.name));
                it.setIp(n.normalizeIp(token.ip));
                it.setMac(n.normalizeMac(token.mac));
            }
            
            trim();
        }
	/**
	 * Trims out tokens that are subsets of each other.
	 * 
	 */
	public void trim()
	{
		// sort the tokens using the token subset comparator
		List<HostToken<T>> tokens = 
			new ArrayList<HostToken<T>>(this.allTokens);
		
		Collections.sort(tokens,new HostTokenSubsetComparator<T>());
		
		for(ListIterator<HostToken<T>> it = tokens.listIterator(); it.hasNext();)
		{
			// compare adjacent tokens
			HostToken<T> lhs = it.next();
			// remove all subset tokens
			while(it.hasNext())
			{
				HostToken<T> rhs = it.next();
				// it is a subset remove it from this store
				if(lhs.contains(rhs))
					this.removeToken(rhs);
				else // removed all subsets: time for next token
				{
					//compare rhs again with next token
					it.previous();
					break;
				}
			}
		}
	
	}
	
	public static <T> HostTokenStore<T> read(Reader in,HostTokenCallback<T> cb)
		throws IOException
	{
		CSVReader reader = null;
		HostTokenStore<T> store = new HostTokenStore<T>();
		store.setHostTokenCallback(cb);
		
		try
		{
			if(!(in instanceof BufferedReader))
				in = new BufferedReader(in);
			reader = new CSVReader(in);
			List<String[]> lines = reader.readAll();
			for(String [] line : lines)
			{
				String name = line[0];
				String ip = line[1];
				String mac = line[2];
				T data = null;
				if(cb != null && line.length > 3)
					data = cb.valueOf(line[3]);
					
				store.addToken(new HostToken<T>(name,ip,mac,data));
			}
		}
		finally
		{
			MiscUtils.closeStream(reader);
		}
		
		return store;

	}
	public void writeFormatDataByMac(Appendable out)
		throws IOException
	{
		writeFormatData(new TreeMap<String,Set<HostToken<T>>>(byMac),KeyType.MAC,out);
	}
	public void writeFormatDataByIp(Appendable out)
		throws IOException
	{
		writeFormatData(new TreeMap<String,Set<HostToken<T>>>(byIp),KeyType.IP,out);
	}
	
	public void writeFormatDataByHost(Appendable out)
		throws IOException
	{
		writeFormatData(new TreeMap<String,Set<HostToken<T>>>(byName),KeyType.HOST,out);
	}
	private void writeFormatData(Map<String,Set<HostToken<T>>> map,KeyType keyType,Appendable out)
		throws IOException
	{
		int width = 25;
		// write title
		String formatStr = "%-" + width + "s%-" + width + "s%-" + width + "s";
		Set<KeyType> dataHeaders = EnumSet.complementOf(EnumSet.of(keyType));
		Iterator<KeyType> dataHeaderIt = dataHeaders.iterator();
		out.append(String.format(formatStr,keyType,dataHeaderIt.next(),dataHeaderIt.next()));
		out.append('\n');
		
		String [] output = new String[3];
		// write out each entry
		for(Map.Entry<String, Set<HostToken<T>>> e : map.entrySet())
		{
			output[0] = e.getKey();
			for(HostToken<T> t : e.getValue())
			{
				Iterator<KeyType> kti = dataHeaders.iterator();
				for(int i = 1; i < 3; ++i)
				{
					switch(kti.next())
					{
					case MAC: output[i] = t.mac; break;
					case IP : output[i] = t.ip; break;
					case HOST:output[i] = t.name; break;
					default:throw new AssertionError();
					}
				}
				out.append(String.format(formatStr,
						MiscUtils.toString(output[0]),
						MiscUtils.toString(output[1]),
						MiscUtils.toString(output[2])));
				out.append('\n');
				// only print the indexer for the first entry
				output[0] = null;
				
					
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public TokenIterator<T> iterator()
	{
		return new _TokenIterator(allTokens);
	}
	
	public TokenIterator<T> iteratorByName(String host)
	{
		return new _TokenIterator(this.getTokensByName(host));
	}
	
	public TokenIterator<T> iteratorByMac(String mac)
	{
		return new _TokenIterator(this.getTokensByMac(mac));
	}
	
	public TokenIterator<T> iteratorByIp(String ip)
	{
		return new _TokenIterator(this.getTokensByIP(ip));
	}
	
	
	
	private class _TokenIterator implements TokenIterator<T>
	{
		private final Map<Integer,HostToken<T>> itMap;
		private int index;
		private final int origMaxIndex;
		private int currMaxIndex;
		
		public _TokenIterator(Set<HostToken<T>> universe)
		{
			itMap = new HashMap<Integer,HostToken<T>>();
			
			int i = 0;
			for(HostToken<T> t : universe)
				itMap.put(i++, t);
			currMaxIndex = origMaxIndex = i - 1;
			index = -1;
		}
		/* (non-Javadoc)
		 * @see edu.uchicago.nsit.iteco.netinf.HostTokenStore<T>.TokenIterator#add(edu.uchicago.nsit.iteco.netinf.HostTokenStore<T>.HostToken<T>)
		 */
		public boolean add(HostToken<T> t) 
		{
			boolean added =  HostTokenStore.this.addToken(t);
			if(added)
				itMap.put(++currMaxIndex, t);
			return added;
		}

		/* (non-Javadoc)
		 * @see edu.uchicago.nsit.iteco.netinf.HostTokenStore<T>.TokenIterator#modifyIp(java.lang.String)
		 */
		public void setIp(String ip) 
		{
			if(!checkRange())
				throw new NoSuchElementException();
			HostToken<T> t = getCurrentToken();
			String prevIp = t.ip;
			ip = HostToken.makeIp(ip);
			if(!MiscUtils.safeEquals(prevIp, ip))
			{
				// if token now completely empty remove it
				// otherwise remove
				if(t.isEmpty())
					remove();
				else
				{
					// must update by removing from set and then readding
					// since the name,ip,mac are part of the invariants of the set
					removeToken(t);
					t.ip = ip;
					addToken(t);
				}
			}
		}
		
		private HostToken<T> getCurrentToken()
		{
			HostToken<T> t = itMap.get(index);
			if(t == null)
				throw new IllegalStateException();
			return t;
		}
		
		private boolean checkRange()
		{
			return index >= 0 && index <= origMaxIndex;
		}
		private boolean checkRangeNext()
		{
			return index >= -1 && index < origMaxIndex;
		}

		/* (non-Javadoc)
		 * @see edu.uchicago.nsit.iteco.netinf.HostTokenStore<T>.TokenIterator#modifyMac(java.lang.String)
		 */
		public void setMac(String mac) 
		{
			if(!checkRange())
				throw new NoSuchElementException();
			
			HostToken<T> t = getCurrentToken();
			mac = HostToken.makeMac(mac);
			String prevMac = t.mac;
			
			if(!MiscUtils.safeEquals(prevMac,mac))
			{
				// if token now completely empty remove it
				// otherwise remove
				if(t.isEmpty())
					remove();
				else
				{
					// must update by removing from set and then readding
					// since the name,ip,mac are part of the invariants of the set
					removeToken(t);
					t.mac = mac;
					addToken(t);
				}
			}
		}

		/* (non-Javadoc)
		 * @see edu.uchicago.nsit.iteco.netinf.HostTokenStore<T>.TokenIterator#modifyName(java.lang.String)
		 */
		public void setName(String name) 
		{
			if(!checkRange())
				throw new NoSuchElementException();
			HostToken<T> t = getCurrentToken();
			name = HostToken.makeName(name);
			String prevName = t.name;
			if(!MiscUtils.safeEquals(prevName, name))
			{
				// if token now completely empty remove it
				// otherwise remove
				if(t.isEmpty())
					remove();
				else
				{
					// must update by removing from set and then readding
					// since the name,ip,mac are part of the invariants of the set
					removeToken(t);
					t.name = name;
					addToken(t);
				}
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() 
		{
			return checkRangeNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public HostToken<T> next() 
		{
			if(!checkRangeNext())
				throw new NoSuchElementException();
			HostToken<T> t = itMap.get(++index);
			if(t == null) throw new AssertionError();
			return t;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() 
		{
			if(!checkRange())
				throw new NoSuchElementException();
			HostToken<T> t = getCurrentToken();
			itMap.remove(index);
			HostTokenStore.this.removeToken(t);
		}
		
	} // _TokenIterator
	
	public Map<String,HostData<T>> toHostData()
	{
		Map<String,HostData<T>> hdata = CollectionUtils.createHashMap(
				this.byName.size(),CollectionUtils.LOAD_FACTOR);
		
		for(String name : this.byName.keySet())
		{
			HostData<T> hd = getHostData(name);
			if(hd == null) throw new AssertionError();
			hdata.put(name, hd);
		}
		
		return hdata;
		
	}
	
	public HostData<T> getHostData(String name)
	{
		if(name == null)
			throw new NullPointerException("name");
		
		Collection<HostToken<T>> htokens = this.byName.get(name);
		T currData = null;
		String nameProp = null;
		
		if(!MiscUtils.isEmpty(htokens))
		{
			HostData<T> hd = new HostData<T>(name,true,null);
			for(HostToken<T> ht : htokens)
			{
				if(currData == null)
					currData = hd.data = ht.data;
				// inconsistent set to null
				else if(ht.data != null && !ht.data.equals(currData))
				{
					hd.data = this.callback != null ? this.callback.merge(ht.data, currData) : null;
				}
				
				String ip = ht.ip;
				String mac = ht.mac;
				if(ip != null)
					hd.getIpAddresses().add(ip);
				if(mac != null)
					hd.getMacAddresses().add(mac);
			}
			
			return hd;
		}
		return null;
	}
	
	public void addHostData(HostData<T> hd)
	{
		if(hd == null) throw new NullPointerException("hd");
		
		this.addAllTokens(hd.toHostTokens());
	}
	
	public void addAllHostData(Collection<HostData<T>> hostData)
	{
		if(hostData == null) throw new NullPointerException("hostData");
		
		for(HostData<T> hd : hostData)
			addHostData(hd);
	}



}
