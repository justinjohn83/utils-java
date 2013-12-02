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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 * Represents a calendar event that occurs on a specific day for a specific duration of time
 * @author jmontgomery
 *
 */
public class CalendarEvent implements Comparable<CalendarEvent>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Weekday dayOfWeek;
	private TimeInterval timeOfDay;
	

	public interface CalendarEventFormatter {
		String formatWeekday(Weekday day);
		String formatTime(Date startTime,Date endTime);
	}
	
	public static class DefaultCalendarEventFormatter implements CalendarEventFormatter {

		public String formatWeekday(Weekday day) {
			return day.getShortestName();
		}

		public String formatTime(Date startTime,Date endTime) {
			DateFormat df = new SimpleDateFormat("h:mm a");
			
			StringBuilder s = new StringBuilder(64);
			
			String start = df.format(startTime);
			String end = df.format(endTime);
			
			// Both AM and PM
			if(start.substring(start.length() - 2,start.length()).equals(
					end.substring(end.length() - 2,end.length()))) {
				s.append(start.substring(0,start.length() - 3));
			}
			else {
				s.append(start);
			}
			s.append(" - ");
			s.append(end);
			
			return s.toString();
		}
		
	}
	public CalendarEvent(Weekday dayOfWeek,Date startTime,Date endTime) {
		if(dayOfWeek == null) {
			throw new NullPointerException("dayOfWeek");
		}
		if(startTime == null) {
			throw new NullPointerException("startTime");
		}
		if(endTime == null) {
			throw new NullPointerException("endTime");
		}
		this.dayOfWeek = dayOfWeek;
		this.timeOfDay = new TimeInterval(getTimeOfDay(startTime),getTimeOfDay(endTime));
		
	}
	static long getTimeOfDay(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		long t = 0;
//		t += c.get(Calendar.MILLISECOND);
		t += c.get(Calendar.SECOND) ;
		t += c.get(Calendar.MINUTE) * 60 ;
		t += c.get(Calendar.HOUR_OF_DAY) * 60 * 60;
		
		return t;
	}
	
	public boolean intersects(CalendarEvent otherEvent) {
		if(otherEvent == null) {
			return false;
		}
		return this.dayOfWeek.equals(otherEvent.dayOfWeek) &&
			   this.timeOfDay.intersects(otherEvent.timeOfDay);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dayOfWeek == null) ? 0 : dayOfWeek.hashCode());
		result = prime * result
				+ ((timeOfDay == null) ? 0 : timeOfDay.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalendarEvent other = (CalendarEvent) obj;
		if (dayOfWeek != other.dayOfWeek)
			return false;
		if (timeOfDay == null) {
			if (other.timeOfDay != null)
				return false;
		} else if (!timeOfDay.equals(other.timeOfDay))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(128);
		CalendarEventFormatter formatter = new DefaultCalendarEventFormatter();
		
		builder.append(formatter.formatWeekday(this.dayOfWeek));
		builder.append(' ');
		builder.append(formatter.formatTime(this.timeOfDay.getStartDate(),this.timeOfDay.getEndDate()));
		return builder.toString();
	}
	
	public static String format(Collection<CalendarEvent> events,CalendarEventFormatter formatter) {
		if(MiscUtils.isEmpty(events)) {
			return "";
		}
		if(formatter == null) {
			throw new NullPointerException("formatter");
		}
		
		events = new TreeSet<CalendarEvent>(events);
		List<CalendarEvent> eventList = new ArrayList<CalendarEvent>(events);
		StringBuilder buf = new StringBuilder(256);

		for(ListIterator<CalendarEvent> it = eventList.listIterator(); it.hasNext();) {
			
			CalendarEvent next = it.next();
			if(buf.length() > 0) {
				buf.append(",");
			}
			buf.append(formatter.formatWeekday(next.getDayOfWeek()));
			
			// see how many occur at same time
			while(it.hasNext()) {
				CalendarEvent e = it.next();
				if(next.getTimeOfDay().equals(e.getTimeOfDay())) {
					buf.append(formatter.formatWeekday(e.getDayOfWeek()));
				}
				else {
					it.previous();
					break;
				}
			}
			buf.append(' ');
			buf.append(formatter.formatTime(next.getTimeOfDay().getStartDate(),next.getTimeOfDay().getEndDate()));
			
			
		}
		
		return buf.toString();
		
		
	}
	
	public static String format(Collection<CalendarEvent> events) {
		return format(events,new DefaultCalendarEventFormatter());
	}
	
	public int compareTo(CalendarEvent other) {
		int result = this.dayOfWeek.compareTo(other.dayOfWeek);
		if(result != 0) {
			return result;
		}
		return this.timeOfDay.compareTo(other.timeOfDay);
	}
	public Weekday getDayOfWeek() {
		return dayOfWeek;
	}
	public TimeInterval getTimeOfDay() {
		return timeOfDay;
	}
}
