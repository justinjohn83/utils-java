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
/* Copyright 2008 University of Chicago
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

import java.util.Collection;
import java.util.Set;

/**
 * @author Justin Montgomery
 * @version $Id: ArraySetTest.java 1269 2009-01-14 23:45:32Z jmontgomery $
 */
public class ArraySetTest extends AbstractCollectionTest {

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractSetTest#createEmptySet()
	 */
	@Override
	protected Set<Integer> createEmptyCollection() 
	{
		return new ArraySet<Integer>();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.AbstractSetTest#createSet(java.util.Collection)
	 */
	@Override
	protected Set<Integer> createCollection(Collection<Integer> c) 
	{
		return new ArraySet<Integer>(c);
	}

}
