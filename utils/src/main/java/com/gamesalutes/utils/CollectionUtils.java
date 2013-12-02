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
package com.gamesalutes.utils;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Adding utility methods for <code>java.util.Collection</code>.
 * 
 * These methods are used to supplement the common tasks for collections
 * implemented in <code>java.util.Collections</code>.
 * 
 * @author Justin Montgomery
 * @version $Id: CollectionUtils.java 2524 2011-01-03 19:32:18Z jmontgomery $
 *
 */
public final class CollectionUtils
{
	public static final float LOAD_FACTOR = 0.7F;

	private CollectionUtils() {}
	
	
	/**
	 * Returns <code>true</code> if <code>source</code> contains at least one
	 * element in <code>target</code>.
	 * 
	 * @param source the source <code>Collection</code>
	 * @param target the target <code>Collection</code>
	 * @return <code>true</code> if <code>source</code> contains at least one
	 *         element in <code>target</code> and <code>false</code> otherwise
	 */
	public static boolean containsSome(Collection<?> source,Collection<?> target)
	{
            if(source == target) return true;
            if((source == null) != (target == null)) return false;
            return !Collections.disjoint(source, target);
	}
	
	
	/**
	 * Determines whether <code>col</code> contains only <code>elm</code>
	 * as its element(s).
	 * 
	 * @param <T> the type of the <code>Collection</code>
	 * @param col input <code>Collection</code>
	 * @param elm the test element
	 * @return <code>true</code> if <code>col</code> contains only
	 *         <code>elm</code> and <code>false</code> otherwise
	 */
	public static <T> boolean containsOnly(Collection<? extends T> col,T elm)
	{
		if(col.size() == 1)
			return col.contains(elm);
		else if(!col.isEmpty() && !(col instanceof Set))
		{
			// make sure every element in this collection is
			// "elm"
			for(T t : col)
			{
				if(!MiscUtils.safeEquals(t,elm))
					return false;
			}
			return true;
		}
		else // collection either empty or is a set with more than 1 element
			return false;
	}
	
	/**
	 * Replaces all the equal elements between <code>source</code> and <code>dest</code>
	 * in <code>dest</code> with the elements in <code>target</code>.
	 * Replacement proceeds for each equal element in order returned by <code>Iterator</code>
	 * of <code>target</code>.  If <code>target</code> is <code>null</code>, then the elements in 
	 * <code>dest</code> that are equal to those in <code>source</code> are removed from
	 * <code>dest</code>.  Otherwise, target must have the same size as the source or an
	 * <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param <T> the <code>Collection</code> type
	 * @param source the reference source
	 * @param dest the <code>Collection</code> to be modified
	 * @param target the replacements elements for equal elements in <code>source</code>
	 *        and <code>dest</code>
	 * @return <code>true</code> if dest modified as result of this method and
	 *         <code>false</code> otherwise
	 * @throws IllegalArgumentException if <code>target</code> is not null and
	 *                                 <code>source.size() != target.size()</code>
	 *                            
	 */
	public static <T> boolean replaceAll(Collection<? extends T> source,
			                             Collection<T> dest,
			                             Collection<? extends T> target)
	{
		if(source == null) throw new NullPointerException("source");
		if(dest == null) throw new NullPointerException("dest");
		if(target == null)
			target = Collections.<T>emptyList();
		else if(source.size() != target.size())
		{
			StringBuilder str = new StringBuilder(1024);
			str.append("source.size()=");
			str.append(source.size());
			str.append(" != target.size()=");
			str.append(target.size());
			str.append(" ; [source=");
			str.append(source);
			str.append(";dest=");
			str.append(dest);
			str.append(";target=");
			str.append(target);
			str.append("]");
			throw new IllegalArgumentException(str.toString());
		}
		boolean modified = false;
		
		// nothing to do
		if(source.isEmpty())
			return false;
		
		// preserve order of replacements
		if(dest instanceof List)
		{
			ListIterator<T> destIt = 
				((List<T>)dest).listIterator();
			
			List<? extends T> sourceList;
			List<? extends T> targetList;
			if(!(source instanceof List))
				sourceList = new ArrayList<T>(source);
			else
				sourceList = (List)source;
			if(!(target instanceof List))
				targetList = new ArrayList<T>(target);
			else
				targetList = (List)target;
			
			while(destIt.hasNext())
			{
				T targetElm = null;
				T destElm = destIt.next();
				
				int index = -1;
				if((index = sourceList.indexOf(destElm)) != -1)
				{
					modified = true;
					if(!targetList.isEmpty())
						targetElm = targetList.get(index);
					// set value to target elm
					if(targetElm != null)
						destIt.set(targetElm);
					else // no match, so delete the destElm
						destIt.remove();
				}
			} // end while
		}
		else
		{
			final int oldSize = dest.size();
			modified = dest.removeAll(source);
			
			if(modified)
			{
				final int count = oldSize - dest.size();
				int i = 0;
				for(Iterator<? extends T> targetIt = target.iterator();
					targetIt.hasNext() && i++ < count;)
				{
					dest.add(targetIt.next());
				}
			}
		}
		
		return modified;
		
	}


