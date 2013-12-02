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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Manager for executors.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ThreadManager implements Disposable
{
	private static final ThreadManager instance = new ThreadManager();
	
	private ExecutorService executor;
	
	public ThreadManager()
	{
		
	}
	
	private void init()
	{
		if(executor == null)
		{
			int numThreads = Runtime.getRuntime().availableProcessors();
			if(numThreads > 1)
				executor = Executors.newFixedThreadPool(numThreads);
			else
				executor = new CurrentThreadExecutor();
		}
	}
	
	
	public static ThreadManager getInstance()
	{
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.Disposable#dispose()
	 */
	public synchronized void dispose()
	{
		if(executor != null)
		{
			ExecutorService currentExec = executor;
			executor = null;
			
			currentExec.shutdown();
		}
	}
	
	
	public synchronized <T> Future<T> submit(Callable<T> task)
	{
		init();
		
		return executor.submit(task);
	}
	
	public synchronized Future<?> submit(Runnable task)
	{
		init();
		
		return executor.submit(task);
	}
	
	public synchronized <T> Future<T> submit(Runnable task,T result)
	{
		init();
		
		return executor.submit(task,result);
	}
	
	
}
