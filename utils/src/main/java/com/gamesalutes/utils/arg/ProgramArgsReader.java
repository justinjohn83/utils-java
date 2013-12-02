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
package com.gamesalutes.utils.arg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.gamesalutes.utils.ChainedIOException;
import com.gamesalutes.utils.DOMUtils;
import com.gamesalutes.utils.FileUtils;
import com.gamesalutes.utils.MiscUtils;

/**
 * Utility for reading command line arguments for multiple programs to be run together.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ProgramArgsReader
{
	
	private static final String ARGS_XSD = "args.xsd";
	private static final String CLASS_ATTR = "class";
	private static final String ORDER_ATTR = "order";
	private static final String SKIP_ATTR = "skip";
	private static final String ARG_OPTION_ATTR = "option";
	private static final String ARG_VALUE_ATTR = "value";
	private static final String ARG_VALUE_ELM = "value";
	
	
	/**
	 * Executable java process as given in the argument xml file.
	 * 
	 * @author Justin Montgomery
	 * @version $Id:$
	 */
	public static class Program
	{
		private final Class<?> clazz;
		private final String [] args;
		private int order;
		
		/**
		 * Constructor.
		 * 
		 * @param clazz main class of the program
		 * @param args arguments to pass to the program
		 * @param order the execution order relative to other given programs
		 */
		Program(Class<?> clazz,String [] args,int order)
		{
			this.clazz = clazz;
			this.args = args;
			this.order = order;
		}
		
		/**
		 * Returns the class with the main method for the program.
		 * 
		 * @return the class
		 */
		public Class<?> getProgramClass()
		{
			return clazz;
		}
		
		/**
		 * Returns the arguments to pass to the program's main method.
		 * 
		 * @return the arguments
		 */
		public String [] getProgramArgs()
		{
			return args;
		}
		
		/**
		 * Returns the execution order of this program relative to the other programs.
		 * 
		 * @return the execution order
		 */
		public int getOrder()
		{
			return order;
		}
		
		@Override
		public String toString()
		{
			StringBuilder buf = new StringBuilder(128);
			
			buf.append("class=").append(clazz.getName()
			  ).append(";args=").append(Arrays.toString(args)
			  ).append(";order=").append(order);
			
			return buf.toString();
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o)
				return true;
			if(!(o instanceof Program))
				return false;
			Program p = (Program)o;
			
			return clazz == p.clazz &&
			       order == p.order &&
			       Arrays.equals(args, p.args);
		}
		
		@Override
		public int hashCode()
		{
			int result = 17;
			final int mult = 31;
			
			result = mult * result + clazz.hashCode();
			result = mult * result + order;
			result = mult * result + Arrays.hashCode(args);
			
			return result;
			
		}
		
	}
	
	// input classes must specify a static execute(String[],RequestCommunicator) method
	private static class ProgramComparator implements Comparator<Program>
	{
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compare(Program p1,Program p2)
		{
			return p1.order - p2.order;
		}
	}
	
	private final List<Program> programs;
	
	/**
	 * Constructor.
	 * 
	 * @param in <code>InputStream</code> to arguments xml file.
	 * 
	 * @throws IOException if error occurs reading from <code>in</code> or parsing the input
	 */
	public ProgramArgsReader(InputStream in)
		throws IOException
	{
		
		Document doc = DOMUtils.loadDocument(
				in, DOMUtils.Validation.SCHEMA, FileUtils.getFileAsStream(ProgramArgsReader.class, ARGS_XSD));
		
		this.programs = parseDocument(doc);
	}
	
	/**
	 * Returns the parsed programs.
	 * 
	 * @return the parsed programs
	 */
	public List<Program> getPrograms()
	{
		return programs;
	}
	
	private List<Program> parseDocument(Document doc)
			throws IOException
	{
		List<Program> programs = new ArrayList<Program>();
		Set<Integer> encounteredOrders = new HashSet<Integer>();
		
		NodeList modules = doc.getDocumentElement().getChildNodes();
		// parse the modules
		for(int i = 0, len = modules.getLength(); i < len; ++i)
		{
			Program p = parseModule((Element)modules.item(i));
			if(p != null)
			{
				// validate the program
				// must not have same order declared twice
				if(!encounteredOrders.add(p.getOrder()))
				{
					throw new IOException("execution order: " + p.getOrder() + " specified for class \"" + p.getClass().getName() + "\" already specified for another class");
				}
				
				programs.add(p);
			}
		}
		
		// sort the programs
		Collections.sort(programs,new ProgramComparator());
		
		return programs;
	}
	
	private Program parseModule(Element module)
		throws IOException
	{
		// read the class,order, and skip attributes
		String className = module.getAttribute(CLASS_ATTR);
		int order = Integer.parseInt(module.getAttribute(ORDER_ATTR));
		boolean skip = Boolean.parseBoolean(module.getAttribute(SKIP_ATTR));
		
		Class<?> clazz;
		String [] args;
		
		// go no further
		if(skip) 
			return null;
		
		// instantiate the class
		try
		{
			clazz = Class.forName(className);
		}
		catch(Exception ex)
		{
			throw new ChainedIOException("class name \"" + className + "\" is not a valid class",ex);
		}
		
		// read arguments		
		ArgWriter argWriter = new ArgWriter();
		
		NodeList argElms = module.getChildNodes();
		
		for(int i = 0, len = argElms.getLength(); i < len; ++i)
		{
			readArgument(className,argWriter,(Element)argElms.item(i));
		}
		
		args = argWriter.generate();
		
		return new Program(clazz,args,order);
	}
	
	private void readArgument(String className,ArgWriter writer,Element argElm)
		throws IOException
	{
		String optionAttr = argElm.getAttribute(ARG_OPTION_ATTR);
		String valueAttr = argElm.hasAttribute(ARG_VALUE_ATTR) ? argElm.getAttribute(ARG_VALUE_ATTR) : null;
		
		// see if there are value elements
		NodeList valueElms = argElm.getChildNodes();
		if(valueElms != null && valueElms.getLength() > 0)
		{
			if(!MiscUtils.isEmpty(valueAttr))
				throw new IOException(className + ": cannot specify both value attribute and value child element");
			
			for(int i = 0, len = valueElms.getLength(); i < len; ++i)
			{
				String value = valueElms.item(i).getTextContent().trim();
				
				if(!MiscUtils.isEmpty(optionAttr))
					writer.addOption(optionAttr, value);
				else if(!MiscUtils.isEmpty(value))
					writer.addArgument(value);
			}
			
		}
		else
		{
			// null value attr means this is a command line switch
			if(!MiscUtils.isEmpty(optionAttr))
				writer.addOption(optionAttr, valueAttr);
			else if(!MiscUtils.isEmpty(valueAttr))
				writer.addArgument(valueAttr);
		}
	}
}
