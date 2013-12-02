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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around a java.nio.FileLock.  Also supports locking within same process as FileLock does not support this feature
 * 
 * @author Justin Montgomery
 * @version $Id: FileLock.java 1868 2010-01-26 00:54:28Z jmontgomery $
 */
public final class FileLock implements Lock 
{
	private File file;
	private volatile java.nio.channels.FileLock lock;
	private FileChannel fileChannel;
	private ReentrantLock internalLock;
	private boolean createIfNotExists;
	
	private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
	/**
	 * Constructor.
	 * 
	 * @param filename the file to lock
	 */
	public FileLock(String filename)
	{
		this(FileUtils.newFile(filename));
	}
	
	/**
	 * Constructor.
	 * 
	 * @param filename the file to lock
	 */
	public FileLock(String filename,boolean createIfNotExists)
	{
		this(FileUtils.newFile(filename),createIfNotExists);
	}
	
	/**
	 * Returns the <code>File</code> used for locking.
	 * 
	 * @return the <code>File</code> used for locking
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param file the file to lock
	 */
	public FileLock(File file)
	{
		this(file,false);
	}
	/**
	 * Constructor.
	 * 
	 * @param file the file to lock
	 */
	public FileLock(File file,boolean createIfNotExists)
	{
		if(file == null)
			throw new NullPointerException("file");
		if(!createIfNotExists && !file.exists())
			throw new IllegalArgumentException("file : " + file + " does not exist");
		if(!createIfNotExists && !file.isFile())
			throw new IllegalArgumentException("file: " + file + " is not a file");
		this.file = file;
		this.createIfNotExists = createIfNotExists;
		
		checkCreated();
		
		// lock internal to this process
		internalLock = new ReentrantLock();
	}
	
