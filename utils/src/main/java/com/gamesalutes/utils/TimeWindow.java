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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 *
 * @author jmontgomery
 */
public final class TimeWindow implements Cloneable,Comparable<TimeWindow>
{

    private Date startDate;
    private Date endDate;


    private static final String DATE_FMT = "MM/dd/yyyy";
    private static final String TIME_FMT = "HH:mm:ss";

    private TimeInterval time;

    private Weekday weekStart;
    private Weekday weekEnd;

    private boolean include;

    // interval between adjacent weeks where entry reoccurs in secs
    private long repeatTimeSecs;

    private final Date now;
    private final Date tomorrow;


    private boolean empty = true;

    private Set<TimeWindow> children = new TreeSet<TimeWindow>();

    public TimeWindow()
    {
        now = this.stripTime(new Date(),true);
        tomorrow = this.stripTime(this.incrementDate(now),false);
    }

    public boolean isInclude()
    {
        return include;
    }

    public Date getStartDate()
    {
        return startDate;
    }
    public Date getEndDate()
    {
        return endDate;
    }
    public TimeInterval getDuration()
    {
        return time;
    }

    public Weekday getWeekStart()
    {
        return weekStart;
    }

    public Weekday getWeekEnd()
    {
        return weekEnd;
    }

    public long getRepeatTimeSeconds()
    {
        return repeatTimeSecs;
    }
    @Override
    public TimeWindow clone()
    {
        try
        {
            TimeWindow copy = (TimeWindow)super.clone();
            copy.children = MiscUtils.deepCollectionCopy(copy.children);

            return copy;
        }
        catch(CloneNotSupportedException e)
        {
            throw new AssertionError(e);
        }
    }

    public TimeWindow add(boolean include,
            Date startDate,Date endDate,Date startTime,Date endTime,Weekday wstart,Weekday wend,long repeatTimeSecs)
    {
        empty = false;

        if(this.time == null)
        {
            this.include = include;
            
            if(startDate != null)
                this.startDate = stripTime(startDate,true);
            if(endDate != null)
                this.endDate = stripTime(endDate,false);
            this.time = new TimeInterval(getSecondsOfDay(startTime),getSecondsOfDay(endTime));
            this.weekStart = wstart;
            this.weekEnd = wend;
            this.repeatTimeSecs = repeatTimeSecs;

        }
        else
        {
            TimeWindow child = new TimeWindow();
            child.add(include, startDate, endDate, startTime, endTime, wstart, wend,repeatTimeSecs);
            children.add(child);
        }

        return this;
    }

    public TimeWindow add(TimeWindow tw)
    {
        empty = false;

        if(time == null)
        {
            this.include = tw.include;
            this.startDate = tw.startDate;
            this.endDate = tw.endDate;
            this.time = tw.time;
            this.weekStart = tw.weekStart;
            this.weekEnd = tw.weekEnd;
        }
        else
        {
            children.add(tw.clone());
        }
        return this;
    }

    public boolean intersects(Date time)
    {
        if(this.time == null)
            return true;
        
        int result;
        boolean intersects = true;
        if(this.startDate != null)
        {
            result = time.compareTo(startDate);
            if(result < 0) intersects = false;
        }
        if(intersects && this.endDate != null)
        {
            result = time.compareTo(this.endDate);
            if(result > 0) intersects = false;
        }

        if(intersects && this.weekStart != null)
        {
            result = Weekday.valueOf(time).compareTo(this.weekStart);
            if(result < 0) intersects = false;
        }
        if(intersects && this.weekEnd != null)
        {
            result = Weekday.valueOf(time).compareTo(this.weekEnd);
            if(result > 0) intersects = false;
        }
        
        if(intersects)
        {
            intersects = this.time.intersects(getSecondsOfDay(time));
            if(!include) intersects = !intersects;
        }

        // children
        if(!intersects)
        {
            for(TimeWindow sw : children)
            {
                intersects = sw.intersects(time);
                if(intersects)
                    return true;
            }
        }

        return true;


    }

    public boolean isEmpty()
    {
        return empty;
    }
    
    private int compareTime(Date d1,Date d2)
    {
        return getSecondsOfDay(d1) - getSecondsOfDay(d2);
    }