	/**
	 * Calculates the optimal hash capacity for given maximum size and the
	 * load factor.
	 * 
	 * @param size maximum size of the collection
	 * @param loadFactor usage percentage before a rehash occurs.  Default value for
	 *                   Collections framework is 0.75.  Lower values result in less
	 *                   chance of collisions at the expense of larger memory footprint
	 * @return the optimal hash capacity
	 */
	public static int calcHashCapacity(int size,float loadFactor)
	{
		return (int)Math.ceil(size/loadFactor);
	}


	/**
	 * Creates a <code>HashMap</code> of the optimal hash capacity for given
	 * maximum size and the load factor.
	 * 
	 * @param <U> key type
	 * @param <V> value type
	 * @param size maximum size of the map
	 * @param loadFactor usage percentage before a rehash occurs.  Default value for
	 *                   Collections framework is 0.75.  Lower values result in less
	 *                   chance of collisions at the expense of larger memory footprint
	 * @return the created <code>HashMap</code>
	 */
	public static <U,V> HashMap<U,V> createHashMap(int size,float loadFactor)
	{
		return new HashMap<U,V>(
				calcHashCapacity(size,loadFactor),loadFactor);
	}
	
	/**
	 * Creates a <code>LinkedHashMap</code> of the optimal hash capacity for given
	 * maximum size and the load factor.
	 * 
	 * @param <U> key type
	 * @param <V> value type
	 * @param size maximum size of the map
	 * @param loadFactor usage percentage before a rehash occurs.  Default value for
	 *                   Collections framework is 0.75.  Lower values result in less
	 *                   chance of collisions at the expense of larger memory footprint
	 * @return the created <code>HashMap</code>
	 */
	public static <U,V> LinkedHashMap<U,V> createLinkedHashMap(int size,float loadFactor)
	{
		return new LinkedHashMap<U,V>(
				calcHashCapacity(size,loadFactor),loadFactor);
	}
	
	/**
	 * Creates a <code>HashMap</code> subclass instance of the optimal hash capacity for given
	 * maximum size and the load factor.
	 * 
	 * @param <U> key type
	 * @param <V> value type
	 * 
	 * @param clazz the subclass instance to create
	 * @param size maximum size of the map
	 * @param loadFactor usage percentage before a rehash occurs.  Default value for
	 *                   Collections framework is 0.75.  Lower values result in less
	 *                   chance of collisions at the expense of larger memory footprint
	 * @return the created <code>LinkedHashSet</code>
	 */
	public static <U,V,C extends HashMap<U,V>> C createHashMap(Class<C> clazz,
			int size,float loadFactor)
	{
		return (C)createHash(clazz,size,loadFactor);
	}


	/**
	 * Creates a <code>HashSet</code> of the optimal hash capacity for given
	 * maximum size and the load factor.
	 * 
	 * @param <E> the element type
	 * 
	 * @param size maximum size of the collection
	 * @param loadFactor usage percentage before a rehash occurs.  Default value for
	 *                   Collections framework is 0.75.  Lower values result in less
	 *                   chance of collisions at the expense of larger memory footprint
	 * @return the created <code>HashSet</code>
	 */
	public static <E> HashSet<E> createHashSet(int size,float loadFactor)
	{
		return new HashSet<E>(
				calcHashCapacity(size,loadFactor),loadFactor);
	}
	
