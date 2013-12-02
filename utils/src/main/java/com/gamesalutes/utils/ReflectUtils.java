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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains some utilities for reflection.
 * 
 * @author Justin Montgomery
 * @version $Id: ReflectUtils.java 1028 2008-07-30 21:50:47Z jmontgomery $
 */
public final class ReflectUtils 
{
	private ReflectUtils() {}
	
	public static Map<String,Method> getAllMethods(Class<?> clazz,boolean setAllAccessible)
	{
		Map<String,Method> methods = new HashMap<String,Method>();
		getAllMethods0(clazz,methods);
		if(setAllAccessible)
		{
			AccessibleObject.setAccessible(
					methods.values().toArray(new AccessibleObject[0]),
					true);
		}
		return methods;
	}
	
	public static Map<String,Field> getAllFields(Class<?> clazz,boolean setAllAccessible)
	{
		Map<String,Field> fields = new HashMap<String,Field>();
		getAllFields0(clazz,fields);
		if(setAllAccessible)
		{
			AccessibleObject.setAccessible(fields.values().toArray(new AccessibleObject[0]),
					true);
		}
		return fields;
	}
	
	
	private static void getAllMethods0(Class<?> clazz,
			Map<String,Method> methods)
	{
		//Collection<Method> toAdd = new HashSet<Method>();
		Method [] declaredMethods = clazz.getDeclaredMethods();
		// be sure to get all declared methods and public methods so that interface
		// methods are added
		//Method [] publicMethods = clazz.getMethods();
		//if(declaredMethods != null) Collections.addAll(toAdd, declaredMethods);
		//if(publicMethods != null) Collections.addAll(toAdd, publicMethods);
		
		if(declaredMethods != null)
		{
			for(Method m : declaredMethods)
			{
				// mangle name so that duplicate entries are allowed
				String name = createMangledMethodName(m.getName(),m.getParameterTypes());
				if(!methods.containsKey(name))
					methods.put(name,m);
			}
		}
		// call recursively on superclass
		Class<?> superClazz = clazz.getSuperclass();
		if(superClazz != null)
			getAllMethods0(superClazz,methods);
	}
	
	private static void getAllFields0(Class<?> clazz,Map<String,Field> fields)
	{
		//Collection<Method> toAdd = new HashSet<Method>();
		Field [] declaredFields = clazz.getDeclaredFields();
		// be sure to get all declared methods and public methods so that interface
		// methods are added
		//Method [] publicMethods = clazz.getMethods();
		//if(declaredMethods != null) Collections.addAll(toAdd, declaredMethods);
		//if(publicMethods != null) Collections.addAll(toAdd, publicMethods);
		
		if(declaredFields != null)
		{
			for(Field f : declaredFields)
			{
				// mangle name so that duplicate entries are allowed
				String name = createMangledFieldName(f.getName(),clazz);
				if(!fields.containsKey(name))
					fields.put(name,f);
			}
		}
		// call recursively on superclass
		Class<?> superClazz = clazz.getSuperclass();
		if(superClazz != null)
			getAllFields0(superClazz,fields);
	}
	
	public static String createMangledMethodName(
			String methodName,Class<?> [] parameterTypes)
	{
		if(methodName == null)
			throw new NullPointerException("methodName");
		StringBuilder newName = new StringBuilder(methodName.length()*3);
		newName.append(methodName);
		if(!MiscUtils.isEmpty(parameterTypes))
			for(Class<?> c : parameterTypes)
				newName.append("__").append(c.getName());
		return newName.toString();
	}
	
	public static String createMangledFieldName(
			String fieldName,Class<?> declaringClass)
	{
		if(fieldName == null)
			throw new NullPointerException("fieldName");
		if(declaringClass == null)
			throw new NullPointerException("declaringClass");
		String clazzName = declaringClass.getName();
		return new StringBuilder(fieldName.length() + clazzName.length() + 2
				).append(fieldName).append("__").append(clazzName).toString();
	}
}
