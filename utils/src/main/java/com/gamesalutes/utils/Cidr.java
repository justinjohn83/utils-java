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

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Classless interdomain routing abstraction for subnet addressing.
 * 
 * @author Justin Montgomery
 * @version $Id: Cidr.java 2726 2011-03-29 21:41:44Z jmontgomery $
 */
public final class Cidr implements Serializable,Comparable<Cidr>
{
	/**
	 * The type of local address.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: Cidr.java 2726 2011-03-29 21:41:44Z jmontgomery $
	 */
	public enum LocalAddressType
	{
		
		/**
		 * Blank 0.0.0.0 address.
		 * 
		 */
		BLANK(new Cidr("0.0.0.0/32")),
		
		/**
		 * Broadcast 255.255.255.255 address.
		 * 
		 */
		BROADCAST(new Cidr("255.255.255.255/32")),
		
		/**
		 * Localhost address.
		 * 
		 */
		LOCALHOST(new Cidr("127.0.0.1/32")),
		
		/**
		 * Class A address range: 10.0.0.0/8.
		 */
		CLASS_A(new Cidr("10.0.0.0/8")),
		
		/**
		 * Class B address range: 172.16.0.0/12.
		 */
		CLASS_B(new Cidr("172.16.0.0/12")),
		
		/**
		 * Class C address range: 192.168.0.0/16.
		 */
		CLASS_C(new Cidr("192.168.0.0/16"));
		
		private final Cidr range;
		
		private LocalAddressType(Cidr range)
		{
			if(range == null)
				throw new NullPointerException("range");
			this.range = range;
		}
		
		/**
		 * Returns the {@link Cidr Cidr} range that this local address type
		 * encapsulates.
		 * 
		 * @return the <code>Cidr</code>
		 */
		public Cidr getCidr() { return range; }
		
		/**
		 * Returns <code>true</code> if the specified address is in
		 * this local address's range and <code>false</code> otherwise.
		 * 
		 * @param address the input ip address
		 * @return <code>true</code> if in range and <code>false</code> otherwise
		 */
		public boolean containsAddress(String address)
		{
			return range.containsAddress(address);
		}
		
		/**
		 * Gets the <code>LocalAddressType</code> of <code>c</code> or <code>null</code>
		 * if it is a public address.
		 * 
		 * @param c the <code>Cidr</code>
		 * @return the <code>LocalAddressType</code> or <code>null</code>
		 */
		public static LocalAddressType getType(Cidr c)
		{
			for(LocalAddressType type : LocalAddressType.values())
			{
				if(type.getCidr().containsCidr(c))
						return type;
			}
			return null;
		}
		
		/**
		 * Gets the <code>LocalAddressType</code> of <code>c</code> or <code>null</code>
		 * if it is a public address.
		 * 
		 * @param address the ip address
		 * @return the <code>LocalAddressType</code> or <code>null</code>
		 */
		public static LocalAddressType getType(String address)
		{
			for(LocalAddressType type : LocalAddressType.values())
			{
				if(type.containsAddress(address))
						return type;
			}
			return null;
		}
	}
		
	private static final long serialVersionUID = 1L;
	
	private final int lowerLimit;
	private final int subnetMask;
	
	private static final Map<LocalAddressType,Integer> typeOrder; 
		
	static
	{	
		// TODO: using an EnumSet/EnumMap in this static context re-referencing Cidr causes an exception
		//typeOrder = new EnumMap<LocalAddressType,Integer>(LocalAddressType.class);
		typeOrder = new HashMap<LocalAddressType,Integer>();
		
		int count = 0;
//		EnumSet<LocalAddressType> s = EnumSet.of(
//				LocalAddressType.CLASS_A,
//				LocalAddressType.CLASS_B,
//				LocalAddressType.CLASS_C,
//				LocalAddressType.LOCALHOST,
//				LocalAddressType.BROADCAST,
//				LocalAddressType.BLANK);
		
		typeOrder.put(LocalAddressType.CLASS_A,count++);
		typeOrder.put(LocalAddressType.CLASS_B,count++);
		typeOrder.put(LocalAddressType.CLASS_C,count++);
		typeOrder.put(LocalAddressType.LOCALHOST, count++);
		typeOrder.put(LocalAddressType.BROADCAST, count++);
		typeOrder.put(LocalAddressType.BLANK, count++);
		
		// be sure to add any new enum types that are not listed here
		// so order is still deterministic
//		s = EnumSet.complementOf(s);
//		for(LocalAddressType t : s)
//			typeOrder.put(t, count++);
	
// this also causes an exception when LocalAddressType.values() called
//		for(LocalAddressType t : LocalAddressType.values())
//			if(!typeOrder.containsKey(t))
//				typeOrder.put(t, count++);
		
		
	}
	
