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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;


/**
 * Contains utility methods for manipulating dates.
 * 
 * @author Justin Montgomery
 * @version $Id: DateUtils.java 2439 2010-11-11 17:15:07Z jmontgomery $
 *
 */
public final class DateUtils
{
	private static final SimpleDateFormat regDateFormat;
	private static final SimpleDateFormat fileDateFormat;
	private static final SimpleDateFormat shortDateFormat;
	
	//date pattern formats
	/**
	 * Date format to use in general for non-file names.
	 * 
	 */
	public static final String REG_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	/**
	 * Date format to use for file names that avoids invalid file characters.
	 * 
	 */
	public static final String FILE_DATE_PATTERN = "yyyy-MM-dd'T'HH-mm-ssZ";
	
	/**
	 * A short date pattern.
	 * 
	 */
	public static final String SHORT_DATE_PATTERN = "MM/dd/yy";
	
	
	/**
	 * Delimiter to use for separating file names from a date appended time stamp.
	 * 
	 */
	public static final String FILE_DATE_DELIM = "__";
	
	static
	{
		regDateFormat = new SimpleDateFormat(REG_DATE_PATTERN);
		fileDateFormat = new SimpleDateFormat(FILE_DATE_PATTERN);
		shortDateFormat = new SimpleDateFormat(SHORT_DATE_PATTERN);
	}

	private DateUtils() {}
	
	/*
	private static String getTime(boolean noColon)
	{
		if(!noColon)
			dateFormat.applyPattern(regDatePattern);
		else
			dateFormat.applyPattern(fileDatePattern);
			
		return dateFormat.format(new Date());
	}
	*/

	/**
	 * Returns a stamp of current time with hms separated by '-'
	 * instead of a ':'.
	 * 
	 * @return the file time stamp string
	 * 
	 */
	public synchronized static String getFileTimeStamp()
	{
		//return getTime(true);
		return fileDateFormat.format(new Date());
	}

	/**
	 * Returns a stamp of the current time in a standard ISO format
	 * @return the time stamp string
	 */
	public synchronized static String getTimeStamp()
	{
		//return getTime(false);
		return regDateFormat.format(new Date());
	}
	
	/**
	 * Converts the given dateText into date String in standard format.
	 * 
	 * @param dateText String version of date
	 * @param oldFormat current format of this date
	 * @return date string in standard format
	 */
	public synchronized static String convertDate(
			String dateText, String oldFormat) throws ParseException
	{
		SimpleDateFormat oldDateFormat = new SimpleDateFormat(oldFormat);
		Date date = oldDateFormat.parse(dateText);
		return regDateFormat.format(date);
		
	}
	
	/**
	 * Returns current date in dd/MM/yy format.
	 * 
	 * @return the short date
	 */
	public synchronized static String getShortDate()
	{
		return shortDateFormat.format(new Date());
	}
	
	/**
	 * Determines whether [start,end] intersects [rangeStart,rangeEnd].  This
	 * intersection occurs if any time in [start,end] intersects some time
	 * in [rangeStart,rangeEnd].
	 * 
	 * @param start the start date to check
	 * @param end the end date to check
	 * @param rangeStart the start of the queried range
	 * @param rangeEnd the end of the queried range
	 * @return <code>true</code> if an intersection occurs and 
	 *         <code>false</code> otherwise
	 * @throws IllegalArgumentException if <code>start</code> &gt end or 
	 * 		   <code>rangeStart</code> &gt <code>rangeEnd</code>
	 */
	public static boolean intersects(Date start,Date end,Date rangeStart,Date rangeEnd)
	{
		if(start.compareTo(end) > 0)
		{
			synchronized(DateUtils.class)
			{
				throw new IllegalArgumentException("start = " + 
						shortDateFormat.format(start) + "; end = " + 
						shortDateFormat.format(end));
			}
		}
		if(rangeStart.compareTo(rangeEnd) > 0)
		{
			synchronized(DateUtils.class)
			{
				throw new IllegalArgumentException("rangeStart = " + 
						shortDateFormat.format(rangeStart) + "; rangeEnd = " + 
						shortDateFormat.format(rangeEnd));
			}
		}
		// have to consider six cases: all combos of overlap
		// case 1: both start and end before rangeStart: return false
		// case 2: start before rangeStart and end in [rangeStart,rangeEnd]: return true
		// case 3: both start and end after rangeEnd: return false
		// case 4: start and end within [rangeStart,rangeEnd]: return true
		// case 5: start before rangeStart and end after rangeEnd: return true
		// case 6: start within [rangeStart,rangeEnd] but end outside rangeEnd: return true
		if(start.compareTo(rangeStart) >= 0)
		{
			if(start.compareTo(rangeEnd) <= 0)
				return true; // cases 4,6
			else
				return false; // case 3
		}
		else //start comes before range
		{
			if(end.compareTo(rangeStart) >= 0)
				return true; // cases 2,5
			else
				return false; // case 1
		}
	}
	
	/**
	 * Converts a unix time stamp to a <code>Date</code>
	 * @param time unix time
	 * @return the <code>Date</code>
	 */
	public static Date unixtimeToDate(long time)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time * 1000);
		return c.getTime();
	}
	
	/**
	 * Converts a <code>Date</code> to a unix time stamp
	 * @param d the <code>Date</code>
	 * @return the unix time stamp
	 */
	public static long dateToUnixtime(Date d)
	{
		return d.getTime() / 1000;
	}

        // returns whether the given date ranges overlap
        public static boolean intersects(long tstart1,long tend1,long tstart2,long tend2)
        {
		if(tstart1 > tend1)
		{
			return false;
		}
		if(tstart2 > tend2)
		{
			return false;
		}
		// have to consider six cases: all combos of overlap
		// case 1: both tstart1 and tend1 before tstart2: return false
		// case 2: tstart1 before tstart2 and tend1 in [tstart2,tend2]: return true
		// case 3: both tstart1 and tend1 after tend2: return false
		// case 4: tstart1 and tend1 within [tstart2,tend2]: return true
		// case 5: tstart1 before tstart2 and tend1 after tend2: return true
		// case 6: tstart1 within [tstart2,tend2] but tend2 outside rangeEnd: return true
		if(tstart1 >= tstart2)
		{
			if(tstart1 <= tend2)
				return true; // cases 4,6
			else
				return false; // case 3
		}
		else //tstart1 comes before tstart2
		{
			if(tend1 >= tstart2)
				return true; // cases 2,5
			else
				return false; // case 1
		}
            }

}
