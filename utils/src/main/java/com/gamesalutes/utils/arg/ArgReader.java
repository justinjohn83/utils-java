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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gamesalutes.utils.StringUtils;
import com.gamesalutes.utils.BidiMap;
import com.gamesalutes.utils.CollectionUtils;
import com.gamesalutes.utils.DualHashBidiMap;
import com.gamesalutes.utils.MiscUtils;

/**
 * Gnu get-opt style command line argument reader utility.
 * 
 * @author Justin Montgomery
 * @version $Id:$
 */
public final class ArgReader
{
	private Map<String,Object> optionMap;
	private Map<String,List<Object>> optionValueMap;
	private boolean parsed;
	private List<String> args;
	private BidiMap<String,String> longShortMap;
	private String [] inputArgs;
	
	/**
	 * Constructor.
	 * 
	 */
	public ArgReader()
	{
		optionMap = new HashMap<String,Object>();
		optionValueMap = new HashMap<String,List<Object>>();
		args = new ArrayList<String>();
		longShortMap = new DualHashBidiMap<String,String>();
		
	}
	
	/**
	 * Adds a boolean option with the given long name.
	 * 
	 * @param name the name of the option
	 * @return this for chaining
	 */
	public ArgReader addBooleanOption(String name)
	{
		return addOption(null,name,Boolean.class);
	}
	
	/**
	 * Adds a boolean option with the given short and long forms.
	 * 
	 * @param shortForm the short form of the option 
	 * @param longForm the long form of the option 
	 * @return this for chaining
	 */
	public ArgReader addBooleanOption(char shortForm,String longForm)
	{
		return addOption(shortForm,longForm,Boolean.class);
	}

       /**
	 * Adds an object option with the given long name.
	 *
	 * @param name the name of the option
         *  @param parser the <code>ArgValue</code> parser to convert into appropriate object
	 * @return this for chaining
	 */
        public ArgReader addObjectOption(String name,ArgValue parser)
        {
            return addOption(null,name,parser);
        }

       /**
	 * Adds an object option with the given short and long forms.
	 * @param shortForm the short form of the option
	 * @param name the name of the option
         * @param parser the <code>ArgValue</code> parser to convert into appropriate object
	 * @return this for chaining
	 */
        public ArgReader addObjectOption(char shortForm,String longForm,ArgValue parser)
        {
            return addOption(shortForm,longForm,parser);
        }
	
	/**
	 * Adds an integer option with the given long name.
	 * 
	 * @param name the name of the option
	 * @return this for chaining
	 */
	public ArgReader addIntegerOption(String name)
	{
		return addOption(null,name,Integer.class);
	}
	
	/**
	 * Adds an integer option with the given short and long forms.
	 * 
	 * @param shortForm the short form of the option
	 * @param longForm the long form of the option
	 * @return this for chaining
	 */
	public ArgReader addIntegerOption(char shortForm,String longForm)
	{
		return addOption(shortForm,longForm,Integer.class);
	}
	
	/**
	 * Adds an double option with the given long name.
	 * 
	 * @param name the name of the option
	 * @return this for chaining
	 */
	public ArgReader addDoubleOption(String name)
	{
		return addOption(null,name,Double.class);
	}
	
	/**
	 * Adds a double option with the given short and long forms.
	 * 
	 * @param shortForm the short form of the option
	 * @param longForm the long form of the option
	 * @return this for chaining
	 */
	public ArgReader addDoubleOption(char shortForm,String longForm)
	{
		return addOption(shortForm,longForm,Double.class);
	}
	
	/**
	 * Adds a long option with the given long name.
	 * 
	 * @param name the name of the option
	 * @return this for chaining
	 */
	public ArgReader addLongOption(String name)
	{
		return addOption(null,name,Long.class);
	}
	
	/**
	 * Adds a long option with the given short and long forms.
	 * 
	 * @param shortForm the short form of the option
	 * @param longForm the long form of the option
	 * @return this for chaining
	 */
	public ArgReader addLongOption(char shortForm,String longForm)
	{
		return addOption(shortForm,longForm,Long.class);
	}
	
	/**
	 * Adds a string option with the given name.  If the length of <code>name</code>
	 * is > 1, then a long-named option is added; otherwise, a short-named option is added.
	 * 
	 * @param name the name of the option
	 * @return this for chaining
	 */
	public ArgReader addStringOption(String name)
	{
		return addOption(null,name,String.class);
	}
	
	/**
	 * Adds a string option with the given short and long forms.
	 * 
	 * @param shortForm the short form of the option
	 * @param longForm the long form of the option
	 * @return this for chaining
	 */
	public ArgReader addStringOption(char shortForm,String longForm)
	{
		return addOption(shortForm,longForm,String.class);
	}
	
	private void reset()
	{
		 parsed = false;
		 optionValueMap.clear();
		 this.args.clear();
		 this.inputArgs = null;
	}
	
