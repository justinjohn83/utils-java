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


/**
 * Wrapper around an object that optionally supports mutability.  This class is not thread-safe.  If a
 * thread-safe wrapper is required, consider using <code>java.util.concurrent.atomic.AtomicReference</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: Wrapper.java 1687 2009-09-14 22:27:58Z jmontgomery $
 */
public final class Wrapper<T> extends AbstractWrapper<T>
{
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor.
	 * 
	 * Wrapped object is immutable.
	 * 
	 * @param wrapped the object to wrap
	 */
	public Wrapper(T wrapped)
	{
		super(wrapped);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param wrapped the object to wrap
	 * @param isMutable <code>true</code> if {@link #set(Object)} should be supported and
	 *                  <code>false</code> otherwise
	 */
	public Wrapper(T wrapped,boolean isMutable)
	{
		super(wrapped,isMutable);
	}
	
	/**
	 * Makes a shallow copy of this wrapper.  The wrapped object itself is not copied.
	 * 
	 * @return shallow copy
	 */
	@Override
	public Wrapper<T> clone()
	{
		return (Wrapper<T>)super.clone();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Wrapper)) return false;
		Wrapper<?> w = (Wrapper<?>)o;
		return MiscUtils.safeEquals(this.get(), w.get());
	}
	
	@Override
	public int hashCode()
	{
		return MiscUtils.safeHashCode(get());
	}

}
