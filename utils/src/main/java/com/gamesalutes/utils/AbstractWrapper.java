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

import java.io.Serializable;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public abstract class AbstractWrapper<T> implements Mutability,Serializable,Cloneable
{
	private static final long serialVersionUID = 1L;
	
	private final boolean isMutable;
	private T wrapped;
	
	
	/**
	 * Constructor.
	 * 
	 * The wrapped object is immutable.
	 * 
	 * 
	 * @param wrapped the object to wrap
	 */
	
	public AbstractWrapper(T wrapped)
	{
		this(wrapped,false);
	}
	/**
	 * Constructor.
	 * 
	 * @param wrapped the object to wrap
	 * @param isMutable <code>true</code> if {@link #set(Object)} should be supported and
	 *                  <code>false</code> otherwise
	 */
	public AbstractWrapper(T wrapped,boolean isMutable)
	{
		this.wrapped= wrapped;
		this.isMutable = isMutable;
	}
	
	/**
	 * Makes a shallow copy of this wrapper.  The wrapped object itself is not copied.
	 * 
	 * @return shallow copy
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AbstractWrapper<T> clone()
	{
		try
		{
			return (AbstractWrapper<T>)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new AssertionError(e);
		}
	}
	
	
	/**
	 * Returns the wrapped object.
	 * 
	 * @return the wrapped object
	 */
	public final T get() { return wrapped; }
	
	
	/**
	 * Sets the object to be wrapped.  {@link #isMutable()} should be called first
	 * to make sure that this wrapper can change its wrapped value after construction.
	 * 
	 * @param wrapped the object to wrap
	 * @throws UnsupportedOperationException if changing the original wrapped object is not supported
	 *          as indicated by {@link #isMutable()} returning <code>false</code>
	 */
	public final void set(T wrapped)
	{
		if(!isMutable())
			throw new UnsupportedOperationException("not mutable");
		this.wrapped = wrapped;
	}
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.Mutability#isMutable()
	 */
	public final boolean isMutable() 
	{
		return isMutable;
	}
	
	@Override
	public final String toString()
	{
		return String.valueOf(wrapped);
	}
}
