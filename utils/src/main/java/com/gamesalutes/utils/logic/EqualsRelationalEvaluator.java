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

import com.gamesalutes.utils.MiscUtils;

/**
 * <code>LogicalEvaluator</code> that implements equals and not equals in terms of 
 *  <code>Object.equals</code>.  All other relational operators throw an <code>UnsupportedOperationException</code>.
 *  To provide a different notion of equality, override the <code>equal</code> method.
 *  
 * @author Justin Montgomery
 * @version $Id:$
 */
public class EqualsRelationalEvaluator<S,T> implements RelationalEvaluator<S,T>
{

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#equal(java.lang.Object, java.lang.Object)
	 */
	public boolean equal(S lhs, T rhs)
	{
		return MiscUtils.safeEquals(lhs, rhs);
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#greaterThan(java.lang.Object, java.lang.Object)
	 */
	public final boolean greaterThan(S lhs, T rhs)
	{
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#greaterThanOrEqual(java.lang.Object, java.lang.Object)
	 */
	public final boolean greaterThanOrEqual(S lhs, T rhs)
	{
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#lessThan(java.lang.Object, java.lang.Object)
	 */
	public final boolean lessThan(S lhs, T rhs)
	{
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#lessThanOrEqual(java.lang.Object, java.lang.Object)
	 */
	public final boolean lessThanOrEqual(S lhs, T rhs)
	{
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.logic.LogicalEvaluator#notEqual(java.lang.Object, java.lang.Object)
	 */
	public final boolean notEqual(S lhs, T rhs)
	{
		return !equal(lhs,rhs);
	}

}
