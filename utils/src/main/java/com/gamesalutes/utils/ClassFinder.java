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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Finds classes in specified packages.
 * 
 * @author Justin Montgomery
 * @version $Id: ClassFinder.java 1868 2010-01-26 00:54:28Z jmontgomery $
 */
public final class ClassFinder
{
	private ClassFinder() {}
	
    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader.
     * 
     * @param packageName the name of the package
     *            the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException if there is a problem getting the classes
     */
    public static List<Class<?>> getClassesForPackage(String packageName)
    	throws ClassNotFoundException
    {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        addClassFiles(packageName,classes);
//        addJarClassFiles(packageName,classes);
        
        return classes;
    }
    

    private static void addClassFiles(String packageName,List<Class<?>> classes)
    	throws ClassNotFoundException
    {
        // This will hold a list of directories matching the packageName
    	// There may be more than one if a package is split over multiple paths
        List<String> resources = new ArrayList<String>();
        try 
        {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = !MiscUtils.isEmpty(packageName) ? packageName.replace('.', '/') : "/";
            // Ask for all resources for the path
            Enumeration<URL> elms = loader.getResources(path);
            while (elms.hasMoreElements()) {
                resources.add(FileUtils.uriDecodeFile(elms.nextElement().toString()));
            }
        } 
        catch (Exception e) 
        {
            throw new ClassNotFoundException(packageName + " does not appear to be a valid package",e);
        }
 
        // For every directory identified capture all the .class files
        for (String resourcePath : resources)
        {
        	int jarFileIndex = resourcePath.indexOf("!/"); 
        	// it is a jar file if it contains "!/
        	if(jarFileIndex == -1)
        	{
                // Get the list of the files contained in the package
        		File directory = new File(resourcePath);
                String[] files = directory.list();
                if(files != null)
                {
	                for (String file : files) 
	                {
	                    // we are only interested in .class files
	                    if (StringUtils.caseInsensitiveEndsWith(file,".class"))
	                    {
	                        // removes the .class extension
	                    	String className =  file.substring(0, file.length() - 6);
	                    	String path;
	                    	if(!MiscUtils.isEmpty(packageName))
	                    		path = packageName + '.' + className;
	                    	else
	                    		path = className;
	                        classes.add(Class.forName(path));
	                    }
	                }
                }
            } 
            else 
            {
            	// lookup into the jar file
            	addJarClassFiles(FileUtils.uriDecodeFile(
            			resourcePath.substring(0,jarFileIndex)),packageName,classes);
            }
        }
    }
    
//  private static void addJarClassFiles(String packageName,List<Class<?>> classes)
//	throws ClassNotFoundException
//{
//	String classpath = System.getProperty("java.class.path");
//	String pathSep = System.getProperty("path.separator");
//	String [] split = MiscUtils.split(classpath, pathSep);
//	for(String entry : split)
//	{
//		// see if its a jar file
//		if(StringUtils.caseInsensitiveEndsWith(entry, ".jar"))
//		{
//			addJarClassFiles(entry,packageName,classes);
//		}
//	}
//}
private static void addJarClassFiles(String jarFile,String packageName,List<Class<?>> classes)
	throws ClassNotFoundException
{
	JarFile jar = null;

	try
	{
    	jar = new JarFile(jarFile,false);
		Enumeration<JarEntry> e = jar.entries();
		boolean emptyPackage = MiscUtils.isEmpty(packageName);
		
		// see if the entry is in the specified package
		while(e.hasMoreElements())
		{
			JarEntry inputEntry = e.nextElement();
			String resourceName = inputEntry.getName();
			if(StringUtils.caseInsensitiveEndsWith(resourceName, ".class"))
			{
				String path = null;
				// find the package path
				int index = resourceName.lastIndexOf('/');
				if(index == -1)
					index = resourceName.lastIndexOf('\\');
				if(index != -1)
				{
					String jarPackage = resourceName.substring(0,index);
					if(packageEquals(jarPackage,packageName))
					{
                    	String className =  resourceName.substring(index+1, resourceName.length() - 6);
                    	path = packageName + '.' + className;
					}
				}
				else if(emptyPackage)
				{
					path = resourceName.substring(0,resourceName.length() - 6);
				}
				if(path != null)
				{
                    classes.add(Class.forName(path));
				}
			}
		} // while
	} // try
	catch(IOException e)
	{
		throw new ClassNotFoundException("Unable to enumerate jarFile=" + jarFile,e);
	}
	finally
	{
		if(jar != null)
		{
			try
			{
				jar.close();
			}
			catch(IOException e) {}
		}
	}
	
}

private static boolean packageEquals(CharSequence lhs,CharSequence rhs)
{
	int len1 = lhs.length();
	int len2 = rhs.length();
	
	if(len1 != len2) return false;
	
	for(int i = 0, len = len1; i < len; ++i)
	{
		char c1 = lhs.charAt(i);
		char c2 = rhs.charAt(i);
		
		// accept any combo of / . or \\
		boolean firstSep = c1 == '.' || c1 == '/' || c1 == '\\';
		boolean secondSep = c2 == '.' || c2 == '/' || c2 == '\\';
		
		// either both are separators or both aren't
		if(firstSep != secondSep) return false;
		
		// if they aren't separators then characters must match
		if(!firstSep && c1 != c2) return false;
		
	}
	
	return true;
}
}
