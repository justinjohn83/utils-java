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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


// NOTE: Refactored out of FullDataLocker (v.1.12) for general reusability
/**
 * Wrapper around a <code>ReentrantReadWriteLock</code> that avoids common
 * deadlock cases. This class automatically prevents dead-lock situations
 * that can occur when attempting an illegal upgrade of a read-lock to a
 * write-lock by not allowing this situation to occur.  A downgrade of a lock
 * from a write-lock to a read-lock is allowed, and calling {@link{unlock()} 
 * once will unlock both lock types.If the read-lock or write-lock is obtained
 * multiple times by the same thread, only a single call to 
 * <code>unlock</code> is required to release all the locks for that thread.
 * 
 * @author Justin Montgomery
 * @version $Id: ReadWriteLock.java 974 2008-06-16 21:16:40Z jmontgomery $
 *
 */
public final class ReadWriteLock
{
	private final Lock readLock,writeLock;
	private final Set<Thread> readLockedThreads;
	private final Set<Thread> writeLockedThreads;	
	
	/**
	 * Constructor.
	 * Creates a new <code>ReadWriteLock</code>.
	 * 
	 */
	public ReadWriteLock()
	{
		final ReentrantReadWriteLock dataLock = new ReentrantReadWriteLock(false);
		readLock = dataLock.readLock();
		writeLock = dataLock.writeLock();
		
		
		readLockedThreads = Collections.synchronizedSet(new HashSet<Thread>());
		writeLockedThreads = Collections.synchronizedSet(new HashSet<Thread>());
	}
	
	private boolean canWriteLock()
	{
		return !readLockedThreads.contains(Thread.currentThread());
	}
	
	/**
	 * Tries to obtain the read lock within <code>time</code>.
	 * 
	 * @param time the timeOut duration 
	 * @param unit the <code>TimeUnit</code>
	 * @return <code> true </code> if locking successful and
	 *         <code> false </code> if timeout occurred
	 */
	public boolean tryReadLock(long time,TimeUnit unit)
	{
		if(hasReadLock()) return true;
		return tryLock(time,unit,true);
	}
	

	/**
	 * Tries to obtain the write lock within <code>time</code>.
	 * 
	 * @param time the timeOut duration 
	 * @param unit the <code>TimeUnit</code>
	 * @return <code> true </code> if locking successful and
	 *         <code> false </code> if timeout occurred
	 */
	public boolean tryWriteLock(long time,TimeUnit unit)
	{
		if(hasWriteLock()) return true;
		return tryLock(time,unit,false);
	}
	
	/**
	 * Obtains the read-lock, blocking until the lock is available
	 * 
	 */
	public void readLock()
	{
		//prevent relocking
		if(!hasReadLock())
		{
			readLock.lock();
			readLockedThreads.add(Thread.currentThread());
		}
	}
	
	/**
	 * Obtains the write-lock if this thread doesn't currently hold read-lock
	 * This method blocks until write-lock can be obtained or returns <code> false </code>
	 * if the current thread holds the read-lock.  Doing this prevents a dead-lock situation
	 * since upgrading from a read-lock to a write-lock is NOT allowed.
	 * @return <code> true </code> if write-lock obtained and <code> false </code> otherwise
	 */
	public boolean writeLock()
	{
        // don't acquire the lock more than once
		if(hasWriteLock())
			return true;
		
		if(canWriteLock())
		{
			writeLock.lock();
			writeLockedThreads.add(Thread.currentThread());
			return true;
		}
		else
			return false;
	}
	
	private boolean tryLock(long time,TimeUnit unit,boolean getReadLock)
	{
		boolean result = false;
		try
		{
			if(getReadLock)
			{
				result = readLock.tryLock(time,unit);
				if(result)
					readLockedThreads.add(Thread.currentThread());
			}
			else
			{
				result = writeLock.tryLock(time,unit);
				if(result)
					writeLockedThreads.add(Thread.currentThread());
			}
		}
		catch(Throwable e) {result = false; }
		
		return result;
	}
	
	
	/**
	 * Returns whether current thread holds a read lock
	 * @return <code> true </code> if current thread holds a read lock and
	 *         <code> false </code> otherwise
	 */
	public boolean hasReadLock()
	{
		return readLockedThreads.contains(Thread.currentThread()); 
	}
	
	/**
	 * Returns whether current thread holds the write lock
	 * @return <code> true </code> if current thread holds the write lock and
	 *         <code> false </code> otherwise
	 */
	public boolean hasWriteLock()
	{
		return writeLockedThreads.contains(Thread.currentThread());
	}
	
	/**
	 * Removes the locks that the currently executing thread holds, if any.  
	 * This method completely unlocks a lock that was write-locked and then
	 * read-locked.  It is safe to call unlock if the current thread holds
	 * no locks.
	 *
	 */
	public void unlock()
	{
        //downgrading causes deadlock if both read lock AND write lock not released
		if(readLockedThreads.remove(Thread.currentThread()))
			readLock.unlock();
		if(writeLockedThreads.remove(Thread.currentThread()))
			writeLock.unlock();
	}
} //end ReadWriteLock