	/**
	 * Creates a cidr from a <code>a.b.c.d/x</code> string, where a,b,c,d in [0,255]
	 * and x in [0,32]. 
	 * 
	 * @param cidr the cidr string
	 */
	public Cidr(String cidr)
	{
		if(cidr == null)
			throw new NullPointerException("cidr");
		String errMsg = "cidr=" + cidr + " not in a.b.c.d/x form";
		
		int index = cidr.indexOf('/');
		if(index == -1 || index == cidr.length() - 1)
			throw new IllegalArgumentException(errMsg);
		String ip = cidr.substring(0,index);
		try
		{
			this.lowerLimit = NetUtils.addressToInt(ip);
		}
		catch(RuntimeException e)
		{
			throw new IllegalArgumentException(errMsg + ";" + e.getMessage());
		}
		String maskStr = cidr.substring(index+1);
		int maskBits;
		try
		{
			maskBits = Integer.parseInt(maskStr);
			if(maskBits < 0 || maskBits > 32)
			{
				throw new IllegalArgumentException(errMsg + "; mask not in [0,32]");
			}
			
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException(errMsg);
		}
		
		this.subnetMask = createSubnetMask(maskBits);
		
		// ensure that mask makes sense for the base address
		this.createUpperLimit(this.lowerLimit, this.subnetMask);
		
		
	}
	
	/**
	 * Creates a cidr directly from a start address and a subnet mask
	 * 
	 * @param address the ip address as long
	 * @param subnetMask the subnet mask as long
	 */
	public Cidr(int address,int subnetMask)
	{
		this.lowerLimit = address;
		this.subnetMask = subnetMask;
		
		// check valid combo
		createUpperLimit(this.lowerLimit,this.subnetMask);
	}


        /**
         * Create a <code>Cidr</code> from a start and end address range
         *
         * @param startAddress the first address in the range
         * @param endAddress the last address in the range
         */
        public Cidr(String startAddress,String endAddress)
        {
            if(startAddress == null)
                throw new NullPointerException("startAddress");
            if(endAddress == null)
                throw new NullPointerException("endAddress");

            int startAddr = NetUtils.addressToInt(startAddress);
            int endAddr = NetUtils.addressToInt(endAddress);

            // figure out the subnet mask from the lower and upper limit
            int diff = (int)(ByteUtils.toUnsigned(endAddr) - ByteUtils.toUnsigned(startAddr));

            this.lowerLimit = startAddr;
            this.subnetMask = ~diff;
        }
	
	
	/**
	 * Returns the dotted decimal ip address of the first address of this cidr range.
	 * 
	 * @return the first address in the range
	 */
	public String getRangeFirst()
	{
		return NetUtils.intToAddress(lowerLimit);
	}
	
	/**
	 * Returns the dotted decimal ip address of the last address of this cidr range.
	 * 
	 * @return the last address in the range
	 */
	public String getRangeLast()
	{
		return NetUtils.intToAddress((int)createUpperLimit(lowerLimit,subnetMask));
	}
	
	/**
	 * Returns the integer mask for the cidr.
	 * 
	 * @return the mask
	 */
	public int getSubnetMask() 
	{
		return subnetMask;
	}
	
	private int createSubnetMask(int maskBits)
	{
		return ~( ( 1 << (32 - maskBits )) - 1 );
	}
	
	/**
	 * Returns the number of bits valid in the mask.
	 * 
	 * @return the mask bits
	 */
	public int getMaskBits() { return Integer.bitCount(subnetMask); }
	
	/**
	 * Returns whether this <code>Cidr</code> includes the specified dotted quad <code>address</code>.
	 * 
	 * @param address the address
	 * @return <code>true</code> if <code>address</code> is in this <code>Cidr</code> and
	 *         <code>false</code> otherwise
	 */
	public boolean containsAddress(String address)
	{
		if(address == null)
			throw new NullPointerException("address");
		return containsRange(NetUtils.addressToInt(address),0xFFFFFFFF);
	}
	
