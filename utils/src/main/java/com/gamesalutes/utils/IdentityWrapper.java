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

import java.io.IOException;


/**
 * 
 * Wraps an object so that equals and hashcode are overriden to use the identity operator and
 * <code>System.identityHashCode</code>. 
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class IdentityWrapper<T> extends AbstractWrapper<T>
{
	private static final long serialVersionUID = 1L;
	
	private transient int hashCode;

		
	/**
	 * Constructor.
	 * 
	 * Wrapped object is immutable.
	 * 
	 * @param wrapped the object to wrap
	 */
	public IdentityWrapper(T wrapped)
	{
		super(wrapped);
		initHashCode();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param wrapped the object to wrap
	 * @param isMutable <code>true</code> if {@link #set(Object)} should be supported and
	 *                  <code>false</code> otherwise
	 */
	public IdentityWrapper(T wrapped,boolean isMutable)
	{
		super(wrapped,isMutable);
		initHashCode();
	}
	
	/**
	 * Makes a shallow copy of this wrapper.  The wrapped object itself is not copied.
	 * 
	 * @return shallow copy
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IdentityWrapper<T> clone()
	{
		return (IdentityWrapper)super.clone();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof IdentityWrapper))
			return false;
		return get() == ((IdentityWrapper)o).get();
	}
	
	@Override
	public int hashCode()
	{
		return hashCode;
	}
	
	
	private void readObject(java.io.ObjectInputStream in)
	  throws IOException, ClassNotFoundException
	  {
		 //read in default state
		 in.defaultReadObject();
		 
		 initHashCode();
	  }
	
	private void initHashCode()
	{
		int result = 17;
		final int mult = 31;
		result = mult * result + (get() != null ? System.identityHashCode(get()) : 0);
		
		hashCode = result;
	}
}
