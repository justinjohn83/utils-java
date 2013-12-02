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
package com.gamesalutes.utils.person;

import com.gamesalutes.utils.MiscUtils;

/**
 * Abstraction for a person's name, allowing lexographic comparisons ordered by last,first,middle.
 * 
 * @author Justin Montgomery
 * @version $Id: PersonName.java 1221 2010-08-11 17:27:20Z jmontgomery $
 */
public final class PersonName implements Comparable<PersonName>
{
	private final String firstName;
	private final String middleName;
	private final String lastName;
        private final Object userData;
	private NameFormatter formatter = new NameFormatters.FirstLastNameFormatter();
	
        /**
         * Formatter for creating the string representation of the <code>PersonName</code> abstraction.
         *
         * @author Justin Montgomery
         * @version $Id: PersonName.java 1221 2010-08-11 17:27:20Z jmontgomery $
         */
        public interface NameFormatter
        {
                /**
                 * Returns the string representaiton of <code>name</code>.
                 * <b><i>Implementations must not call name.toString() as this will result in infinite recursion!</i></b>.
                 *
                 * @param name the name
                 * @return the string representation
                 */
                String toNameString(PersonName name);
        }
	
	public PersonName(String first,String middle,String last)
	{
            this(first,middle,last,null);
	}

        public PersonName(String first,String middle,String last,Object userData)
        {
                if(last == null)
			throw new NullPointerException("last is null; first=" + first + ";last=" + last);
		if(MiscUtils.isEmpty(last))
			throw new IllegalArgumentException("last is empty; first=" + first + ";last=" + last);
		this.firstName = !MiscUtils.isEmpty(first) ? first : "";
		this.middleName = !MiscUtils.isEmpty(middle) ? middle: "";
		this.lastName = last;
                this.userData = userData;
        }

        public PersonName(String fullName)
        {
            this(fullName,null);
        }
	public PersonName(String fullName,Object userData)
	{
		if(fullName == null)
			throw new NullPointerException("fullName");
		if(MiscUtils.isEmpty(fullName))
			throw new IllegalArgumentException("fullName is empty");
		String [] parts = MiscUtils.split(fullName, "\\s+");
		
		// FIXME: it may be impossible to know whether the middle name is a true middle name or a two or more part last name
		if(parts.length >= 3)
		{
			this.firstName = parts[0];
			int lastStart = 2;
			// default as part of last name if middle name is longer than a single initial
			if(parts[1].length() > 1)
			{
				this.middleName = "";
				lastStart = 1;
			}
			else
				this.middleName = parts[1];
			
			StringBuilder last = new StringBuilder(128);
			for(int i = lastStart; i < parts.length; ++i)
			{
				last.append(parts[i]);
				if(i < parts.length - 1)
					last.append(' ');
			}
			this.lastName = last.toString();
		}
		else if(parts.length == 2)
		{
			this.firstName = parts[0];
			this.middleName = "";
			this.lastName = parts[1];
		}
		else if(parts.length == 1)
		{
			this.firstName = "";
			this.middleName = "";
			this.lastName = parts[0];
		}
		else
			throw new IllegalArgumentException("Illegal fullName: " + fullName);

                this.userData = userData;
		
		
	}
	
	public String getFirst() { return firstName; }
	public String getMiddle() { return middleName; }
	public String getLast() { return lastName; }
        public Object getUserData() { return userData; }
	
	
	public PersonName setNameFormatter(NameFormatter format)
	{
		if(format == null)
			format = new NameFormatters.FirstLastNameFormatter();
		this.formatter = format;
		
		return this;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof PersonName)) return false;
		PersonName n = (PersonName)o;
		return lastName.equals(n.lastName) &&
		       firstName.equals(n.firstName) &&
		       middleName.equals(n.middleName) &&
                       MiscUtils.safeEquals(userData, n.userData);
	}
	@Override
	public int hashCode()
	{
		int result = 17;
		final int mult = 37;
		result = result * mult + firstName.hashCode();
		result = result * mult + middleName.hashCode();
		result = result * mult + lastName.hashCode();
                result = result * mult + MiscUtils.safeHashCode(userData);
		
		return result;
	}
	@Override
	public String toString()
	{
		return formatter.toNameString(this);
	}
	public int compareTo(PersonName o) 
	{
		// compare last,first,middle
		
		int result = lastName.compareTo(o.lastName);
		if(result != 0) return result;
		
		result = firstName.compareTo(o.firstName);
		if(result != 0) return result;
		
		result = middleName.compareTo(o.middleName);
                if(result != 0) return result;

                return MiscUtils.safeCompareTo(userData, o.userData, true);

	}
	
	public static PersonName valueOf(String name)
	{
		return new PersonName(name);
	}
} // Name
