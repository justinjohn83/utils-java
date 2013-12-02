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

import java.util.concurrent.TimeUnit;

/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class Timer
{
	private long start;
	private long sum;
	private boolean running;
	
	
	private final TimeUnit unit;
	
	public Timer(TimeUnit unit)
	{
		if(unit == null)
			throw new NullPointerException("unit");
		this.unit = unit;
	}
	
	public void reset()
	{
		this.start = this.sum = 0;
		this.running = false;
	}
	public void start()
	{
		if(!this.running)
		{
			this.start = System.nanoTime();
			this.running = true;
		}
	}
	
	public void stop()
	{
		if(this.running)
		{
			this.running = false;
			
			long current = System.nanoTime();
			this.sum += current - start;
		}
		
	}
	
	public long getDelta()
	{
		return convertTime(sum, TimeUnit.NANOSECONDS, this.unit);
	}

        public double getElapsed()
        {
            return convertTimeDouble(sum, TimeUnit.NANOSECONDS, this.unit);
        }

        public boolean isRunning() { return running; }
	
	public static long convertTime(long time,TimeUnit input,TimeUnit output)
	{
		double conv = convertTimeDouble(time,input,output);
		long result = Math.round(conv);
		return result > 0 ? result : 1;
	}

        public static double convertTimeDouble(double time,TimeUnit input,TimeUnit output)
        {
            return time * getMultiplier(output) / getMultiplier(input);
        }
	
	private static double getMultiplier(TimeUnit unit)
	{
		switch(unit)
		{
			case MICROSECONDS: return 1.0e6;
			case MILLISECONDS: return 1.0e3;
			case NANOSECONDS: return 1.0e9;
			case SECONDS: return 1.0;
			default: throw new IllegalArgumentException("unit=" + unit);
			
		}
	}
}