	/**
	 * Creates a <code>LinkedHashSet</code> of the optimal hash capacity for given
	 * maximum size and the load factor.
	 * 
	 * @param <E> the element type
	 * 
	 * @param size maximum size of the collection
	 * @param loadFactor usage percentage before a rehash occurs.  Default value for
	 *                   Collections framework is 0.75.  Lower values result in less
	 *                   chance of collisions at the expense of larger memory footprint
	 * @return the created <code>LinkedHashSet</code>
	 */
	public static <E> LinkedHashSet<E> createLinkedHashSet(int size,float loadFactor)
	{
		return new LinkedHashSet<E>(
				calcHashCapacity(size,loadFactor),loadFactor);
	}
	
	
	/**
	 * Creates a <code>HashSet</code> subclass instance of the optimal hash capacity for given
	 * maximum size and the load factor.
	 * 
	 * @param <E> the element type
	 * 
	 * @param clazz the subclass instance to create
	 * @param size maximum size of the collection
	 * @param loadFactor usage percentage before a rehash occurs.  Default value for
	 *                   Collections framework is 0.75.  Lower values result in less
	 *                   chance of collisions at the expense of larger memory footprint
	 * @return the created <code>LinkedHashSet</code>
	 */
	public static <E,C extends HashSet<E>> C createHashSet(Class<C> clazz,
			int size,float loadFactor)
	{
		return (C)createHash(clazz,size,loadFactor);
	}
	
	private static Object createHash(Class<?> clazz,int size,float loadFactor)
	{
		// try to use reflection to create the instance
		try
		{
			// use performance parameters
			Constructor<?> c = clazz.getConstructor(int.class,float.class);
			return c.newInstance(calcHashCapacity(size,loadFactor),loadFactor);
		}
		catch(Exception e)
		{
			try
			{
				// try default constructor
				return clazz.newInstance();
			}
			catch(Exception e2)
			{
				throw new IllegalArgumentException("clazz=" + clazz);
			}
		}
	}

	/**
	 * Returns a hashcode that is computed irrespective of the order of the collection.  This method should be used
	 * to compute a hashcode of a collection field when <code>unorderedListEquals</code> or
	 * <code>unorderedCollectionEquals</code> is used for that field in the object's equals method. 
	 * 
	 * @param c the <code>Collection</code>
	 * @return the hashcode
	 */
	public static int unorderedCollectionHashCode(Collection<?> c)
	{
		if(c == null) return 0;
		
		int h = 0;
		Iterator<?> i = c.iterator();
		while (i.hasNext()) 
		{
			Object o = i.next();
			if(o != null)
				h += o.hashCode();
		}
		return h;
	}
	/**
	 * Returns whether two collections are equal irrespective of their order and regardless of 
	 * their implemented subinterfaces of <code>Collection</code>.  The running time of this method is
	 * no worse than <code>O(nlogn)</code>.
	 * 
	 * By definition a <code>java.util.Set</code> cannot be equal to a <code>java.util.List</code>
	 * in order to obey the contracts of their respective interfaces. This method gets around that by
	 * comparing the two collections using <code>equals</code> if they are both <code>java.util.Set</code>.
	 * Otherwise, the collections are dumped into arrays, sorted using <code>GeneralComparator</code> and
	 * then the sorted arrays are compared.
	 * 
	 * @param c1 first <code>Collection</code>
	 * @param c2 second <code>Collection</code>
	 * @return <code>true</code> if they have the same size and are equal by containment and 
	 *         <code>false</code> otherwise
	 */
	public static boolean unorderedCollectionEquals(Collection<?> c1, Collection<?> c2)
	{
		if(c1 == c2) return true;
		// null checks
		if(c1 == null || c2 == null) return false;
		
		int size1 = c1.size();
		int size2 = c2.size();
		// sizes must be the same
		if(size1 != size2) return false;
		
		// compare using regular equals if both collections are sets since this is O(n)
		if(c1 instanceof Set && c2 instanceof Set)
			return c1.equals(c2);
		// dump both collections into arrays, sort them using GeneralComparator and then compare the arrays
		Object[] a1 = c1.toArray();
		Object[] a2 = c2.toArray();
		
		Arrays.sort(a1,new GeneralComparator(true));
		Arrays.sort(a2,new GeneralComparator(true));
		
		return Arrays.equals(a1, a2);
		
	}
	
