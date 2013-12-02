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
package com.gamesalutes.utils;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLockTest {

	private static final int TIMEOUT = 10000;
	
	private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

	private File lockFile;
		
	@BeforeClass
	public static void beforeClass(){
		LoggingUtils.initializeLogging(FileLockTest.class.getResourceAsStream("/log4j.properties"));
		
	}
	@Before
	public void before() throws IOException {
		lockFile = File.createTempFile("lock", ".lock");
	}
	
	@After
	public void after() {
		lockFile.delete();
		lockFile = null;
	}
	
	@Test(timeout=1000)
	public void basicTest() throws Exception {
		final FileLock fileLock = new FileLock(lockFile,true);

		fileLock.lock();
		fileLock.unlock();
		
		assertFalse(fileLock.isLocked());
		
	}
	@Test(timeout=TIMEOUT)
	public void stressTest() throws Exception {
		
		final int COUNT = 50;
		
		// create a bunch of resources locking and unlocking
		final CountDownLatch latch = new CountDownLatch(COUNT);
		final FileLock fileLock = new FileLock(lockFile,true);
		
		ExecutorService exec = Executors.newFixedThreadPool(COUNT);
		
		for(int i = 1; i<= COUNT; ++i) {
			
			final int count = i;
			
			exec.execute(new Runnable() {
	
				
				public void run() {
					
					logger.info("Run: " + count + " locking...");
					// create the lock
					fileLock.lock();
					//fileLock.lock();
					
					logger.info("Run: " + count + " LOCKED.");

					// sleep for a bit
					try {
						Thread.currentThread().sleep(20);
					}
					catch(InterruptedException e) {
						Thread.currentThread().interrupt();
						throw new RuntimeException(e);
					}
					
					logger.info("Run: " + count + " unlocking...");
					fileLock.unlock();
					//fileLock.unlock();
					logger.info("Run: " + count + " UNLOCKED.");

					latch.countDown();
				}
			}); 
			 
			
		} // for
		
		latch.await();
		
		
	}

}