	/**
	 * Returns whether this <code>Cidr</code> includes the specified <code>address</code>.
	 * 
	 * @param address the address
	 * @return <code>true</code> if <code>address</code> is in this <code>Cidr</code> and
	 *         <code>false</code> otherwise
	 */
	public boolean containsAddress(InetAddress address)
	{
		if(address == null)
			throw new NullPointerException("address");
		return containsAddress(address.getHostAddress());
	}
	
	/**
	 * Returns whether this <code>Cidr</code> includes <code>c</code> in all of its range.
	 * 
	 * @param c another <code>Cidr</code>
	 * @return <code>true</code> if it fully contains <code>c</code> and <code>false</code> otherwise
	 */
	public boolean containsCidr(Cidr c)
	{
		if(c == null) 
			throw new NullPointerException("c");
		
		return containsRange(c.lowerLimit,c.subnetMask);
	}
	
	/**
	 * Returns whether this <code>Cidr</code> intersects <code>c</code> in its range.
	 * 
	 * @param c another <code>Cidr</code>
	 * @return <code>true</code> if it intersects <code>c</code> and <code>false</code> otherwise
	 */
	public boolean intersectsCidr(Cidr c)
	{
		if(c == null)
			throw new NullPointerException("c");
		
		return intersectsRange(c.lowerLimit,c.subnetMask);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Cidr)) return false;
		Cidr c = (Cidr)o;
		return this.lowerLimit == c.lowerLimit &&
		       this.subnetMask == c.subnetMask;
	}
	
	@Override
	public int hashCode()
	{
		int result = 37;
		final int mult = 17;
		result = mult * result + this.lowerLimit;
		result = mult * result + this.subnetMask;
		return result;
	}
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder(20);
		str.append(NetUtils.intToAddress(this.lowerLimit));
		str.append('/');
		str.append(this.getMaskBits());
		return str.toString();
	}
	
	/**
	 * Compares this <code>Cidr</code> to <code>o</code> by ordering public addresses first,
     * then class A,B,C,localhost,broadcast,blank and then ordering
     * by number of mask bits if they have the same type.  If the mask bits are the same, then
     * the subnet masks are compared.
     * 
	 * 
	 * @return positive integer,0,negative integer if <code>o</code> is
	 *         less than, equal to, or greater than this <code>Cidr</code>
	 *         
	 */
	public int compareTo(Cidr o)
	{
		LocalAddressType type1 = LocalAddressType.getType(this);
		LocalAddressType type2 = LocalAddressType.getType(o);
		
		// compare address type
		// public type is null so will be ordered before all others
		if(type1 != type2)
			return MiscUtils.safeCompareTo(typeOrder.get(type1),typeOrder.get(type2),true);
		
		// compare mask bits
		int m = getMaskBits();
		int om = o.getMaskBits();
		
		if(m < om) return -1;
		if(m > om) return 1;
		
		// compare addresses
		long ll = ByteUtils.toUnsigned(lowerLimit);
		long oll = ByteUtils.toUnsigned(o.lowerLimit);
		
		if(ll < oll) return -1;
		if(ll > oll) return 1;
		
		// equal
		return 0;
	}
	
	private long createUpperLimit(int lowerLimit,int subnetMask)
	{
		long limit =  ByteUtils.toUnsigned(lowerLimit) + ByteUtils.toUnsigned(~subnetMask);
		// make sure not greater than int range
		if(!ByteUtils.isUnsignedInt(limit))
		{
			throw new IllegalArgumentException("subnetMask=" + NetUtils.intToAddress(subnetMask) + 
					" too large for address=" + 
				NetUtils.intToAddress(lowerLimit));
		}
		
		return limit;
	}
	
	private boolean containsRange(int lowerLimit,int subnetMask)
	{
		long myLowerLimit = ByteUtils.toUnsigned(this.lowerLimit);
		long longLower = ByteUtils.toUnsigned(lowerLimit);
		long myUpperLimit = createUpperLimit(this.lowerLimit,this.subnetMask);
		
			return myLowerLimit <= longLower &&
			       myUpperLimit >= createUpperLimit(lowerLimit,subnetMask);
		
	}
	
	private boolean intersectsRange(int lowerLimit,int subnetMask)
	{
		long myLowerLimit = ByteUtils.toUnsigned(this.lowerLimit);
		long longLower = ByteUtils.toUnsigned(lowerLimit);
		long myUpperLimit = createUpperLimit(this.lowerLimit,this.subnetMask);
		long upperLimit = createUpperLimit(lowerLimit,subnetMask);
		
		if(myLowerLimit <= longLower)
			return myUpperLimit >= longLower;
		return myLowerLimit <= upperLimit;
	}
}
