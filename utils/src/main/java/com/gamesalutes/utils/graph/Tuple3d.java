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
 * Double precision 3d tuple
 * 
 * @author Justin Montgomery
 * @version $Id: Tuple3d.java 697 2008-02-20 18:29:39Z jmontgomery $
 *
 */
public abstract class Tuple3d
{
	protected double x,y,z;
	
	/**
	 * Default Constructor
	 *
	 */
	protected Tuple3d() {}
	
	/**
	 * Constructor.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	protected Tuple3d(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 * Returns the x coordinate
	 * @return the x coordinate
	 */
	public final double getX() { return x; }
	
	/**
	 * Returns the y coordinate
	 * @return the y coordinate
	 */
	public final double getY() { return y; }
	
	/**
	 * Returns the z coordinate
	 * @return the z coordinate
	 */
	public final double getZ() { return z; }
	
	/**
	 * Sets the x coordinate
	 * @param x value of x coordinate
	 */
	public final void setX(double x) { this.x = x; }
	
	/**
	 * Sets the y coordinate
	 * @param y value of y coordinate
	 */
	public final void setY(double y) { this.y = y; }
	
	/**
	 * Sets the z coordinate
	 * @param z value of z coordinate
	 */
	public final void setZ(double z) { this.z = z; }
	
	/**
	 * Sets the components of this <code>Tuple3d</code>.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 */
	public final void set(double x, double y, double z)
	{
		this.x = x; this.y = y; this.z = z;
	}
	
	/**
	 * Sets the value of this <code>Tuple3d</code> to <code>t</code>.
	 * 
	 * @param t another <code>Tuple3d</code>
	 */
	public final void set(Tuple3d t)
	{
		this.x = t.x; this.y = t.y; this.z = t.z;
	}
	
	/**
	 * Scales this <code>Tuple3d</code> by <code>value</code>.
	 * 
	 * @param value scaling value
	 */
	public void scale(double value)
	{
		x *= value; y *= value; z *= value;
	}
	
	/**
	 * Sets the value of this tuple to the scaling of <code>t</code>.
	 * 
	 * @param value scaling value
	 * @param t another <code>Tuple3d</code>
	 */
	public void scale(double value,Tuple3d t)
	{
		set(t);
		scale(value);
	}
	
	/**
	 * Adds <code>t</code> to this <code>Tuple3d</code>.
	 * 
	 * @param t another <code>Tuple3d</code>
	 */
	public void add(Tuple3d t)
	{
		x += t.x; y += t.y; z += t.z;
	}
	
	/**
	 * Sets the value of this tuple to that of sum of <code>t1</code>
	 *  and <code>t2</code>.
	 * 
	 * @param t1 first <code>Tuple3d</code>
	 * @param t2 second <code>Tuple3d</code>
	 */
	public void add(Tuple3d t1, Tuple3d t2)
	{
		x = t1.x + t2.x;
		y = t1.y + t2.y;
		z = t1.z + t2.z;
	}
	
	/**
	 * Subtracts <code>t</code> from this <code>Tuple3d</code>.
	 * 
	 * @param t another <code>Tuple3d</code>
	 */
	public void sub(Tuple3d t)
	{
		x -= t.x; y -= t.y; z -= t.z;
	}
	
	/**
	 * Sets the value of this tuple to that of difference of <code>t1</code>
	 *  and <code>t2</code>.
	 * 
	 * @param t1 first <code>Tuple3d</code>
	 * @param t2 second <code>Tuple3d</code>
	 */
	public void sub(Tuple3d t1, Tuple3d t2)
	{
		x = t1.x - t2.x;
		y = t1.y - t2.y;
		z = t1.z - t2.z;
	}
	
	/**
	 * Writes out the components of this <code>Tuple3d</code> in space-separated format
	 * @return the string representation of this <code>Tuple3d</code>
	 */
	public String toString()
	{
		return x + " " + y + " " + z + " ";
	}

}
