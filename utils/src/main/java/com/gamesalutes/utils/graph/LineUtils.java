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


import com.gamesalutes.utils.Pair;

import java.awt.geom.*;
/**
 * Utilities for producing equations of lines and vectors from them.
 * 
 * @author Justin Montgomery
 * @version $Id: LineUtils.java 697 2008-02-20 18:29:39Z jmontgomery $
 *
 */
public class LineUtils 
{
	private LineUtils() {}
	
	private static final double EPSILON = 1E-5;
	
	/**
	 * Returns the equation of the line formed by the parameters in slope-intercept
	 * form: <code>y = mx + b</code>. 
	 * The first entry in the pair is <code>m</code> and the second entry is
	 * <code>b</code>.
	 *   
	 * @param x1 first x-coord
	 * @param y1 first y-coord
	 * @param x2 second x-coord
	 * @param y2 second y-coord
	 * @return {@link Pair} containing <code>m,b</code> of the slope-intercept equation
	 */
	public static Pair<Double,Double> getLineEquation(double x1,double y1,double x2,double y2)
	{
		//use Cramer's rule to compute the slope and y-intercept
		//y1 = m*x1 + b
		//y2 = m*x2 + b
		//m = (y1 - y2) / (x1 - x2)
		//b = (x1 * y2 - x2 * y1) / (x1 - x2)
		//prevent division by zero
		if(Math.abs(x1 - x2) < EPSILON)
		{
			if(x1 < x2)
				x2 += EPSILON;
			else
				x1 += EPSILON;
		}
		/*
		if(Math.abs(y1 - y2) < EPSILON)
		{
			if(y1 < y2)
				y2 += EPSILON;
			else
				y1 += EPSILON;
		}
		*/
		double m = (y1 - y2) / (x1 - x2);
		double b = (x1 * y2 - x2 * y1) / (x1 - x2);
		return new Pair<Double,Double>(m,b);
	}
	

	
	/**
	 * Returns the equation of the line perpendicular to the one formed by the paramters
	 * in slope-intercept form: <code>y = mx + b</code>.
	 * The first entry in the pair is <code>m</code> and the second entry is
	 * <code>b</code>.
	 * 
	 * @param x1 first x-coord
	 * @param y1 first y-coord
	 * @param x2 second x-coord
	 * @param y2 second y-coord
	 * @return {@link Pair} containing <code>m,b</code> of the slope-intercept equation
	 *         of the perpendicular line
	 */
	public static Pair<Double,Double> getPerpendicularLineEquation(double x1,double y1,double x2,double y2)
	{
		Pair<Double,Double> line = getLineEquation(x1,y1,x2,y2);
		return getPerpendicularLineEquation(line);
	}
	
	/**
	 * Returns the equation of the line perpendicular to the one given by <code>origLineEqn</code>
	 * in slope-intercept form: <code>y = mx + b</code>.
	 *  
	 * The first entry in the pair is <code>m</code> and the second entry is
	 * <code>b</code>.
	 * 
	 * @param origLineEqn original slope intercept-form equation
	 * @return {@link Pair} containing <code>m,b</code> of the slope-intercept equation
	 *         of the perpendicular line
	 */
	public static Pair<Double,Double> getPerpendicularLineEquation(Pair<Double,Double> origLineEqn)
	{
		//pependicular line has negative reciprocal slope of original line
		
		//prevent division by zero
		double x = origLineEqn.first;
		if(Math.abs(x) < EPSILON)
		{
			if(x > 0)
				x += EPSILON;
			else
				x -= EPSILON;
		}
		return new Pair<Double,Double>(-1 / x,origLineEqn.second);
	}
	
	
	
	/**
	 * Returns a {@link Point3d Point3d} containing end point of line described by
	 * the parameters.
	 * 
	 * @param start start <code>Point3d</code>
	 * @param dir direction of line given as {@link Vector3d Vector3d}.
	 * @return <code>Point3d</code> containing the line end point
	 */
	public static Point3d getEndPoint(Point3d start,Vector3d dir,double len)
	{
		Vector3d normDir = new Vector3d(dir);
		normDir.normalize();
		
		Point3d tempPos = new Point3d(start);
		normDir.scale(len);
		tempPos.add(normDir);
		return tempPos;
	}
	
	/**
	 * Returns the {@link Vector3d Vector3d} representing the direction of the 
	 * line described by the parameters.
	 * 
	 * @param x1 first x-coord
	 * @param y1 first y-coord
	 * @param x2 second x-coord
	 * @param y2 second y-coord
	 * @return <code>Vector3d</code> containing the line direction
	 */
	public static Vector3d getLineDir(double x1,double y1,double x2,double y2)
	{
		Point3d start = new Point3d(x1,y1,0);
		Point3d end = new Point3d(x2,y2,0);
		return new Vector3d(start,end);
		
	}
	/**
	 * Returns the {@link Vector3d Vector3d} representing the direction of the 
	 * line perpendicular to that described by the parameters.  The line direction
	 * is that from positive x-axis to positive y-axis.
	 * 
	 * @param x1 first x-coord
	 * @param y1 first y-coord
	 * @param x2 second x-coord
	 * @param y2 second y-coord
	 * @return <code>Vector3d</code> containing the perpendicular line direction
	 */
	public static Vector3d getPerpendicularLineDir(double x1,double y1,double x2,double y2)
	{
		AffineTransform transform = new AffineTransform();
		//make a 90-degree rotation clockwise
		transform.rotate(- Math.PI / 2);
		
		double [] srcPts = {x1,y1,x2,y2};
		double [] destPts = new double [4];
		
		transform.transform(srcPts,0,destPts,0,2);
		
		return new Vector3d(new Point3d(destPts[0],destPts[1],0),
							new Point3d(destPts[2],destPts[3],0));
		
	} //end getPerpendicularLineDir
}