	/**
	 * Compares two collections irrespective of their order and regardless of 
	 * their implemented subinterfaces of <code>Collection</code>.  The running time of this method is
	 * no worse than <code>O(nlogn)</code>.
	 * 
     * The collections are dumped into arrays, sorted using <code>GeneralComparator</code> and
	 * then the sorted arrays are compared one element at a time.
	 * 
	 * @param c1 first <code>Collection</code>
	 * @param c2 second <code>Collection</code>
	 * @return negative integer,zero, or positive integer as <code>c1</code> is less than, equal to, 
	 * 		   or greater than <code>c2</code>, respectively
	 */
	public static int unorderedCollectionCompare(Collection<?> c1,Collection<?> c2)
	{
		if(c1 == c2) return 0;
		// null checks
		if(c1 == null) return -1;
		if(c2 == null) return 1;
		
		Comparator<Object> comp = new GeneralComparator(true);
		
		// dump both collections into arrays, sort them using GeneralComparator and then compare the arrays
		Object[] a1 = c1.toArray();
		Object[] a2 = c2.toArray();
		
		Arrays.sort(a1,comp);
		Arrays.sort(a2,comp);
		
		//now compare values
		for(int i = 0, size = Math.min(a1.length,a2.length); i < size; ++i)
		{
			int result = comp.compare(a1[i], a2[i]);
			if(result != 0) return result;
		}
		return a1.length - a2.length;
	}
	
	/**
	 * Compares two ordered collections using <code>GeneralComparator</code>.
	 * 
	 * 
	 * @param c1 first <code>Collection</code>
	 * @param c2 second <code>Collection</code>
	 * @return negative integer,zero, or positive integer as <code>c1</code> is less than, equal to, 
	 * 		   or greater than <code>c2</code>, respectively
	 */
	public static int orderedCollectionCompare(Collection<?> c1,Collection<?> c2)
	{
		if(c1 == c2) return 0;
		// null checks
		if(c1 == null) return -1;
		if(c2 == null) return 1;
		
		Comparator<Object> comp = new GeneralComparator(true);

		Iterator<?> i1 = c1.iterator();
		Iterator<?> i2 = c2.iterator();
		
		while(i1.hasNext() && i2.hasNext())
		{
			int result = comp.compare(i1.next(), i2.next());
			if(result != 0) return result;
		}
		
		return c1.size() - c2.size();
	}


	/**
	 * Converts a <code>Collection</code> into a deliminator-separated String
	 * @param input <code>Collection</code> to convert
	 * @param del the deliminator character
	 * @return the deliminated string
	 */
	public static String convertCollectionIntoDelStr(Collection<?> input,String del)
	{
		if(input == null)
			return null;
		if(del == null)
			throw new NullPointerException("del");
		StringBuilder str = new StringBuilder();
		
		boolean first = true;
		for(Iterator<?> it = input.iterator(); it.hasNext();)
		{
			String s = MiscUtils.toString(it.next());
//			if(!MiscUtils.isEmpty(s))
//			{
				if(!first)
					str.append(del);
				str.append(s);
				first = false;
//			}
		}
		return str.toString();
	}
	
	/**
	 * Converts an input <code>Collection</code> of objects into a collection of the string
	 * representations of those objects.  The order of the elements in the returned collection is
	 * the same as those returned by <code>input.iterator()</code>.
	 * 
	 * @param input the input <code>Collection</code>
	 * @return <code>input</code> converted to a collection of strings
	 */
	public static Collection<String> toStringCollection(Collection<?> input)
	{
		if(input == null) throw new NullPointerException("input");
		List<String> values = new ArrayList<String>(input.size());
		for(Object o : input)
			values.add(MiscUtils.toString(o));
		return values;
	}