	/**
	 * Clears the state of this reader so that all option mappings are removed.
	 * 
	 */
	public void clear()
	{
		 reset();
		 optionMap.clear();
		 longShortMap.clear();
	}
	private ArgReader addOption(Character shortForm,String longForm,Object type)
	{
		 if(parsed && !optionMap.isEmpty())
			 throw new IllegalStateException("cannot add option after parsing: must clear first!");
		 
		 longForm = StringUtils.trimNull(longForm);
		 
		 if(longForm == null && shortForm == null)
			 throw new IllegalArgumentException();
		 // see if given long form actually a short form also
		 if(shortForm == null && longForm != null)
		 {
			 if(longForm.length() == 1)
				 shortForm = longForm.charAt(0);
		 }
		 if(shortForm != null)
			 optionMap.put(shortForm.toString(), type);
		 if(longForm != null)
			 optionMap.put(longForm, type);
		 if(shortForm != null && longForm != null)
			 longShortMap.put(longForm, shortForm.toString());
		 
		 // add the mapping
		 return this;
	}
	
	/**
	 * Parses the given arguments using the currently set options of the reader.
	 * 
	 * @param args the command line arguments
	 * @throws ArgumentException if error occurs during parsing from invalid options being specified
	 */
	public void parse(String [] args)
		throws ArgumentException
	{
		reset();
		
		if(!MiscUtils.isEmpty(args))
		{
		
			this.inputArgs = args;
			
			process();
			
		}
		
		parsed = true;
	}
	
	private boolean isBoolean(String option)
		throws ArgumentException
	{
		Object type = optionMap.get(option);
		if(type == null)
		{
			throw new ArgumentException("Unknown option \"" + toPrintOption(option)  + "\": " + Arrays.toString(inputArgs));
		}
		return type == Boolean.class;
	}
	private void addValue(String option,Object value)
		throws ArgumentException
	{
		if(value == null)
			throw new AssertionError("value=null");
		
		// lookup in map
		Object type = optionMap.get(option);
		if(type == null)
		{
			throw new ArgumentException("Unknown option \"" + toPrintOption(option)  + "\": " + Arrays.toString(inputArgs));
		}

                if(type == Boolean.class)
		{
			if(!(value instanceof Boolean))
				throw new ArgumentException("flag option \"" + toPrintOption(option) + "\" was valued=" + value + ": " + Arrays.toString(inputArgs));
		}
		else
		{
			if(!(value instanceof String))
				throw new AssertionError("value \"" + value + "\": " + MiscUtils.getClassName(value) + " not a string");
			
			                // parse custom value
                        if(type instanceof ArgValue)
                        {
                            try
                            {
                                value = ((ArgValue)type).getValue((String)value);
                            }
                            catch(Exception e)
                            {
                                throw new ArgumentException("value=" + value + " for option=" + toPrintOption(option) + " could not be converted by \"" + type.getClass().getCanonicalName() + "\": " + Arrays.toString(inputArgs),e);
                            }
                        }
                        else if(type == Integer.class)
			{
				try
				{
					value = Integer.valueOf((String)value);
				}
				catch(NumberFormatException e)
				{
					throw new ArgumentException("value=" + value + " for option=" + toPrintOption(option) + " not an integer: " + Arrays.toString(inputArgs));
				}
			}
			else if(type == Double.class)
			{
				try
				{
					value = Double.valueOf((String)value);
				}
				catch(NumberFormatException e)
				{
					throw new ArgumentException("value=" + value + " for option=" + toPrintOption(option) + " not a double: " + Arrays.toString(inputArgs));
				}
			}
			else if(type == Long.class)
			{
				try
				{
					value = Long.valueOf((String)value);
				}
				catch(NumberFormatException e)
				{
					throw new ArgumentException("value=" + value + " for option=" + toPrintOption(option) + " not a long: " + Arrays.toString(inputArgs));
				}
			}
		}
		
		// put the value
		putValue(option,value);

	}
	
	private void putValue(String option,Object value)
	{
		List<Object> values = optionValueMap.get(option);
		if(values == null)
		{
			values = new ArrayList<Object>();
			optionValueMap.put(option, values);
			
			// put to the alias
			String alias = longShortMap.get(option);
			if(alias == null)
				alias = longShortMap.getKey(option);
			if(alias != null)
				optionValueMap.put(alias, values);
		}
		values.add(value);
	}
	
	private void completeBooleans()
		throws ArgumentException
	{
		for(Map.Entry<String, Object> E: optionMap.entrySet())
		{
			String option = E.getKey();
			Object type = E.getValue();
			if(type == Boolean.class && !optionValueMap.containsKey(option))
				addValue(option,Boolean.FALSE);
		}
	}
	
