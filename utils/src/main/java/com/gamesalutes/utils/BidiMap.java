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

import java.util.Map;

/**
 * A map that can efficiently retrieve keys and values.  The mapping of keys to values must be 1:1 so 
 * that <code>getKey</code> always returns <code>null</code> or the single key mapped to that value.
 * 
 * 
 * @author Justin Montgomery
 * @version $Id: BidiMap.java 1271 2009-01-16 20:45:44Z jmontgomery $
 */
public interface BidiMap<K,V> extends Map<K, V>
{
	/**
	 * Gets the key in the key-value pair corresponding to <code>value</code>.
	 * 
	 * @param value the lookup value
	 * @return the key associated with <code>value</code> or <code>null</code> if not found
	 */
	K getKey(Object value);
	
	/**
	 * Removes the key in the key-value pair corresponding to <code>value</code>.
	 * 
	 * @param value the lookup value
	 * @return the old key associated with <code>value</code> or <code>null</code> if no association
	 */
	K removeValue(Object value);
}