	/**
	 * Converts a deliminated string into a collection of the separated parts.
	 * Each delimited entry is trimmed of whitespace.
	 * 
	 * @param input the deliminated string
	 * @param del the deliminator charactor
	 * @return the <code>Collection</code> of the deliminated terms
	 */
	public static Collection<String> convertDelStrIntoCollection(String input,String del)
	{
		if(input == null) return null;
		if(del == null) throw new NullPointerException("del");
		String [] split = delimSplit(input,del);
		List<String> values = new ArrayList<String>(split.length);
		if(split.length != 1 || !MiscUtils.isEmpty(split[0]))
		{
			for(String s : split)
			{
				s = s.trim();
	//			if(!MiscUtils.isEmpty(s))
					values.add(s);
			}
		}
		return values;
	}
	
	private static String[] delimSplit(String s,String del)
	{
//		del = !del.endsWith("?") ? 
//				new StringBuilder(del.length() + 3).append('(').append(del).append(")+").toString() :
//					del;
//		return s.split(del);
		
		// don't discard trailing empty values
		return s.split(del,-1);
	}
	
	/**
	 * Converts a map into a delimited string.  Each map entry is trimmed of whitespace.
	 * 
	 * @param input the input map
	 * @param entryDel the delimiter sequence for separating map entries
	 * @param keyValueDelim the delimiter sequence for separating keys and values
	 * @return the delimited string
	 */
	public static String convertMapIntoDelStr(Map<?,?> input,String entryDel,String keyValueDelim)
	{
		if(input == null)
			return null;
		if(entryDel == null)
			throw new NullPointerException("entryDel");
		if(keyValueDelim == null)
			throw new NullPointerException("keyValueDel");
		
		StringBuilder str = new StringBuilder();
		
		boolean first = true;
		for(Iterator<?> it = input.entrySet().iterator();it.hasNext();)
		{
			Map.Entry<?,?> E = (Map.Entry<?, ?>)it.next();
			String key = MiscUtils.toString(E.getKey()).trim();
			String value = MiscUtils.toString(E.getValue()).trim();
			
//			if(!MiscUtils.isEmpty(key))
//			{
				if(!first)
					str.append(entryDel);
				str.append(key);
				str.append(keyValueDelim);
				str.append(value);
				first = false;
//			}
		}
		
		return str.toString();
	}
	
	/**
	 * This method is the inverse of {@link #convertMapIntoDelStr(Map, String, String)} and converts
	 * the string-delimited map back into a <code>java.util.Map</code>, but each key-value
	 * is a string.
	 * 
	 * @param input the delimited input string
	 * @param entryDel the delimiter sequence for separating map entries
	 * @param keyValueDelim the delimiter sequence for separating keys and values
	 * @return the parsed map
	 */
	public static Map<String,String> convertDelStrIntoMap(String input,String entryDel,String keyValueDelim)
	{
		if(input == null)
			return null;
		if(entryDel == null)
			throw new NullPointerException("entryDel");
		if(keyValueDelim == null)
			throw new NullPointerException("keyValueDel");
		
		Map<String,String> map = new LinkedHashMap<String,String>();
		
		String [] entries = delimSplit(input,entryDel);
		for(String entry : entries)
		{
			entry = entry.trim();
			if(!MiscUtils.isEmpty(entry))
			{
				String [] split = delimSplit(entry,keyValueDelim);
				if(split.length == 2)
				{
					map.put(split[0], split[1]);
				}
				// check for empty key or value
				else if(split.length == 1)
				{
					String keyValue = split[0];
					if(entry.startsWith(keyValueDelim))
						map.put("", keyValue);
					else if(entry.endsWith(keyValueDelim))
						map.put(keyValue, "");
					else
						throw new IllegalArgumentException("input=[" + input + "] contains invalid entry: " + entry);
						
				}
				// empty key and value
				else if(split.length == 0)
				{
					map.put("", "");
				}
				else // bad input
				{
					throw new IllegalArgumentException("input=[" + input + "] contains invalid entry: " + entry);
				}
				
			}
		}
		return map;
	}

        
        /**
         * Creates a <code>Map</code> from a <code>List</code> of pairs by mapping the first element
         * of each pair in the list to its corresponding second element.  The order in the map is the same
         * as the order in the list.  If a first element in a pair is repeated then the last such pair encountered
         * will be the entry in the map.
         * 
         * @param pairList the list of pairs
         * @return a <code>Map</code> of the pairs
         */
        public static <S,T> Map<S,T> createMapFromPairList(List<Pair<S,T>> pairList)
        {
            if(pairList == null)
                return null;
            Map<S,T> m = CollectionUtils.createLinkedHashMap(pairList.size(), LOAD_FACTOR);

            for(Pair<S,T> p : pairList)
                m.put(p.first,p.second);

            return m;
        }

