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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class CalendarEventTest {

	@Test
	public void testCreateEvent() {
		CalendarEvent evt = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(10,50));
		assertEquals("M 10:00 - 10:50 AM",evt.toString());
	}
	
	@Test
	public void testCreateEvent2() {
		CalendarEvent evt = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(13,0));
		assertEquals("M 10:00 AM - 1:00 PM",evt.toString());
	}
	
	@Test
	public void testIntersects() {
		CalendarEvent evt1 = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(11,0));
		CalendarEvent evt2 = new CalendarEvent(Weekday.Tuesday,createTime(10,0),createTime(11,0));
		
		assertFalse(evt1.intersects(evt2));
		assertFalse(evt1.equals(evt2));

		
		evt2 = new CalendarEvent(Weekday.Monday,createTime(10,30),createTime(13,0));
		assertTrue(evt1.intersects(evt2));
		
		evt2 = new CalendarEvent(Weekday.Monday,createTime(10,30),createTime(10,45));
		assertTrue(evt1.intersects(evt2));

		evt2 = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(11,0));
		assertTrue(evt1.intersects(evt2));
		assertTrue(evt1.equals(evt2));

		
		
		
		
	}
	
	@Test
	public void testSortEvents1() {
		
		CalendarEvent event2 = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(11,0));
		CalendarEvent event1 = new CalendarEvent(Weekday.Monday,createTime(15,30),createTime(17,0));
		
		List<CalendarEvent> expected = Arrays.asList(event2,event1);
		
		List<CalendarEvent> actual = new ArrayList<CalendarEvent>();
		Collections.addAll(actual, event1,event2);
		
		Collections.sort(actual);
		
		assertEquals(expected,actual);
		
	}
	
	@Test
	public void testSortEvents2() {
		
		CalendarEvent event2 = new CalendarEvent(Weekday.Wednesday,createTime(10,0),createTime(11,0));
		CalendarEvent event1 = new CalendarEvent(Weekday.Monday,createTime(15,30),createTime(17,0));
		
		List<CalendarEvent> expected = Arrays.asList(event1,event2);
		
		List<CalendarEvent> actual = new ArrayList<CalendarEvent>();
		Collections.addAll(actual, event1,event2);
		
		Collections.sort(actual);
		
		assertEquals(expected,actual);
		
	}
	
	@Test
	public void testGroupAllDays() {
		CalendarEvent event1 = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(11,0));
		CalendarEvent event2 = new CalendarEvent(Weekday.Wednesday,createTime(10,0),createTime(11,0));
		CalendarEvent event3 = new CalendarEvent(Weekday.Friday,createTime(10,0),createTime(11,0));
		
		String actual = CalendarEvent.format(Arrays.asList(event1,event2,event3));
		
		assertEquals("MWF 10:00 - 11:00 AM",actual);


	}
	
	@Test
	public void testGroupNone() {
		CalendarEvent event1 = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(11,0));
		CalendarEvent event2 = new CalendarEvent(Weekday.Wednesday,createTime(14,0),createTime(15,0));
		CalendarEvent event3 = new CalendarEvent(Weekday.Friday,createTime(16,0),createTime(17,0));
		
		String actual = CalendarEvent.format(Arrays.asList(event1,event2,event3));
		
		assertEquals("M 10:00 - 11:00 AM,W 2:00 - 3:00 PM,F 4:00 - 5:00 PM",actual);
	}
	
	@Test
	public void testGroupNone2() {
		CalendarEvent event1 = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(11,0));
		CalendarEvent event2 = new CalendarEvent(Weekday.Wednesday,createTime(14,0),createTime(15,0));
		CalendarEvent event3 = new CalendarEvent(Weekday.Friday,createTime(10,0),createTime(11,0));
		
		String actual = CalendarEvent.format(Arrays.asList(event1,event2,event3));
		
		assertEquals("M 10:00 - 11:00 AM,W 2:00 - 3:00 PM,F 10:00 - 11:00 AM",actual);
	}
	
	@Test
	public void testSplitGroup() {
		CalendarEvent event1 = new CalendarEvent(Weekday.Monday,createTime(10,0),createTime(11,0));
		CalendarEvent event2 = new CalendarEvent(Weekday.Wednesday,createTime(10,0),createTime(11,0));
		CalendarEvent event3 = new CalendarEvent(Weekday.Friday,createTime(16,0),createTime(17,0));
		
		String actual = CalendarEvent.format(Arrays.asList(event1,event2,event3));
		
		assertEquals("MW 10:00 - 11:00 AM,F 4:00 - 5:00 PM",actual);
	}
	
	
	private Date createTime(int hr,int min) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, hr);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		
		return c.getTime();
	}
}
