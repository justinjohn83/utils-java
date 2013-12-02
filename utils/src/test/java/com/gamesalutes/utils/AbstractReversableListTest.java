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

import java.util.ArrayList;
import java.util.Collection;
//import java.util.Collections;
import java.util.List;

//import org.junit.*;
//import static org.junit.Assert.*;
/**
 * @author Justin Montgomery
 * @version $Id:$
 */
public abstract class AbstractReversableListTest extends AbstractListTest
{
	@Override
	protected List<Integer> createCollection(Collection<Integer> c)
	{
		return new ReversableList<Integer>(new ArrayList<Integer>(c),reverse());
		
	}
	
	protected abstract boolean reverse();
	

	@Override
	protected List<Integer> createEmptyCollection()
	{
		return new ReversableList<Integer>(new ArrayList<Integer>(),reverse());
	}
	
	
//	@Test
//	public void testReverse()
//	{
//		List<Integer> origList = new ArrayList<Integer>(orig);
//		
//		List<Integer> origListReversed = new ArrayList<Integer>(orig);
//		Collections.reverse(origListReversed);
//		
//		ReversableList<Integer> list = (ReversableList)createCollection(orig);
//		
//		int fromIndex = origList.size() / 3;
//		int toIndex = 2 * origList.size() / 3;
//		
//		List<Integer> subList = list.subList(fromIndex,toIndex);
//		
//		list.reverse();
//		
//		if(reverse())
//			assertEquals(origList.subList(fromIndex, toIndex),subList);
//		else
//			assertEquals(origListReversed.subList(fromIndex, toIndex),subList);
//		
//	}

}