        /*
         * Creates a <code>List</code> of pairs from the input <code>Map</code>.  Each pair is formed
         * from the key,value pairing in each entry in the map.  The order of the returned <code>List</code>
         * is the same as that of the iteration order of the map.
         *
         * @param the map
         * @return a <code>List</code> of pairs holding the entries of the map
         */
        public static <S,T> List<Pair<S,T>> createPairListFromMap(Map<S,T> map)
        {
            if(map == null)
                return null;

            List<Pair<S,T>> pairs = new ArrayList<Pair<S,T>>(map.size());

            for(Map.Entry<S,T> e : map.entrySet())
                pairs.add(Pair.makePair(e.getKey(), e.getValue()));

            return pairs;
        }


	/**
	 * Returns a new <code>ArrayList</code> containing only those elements
	 * in <code>origList</code> that are also present in <code>universe</code>.
	 * This method is meant to be a faster alternative to <code>Collection.retainAll</code>
	 * for <code>ArrayList</code> since removal operations take O(n) in <code>ArrayList</code>.
	 * 
	 * @param <E> type of element
	 * @param origList original element <code>ArrayList</code>
	 * @param universe <code>Set</code> of all valid elements
	 * @return new <code>ArrayList</code> containing only those elements
	 *         in <code>origList</code> that are also present in <code>universe</code>
	 */
	public static<E> ArrayList<E> retainAll(ArrayList<E> origList,Set<E> universe)
	{
		ArrayList<E> newElms = new ArrayList<E>(origList.size());
		for(E elm : origList)
		{
			if(universe.contains(elm))
				newElms.add(elm);
		}
		return newElms;
	}


	/**
	 * Compares whether two lists contain the same objects, irrespective of order.
	 * May be faster than {@link #unorderedCollectionEquals(Collection, Collection)}
	 * for lists.
	 * 
	 * @param lhs the first list
	 * @param rhs the second list
	 * @return <code>true</code> if the lists containing the same strings, disregarding
	 *         order, and <code>false</code> otherwise
	 */
	public static <T extends Comparable<? super T>> boolean unorderedListEquals(List<T> lhs,List<T> rhs)
	{
		return unorderedCollectionEquals(lhs,rhs);
	}
	
	/**
	 * Returns a read-only copy of 
	 * <code>Map</code> that orders its entries according
	 * to the natural order of its values.  The copy is read-only
	 * because further invocations of <code>Map.put</code> may break the 
	 * established ordering.
	 * 
	 * @param m the <code>Map</code> for which to return a value-ordered copy
	 * @return a read-only <code>Map</code> ordered by its values
	 * @throws NullPointerException if a <code>null</code> key or value exists in <code>m</code>
	 *         or if a key or value does not implement the <code>java.lang.Comparable</code> interface
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> valueSortedMap(Map<K,V> m)
	{
		if(m == null)
			throw new NullPointerException("m");
		// use an array since it is faster to sort than a list since no extra
		// copies are made
		Pair [] entries = new Pair [m.size()];
		int count = 0;
		for(Map.Entry<K, V> E : m.entrySet())
			entries[count++] = new Pair<K,V>(E.getKey(),E.getValue());
		Arrays.sort(entries);
		
		final float lf = CollectionUtils.LOAD_FACTOR;
		Map<K,V> orderedMap =
			new LinkedHashMap<K,V>(CollectionUtils.calcHashCapacity(
				m.size(), lf),lf);
		
		for(int i = 0, len = entries.length; i < len; ++i)
		{
			Pair<K,V> entry = entries[i];
			orderedMap.put(entry.first,entry.second);
		}
		
		return Collections.unmodifiableMap(orderedMap);

	}
	
	/**
	 * Creates a synchronization wrapper for <code>BidiMap</code>.
	 * 
	 * @param <K> key type
	 * @param <V> value type
	 * @param map the <code>BidiMap</code>
	 * @return a synchronized version of the original <code>map</code>
	 */
	public static <K,V> BidiMap<K,V> synchronizedBidiMap(BidiMap<K,V> map)
	{
		return new SynchronizedBidiMap<K,V>(map);
	}
	
