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

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Abstract iterator implementation.
 * 
 * @author Justin Montgomery
 * @version $Id: AbstractIterator.java 1087 2008-09-05 22:22:09Z jmontgomery $
 */
public abstract class AbstractIterator<T> implements Iterator<T>//,Serializable
{
	    //private static final long serialVersionUID = 1L;
	    
		private final Object[] objects;
		private final BitSet removed;
		// start one behind the first
		private int index = -1;
		
		protected AbstractIterator(Collection<? extends T> objects)
		{
			if(objects == null)
				throw new NullPointerException("objects");
			this.objects = objects.toArray();
			// this.objects = new Object[objects.size()];
			//int count = 0;
			//for(Object o : objects)
			//	this.objects[count++] = o;
			removed = new BitSet(this.objects.length);
				
		}
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public final boolean hasNext() 
		{
			return index < objects.length - 1;
		}
	
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@SuppressWarnings("unchecked")
		public final T next() 
		{
			if(!hasNext())
				throw new NoSuchElementException();
			return (T)objects[++index];
		}
		
		@SuppressWarnings("unchecked")
		public final void remove()
		{
			if(index < 0 || index >= objects.length ||
				removed.get(index))
			{
				throw new IllegalStateException();
			}
			removed.set(index);
			doRemove((T)objects[index]);
		}
		/**
		 * Removes the <code>o</code> from the backing data structure.
		 * 
		 */
		protected abstract void doRemove(T o);
		
} // end AbstractIterator
