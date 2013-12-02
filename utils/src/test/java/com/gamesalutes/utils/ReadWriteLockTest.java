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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Justin Montgomery
 * @version $Id: ReadWriteLockTest.java 978 2008-06-18 21:53:01Z jmontgomery $
 */
public final class ReadWriteLockTest
{
	private static final int TIMEOUT = 1000; //1000 ms
	private static final int REQUEST_TIMEOUT = TIMEOUT / 10;
	private static final int SLEEP_TIME = 20; //20ms
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
	private volatile boolean executed = false;
	private static final String READ_LOCKED_FIELD = "readLockedThreads";
	private static final String WRITE_LOCKED_FIELD = "writeLockedThreads";
	private Set<Thread> readLockedThreads,writeLockedThreads;
	private ReadWriteLock lock;
	
	
	private static Object getField(Object o,String name)
		throws Exception
	{
		Field f = o.getClass().getDeclaredField(name);
		f.setAccessible(true);
		return f.get(o);
	}
	@Before
	public void setUp() throws Exception
	{
		lock = new ReadWriteLock();
		readLockedThreads = (Set)getField(lock,READ_LOCKED_FIELD);
		writeLockedThreads = (Set)getField(lock,WRITE_LOCKED_FIELD);
	}
	
	@After
	public void tearDown() throws Exception
	{
		lock.unlock();
		readLockedThreads = null;
		writeLockedThreads = null;
		lock = null;
	}
	
	/**
	 * Tests {@link ReadWriteLock#readLock()}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testReadLock()
	{
		try
		{
			lock.readLock();
			assertTrue(readLockedThreads.contains(Thread.currentThread()));
		}
		finally
		{
			lock.unlock();
		}
	}
	
	@Test(timeout=TIMEOUT)
	public void testWriteLock()
	{
		try
		{
			lock.writeLock();
			assertTrue(writeLockedThreads.contains(Thread.currentThread()));
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Tests {@link ReadWriteLock#tryReadLock(long,TimeUnit)}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testTryReadLockPass()
	{
		//should pass
		try
		{
			assertTrue(lock.tryReadLock(REQUEST_TIMEOUT,TIME_UNIT));
			assertTrue(readLockedThreads.contains(Thread.currentThread()));
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Tests {@link ReadWriteLock#tryReadLock(long,TimeUnit)}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testTryReadLockFail()
	{
		testTryLockFail(true);
	}

	/**
	 * Tests {@link ReadWriteLock#tryReadLock(long,TimeUnit)}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testTryWriteLockPass()
	{
		//should pass
		try
		{
			assertTrue(lock.tryWriteLock(REQUEST_TIMEOUT,TIME_UNIT));
			assertTrue(writeLockedThreads.contains(Thread.currentThread()));
		}
		finally
		{
			lock.unlock();
		}
	}
	
	
	private void testTryLockFail(final boolean read)
	{
		//should fail
		Thread t = new Thread()
		{
			public void run()
			{
				if(read)
					lock.writeLock();
				else
					lock.readLock();
				try
				{
					Thread.sleep(REQUEST_TIMEOUT);
				}
				catch(InterruptedException e) {}
				finally
				{
					lock.unlock();
				}
			}
		};
		t.start();
		try
		{
			Thread.sleep(SLEEP_TIME);
		}
		catch(InterruptedException e) {}
		try
		{
			if(read)
				assertFalse(lock.tryReadLock(0,TIME_UNIT));
			else
				assertFalse(lock.tryWriteLock(0,TIME_UNIT));
		}
		finally
		{
			lock.unlock();
		}
		
		try
		{
			t.join();
		}
		catch(InterruptedException e) {}
	}
	/**
	 * Tests {@link ReadWriteLock#tryReadLock(long,TimeUnit)}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testTryWriteLockFail()
	{
		testTryLockFail(false);
	}
	
	/**
	 * Tests {@link ReadWriteLock#unlock()}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testUnlock() 
	{
		try
		{
			lock.readLock();
		}
		finally
		{
			lock.unlock();
			assertTrue(!readLockedThreads.contains(Thread.currentThread()));
		}
		
		try
		{
			lock.writeLock();
		}
		finally
		{
			lock.unlock();
			assertTrue(!writeLockedThreads.contains(Thread.currentThread()));
		}
	}
	
	/**
	 * Tests {@link ReadWriteLock#hasReadLock()}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testHasReadLock()
	{
		assertFalse(lock.hasReadLock());
		try
		{
			lock.readLock();
			assertTrue(lock.hasReadLock());
		}
		finally
		{
			lock.unlock();
		}
		
	}

	/**
	 * Tests {@link ReadWriteLock#hasWriteLock()}.
	 *
	 */
	@Test(timeout=TIMEOUT)
	public void testHasWriteLock()
	{
		assertFalse(lock.hasWriteLock());
		try
		{
			lock.writeLock();
			assertTrue(lock.hasWriteLock());
		}
		finally
		{
			lock.unlock();
		}
		
	}
	
	/**
	 * Tests general behavior
	 */
	@Test(timeout=TIMEOUT)
	public void testMulti()
	{
		lock.readLock();
		lock.readLock();
		assertFalse(lock.writeLock());
		lock.unlock();
		assertTrue(lock.writeLock());
		lock.writeLock();
		lock.unlock();
		lock.readLock();
		lock.unlock();
		assertTrue(lock.writeLock());
		lock.readLock();
		lock.unlock();
		assertTrue(!readLockedThreads.contains(Thread.currentThread()));
		assertTrue(!writeLockedThreads.contains(Thread.currentThread()));
	}
}