	/**
	 * Creates a unmodifiable wrapper for <code>BidiMap</code>.
	 * 
	 * @param <K> key type
	 * @param <V> value type
	 * @param map the <code>BidiMap</code>
	 * @return an unmodifiable version of the original <code>map</code>
	 */
	public static <K,V> BidiMap<K,V> unmodifiableBidiMap(BidiMap<K,V> map)
	{
		return new UnmodifiableBidiMap<K,V>(map);
	}
	
	/**
	 * Wraps <code>map</code> in an object that implements the <code>BidiMap</code> interface.
	 * The <code>getKey</code> and <code>removeValue</code> operations of <code>BidiMap</code>
	 * as well as the <code>containsValue</code> method of <code>java.util.Map</code>
	 * may be implemented by iterating over the entry set of <code>map</code> and therefore can
	 * be expected to perform in <code>O(n)</code> time.  The resulting map is unsynchronized;
	 * a synchronized version can be obtained by calling {@link #synchronizedBidiMap(BidiMap)}
	 * with the returned <code>BidiMap</code>.
	 * 
	 * @param <K> key type
	 * @param <V> value type
	 * @param map the <code>Map</code> to wrap
	 * @return a <code>BidiMap</code> wrapper around <code>map</code>
	 */
	public static <K,V> BidiMap<K,V> bidiMapWrapper(Map<K,V> map)
	{
		return new BidiMapWrapper<K,V>(map);
	}
	
	
	/**
	 * Removes the <code>null</code> values from the list.
	 * 
	 * @param values the values
	 */
	public static void removeNulls(List<?> values)
	{
		if(values != null)
		{
			for(ListIterator<?> i = values.listIterator(values.size());i.hasPrevious();)
			{
				Object value = i.previous();
				if(value == null)
					i.remove();
			}
		}
	}
	
	/**
	 * Removes the duplicates from the given list and optionally stores elements
	 * that were duplicated in <code>outDuplicated</code>.
	 * 
	 * @param list the <code>List</code>
	 * @param outDuplicated <code>Set</code> to store duplicated elements or <code>null</code>
	 *   
	 */
	public static <T> void removeDuplicates(List<T> list,Set<T> outDuplicated)
	{
		for(ListIterator<T> it = list.listIterator(); it.hasNext();)
		{
			T first = it.next();
			while(it.hasNext())
			{
				T second = it.next();
				// remove and note the duplicate
				if(MiscUtils.safeEquals(first, second))
				{
					it.remove();
					if(outDuplicated != null)
						outDuplicated.add(second);
				}
				// compare second with following element
				else
				{
					it.previous();
					break;
				}
			}
		}
	}
	
	/**
	 * Removes all occurrences of <code>e</code> from <code>c</code>.
	 * 
	 * @param c the <code>Collection</code>
	 * @param e the element to remove
	 */
	public static <T> void removeAll(Collection<?> c,Object e)
	{
		if(c instanceof List)
		{
			List<?> list = (List<?>)c;
			
			int index = -1;
			do
			{
				index = list.lastIndexOf(e);
				if(index != -1)
					list.remove(index);
			}
			while(index != -1);
			
		}
		else if(c instanceof Set)
		{
			c.remove(e);
		}
		else
		{
			while(c.remove(e))
				;
		}
	}


        public static <T extends Comparable<? super T>> Comparator<T> createComparatorFromComparable(T c)
        {
            return new Comparator<T>()
            {

                public int compare(T s, T t)
                {
                    return s.compareTo(t);
                }

            };
        }	

}