    private int getSecondsOfDay(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int s = c.get(Calendar.SECOND) + c.get(Calendar.MINUTE) * 60 +
                  c.get(Calendar.HOUR_OF_DAY) * 60 * 60;
        return s;
    }
    private int compareDate(Date d1,Date d2)
    {
        return stripTime(d1,true).compareTo(stripTime(d2,true));
    }

    private Date timeToDate(long t)
    {
        Calendar c = Calendar.getInstance();
        int h = (int)t / (60 * 60);
        int m = (int)t % (60 * 60) / 60;
        int s = (int)t % 60;

        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE,m);
        c.set(Calendar.SECOND, s);
        
        return c.getTime();
    }
    private Date stripTime(Date d,boolean midnight)
    {
        if(d == null) return null;
        
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, midnight ? 0 : 23);
        c.set(Calendar.MINUTE, midnight ? 0 : 59);
        c.set(Calendar.SECOND, midnight ? 0 : 59);
        c.set(Calendar.MILLISECOND,midnight ? 0 : 999);

        return c.getTime();
    }

    private Date incrementDate(Date d)
    {
        if(d == null) return null;

        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.DAY_OF_YEAR,c.get(Calendar.DAY_OF_YEAR) + 1);

        return c.getTime();
    }

    private Date incrementDate(Date d,long secs)
    {
        if(d == null) return null;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(d.getTime() + secs * 1000);
        return c.getTime();
    }
    private int compareDayofWeek(Date d1,Date d2)
    {
        return Weekday.valueOf(d1).compareTo(Weekday.valueOf(d2));
    }


    private boolean dayIntersection(Date time)
    {
        int result;
        boolean intersects = true;
        if(this.startDate != null)
        {
            result = time.compareTo(this.startDate);
            if(result < 0) intersects = false;
        }
        if(intersects && this.endDate != null)
        {
            result = time.compareTo(this.endDate);
            if(result > 0) intersects = false;
        }

        if(intersects && this.weekStart != null)
        {
            result = Weekday.valueOf(time).compareTo(this.weekStart);
            if(result < 0) intersects = false;
        }
        if(intersects && this.weekEnd != null)
        {
            result = Weekday.valueOf(time).compareTo(this.weekEnd);
            if(result > 0) intersects = false;
        }
        return intersects;
    }

    private Date getMinTime(Date time)
    {
        if(startDate != null)
            time = new Date(startDate.getTime());
        if(weekStart != null)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(time);
            c.set(Calendar.DAY_OF_WEEK, weekStart.ordinal() + 1);
            time = c.getTime();
        }
        if(this.time != null)
        {
            time = setSecondsOfDay(time,(int)this.time.getStart());
        }

        return time;
    }
    private Date getMaxTime(Date time)
    {
        if(endDate != null)
        {
            time = new Date(endDate.getTime());
        }
        if(weekEnd != null)
        {
            time = weekEnd.next(time);
        }
        if(this.time != null)
        {
            time = setSecondsOfDay(time,(int)this.time.getEnd());
        }

        return time;
    }
    public Date nextTime(Date time,boolean startpoint)
    {
        if(this.time == null)
            return null;

        Date next = null;
        boolean intersects = false;


        Date maxTime = getMaxTime(time);

        while(time.compareTo(maxTime) <= 0 && !(intersects = dayIntersection(time)))
        {
            time = incrementDate(time);
            time = stripTime(time,true);
        }

        if(intersects)
        {
            if(startpoint)
            {
                intersects = this.time.intersects(getSecondsOfDay(time));

                if(intersects)
                {
                    if(include)
                        next = time;
                    else
                        next = setSecondsOfDay(time,(int)this.time.getEnd());
                }
                else
                {
                    if(include)
                    {
                        next = setSecondsOfDay(time,(int)this.time.getStart());
                        if(next.compareTo(time) < 0)
                        {
                            do
                            {
                                time = incrementDate(time);
                            }
                            while(time.compareTo(maxTime) <= 0 && !dayIntersection(time));

                            next = setSecondsOfDay(time,(int)this.time.getStart());
                        }
                    }
                    else
                        next = time;
                }
            } // startpoint
            // endpoint
            else
            {
                if(include)
                    next = setSecondsOfDay(time,(int)this.time.getEnd());
                // intersecting at end point of window
                // get it on start of next day or current day
                else
                {
                    next = setSecondsOfDay(time,(int)this.time.getStart());
                    if(next.compareTo(time) < 0)
                    {
                        do
                        {
                            time = incrementDate(time);
                        }
                        while(time.compareTo(maxTime) <= 0 && !dayIntersection(time));

                        next = setSecondsOfDay(time,(int)this.time.getStart());
                    }
                }
            } // endpoint
        } // intersects

        List<Date> times = new ArrayList<Date>();
        if(next != null) times.add(next);

        // children
        for(TimeWindow sw : children)
        {
            next = sw.nextTime(time,startpoint);
            if(next != null)
                times.add(next);
        }
        if(!times.isEmpty())
        {
            Collections.sort(times);
            return times.get(0);
        }

        return null;
    }

    private Date setSecondsOfDay(Date d,int t)
    {
        Calendar c = Calendar.getInstance();
        if(d != null)
            c.setTime(d);
        int h = t / (60 * 60);
        int m = t % (60 * 60) / 60;
        int s = t % 60;
        c.set(Calendar.HOUR_OF_DAY,h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, s);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }
    public List<TimeInterval> getInterval(TimeInterval total)
    {

        Date tstart = new Date(total.getStart() * 1000);
        Date tend = new Date(total.getEnd() * 1000);

        List<TimeInterval> intervals = new ArrayList<TimeInterval>();


        if(this.time != null)
        {
            Date curr = new Date(tstart.getTime());
            while(curr.compareTo(tend) < 0)
            {
                Date first = nextTime(curr,true);
                if(first == null || first.compareTo(tend) >= 0)
                    break;
                Date second = nextTime(first,false);
                if(second == null || second.compareTo(tend) > 0)
                {
                    second = tend;
                }
                intervals.add(new TimeInterval(first,second));
                curr = second;
                if(Weekday.valueOf(curr) == weekStart)
                {
                    curr = this.incrementDate(curr,this.repeatTimeSecs);
                }
            }
        }
        else
        {
            Collections.addAll(intervals,total);
        }


        // create interval based on whole total
//        if(intervals.isEmpty())
//            intervals.add(total);

        return intervals;


    }

    @Override
    public String toString()
    {

        StringBuilder s = new StringBuilder(512);

        if(time != null)
        {

            DateFormat dateFmt = new SimpleDateFormat(DATE_FMT);
            DateFormat timeFmt = new SimpleDateFormat(TIME_FMT);
//            s.append("[").append(include ? "inclusive:" : "exclusive:");
            if(startDate != null || endDate != null)
            {
                s.append(" [");
                if(startDate != null)
                    s.append(dateFmt.format(startDate));
                else
                    s.append("?");
                s.append("-");
                if(endDate != null)
                    s.append(dateFmt.format(endDate));
                else
                    s.append("?");
                s.append("]");
            }
            if(weekStart != null || weekEnd != null)
            {
                s.append(" [");
                if(weekStart != null)
                    s.append(weekStart);
                else
                    s.append("?");
                s.append("-");
                if(weekEnd != null)
                    s.append(weekEnd);
                else
                    s.append("?");
                s.append(" ");
            }
            else
            {
                s.append(" [");
            }
            
            s.append(timeFmt.format(setSecondsOfDay(null,(int)time.getStart())));
            s.append("-");
            s.append(timeFmt.format(setSecondsOfDay(null,(int)time.getEnd())));
            s.append("]");

            if(repeatTimeSecs > 0)
            {
                s.append(" [").append(new TimeInterval(0,this.repeatTimeSecs).toStringPretty()).append("]");
            }

            return s.toString();
        }

        for(TimeWindow w : this.children)
        {
            s.append(",");
            s.append(w.toString());
        }

        return s.toString();

    }

    /**
     * If this time window composed of many split into individual components
     */
    public List<TimeWindow> split()
    {
        List<TimeWindow> windows = new ArrayList<TimeWindow>();

        windows.add(this);
        
        if(!MiscUtils.isEmpty(children))
        {
            for(TimeWindow w : children)
            {
                windows.addAll(w.split());
            }
        }

        return windows;
    }

    /**
     * Returns excluding if including and vice versa.
     */
    public TimeWindow inverse()
    {
        TimeWindow inverse = new TimeWindow();

        boolean include = !this.include;
        Date startDate = this.startDate;
        Date endDate = this.endDate;
        Weekday weekStart = this.weekStart;
        Weekday weekEnd = this.weekEnd;


        List<TimeInterval> time = this.time.inverse(0, 24 * 60 * 60 - 1);


        if(weekStart != null || weekEnd != null)
        {


            if(weekStart != null && weekEnd != null)
            {

                for(TimeInterval t : time)
                {
                    inverse.add(include,
                            startDate,
                            endDate,
                            this.timeToDate(t.getStart()),
                            this.timeToDate((t.getEnd())),
                            weekStart,
                            weekEnd, repeatTimeSecs);
                }
                
                weekStart = this.weekEnd.next(1);
                weekEnd = this.weekStart.next(-1);
                
                inverse.add(include,
                            startDate,
                            endDate,
                            this.timeToDate(0),
                            this.timeToDate(60 * 60 * 24 - 1),
                            weekStart,
                            weekEnd,
                            repeatTimeSecs);

            }
            else
            {

                // include all other days
                EnumSet<Weekday> days = EnumSet.complementOf(EnumSet.of(this.weekStart != null ? this.weekStart : this.weekEnd));

                for(Weekday d : days)
                {
                    inverse.add(include,
                    startDate,
                    endDate,
                    this.timeToDate(0),
                    this.timeToDate(60 * 60 * 24 - 1),
                    d,
                    null, repeatTimeSecs);
                }
            }
        }
        else
        {
            for(TimeInterval t : time)
            {
                inverse.add(include,
                        startDate,
                        endDate,
                        this.timeToDate(t.getStart()),
                        this.timeToDate((t.getEnd())),
                        weekStart,
                        weekEnd, repeatTimeSecs);
            }
        }



        for(TimeWindow w : children)
            inverse.add(w.inverse());

        return inverse;

    }

    public int compareTo(TimeWindow o)
    {
        int result;

        Date mStart = this.startDate != null ? this.startDate : this.now;
        Date oStart = o.startDate != null ? o.startDate : o.now;

        result = mStart.compareTo(oStart);
        if(result != 0)
            return result;

       result = MiscUtils.safeCompareTo(time, o.time, true);
       if(result != 0)
           return result;

       result = MiscUtils.safeCompareTo(weekStart, o.weekStart,true);
       if(result != 0)
           return result;

       result = MiscUtils.safeCompareTo(weekEnd,o.weekEnd,true);
       if(result != 0)
           return result;

       Date mEnd = this.endDate != null ? this.endDate : this.tomorrow;
       Date oEnd = o.endDate != null ? o.endDate : o.tomorrow;
       result = mEnd.compareTo(oEnd);
       if(result != 0)
           return result;

       if(this.include != o.include)
           return !this.include ? -1 : 1;

       if(this.repeatTimeSecs < o.repeatTimeSecs)
           return -1;
       else if(this.repeatTimeSecs > o.repeatTimeSecs)
           return 1;


       Iterator<TimeWindow> mIt = this.children.iterator();
       Iterator<TimeWindow> oIt = o.children.iterator();

       while(mIt.hasNext() && oIt.hasNext())
       {
           result = mIt.next().compareTo(oIt.next());
           if(result != 0)
               return result;
       }

       return this.children.size() - o.children.size();
       
    }


    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(!(o instanceof TimeWindow))
            return false;

        return this.compareTo((TimeWindow)o) == 0;
    }

    @Override
    public int hashCode()
    {
        int result = 17;

        result = 31 * result + (startDate != null ? startDate.hashCode() : now.hashCode());
        result = 31 * result + MiscUtils.safeHashCode(time);
        result = 31 * result + MiscUtils.safeHashCode(weekStart);
        result = 31 * result + MiscUtils.safeHashCode(weekEnd);
        result = 31 * result + (endDate != null ? endDate.hashCode() : tomorrow.hashCode());
        result = 31 * result + (include ? 1 : 0);

        result = 31 * result + children.hashCode();

        return result;
    }



}
