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
package com.gamesalutes.utils.arg;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.gamesalutes.utils.StringUtils;
import com.gamesalutes.utils.MiscUtils;

/**
 * Gnu get-opt style command line argument producer.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public class ArgWriter
{
	private Collection<Argument> arguments;
	
	/**
	 * constructor.
	 * 
	 */
	public ArgWriter()
	{
		arguments = new LinkedHashSet<Argument>();
	}
	
	/**
	 * Adds an option with the given name and value.  If the length of <code>name</code> is
	 * &gt 1, then <code>name</code> is preceeded by <code>--</code>; otherwise, it is preceeded 
	 * by <code>-</code>. If <code>value</code> is <code>null</code> then the option is assumed to be 
	 * a switch and nothing is printed after <code>name</code>.
	 * 
	 * @param name the name of the option
	 * @param value the value of the option or <code>null</code> if this is a switch
	 * @return this for chaining
	 */
	public ArgWriter addOption(String name,String value)
	{
		if(name == null)
			throw new NullPointerException("name");
		if(MiscUtils.isEmpty(name))
			throw new IllegalArgumentException("name is empty");
		
		arguments.add(new Argument(name.trim(),value));
		
		return this;
	}
	
	/**
	 * Adds an argument with the given <code>value</code>.
	 * 
	 * @param value the value of the argument
	 * @return this for chaining
	 */
	public ArgWriter addArgument(String value)
	{
		if(MiscUtils.isEmpty(value))
			throw new IllegalArgumentException("value is empty");
		arguments.add(new Argument(null,value));
		
		return this;
	}
	
	/**
	 * Returns an array of the arguments delimited by spaces.
	 * This form can be passed to a <code>main</code> method.
	 * 
	 * @return the command line arguments
	 */
	public String [] generate()
	{
		String s = toString();
		
		// split by spaces
		return MiscUtils.split(s, " ");
	}
	
	/**
	 * Returns the arguments generated in a single string.
	 * 
	 * @return the argument string
	 */
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		int i = 0;
		int len = arguments.size();
		for(Argument a : arguments)
		{
			buf.append(a);
			
			if(++i < len)
				buf.append(' ');
		}
		
		return buf.toString();
	}
	
	private static class Argument
	{
		private final String name;
		private final String value;
		
		public Argument(String name,String value)
		{
			this.value = StringUtils.trim(value);
			// if value is null then this is command line switch; otherwise, shouldn't print an option with empty value
			this.name =  (this.value == null || this.value.length() > 0) ? StringUtils.trimNull(name) : null;
		}
		
		public String getName() { return name; }
		public String getValue() { return value; }
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(!( o instanceof Argument)) return false;
			
			Argument a = (Argument)o;
			
			if((name == null) != (a.name == null)) return false;
			if(name != null && !name.equals(a.name)) return false;
			// arguments without names (i.e. plain values) are never equal
			if(name == null) return false;
			
			return MiscUtils.safeEquals(value, a.value);
			
		}
		
		@Override
		public int hashCode()
		{
			int result = 17;
			final int mult = 31;
			
			result = mult * result + MiscUtils.safeHashCode(name);
			result = mult * result + MiscUtils.safeHashCode(value);
			
			return result;
		}
		
		@Override
		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			if(name != null)
			{
				if(name.length() > 1)
					buf.append("--");
				else
					buf.append("-");
				buf.append(name);
			}
			if(value != null)
			{
				if(buf.length() > 0)
					buf.append(" ");
				buf.append(ArgUtils.quoteStringValue(value));
			}
			
			return buf.toString();
		}
	}
}