	private String toPrintOption(String optionKey)
	{
		if(optionKey.length() > 1)
			return "--" + optionKey;
		else
			return "-" + optionKey;
	}
	private void process()
		throws ArgumentException
	{
		// extract the quoted arguments
		String [] extractedArgs = ArgUtils.extractStringValues(this.inputArgs);

                String inputArgStr = CollectionUtils.convertCollectionIntoDelStr(Arrays.asList(this.inputArgs)," ");
		
		String lastOption = null;
		
		for(int i = 0; i < extractedArgs.length; ++i)
		{
			String rawArg = extractedArgs[i];

                        int rawArgIndex = inputArgStr.indexOf(rawArg);
                        boolean quotedArg = false;
                        if(rawArgIndex > 0 && inputArgStr.charAt(rawArgIndex-1) == '"')
                            quotedArg = true;

			
			int argLen = rawArg.length();
			
			// option
			if(!quotedArg && rawArg.charAt(0) == '-')
			{
				if(lastOption != null)
					throw new ArgumentException("unvalued option \"" + toPrintOption(lastOption) + "\"" + Arrays.toString(this.inputArgs));
				
				if(argLen == 1 || (argLen == 2 && rawArg.charAt(1) == '-'))
					throw new ArgumentException("empty option - : " + Arrays.toString(this.inputArgs));
				if(argLen > 2 && rawArg.charAt(1) != '-')
					throw new ArgumentException("invalid option: " + rawArg + " long form requires \"--\": " + Arrays.toString(this.inputArgs));
				
				if(argLen == 2)
					lastOption = rawArg.substring(1);
				else
					lastOption = rawArg.substring(2);
				

				
				// simple switch
				if(isBoolean(lastOption))
				{
					addValue(lastOption,Boolean.TRUE);
					lastOption = null;
				}
			}
			// option argument or program argument
			else
			{
				// value the option
				if(lastOption != null)
				{
					addValue(lastOption,rawArg);
					lastOption = null;
				}
				// store as a program argument
				else
				{
					this.args.add(rawArg);
				}
			}
		} // for
		
		completeBooleans();
	}
	
	/**
	 * Returns the value of the option with the given name or <code>null</code> if the
	 * option was not among the parsed tokens.  If more than one option with the given name 
	 * existed, then the first one in the original argument list is returned.
	 * 
	 * @param name the name of the option
	 * @throws IllegalArgumentException if <code>name</code> was not a configured option
	 * @throws IllegalStateException if <code>parse</code> has not been called
	 * @return the option value
	 */
	public <T> T getOptionValue(String name)
	{
		return (T)getOptionValue(name,null);
	}
	
	/**
	 * Returns the value of the option with the given name or <code>defaultValue</code> if the
	 * option was not among the parsed tokens.  If more than one option with the given name 
	 * existed, then the first one in the original argument list is returned.
	 * 
	 * @param name the name of the option
	 * @throws IllegalArgumentException if <code>name</code> was not a configured option
	 * @throws IllegalStateException if <code>parse</code> has not been called
	 * @return the option value
	 */
	public <T> T getOptionValue(String name,T defaultValue)
	{
		List<T> values = getOptionValues(name,defaultValue);
		if(!values.isEmpty())
			return values.get(0);
		return null;
	}
	
	/**
	 * Returns the value of the options with the given name or an empty list if the
	 * option was not among the parsed tokens.  
	 * 
	 * @param name the name of the option
	 * @throws IllegalArgumentException if <code>name</code> was not a configured option
	 * @throws IllegalStateException if <code>parse</code> has not been called
	 * @return the option values
	 */
	public <T> List<T> getOptionValues(String name)
	{
		return (List<T>)getOptionValues(name,null);
	}
	
	/**
	 * Returns the value of the options with the given name or a list with <code>defaultValue</code>
	 * if the option was not among the parsed tokens and <code>defaultValue</code> is not <code>null</code>.   
	 * 
	 * @param name the name of the option
	 * @throws IllegalArgumentException if <code>name</code> was not a configured option
	 * @throws IllegalStateException if <code>parse</code> has not been called
	 * @return the option values
	 */
	public <T> List<T> getOptionValues(String name,T defaultValue)
	{
		if(!parsed)
			throw new IllegalStateException();
		if(!this.optionMap.containsKey(name))
			throw new IllegalArgumentException("name=" + name);
		List<T> values = (List<T>)optionValueMap.get(name);
		
		if(MiscUtils.isEmpty(values))
		{
			values = new ArrayList<T>(1);
			if(defaultValue != null)
				values.add(defaultValue);
			return values;
		}	
		
		return new ArrayList<T>(values);
	}
	
	/**
	 * Returns the non-option values among the original argument list in the order they appeared in the original list.
	 * 
	 * @throws IllegalStateException if <code>parse</code> has not been called
	 * @return the remaining arguments
	 */
	public String [] getRemainingArguments()
	{
		if(!parsed)
			throw new IllegalStateException();
		
		return this.args.toArray(new String[args.size()]);
	}
	
	/**
	 * Removes the argument in the array of remaining arguments as given by <code>getRemainingArguments</code>
	 * with the given index.
	 * 
	 * @param index the index of the argument
	 * @return this for chaining
	 * @throws IllegalStateException if <code>parse</code> has not been called
	 * @throws IndexOutOfBoundsException if <code>index &lt 0 </code> or <code>index &gt= getRemainingArguments().length</code>
	 */
	public ArgReader consumeArgument(int index)
	{
		if(!parsed)
			throw new IllegalStateException();
		if(index < 0 || index >= this.args.size())
			throw new IndexOutOfBoundsException("index=" + index);
		this.args.remove(index);
		
		return this;
	}
	
	@Override
	public String toString()
	{
		return Arrays.toString(this.inputArgs);
	}
}
