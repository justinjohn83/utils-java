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
 * Double-precision vector in 3d space.
 * 
 * @author Justin Montgomery
 * @version $Id: Vector3d.java 697 2008-02-20 18:29:39Z jmontgomery $
 *
 */
public final class Vector3d extends Tuple3d
{
	
	private static final Vector3d X_AXIS = new Vector3d(1,0,0);
	private static final Vector3d Y_AXIS = new Vector3d(0,1,0);
	private static final Vector3d Z_AXIS = new Vector3d(0,0,1);
	private static final Vector3d ZERO_VEC = new Vector3d(0,0,0);
	/**
	 * Default Constructor.
	 *
	 */
	public Vector3d() {}
	
	/**
	 * Constructor.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 */
	public Vector3d(double x, double y, double z)
	{
		super(x,y,z);
	}
	
	/**
	 * Constructs a Vector3d from {@link Tuple3d}.
	 * 
	 * @param t the <code>Tuple3d</code>
	 */
	public Vector3d(Tuple3d t)
	{
		super(t.x,t.y,t.z);
	}
	
	/**
	 * Constructs a Vector3d from a start and end {@link Point3d}.
	 * 
	 * @param start start <code>Point3d</code>
	 * @param end end <code>Point3d</code>
	 */
	public Vector3d(Point3d start, Point3d end)
	{
		super(end.x - start.x,end.y - start.y, end.z - start.z);
	}
	
	/**
	 * Normalizes this Vector3d
	 *
	 */
	public void normalize()
	{
		double mag = length();
		
		if(Double.compare(mag, 0.0) != 0)
		{
			x /= mag; y/= mag; z/= mag;
		}
	}
	
	/**
	 * Reverses the direction of this <code>Vector3d</code>.
	 * Equivalent to calling {@link #scale(double)} with argument
	 * of -1.
	 */
	public void reverse()
	{
		scale(-1.0);
	}
	
	/**
	 * Computes the length/magnitude of this vector.
	 * 
	 * @return the length
	 */
	public double length()
	{
		return Math.sqrt(lengthSquared());
	}
	
	/**
	 * Computes the length/mangitude squared of this vector.
	 * 
	 * @return the length squared
	 */
	public double lengthSquared()
	{
		return x*x + y*y + z*z;
	}
	
	/**
	 * Computes the angle between this vector and <code>v</code> with the result [0,PI]
	 * @param v the other <code>Vector3d</code>
	 * @return the angle in radians in the range [0,PI]
	 */
	public double angle(Vector3d v)
	{
		return Math.acos(dot(v)/(length()*v.length()));
	}
	
	/**
	 * Computes the dot product between this vector and <code>v</code>
	 * @param v the other <code>Vector3d</code>
	 * @return the dot product
	 */
	public double dot(Vector3d v)
	{
		return x*v.x + y*v.y + z*v.z;
	}
	
	/**
	 * Sets this <code>Vector3d</code> to the cross product of v1 and v2.
	 * 
	 * @param v1 first vector
	 * @param v2 second vector
	 */
	public void cross(Vector3d v1, Vector3d v2)
	{
		x = v1.y*v2.z - v2.y*v1.z;
		y = -v1.x*v2.z + v2.x*v1.z;
		z = v1.x*v2.y - v2.x*v1.y;
	}
	
	/**
	 * Computes the inclination angles of the x,y, and z axes to this vector.
	 * 
	 * @param angles <code>double[]</code> of size at least 3 or <code>null</code>
	 *               if simply want one allocated
	 * @param radians <code>true</code> if want result in radians and 
	 *                <code>false</code> if want result in degrees
	 *              
	 * @return <code>double[]</code> of at least size 3 containing x,y,z angles in radians or degrees
	 * @throws IllegalArgumentException if <code>angles</code> is not <code>null</code> but
	 *                                  has a length less than 3
	 */
	public double[] axesAngles(double[] angles,boolean radians)
	{
		double[] result = angles;
		if(result == null)
			result = new double[3];
		else if(result.length < 3)
			throw new IllegalArgumentException("angles.length = " + result.length);
		
		//find angle wrt to x-axis
		result[0] = angle(X_AXIS);
		result[1] = angle(Y_AXIS);
		result[2] = angle(Z_AXIS);
		
		if(!radians)
		{
			result[0] = Math.toDegrees(result[0]);
			result[1] = Math.toDegrees(result[1]);
			result[2] = Math.toDegrees(result[2]);
		}
		return result;
	} //end axesAngles

}
