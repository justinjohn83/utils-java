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
/* Copyright 2008 - 2010 University of Chicago
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jmontgomery
 */
public enum Weekday
{
    Sunday("Sun","Su"),
    Monday("Mon","M"),
    Tuesday("Tues","Tu"),
    Wednesday("Wed","W"),
    Thursday("Thurs","Th"),
    Friday("Fri","F"),
    Saturday("Sat","Sa");

    
	private final String name;
	private final String shortName;
	
	private Weekday(String name,String shortName)
	{
		this.name = name;
		this.shortName = shortName;
	}
	
	public long getSeconds()
	{
		return ordinal() * (24 * 60 * 60);
	}
	
	public static List<Weekday> getWeekdays()
	{
		List<Weekday> days = new ArrayList<Weekday>(5);
		Collections.addAll(days, Monday,Tuesday,Wednesday,Thursday,Friday);
		return days;
	}
	public static List<Weekday> getWeekend()
	{
		List<Weekday> days = new ArrayList<Weekday>(2);
		Collections.addAll(days, Saturday,Sunday);
		return days;
	}
	
	public String getShortName()
	{
		return name;
	}
	public String getShortestName()
	{
		return shortName;
	}
    public static Weekday valueOf(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int dow = c.get(Calendar.DAY_OF_WEEK) - 1;
        return Weekday.values()[dow];
    }

    public Date next(Date d)
    {
        // must be after today's date!
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.DAY_OF_WEEK, ordinal() + 1);
        Date newTime = c.getTime();

        // before today's date so go forward one week
        if(newTime.compareTo(d) < 0)
            c.add(Calendar.DAY_OF_MONTH,7);
        return c.getTime();
    }

    public Date prev(Date d)
    {
        // must be before today's date!
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.DAY_OF_WEEK, ordinal() + 1);
        Date newTime = c.getTime();

        // after today's date so go backward one week
        if(newTime.compareTo(d) > 0)
            c.add(Calendar.DAY_OF_MONTH,-7);
        return c.getTime();
    }

    public static Weekday parse(String s)
    {
        if(s == null)
            throw new NullPointerException("s");
        if(StringUtils.caseInsensitiveStartsWith(s, "Su"))
            return Sunday;
        if(StringUtils.caseInsensitiveStartsWith(s,"M"))
            return Monday;
        if(StringUtils.caseInsensitiveStartsWith(s,"Tu"))
            return Tuesday;
        if(StringUtils.caseInsensitiveStartsWith(s,"W"))
            return Wednesday;
        if(StringUtils.caseInsensitiveStartsWith(s,"Th"))
            return Thursday;
        if(StringUtils.caseInsensitiveStartsWith(s,"F"))
            return Friday;
        if(StringUtils.caseInsensitiveStartsWith(s, "Sa"))
            return Saturday;

        throw new IllegalArgumentException("s=" + s);

    }

    public Weekday next(int skip)
    {
        int ordinal;

        int len = values().length;

        ordinal = (ordinal() + skip) % len;
        if(ordinal < 0)
            ordinal += len;

        return values()[ordinal];
    }
}
