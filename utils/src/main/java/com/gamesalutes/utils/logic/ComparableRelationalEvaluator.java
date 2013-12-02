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
package com.gamesalutes.utils.logic;

/**
 * <code>LogicalEvaluator</code> that implements functionality in terms of <code>java.lang.Comparable</code>.
 * Objects of type S and T must be mutually comparable or a <code>ClassCastException</code> will be thrown.
 * All functionality is implemented using <code>compare</code> so subclasses need only override this method
 * if it is decided to use a different comparison method.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public class ComparableRelationalEvaluator<S,T> implements RelationalEvaluator<S,T>
{

	/*
	 * As an interesting node in c++ this is implemented in terms of bool operator <.
	 * 
	 * 
	 *   operator == : !(a < b) && !( b < a)
	 *   operator != : !(a == b)
	 *   operator <= : a < b || !(b < a)
	 *   operator > : !(a <= b)
	 *   operator >= : !(a < b)
	 */
	
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#equal(java.lang.Object, java.lang.Object)
	 */
	public final boolean equal(S lhs, T rhs)
	{
		return compare(lhs,rhs) == 0;
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#greaterThan(java.lang.Object, java.lang.Object)
	 */
	public final boolean greaterThan(S lhs, T rhs)
	{
		return compare(lhs,rhs) > 0;
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#greaterThanOrEqual(java.lang.Object, java.lang.Object)
	 */
	public final boolean greaterThanOrEqual(S lhs, T rhs)
	{
		return compare(lhs,rhs) >= 0;
	}

	
	@SuppressWarnings("unchecked")
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#lessThan(java.lang.Object, java.lang.Object)
	 */
	public final boolean lessThan(S lhs, T rhs)
	{
		return compare(lhs,rhs) < 0;
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#lessThanOrEqual(java.lang.Object, java.lang.Object)
	 */
	public final boolean lessThanOrEqual(S lhs, T rhs)
	{
		return compare(lhs,rhs) <= 0;
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#notEqual(java.lang.Object, java.lang.Object)
	 */
	public final boolean notEqual(S lhs, T rhs)
	{
		return !equal(lhs,rhs);
	}
	
	protected int compare(Object lhs,Object rhs)
	{
		return ((Comparable)lhs).compareTo(rhs);
	}

}
