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
package com.gamesalutes.utils.graph;


/**
 * Double precesion point in 3d space
 * 
 * @author Justin Montgomery
 * @version $Id: Point3d.java 697 2008-02-20 18:29:39Z jmontgomery $
 *
 */
public final class Point3d extends Tuple3d
{
	/**
	 * Default Constructor.
	 *
	 */
	public Point3d() {}
	
	/**
	 * Constructor.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public Point3d(double x, double y, double z)
	{
		super(x,y,z);
	}
	
	/**
	 * Constructs a Point3d from {@link Tuple3d}
	 * @param t the <code>Tuple3d</code>
	 */
	public Point3d(Tuple3d t)
	{
		super(t.x,t.y,t.z);
	}
	
	/**
	 * Returns the distance between this <code>Point3d</code> and <code>p</code>.
	 * @param p another <code>Point3d</code>
	 * @return the distance between the two points
	 */
	public double distance(Point3d p)
	{
		return Math.sqrt(distanceSquared(p));
	}
	
	/**
	 * Returns the distance squared between this <code>Point3d</code> and <code>p</code>.
	 * @param p another <code>Point3d</code>
	 * @return the distance squared between the two points
	 */
	public double distanceSquared(Point3d p)
	{
		return (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y) + (z-p.z)*(z-p.z);
	}
}