	private void checkCreated() {
		if(createIfNotExists)
		{
			try {
				this.file.createNewFile();
				
				if(!file.isFile()) {
					throw new IllegalArgumentException("file: " + file + " is not a file");
				}
				
			}
			catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Returns <code>true</code> if a lock is held on the file and
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if a lock is held on the file and
	 * <code>false</code> otherwise
	 */
	public synchronized boolean isLocked()
	{
		return lock != null;
	}
	
	/**
	 * Attempts to obtain a lock on the file immediately and returns <code>false</code>
	 * if the lock could not be obtained.
	 * 
	 * @return <code>true</code> if the file was locked and 
	 *         <code>false</code> otherwise
	 * @throws IOException if an error occurs during lock acquistion
	 */
	public boolean tryLock() 
	{
		return lock(false);
	}
	
	/**
	 * Waits to obtain the lock on the file and returns once the lock is obtained or
	 * throws an <code>IOException</code> if there was a problem obtaining the lock.
	 * 
	 * @throws IOException
	 */
	public void lock()
	{
		lock(true);
	}
	
	private synchronized boolean lock(boolean wait) 
	{
		if(logger.isDebugEnabled()) {
			logger.debug("FileLock.lock():Enter lock(" + wait + ")...current thread=" + Thread.currentThread() + ": fileLock=" + lock + ";internal lock hold count=" +  internalLock.getHoldCount());
		}
		

		if(logger.isDebugEnabled()) {
			logger.debug("FileLock.lock():Internal Lock ATTEMPTING LOCK...current thread=" + Thread.currentThread() + ": hold count=" +  internalLock.getHoldCount());
		}
		// first grab in-process lock
		if(wait) {
			internalLock.lock();
		}
		else if(!internalLock.tryLock()) {
			return false;
		}
		
		// if already have the lock, then simply return here
		if(lock != null) return true;
		
		if(logger.isDebugEnabled()) {
			logger.debug("FileLock.lock()Internal Lock LOCK ACQUIRED...current thread=" + Thread.currentThread() + ": hold count=" +  internalLock.getHoldCount());
		}
		synchronized(this) {
			try
			{		
				checkCreated();
				
				// open file for append mode in case a real file is used for locking
				fileChannel = new FileOutputStream(file,true).getChannel();
			
			// attempt to get the file lock
	
				if(wait)
					lock = fileChannel.lock();
				else
					lock = fileChannel.tryLock();
				
				if(logger.isDebugEnabled()) {
					logger.debug("FileLock.lock():File Lock LOCK...current thread=" + Thread.currentThread() + ": fileLock acquired=" +  (lock != null));
				}
				return lock != null;
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				// close the channel now if the lock could not be obtained
				if(lock == null) {
					
					if(logger.isDebugEnabled()) {
						logger.debug("FileLock.lock():File Lock FAILED...current thread=" + Thread.currentThread() + ": internal lock hold count=" +  internalLock.getHoldCount());
					}
					
					try {
						if(internalLock.isHeldByCurrentThread()) {
							internalLock.unlock();
							
							if(logger.isDebugEnabled()) {
								logger.debug("FileLock.lock():File Lock failed...current thread=" + Thread.currentThread() + ": internal lock RELEASED hold count=" +  internalLock.getHoldCount());
							}
						}
					}
					catch(Exception e2) {
						if(logger.isWarnEnabled()) {
							logger.warn("Unable to release internal lock",e2);
						}
					}
					
					MiscUtils.closeStream(fileChannel);
				}
			}
		} // synchronized

	}
	
	/**
	 * Releases the lock on the file.
	 * 
	 */
	public void unlock()
	{
		if(logger.isDebugEnabled()) {
			logger.debug("Enter unlock()...current thread=" + Thread.currentThread() + ": fileLock=" + lock + ";internal lock hold count=" +  internalLock.getHoldCount());
		}
		
		
		if(logger.isDebugEnabled()) {
			logger.debug("FileLock.unlock()...ATTEMPTING UNLOCK current thread=" + Thread.currentThread() + ": fileLock=" + lock + ";internal lock hold count=" +  internalLock.getHoldCount());
		}
		
		// release internal lock
		try
		{
			if(internalLock.isHeldByCurrentThread()) {
				internalLock.unlock();
				
				if(logger.isDebugEnabled()) {
					logger.debug("FileLock.unlock()...INTERNAL LOCK UNLOCK current thread=" + Thread.currentThread() + ": fileLock=" + lock + ";internal lock hold count=" +  internalLock.getHoldCount());
				}
			}
		}
		catch(Exception e)
		{
			if(logger.isWarnEnabled()) {
				logger.warn("Error releasing internal lock",e);
			}
		}
		
		// simply return now if the lock was never obtained
		// shouldn't be able to get to this state though
		if(lock == null) return;
		
		// release the lock and close the channel
		if(!internalLock.isHeldByCurrentThread()) {
			synchronized(this) {
				try
				{
					if(lock != null) {

						if(logger.isDebugEnabled()) {
							logger.debug("FileLock.unlock()...INTERNAL LOCK UNLOCK current thread=" + Thread.currentThread() + ": fileLock=" + lock + ";internal lock hold count=" +  internalLock.getHoldCount());
						}
						lock.release();
						
						if(logger.isDebugEnabled()) {
							logger.debug("FileLock.unlock()...INTERNAL LOCK UNLOCK current thread=" + Thread.currentThread() + ": File Lock released successfully.");
						}
					}
				}
				catch(Exception e)
				{
					if(logger.isWarnEnabled()) {
						logger.warn("Error releasing file lock",e);
					}
				}
				finally 
				{
					lock = null;
					MiscUtils.closeStream(fileChannel);
					fileChannel = null;
					
					if(logger.isDebugEnabled()) {
						logger.debug("FileLock.unlock()...INTERNAL LOCK UNLOCK current thread=" + Thread.currentThread() + ": File Lock: " + lock);
					}
				}
			}
		}
		
	}

	public void lockInterruptibly() throws InterruptedException {
		lock(true);
	}

	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}

	public boolean tryLock(long time,
            TimeUnit unit)
            throws InterruptedException
    {
		return lock(true);
    }
}
