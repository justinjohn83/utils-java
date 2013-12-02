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
 * Edge in a graph.
 * 
 * @author Justin  Montgomery
 * @version $Id: Edge.java 1946 2010-02-26 17:55:34Z jmontgomery $
 * 
 * @param <V> vertex type
 * @param <E> edge type
 *
 */
public class Edge<V,E> extends GraphObject<E>
{
	private Vertex<V> from;
	private Vertex<V> to;
	private boolean reversed;
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor.
	 * 
	 * 	@param from source endpoint {@link Vertex}
	 * @param to destination endpoint <code>Vertex</code>
	 * 
	 */
	public Edge(Vertex<V> from,Vertex<V> to)
	{
		this(from,to,null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param from source endpoint {@link Vertex}
	 * @param to destination endpoint <code>Vertex</code>
	 * @param data data to store
	 */
	public Edge(Vertex<V> from,Vertex<V> to,E data)
	{
		this(from,to,data,false);
	}
	
	private Edge(Vertex<V> from,Vertex<V> to,E data,boolean reversed)
	{
		super(data);
		
		if(from == null)
			throw new NullPointerException("from");
		if(to == null)
			throw new NullPointerException("to");
		
		this.from = from;
		this.to = to;
		this.reversed = reversed;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param e another <code>Edge</code>
	 */
	public Edge(Edge<V,E> e)
	{
		super(e);
		this.from = e.getFrom();
		this.to = e.getTo();
		this.reversed = e.reversed;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected GraphObject<E> shallowCopy(GraphObject<E> orig,GraphObject<E> copy)
	{
		if(copy == null) // just use copy constructor
			return new Edge<V,E>((Edge<V,E>)orig);
		else
		{
			Edge<V,E> ecopy = (Edge<V,E>)copy;
			Edge<V,E> eorig = (Edge<V,E>) orig;
			
			ecopy.from = eorig.from;
			ecopy.to = eorig.to;
			ecopy.reversed = eorig.reversed;

			return super.shallowCopy(eorig, ecopy);
			
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public Edge<V,E> clone()
	{
		return (Edge<V,E>)super.clone();
	}
	
	/**
	 * Returns the source endpoint {@link Vertex}.
	 * 
	 * @return the source <code>Vertex</code>
	 */
	public Vertex<V> getFrom() { return from; }
	
	/**
	 * Returns the dest endpoint {@link Vertex}.
	 * 
	 * @return the dest <code>Vertex</code>
	 */
	public Vertex<V> getTo() { return to; }
	
	/**
	 * Sets the destination {@link Vertex} of this <code>Edge</code>.
	 * In practice, this should only be called during a copy operation.
	 * 
	 * @param newTo the new destination endpoint
	 * @return the old destination endpoint
	 */
	public Vertex<V> setTo(Vertex<V> newTo)
	{
		if(newTo == null) throw new NullPointerException("newTo");
		Vertex<V> prevTo = this.to;
		this.to = newTo;
		
		return prevTo;
	}
	
	/**
	 * Sets the source {@link Vertex} of this <code>Edge</code>.
	 * In practice, this should only be called during a copy operation.
	 * 
	 * @param newFrom the new source endpoint
	 * @return the old source endpoint
	 */
	public Vertex<V> setFrom(Vertex<V> newFrom)
	{
		if(newFrom == null) throw new NullPointerException("newFrom");
		Vertex<V> prevFrom = this.from;
		this.from = newFrom;
		
		return prevFrom;
	}
	
	
	
	/**
	 * Returns the opposing endpoint of the edge opposite of <code>vertex</code>.
	 * 
	 * @param vertex an endpoint
	 * @return the endpoint opposite of <code>vertex</code>
	 * @throws IllegalArgumentException if <code>vertex</code> is not one of the
	 *                                  endpoints
	 */
	public Vertex<V> getOtherEndPoint(Vertex<V> vertex)
	{
		if(from.equals(vertex))
			return to;
		else if(to.equals(vertex))
			return from;
		else
			throw new IllegalArgumentException("vertex not in edge");
	}
	
	/**
	 * Returns whether <code>vertex</code> is an endpoint of this edge.
	 * 
	 * @param vertex the <code>Vertex</code>
	 * @return <code>true</code> if <code>vertex</code> is an endpoint
	 *         and <code>false</code> otherwise
	 */
	public boolean isEndpoint(Vertex<V> vertex)
	{
		return from.equals(vertex) || to.equals(vertex);
	}
	/**
	 * Returns the reverse of this <code>Edge</code>
	 * @return reverse of this <code>Edge</code>
	 */
	public Edge<V,E> reverse()
	{
		if(!isSelfLoop())
			return new Edge<V,E>(to,from,getData(),!reversed);
		else
			return this;
	}
	
	/**
	 * Returns whether this edge was the reversal of the original edge.
	 * 
	 * @return <code>true</code> if this edge as a reversal of an original edge and <code>false</code> otherwise
	 */
	public boolean isReversed()
	{
		return reversed;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(!(o instanceof Edge)) return false;
		if(!super.equals(o)) return false;
		Edge e = (Edge)o;
		return from.equals(e.from) && to.equals(e.to);
	}
	
	/**
	 * Returns whether this edge is a self-loop, i.e. and edge that starts
	 * and terminates on the same vertex.
	 * 
	 * @return <code>true</code> if self-loop and <code>false</code> otherwise
	 */
	public boolean isSelfLoop()
	{
		return from.equals(to);
	}
	
	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 37 * result + from.hashCode();
		result = 37 * result + to.hashCode();
		return result;
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append("[");
		str.append(from);
		str.append(" --> ");
		str.append(to);
		str.append("]");
		return str.toString();
		
	}
}
