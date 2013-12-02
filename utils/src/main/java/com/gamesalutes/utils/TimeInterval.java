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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Interval of time defined as [start,end).
 *
 * @author jmontgomery
 */
public final class TimeInterval implements Comparable<TimeInterval>,Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final long start;
    private final long end;
    
    private static final long ONE_DAY_SECS = 1000 * 60 * 60 * 24;


    public TimeInterval(Date start,Date end)
    {
        this.start = start.getTime() / 1000;
        this.end = end.getTime() / 1000;
    }
    public TimeInterval(long start,long end)
    {
        this.start = start;
        this.end = end;
    }

    public long getStart() { return start; }
    public long getEnd() { return end; }
    
    private static Date asDate(long time) {
    	Calendar c = Calendar.getInstance();
    	if(time < ONE_DAY_SECS) {
    		c.setTime(new Date());
    		c.set(Calendar.HOUR_OF_DAY, (int)(time / (3600)));
    		c.set(Calendar.MINUTE, (int)((time / 60) % 60));
    		c.set(Calendar.SECOND,(int)(time % 60));
    		c.set(Calendar.MILLISECOND, 0);
    		
    	}
    	else {
    		c.setTimeInMillis(time*1000);
    	}
    	return c.getTime();
    }
    
    public Date getStartDate() { return asDate(start); }
    public Date getEndDate() { return asDate(end); }

    public int compareTo(TimeInterval t)
    {
        if(start < t.start)
            return -1;
        else if(start > t.start)
            return 1;
        if(end < t.end)
            return -1;
        else if(end > t.end)
            return 1;
        return 0;
    }

    public boolean intersects(TimeInterval t)
    {
        return DateUtils.intersects(start, end, t.start, t.end);
    }
    public boolean intersects(long time)
    {
        if(time < start) return false;
        if(time >= end) return false;
        return true;
    }
    @Override
    public boolean equals(Object o)
    {
       if(this == o) return true;
       if(!(o instanceof TimeInterval)) return false;
       TimeInterval ti = (TimeInterval)o;
       return start == ti.start && end == ti.end;
    }

    @Override
    public int hashCode()
    {
        int result = 17;
        result = 31 * result + MiscUtils.hashLong(start);
        result = 31 * result + MiscUtils.hashLong(end);
        return result;
    }

    @Override
    public String toString()
    {
        return "[" + start + "," + end + "]";
    }


    public List<TimeInterval> inverse(long start,long end)
    {
        List<TimeInterval> inverse = new ArrayList<TimeInterval>(2);

        if(start < this.start)
        {
            inverse.add(new TimeInterval(start,this.start));
        }
        if(end > this.end)
        {
            inverse.add(new TimeInterval(this.end,end));
        }

        return inverse;
    }

    public long getTotalYears()
    {
        return getTotalDays() / 365;
    }
    public long getTotalWeeks()
    {
        return getTotalDays() / 7;
    }

    public long getTotalDays()
    {
        return getTotalHours() / 24;
    }

    public long getTotalHours()
    {
        return getTotalMinutes() / 60;
    }

    public long getTotalMinutes()
    {
        return getTotalSeconds() / 60;
    }

    public long getTotalSeconds()
    {
        return end - start;
    }

    public long getYears()
    {
        return getTotalYears();
    }
    public long getWeeks()
    {
        return getTotalWeeks() % 52;
    }
    public long getDays()
    {
        return getTotalDays() % 7;
    }
    public long getHours()
    {
        return getTotalHours() % 24;
    }
    public long getMinutes()
    {
        return getTotalMinutes() % 60;
    }

    public long getSeconds()
    {
        return getTotalSeconds() % 60;
    }
    public String toStringPretty()
    {
        StringBuilder s = new StringBuilder(512);

        long dur = end - start;

        // years
        long t = dur / (52 * 7 * 24 * 60 * 60);
        if(t > 0)
        {
            s.append(t).append("y");
            dur -= t * (52 * 7 * 24 * 60 * 60);
        }
        if(dur == 0)
            return s.toString();

        // weeks
        t = dur / (7 * 24 * 60 * 60);
        if(t > 0)
        {
            if(s.length() > 0)
                s.append(" ");
            s.append(t).append("w");
            dur -= t * (7 * 24 * 60 * 60);
        }
        if(dur == 0)
            return s.toString();

        // days
        t = dur / (24 * 60 * 60);
        if(t > 0)
        {
            if(s.length() > 0)
                s.append(" ");
            s.append(t).append("d");
            dur -= t * (24 * 60 * 60);
        }
        if(dur == 0)
            return s.toString();

        // hours
        t = dur / (60 * 60);
        if(t > 0)
        {
            if(s.length() > 0)
                s.append(" ");
            s.append(t).append("h");
            dur -= t * (60 * 60);
        }
        if(dur == 0)
            return s.toString();

        // minutes
        t = dur / 60;
        if(t > 0)
        {
            if(s.length() > 0)
                s.append(" ");
            s.append(t).append("m");
            dur -= t * 60;
        }
        if(dur == 0)
            return s.toString();

        // seconds
        t = dur;
        if(s.length() > 0)
                s.append(" ");
        s.append(t).append("s");

        return s.toString();

    }

}
