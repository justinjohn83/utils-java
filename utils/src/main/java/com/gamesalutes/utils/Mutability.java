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

/**
 * Interface to designate the mutability of the specified object.  An immutable object has all fields
 * final, does not provide accessor methods, and does not return nor expose
 * any references to internals of the object to outside classes.
 * Classes that are immutable should implement this interface and have
 * {@link Mutability#isMutable()} return <code>false</code>.
 * This class is primarily aimed at helping not make unnecessary copies during a call to 
 * {@link MiscUtils#copy(Object)}.
 * 
 * @author Justin Montgomery
 * @version $Id: Mutability.java 1086 2008-09-05 19:00:25Z jmontgomery $
 */
public interface Mutability
{
	/**
	 * Returns <code>true</code> if mutable and <code>false</code> otherwise.
	 * 
	 * @return the mutability of this object
	 */
	boolean isMutable();
}
