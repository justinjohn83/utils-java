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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Contains utility methods for working with files.
 *
 * @author Justin Montgomery
 * @version $Id: FileUtils.java 2798 2011-04-28 17:16:48Z jmontgomery $
 *
 */
public final class FileUtils
{
	private FileUtils() {}

	private static final int BUF_SIZE = 4096;
	
	private static String appDir = null;


        /**
         * Processor for a line of text.
         *
         */
        public interface LineProcessor
        {
            /**
             * Process the current <code>line</code> of text
             *
             * @param line the current line
             */
            void process(String line);
        }


	/**
	 * Copies the file pointed to by <code>inputPath</code> to the
	 * <code>outputPath</code>.  If <code>inputPath</code> is the same as
	 * <code>outputPath</code> path, then this method has no effect.
	 * If <code>inputPath</code> is a directory then its contents are copied recursively
	 * to the directory represented by <code>outputPath</code>.
	 * (The inputFile directory name itself
	 * will not appear inside <code>outputFile</code>).
	 *
	 * @param inputPath file name of input file
	 * @param outputPath file name of output file
	 * @throws IOException if error occurs during copying
	 */
	public static void copyFile(String inputPath,String outputPath)
		throws IOException
	{
		if(inputPath == null)
			throw new NullPointerException("inputPath");
		if(outputPath == null)
			throw new NullPointerException("outputPath");

		copyFile(FileUtils.newFile(inputPath),FileUtils.newFile(outputPath));
	}
	
	
	/**
	 * Lists all the files recursively in <code>dir</code>.  If <code>dir</code>
	 * does not exist or it is not a directory then an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param dir the input path to list
	 * @return all the files along <code>path</code>
	 */
	public static Collection<String> listFiles(File dir)
	{
		return listFiles(dir,null);
	}
	/**
	 * Lists all the files recursively along <code>dir</code> that pass the specified 
	 * <code>filter</code>.  If <code>dir</code>
	 * does not exist or it is not a directory then an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param dir the input path to list
	 * @return all the files along <code>path</code>
	 */
	public static Collection<String> listFiles(File dir,FileFilter filter)
	{
		if(dir == null)
			throw new NullPointerException("dir");
		
		List<String> files = new ArrayList<String>();
		listFiles0(dir,filter,files);
		return files;
	}
	
	private static void listFiles0(File dir,FileFilter filter,Collection<String> files)
	{
		if(!dir.exists())
			throw new IllegalArgumentException("dir=" + dir + " does not exist");
		if(!dir.isDirectory())
			throw new IllegalArgumentException("dir=" + dir + " is not a directory");
		
		File [] contents = dir.listFiles(filter);
		
		for(File f : contents)
		{
			if(f.isDirectory())
				listFiles0(f,filter,files);
			else
				files.add(f.toString());
				
		}
	}

	/**
	 * Deletes the specified (if it is a directory it deletes all its contents recursively).
	 *
	 * @param file the <code>File</code> to delete
	 */
	public static void deleteFile(File file)
	{
		if(file.isDirectory())
		{
			for(File f : file.listFiles())
				deleteFile(f);

		}
		boolean delete = file.delete();
		if(!delete)
			file.deleteOnExit();
	}

	/**
	 * Deletes the specified (if it is a directory it deletes all its contents recursively).
	 *
	 * @param file the file to delete
	 */
	public static void deleteFile(String file)
	{
		deleteFile(FileUtils.newFile(file));
	}

        /**
         * Creates the given directory and its dependency if it does not yet exist and ensures that the directory is writable.
         *
         *
         * @param dir the directory
         *
         * @throws IOException if the directory does not yet exist and cannot be created or the directory is not writable or the directory is a regular file
         */
        public static void createDirectory(String dir)
                throws IOException
        {
            createDirectory(new File(dir));
        }

        /**
         * Creates the given directory and its dependency if it does not yet exist and ensures that the directory is writable.
         *
         *
         * @param dir the directory
         *
         * @throws IOException if the directory does not yet exist and cannot be created or the directory is not writable or the directory is a regular file
         */
        public static void createDirectory(File dir)
                throws IOException
        {
            if(dir == null)
                throw new NullPointerException("dir");

            if(!dir.exists())
            {
                dir.mkdirs();
            }

            if(dir.exists())
            {
                if(!dir.canWrite() || !dir.canRead())
                {
                    throw new IOException("dir=\"" + dir + "\" is not readable and writable");
                }
                if(!dir.isDirectory())
                    throw new IOException("dir=" + dir + "\" is not a directory");
            }
            else
            {
                throw new IOException("Unable to create directory: " + dir);
            }
        }



	private static void copyFile0(File inputFile,File outputFile)
		throws IOException
	{
		// if input is a directory then simply create the output directory
		if(inputFile.isDirectory())
			outputFile.mkdirs();
		else // copy the contents of inputFile to outputFile
		{

			FileChannel srcChannel = null;
			FileChannel destChannel = null;

			// do nothing if the files are the same
			if(inputFile.equals(outputFile))
				return;

			try
			{
				try
				{
					//attempt to open input stream
				     srcChannel =
				    	 new FileInputStream(inputFile).getChannel();

				     if(srcChannel == null)
				     {
				    	 throw new IOException("Unable to obtain input file channel");
				     }
				}
			    catch(Exception e)
			    {
						throw new ChainedIOException("Unable to open " + "\"" + inputFile + "\"" +
							" for reading during file copy attempt [reason: " + e + "]",e);
			    }

			    //attempt to open output stream
			    try
			    {
			    	 // create any non-existant parent directories
			    	 File parent = outputFile.getParentFile();
			    	 if(parent != null) parent.mkdirs();

				     destChannel =
				    	 new FileOutputStream(outputFile).getChannel();
				     if(destChannel == null)
				     {
				    	 throw new IOException("Unable to obtain output file channel");
				     }

			    }
			    catch(Exception e)
			    {

					throw new ChainedIOException("Unable to create \"" + outputFile + "\"" +
						" for writing during file copy attempt [reason: " + e + "]",e);
			    }

			    //attempt to copy input stream to output stream
			    try
			    {
			    	// make sure we transfer all the bytes
			    	long count = srcChannel.size();
			    	long pos = 0;
			    	long written;
			    	while(count > 0 && (written = srcChannel.transferTo(pos, count, destChannel)) > 0)
			    	{
			    		pos += written;
			    		count -= written;
			    	}

			    }
			    catch(Exception e)
			    {
			    	throw new ChainedIOException("Unable to write from \"" + inputFile + "\" to \"" +
			    			outputFile + "\"" + "during file copy attempt [reason: " + e + "]",e);
			    }
			}
			finally
			{
				MiscUtils.closeStream(srcChannel);
				MiscUtils.closeStream(destChannel);
			}
		}
	}
	/**
	 * Copies the file pointed to by <code>inputPath</code> to the
	 * <code>outputPath</code>.  If <code>inputPath</code> is the same as
	 * <code>outputPath</code> path, then this method has no effect.
	 * If <code>inputFile</code> is a directory then its contents are copied recursively
	 * to the directory represented by <code>outputFile</code> (The inputFile directory name itself
	 * will not appear inside <code>outputFile</code>).
	 *
	 * @param inputFile input file
	 * @param outputFile output file
	 * @throws IOException if error occurs during copying
	 */
	public static void copyFile(File inputFile, File outputFile)
		throws IOException
	{
		if(inputFile == null)
			throw new NullPointerException("inputFile");
		if(!inputFile.exists())
			throw new IllegalArgumentException("input file: \"" + inputFile + "\" does not exist");
		if(outputFile == null)
			throw new NullPointerException("outputPath");

		// if we wanted to copy the inputFile directory itself then would just
		// pass down outputFile/inputFile.getName() as the refDir to getFilesToCopy
		// instead of just outputFile

		// get all the current files in the directory
		if(inputFile.isDirectory())
		{
			List<Pair<File,File>> toCopy = getFilesToCopy(inputFile,outputFile);
			for(Pair<File,File> pair : toCopy)
				copyFile0(pair.first,pair.second);

		}
		else
			copyFile0(inputFile,outputFile);
	} //end copyFile
	
	/**
	 * Moves the file pointed to by <code>inputPath</code> to the
	 * <code>outputPath</code>.  If <code>inputPath</code> is the same as
	 * <code>outputPath</code> path, then this method has no effect.
	 * If <code>inputFile</code> is a directory then its contents are copied recursively
	 * to the directory represented by <code>outputFile</code> (The inputFile directory name itself
	 * will not appear inside <code>outputFile</code>).
	 *
	 * @param inputFile input file
	 * @param outputFile output file
	 * @throws IOException if error occurs during moving
	 */
	public static void moveFile(File inputFile,File outputFile)
		throws IOException
	{
		if(inputFile == null)
			throw new NullPointerException("inputFile");
		if(!inputFile.exists())
			throw new IllegalArgumentException("input file: \"" + inputFile + "\" does not exist");
		if(outputFile == null)
			throw new NullPointerException("outputPath");
		
		if(inputFile.equals(outputFile)) return;
		
		if(!inputFile.renameTo(outputFile))
			throw new IOException("Unable to rename \"" + inputFile + "\"" + " to \"" + outputFile + "\"");
	}

	private static List<Pair<File,File>> getFilesToCopy(File dir,File refDir)
	{
		List<Pair<File,File>> data = new ArrayList<Pair<File,File>>();

		// add the current entry
		data.add(Pair.makePair(dir,refDir));

		if(dir.isDirectory())
		{
			// search recursively
			for(File f : dir.listFiles())
				data.addAll(getFilesToCopy(f,new File(refDir,f.getName())));
		}
		return data;
	}
	/**
	 * Removes the last "." character from a file string that indicates an extension.
	 *
	 * @param file the file string
	 * @return the file string without the file extension string
	 */
	public static String removeFileExt(String file)
	{
		int index = file.lastIndexOf(".");
		if(index != -1)
			return file.substring(0,index);
		else
			return file;
	}

	/**
	 * Returns all the files that match the given file regex.  <code>fileRegex</code>
	 * will match wildcards much like the unix command line tools.  Directories will be
	 * searched recursivley, if <code>recursive</code> is <code>true</code>.  Wildcards
	 * can only appear in <code>fileNameRegex</code>
	 *
	 * @param dir the directory to search
	 * @param fileNameRegex the file regular expression
	 * @param recursive <code>true</code> to search subdirectories in <code>dir</code> and
	 *                  <code>false</code> otherwise
	 * @return the file matches
	 */
	public static java.util.List<File> getMatchingFiles(File dir,
			String fileNameRegex,boolean recursive)
	{
		if(dir == null)
			throw new NullPointerException("dir");
		if(fileNameRegex == null)
			throw new NullPointerException("fileNameRegex");
		List<File> matches = new ArrayList<File>();
		// must replace literal "." with escaped "\." version
		// since . is symbol for any character in regular expressions
		fileNameRegex = fileNameRegex.replace(".", "\\.");
		// must replace * with ".*" for regular expressions
		fileNameRegex = fileNameRegex.replace("*", ".*");

		// match files that match the given file name regex
		FileFilter filter = new RegexFileFilter(fileNameRegex);
		getMatchingFiles0(matches,dir,filter,recursive);
		return matches;
	}

	private static void getMatchingFiles0(List<File> matches,
			File root,FileFilter filter,boolean recursive)
	{
		// do a level-order search on the root directory
		Queue<File> queue = new LinkedList<File>();
		queue.add(root);

		while(!queue.isEmpty())
		{
			File dir = queue.poll();
			File [] files = dir.listFiles(filter);
			// make the matches contain the full file paths

			if(files != null)
				Collections.addAll(matches,files);
			// only search directories if asked by the user
			if(recursive)
			{
				File [] subDirs = dir.listFiles(new DirFileFilter(true));
				for(File f : subDirs)
					queue.add(f);
			}
		}

	}

	/**
	 * Returns the extension of <code>file</code> if one exists.
	 *
	 * @param file the file string
	 * @return the extension of <code>file</code> or <code>null</code>
	 *         if one doesn't exist
	 */
	public static String getFileExt(String file)
	{
		int index = file.lastIndexOf(".");
		final int len = file.length();
		//is extension valid?
		if(index != -1 && len > index + 1)
			return file.substring(index+1,len);
		else
			return null;
	}




	/**
	 * Appends <code>ext</code> to <code>filename</code> if it is not present already
	 * and returns the result.
	 *
	 * @param filename name of file
	 * @param ext file extension
	 * @return filename with appended extension if it is not already present
	 */
	public static final String appendFileExtension(String filename,String ext)
	{
		String extStr;
		if(!ext.startsWith("."))
			extStr = "." + ext;
		else
			extStr = ext;

		//int index = filename.indexOf(extStr);
		//if(index == -1)
		if(!filename.endsWith(extStr))
			return filename += extStr;
		else
			return filename;
	}


	/**
	 * Creates a <code>PrintWriter</code> that outputs to specified
	 * <code>filename</code>.
	 *
	 * @param filename name of file to write to
	 * @return the <code>PrintWriter</code>
	 * @throws IOException if file could not be opened for writing
	 */
	public static final PrintWriter createPrintWriter(String filename) throws IOException
	{
		try
		{
			return new PrintWriter(new BufferedWriter(FileUtils.newFileWriter(filename)));
		}
		catch(FileNotFoundException e)
		{
			throw new IOException(e.getMessage());
		}
	}


	/**
	 * Convenience method for retrieving a property value from a <code>Properties</code> object
	 * @param prop the <code>Properties</code>
	 * @param propName the name of the property
	 * @param assertNonNull indicates whether an <code>IOException</code> should be thrown
	 * 						if value is <code>null</code> or the empty string
	 * @return the property value associated with <code>propName</code> in <code>prop</code>
	 * @throws IOException if <code>assertNonNull</code> is <code>true</code> and the value of
	 *                     the property associated with <code>propName</code> is <code>null</code>
	 *                     or the empty string
	 */
	public static String getConfigProperty(Properties prop,String propName,boolean assertNonNull)
	throws IOException
	{
		String value = prop.getProperty(propName);
		if(assertNonNull && (value == null || value.length() == 0))
			throw new IOException("Config property " + propName + " is undefined");
		if(value != null && (value = value.trim()).length() != 0)
			return value;
		else
			return null;
	}
	
	/**
	 * Convenience method for replacing the property value of <code>origPropName</code> with the value of
	 * <code>newPropName</code> if <code>newPropName</code> exists. If <code>newPropName</code> exists and
	 * <code>origPropName</codde> does not exist, then <code>origPropName</code> is added with the value of
	 * <code>newPropName</code>.
	 * 
	 * @param prop the properties
	 * @param origPropName the original property to replace/add
	 * @param newPropName the property to use as a replacement if it exists
	 */
	public static void replaceConfigProperty(Properties prop,String origPropName,String newPropName)
	{
		String newValue = prop.getProperty(newPropName);
		if(!MiscUtils.isEmpty(newValue))
			prop.setProperty(origPropName, newValue);
		
	}

	/**
	 * Returns a configuration property value from the <code>Properties</code>
	 * map.
	 *
	 * @param prop the <code>Properties</code>
	 * @param propName the name of the property to extract from <code>prop</code>
	 * @return the property value
	 * @throws IOException if <code>prop</code> does not have a mapping for
	 *                     <code>propName</code>; or if the property value is
	 *                     the empty string
	 */
	public static String getConfigProperty(Properties prop,String propName)
	throws IOException
	{
		return getConfigProperty(prop,propName,true);
	}

	/**
	 * Loads the properties from <code>filename</code>.
	 *
	 * @param filename file from which to load properties
	 * @return the <code>Properties</code>
	 * @throws IOException if error occurs in reading the file
	 */
	public static Properties loadPropertiesFile(String filename)
		throws IOException
	{
		return loadPropertiesFile(new
				BufferedInputStream(FileUtils.newFileInputStream(filename)));
	}

	/**
	 * Loads the properties from <code>stream</code>.
	 * The stream is closed whether or not this method returns
	 * normally.
	 *
	 * @param stream the <code>InputStream</code> source
	 * @return the loaded <code>Properties</code>
	 * @throws IOException if error occurs during reading
	 */
	public static Properties loadPropertiesFile(InputStream stream)
		throws IOException
	{
		try
		{
			Properties prop = new Properties();
			prop.load(stream);
			return prop;
		}
		finally
		{
			if(stream != null)
				stream.close();
		}
	}

	/**
	 * Relativizes <code>f</code> against ancestor if possible.
	 *
	 * @param ancestor the ancestor of <code>f</code>
	 * @param f the file to relativize
	 * @return the relativized file
	 */
	public static String relativizeFile(String ancestor,String f)
	{
		if(ancestor == null)
			throw new NullPointerException("ancestor");
		if(f == null)
			throw new NullPointerException("f");

		URI parent = null;
		URI child = null;
		try
		{
			parent = new URI(uriEncodeFile(ancestor.toString())).normalize();
		}
		catch(URISyntaxException e)
		{
			throw new IllegalArgumentException("ancestor=" + ancestor + " is invalid",e);
		}
		try
		{
			child = new URI(uriEncodeFile(f.toString())).normalize();
		}
		catch(URISyntaxException e)
		{
			throw new IllegalArgumentException("f=" + f + " is invalid",e);
		}

		return parent.relativize(child).getPath();
	}


	// This version is too error-prone as it returns files that use the platform-specific
	// file separator encapsulated in the File object
	///**
	// * Relativizes <code>f</code> against ancestor if possible.
	// *
	// * @param ancestor the ancestor of <code>f</code>
	// * @param f the file to relativize
	// * @return the relativized <code>File</code>
	// */
	//public static File relativizeFile(File ancestor,File f)
	//{
	//	if(ancestor == null)
	//		throw new NullPointerException("ancestor");
	//	if(f == null)
	//		throw new NullPointerException("f");
	//	return FileUtils.newFile(relativizeFile(ancestor.toString(),f.toString()));
	//}

	/**
	 * Gets a file path from the resources of the specified class.
	 *
	 * @param resourceClass <code>Class</code> for which to search for resources
	 * @param resourceName name of resource to get file path for
	 * @return the absolute path name to the file specified by <code>resourceName</code>
	 */
	public static String getFile(Class<?> resourceClass,String resourceName)
	{
		if(resourceName == null)
			throw new NullPointerException("resourceName");
		// just a name, make absolute
//		if(!resourceName.startsWith(("/")))
//		{
//			resourceName = "/" + resourceClass.getPackage().getName().replace(".","/") + "/" + resourceName;
//		}
		
		URL fileURL = resourceClass.getResource(resourceName);
		if(fileURL == null)
		{
			throw new MissingResourceException("resourceName=\"" + resourceName +
					"\" cannot be found using resourceClass=\"" + resourceClass + "\"",
					MiscUtils.getClassName(resourceClass),resourceName);
		}
		String raw = fileURL.getFile();
		return replaceInvalidFileChars(raw);
	}

	/**
	 * Encodes the specified file path into url-encoded format.
	 *
	 * @param file the file
	 * @return the encoded string
	 */
	public static String urlEncode(File file)
	{
		return urlEncode(file.toString());
	}

	/**
	 * Encodes the specified file path into url-encoded format.
	 *
	 * @param file the file
	 * @return the encoded string
	 */
	public static String urlEncode(String file)
	{
		StringBuilder str = new StringBuilder();
		// add the file protocol
		str.append("file://");
		// add an additional "/" character if the file string does not begin with one already
		if(file.charAt(0) != '/')
			str.append('/');
		// Escape all space characters with escape code
		str.append(file.replaceAll("\\s","%20"));
		return str.toString();
	}

	/**
	 * Returns the resource file as an <code>InputStream</code>.
	 *
	 * @param resourceClass <code>Class</code> for which to search for resources
	 * @param resourceName name of resource to get file path for
	 * @return <code>InputStream</code> to the file
	 */
	public static InputStream getFileAsStream(Class<?> resourceClass,String resourceName)
	{
		InputStream in =  resourceClass.getResourceAsStream(resourceName);
		if(in == null)
		{
			throw new MissingResourceException("resourceName=\"" + resourceName +
					"\"cannot be found using resourceClass=\"" +
					MiscUtils.getClassName(resourceClass) + "\"",
					MiscUtils.getClassName(resourceClass),resourceName);
		}
		return in;
	}

	/**
	 * Removes invalid characters from the inputted <code>fileStr</code>
	 * that are not appropriate for methods taking a file string as a parameter.
	 * These characters usually appear after retrieving a url or uri string from
	 * a class resource.
	 *
	 * @param fileStr raw file string
	 * @return the reformatted file string
	 */
	public static String replaceInvalidFileChars(String fileStr)
	{
		return uriDecodeFile(fileStr);
	}

	/**
	 * Resolves <code>file</code> against <code>defaultPath</code> if
	 * <code>file</code> is not already absolute and <code>defaultPath</code>
	 * is not empty.  If it is not already
	 * absolute, the returned <code>File</code> will be <code>file</code> with
	 * <code>defaultPath</coded> pre-pended to it; otherwise, <code>file</code>
	 * is simply returned.
	 *
	 * @param file the input file
	 * @param defaultPath the default path to use if <code>file</code> is not absolute
	 * @return the resolved file
	 */
	public static String resolveFile(String file,String defaultPath)
	{
		if(file == null)
			throw new NullPointerException("file");
		if(MiscUtils.isEmpty(defaultPath) || defaultPath.equals("."))
			return file;
		File orig = new File(file);
		if(orig.isAbsolute())
			return orig.toString();

		return new File(defaultPath,file).toString();
	}
	
	/**
	 * Encodes the given path so that it can be read correctly as a java properties file.
	 * 
	 * @param path the path string
	 * @return the encoded path string
	 */
	public static String propertiesEncodePath(String path)
	{
		return applyFileSeparator(path,"/");
	}
	
	/**
	 * Decodes the given path that was encoded into a properties file.
	 * 
	 * @param path the encoded path
	 * @return the decoded path string
	 */
	public static String propertiesDecodePath(String path)
	{
		return applyFileSeparator(path);
	}
	
	/**
	 * Resolves <code>file</code> against the application directory if
	 * <code>file</code> is not already absolute. If it is not already
	 * absolute, the returned <code>File</code> will be <code>file</code> with
	 * the application directory pre-pended to it; otherwise, <code>file</code>
	 * is simply returned.
	 *
	 * @param file the input file
	 * @return the resolved file
	 */
	public static String resolveFile(String file)
	{
		return resolveFile(file,getApplicationDirectory());
	}

	/**
	 * Reads all the data in <code>reader</code> and returns the result
	 * in a <code>String</code>.  The <code>reader</code> is closed as a result
	 * of invoking this method whether or not an <code>IOException</code> is thrown.
	 *
	 * @param reader the <code>Reader</code>
	 * @return all the data read from <code>reader</code>
	 * @throws IOException if error occurs in reading from <code>reader</code>
	 */
	public static String readData(Reader reader)
		throws IOException
	{
            if(reader == null)
                throw new NullPointerException("reader");
            
            try
            {
                int read;
                char [] buf = new char[BUF_SIZE >> 1];

                // create large buffer to hold data
                StringBuilder output = new StringBuilder(100000);

                while((read = reader.read(buf)) > 0)
                    output.append(buf,0,read);

                return output.toString();
            }
            finally
            {
                MiscUtils.closeStream(reader);
            }


	}


       /**
	 * Reads all the data in <code>reader</code> and returns the result
	 * in a <code>String</code>.  The <code>reader</code> is closed as a result
	 * of invoking this method whether or not an <code>IOException</code> is thrown.
	 *
	 * @param reader the <code>Reader</code>
	 * @return all the data read from <code>reader</code>
	 * @throws IOException if error occurs in reading from <code>reader</code>
	 */
        public static String readData(InputStream in)
                throws IOException
        {
           return new String(readDataChars(in));

        }


        /**
         * Creates a UTF-8 InputStreamReader that replaces any invalid characters.
         *
         * @param in the <code>InputStream</code>
         *
         * @return the <code>InputStreamReader</code>
         *
         */
        public static InputStreamReader createInputStreamReader(InputStream in)
        {
            if(in == null)
                throw new NullPointerException("in");

           CharsetDecoder utf8Decoder = Charset.forName("UTF-8").newDecoder();
           utf8Decoder.onMalformedInput(CodingErrorAction.REPLACE);
           utf8Decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);

           return new InputStreamReader(in,utf8Decoder);
        }

        /**
	 * Reads all the data in <code>reader</code> and returns the result
	 * in a <code>char []</code>.  The <code>reader</code> is closed as a result
	 * of invoking this method whether or not an <code>IOException</code> is thrown.
	 *
	 * @param reader the <code>Reader</code>
	 * @return all the data read from <code>reader</code>
	 * @throws IOException if error occurs in reading from <code>reader</code>
	 */
        public static char [] readDataChars(InputStream in)
                throws IOException
        {
           if(in == null)
               throw new NullPointerException("in");

           CharsetDecoder utf8Decoder = Charset.forName("UTF-8").newDecoder();
           utf8Decoder.onMalformedInput(CodingErrorAction.REPLACE);
           utf8Decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
           ByteBuffer bytes = ByteUtils.readBytes(in, null);
           CharBuffer parsed = utf8Decoder.decode(bytes);

           char [] chars;
           if(parsed.hasArray())
               chars = parsed.array();
           else
           {
               chars = new char[parsed.limit()];
               parsed.get(chars);
           }

           return chars;
        }

        private static class StringProcessor implements LineProcessor
        {

            // create a large character buffer for reading the data
            private final StringBuilder data = new StringBuilder(100000);

            public void process(String line)
            {
                data.append(line);
		data.append('\n');
            }

            @Override
            public String toString()
            {
                return data.toString();
            }

        }
       /**
	 * Reads all the data in <code>reader</code> and processes it using <code>proc</code>.
        * The <code>reader</code> is closed as a result
	 * of invoking this method whether or not an <code>IOException</code> is thrown.
	 *
	 * @param reader the <code>Reader</code>
	 * @param proc the <code>LineProcessor</code>
	 * @throws IOException if error occurs in reading from <code>reader</code>
	 */
        public static void readData(Reader reader,LineProcessor proc)
                throws IOException
        {
                if(reader == null)
                    throw new NullPointerException("reader");
                if(proc == null)
                    throw new NullPointerException("proc");

		BufferedReader bufReader;
                if(!(reader instanceof BufferedReader))
                    bufReader = new BufferedReader(reader);
                else
                    bufReader = (BufferedReader)reader;

		String line = null;

		try
		{
			while((line = bufReader.readLine()) != null)
                            proc.process(line);
		}
		finally
		{
			bufReader.close();
		}
        }

	/**
	 *
	 * <code>FileFilter</code> that accepts
	 * all files that are either directories or regular files, depending
	 * on the constructor parameter.
	 *
	 * @author Justin Montgomery
	 * @version $Id: FileUtils.java 2798 2011-04-28 17:16:48Z jmontgomery $
	 */
	private static class DirFileFilter implements FileFilter
	{
		private final boolean dirs;
		/**
		 * Constructor.
		 *
		 * @param dirs <code>true</code> to only accept directories and
		 *             <code> false</code> to only accept regular files
		 */
		public DirFileFilter(boolean dirs)
		{
			this.dirs = dirs;
		}
		public boolean accept(File f)
		{
			if(dirs)
				return f.isDirectory();
			else
				return f.isFile();
		}
	}

	/**
	 * <code>FileFilter</code> that accepts files matching the indicated
	 * file name regular expression.  Directories are not matched.
	 *
	 * @author Justin Montgomery
	 * @version $Id: FileUtils.java 2798 2011-04-28 17:16:48Z jmontgomery $
	 */
	private static class RegexFileFilter implements FileFilter
	{
		private final Pattern pattern;

		/**
		 * Constructor.
		 * <code>fileNameRegex</code> must specify only the terminating
		 * name of the file, not the whole path.
		 *
		 * @param fileNameRegex the file name regex
		 */
		public RegexFileFilter(String fileNameRegex)
		{
			if(fileNameRegex == null)
				throw new NullPointerException("fileRegex");
			this.pattern = Pattern.compile(fileNameRegex);
		}
		/* (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname)
		{
			// only match files, not directories
			if(!pathname.isFile())
				return false;

			String strFile = pathname.getName();
			return pattern.matcher(strFile).matches();
		}

	}

	/**
	 * Zips the data described by <code>data</code> to ZIP file
	 * <code>outputFile</code>.
	 *
	 * @param outputFile the output zip file
	 * @param entries <code>List</code> of entry names to write
	 * @param cb <code>ZipCallback</code> for getting <code>InputStream</code>
	 *           to data in <code>entries</code>
	 * @throws IOException if error occurs during writing
	 */
	public static void zipData(String outputFile,
			List<String> entries,ZipCallback cb)
		throws IOException
	{
		if(outputFile == null)
			throw new NullPointerException("outputFile");
		if(entries == null)
			throw new NullPointerException("entries");
		if(cb == null)
			throw new NullPointerException("cb");

		ZipOutputStream zout = null;
		try
		{
			zout = new ZipOutputStream(
					new BufferedOutputStream(FileUtils.newFileOutputStream(outputFile),BUF_SIZE));
			// max compression
			zout.setMethod(ZipOutputStream.DEFLATED);
			zout.setLevel(9);
			for(String entry : entries)
			{
				ZipEntry ze = new ZipEntry(entry);
				zout.putNextEntry(ze);
				try
				{
					cb.onWrite(entry,zout);
				}
				finally
				{
					zout.closeEntry();
				}
			}
			zout.flush();
		}
		finally
		{
			MiscUtils.closeStream(zout);
		}
	}

	/**
	 * Zips all the <code>inputFiles</code> and writes them to the ZIP file
	 * <code>outputFile</code>.
	 *
	 * @param outputFile the output zip file
	 * @param inputBase the base directory for which to relativize <code>inputFiles</code> when stored
	 *                  in <code>outputFile</code>
	 * @param inputFiles files to zip (may be directories in which case the directory and all subdirectories/files
	 *                   are zipped as well)
	 * @throws IOException if error occurs during writing
	 * @throws IllegalArgumentException if error occurs relativizing <code>inputFiles</code> against
	 *                                  <code>inputBase</code>
	 */
	public static void zipData(String outputFile,String inputBase,String...inputFiles)
		throws IOException
	{
		if(outputFile == null)
			throw new NullPointerException("outputFile");
		if(inputFiles == null || inputFiles.length < 1)
			throw new IllegalArgumentException("no inputFiles");

		// stores a mapping from zip entry names to the absolute files they point to
		final Map<String,String> fileEntryMap = createZipFileEntryMap(inputBase,Arrays.asList(inputFiles));
				
		zipData(outputFile,new ArrayList<String>(fileEntryMap.keySet()),new DefaultZipCallback()
		{
			@Override
			protected String getFullInputPath(String entry)
			{
				return fileEntryMap.get(entry);
			}
		});
	}
	
	/**
	 * Creates an entry lookup map from entry names to full file paths.
	 * 
	 * @param inputBase the base directory for which to relativize <code>inputFiles</code> when stored
	 *                  in <code>outputFile</code>
	 * @param inputFiles files to zip (may be directories in which case the directory and all subdirectories/files
	 *                   are zipped as well)
	 * @return entry lookup map from entry names to full file paths
	 * @throws IOException if an input file could not be found
	 */
	public static Map<String,String> createZipFileEntryMap(String inputBase,Collection<String> inputFiles)
		throws IOException
	{
		if(inputFiles == null)
			throw new NullPointerException("inputFiles");
		
		List<String> files = new ArrayList<String>();
		for(String input : inputFiles)
		{
			File file = new File(input);
			if(!file.exists())
				throw new IOException("inputFile=" + file + " does not exist!");
			if(file.isDirectory())
			{
				List<File> matches = getMatchingFiles(file,"*",true);
				for(File f : matches)
					files.add(f.toString());
			}
			else
				files.add(input);
		}

		// stores a mapping from zip entry names to the absolute files they point to
		final Map<String,String> fileEntryMap = new HashMap<String,String>();

		// relativize the entries
		if(!MiscUtils.isEmpty(inputBase) && !inputBase.equals("."))
		{
			// required to replace backslashes with forward slashes in URI syntax
			URI inputBaseUri;
			try
			{
				inputBaseUri = new URI(
					uriEncodeFile(inputBase)).normalize();
			}
			catch(URISyntaxException e) { throw new IllegalArgumentException(e); }
			// relativize the entries
			for(ListIterator<String> it = files.listIterator(); it.hasNext();)
			{
				String file = it.next();
				URI fileUri;
				try
				{
					fileUri = new URI(uriEncodeFile(file)).normalize();
				}
				catch(URISyntaxException e) { throw new IllegalArgumentException(e); }

				String entry = inputBaseUri.relativize(fileUri).getPath();
				//System.out.println("inputBaseURI: " + inputBaseUri.toString());
				//System.out.println("fileUri: " + fileUri.toString());
				//System.out.println("relativization: " + inputBaseUri.relativize(fileUri).getPath());
				//System.out.println("entry=" + entry);
				it.set(entry);
				fileEntryMap.put(entry, file);
			}
		}
		else // simply store identity file entry map mappings
		{
			for(String file  : files)
				fileEntryMap.put(file, file);
		}
		
		return fileEntryMap;
	}

	/**
	 * Encodes <code>file</code> so that it can be constructed as a valid
	 * <code>URI</code>.
	 *
	 * @param file the file string
	 * @return the encoded file string
	 */
	public static String uriEncodeFile(String file)
	{
		return WebUtils.urlEncode(file.replace('\\', '/'));
	}

	/**
	 * Decodes <code>fileURI</code> so that it can be constructed as a valid
	 * <code>File</code>.  This undoes the changes made in {@link #uriEncodeFile(String)}.
	 *
	 * @param fileUri the file uri string
	 * @return the decoded file string
	 */
	public static String uriDecodeFile(String fileUri)
	{
		final String fileSep = getPlatformSeparator();
		//replace url space character sequence with regular space character
		// decode use the getPath method of java.net.URI
//		try
//		{
//			String path = new URI(fileStr).getPath();
			String path = WebUtils.urlDecode(fileUri);
			// remove the file://
			String token = "file://";
			int index = path.indexOf(token);
			if(index == -1)
			{
				token = "file:/";
				index = path.indexOf(token);
			}
			if(index != -1)
			{
				path = path.substring(index + token.length());
				if("/".equals(fileSep) && (path.length() == 0 || path.charAt(0) != '/'))
				{
					path = "/" + path;
				}
			}
			return applyFileSeparator(path,fileSep);
//		}
//		catch(URISyntaxException e)
//		{
//			throw new IllegalArgumentException("fileStr=" + fileStr,e);
//		}
	}
	
	/**
	 * Gets the platform file separator.
	 * 
	 * @return the platform file separator
	 */
	public static String getPlatformSeparator()
	{
		return System.getProperty("file.separator");
	}
	
	/**
	 * Gets the platform line separator.
	 * 
	 * @return the platform line separator
	 */
	public static String getPlatformLineSeparator()
	{
		return System.getProperty("line.separator");
	}
	
	/**
	 * Applies the current platform's file separator to the given path
	 * @param path the path
	 * @return the updated file string
	 */
	public static String applyFileSeparator(String path)
	{
		return applyFileSeparator(path,getPlatformSeparator());
	}
	
	/**
	 * Applies the given platform file separator to <code>path</code>
	 * 
	 * @param path the path
	 * @param sep the platform separator 
	 * @return the updated file
	 */
	public static String applyFileSeparator(String path,String sep)
	{
		if(path == null)
			throw new NullPointerException("path");
		if("/".equals(sep))
			path = path.replace("\\","/");
		else if("\\".equals(sep))
		{
//			path = path.replaceAll("(^(!/))(/)","\\");
			path = path.replace("/", "\\");
			path = path.replace("!\\","!/");
		}
		
		return path;
	}
	
	

	/**
	 * Unzips the data stored in <code>zipFile</code>.
	 *
	 * @param zipFile the input ZIP file
	 * @param cb a {@link UnzipCallback} for processing each unzipped
	 *           entry
	 * @throws IOException if error occurs during the unzip operation
	 */
	public static void unzipData(String zipFile,UnzipCallback cb)
		throws IOException
	{
		if(zipFile == null)
			throw new NullPointerException("zipFile");
		if(cb == null)
			throw new NullPointerException("cb");
		ZipFile zf = null;
		try
		{
			zf = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> e = zf.entries();
			
			// enumerate once to get all the zip entry names
			// and validate them
			Set<String> entries = new HashSet<String>();
			
			while(e.hasMoreElements())
				entries.add(e.nextElement().getName());
			
			// validate the entries
			cb.validateEntries(entries);
			
			// now enumerate to read in the data
			e = zf.entries();
			while(e.hasMoreElements())
			{
				ZipEntry ze = e.nextElement();

				if(!ze.isDirectory())
				{
					InputStream in = zf.getInputStream(ze);
					try
					{
						cb.onRead(ze.getName(), in);
					}
					finally { MiscUtils.closeStream(in); }
				}
			}
		}
		finally
		{
			if(zf != null) zf.close();
		}
	}
	/**
	 * Unzips the data stored in <code>zipFile</code> and write it out to files
	 * described by the entries in the zip file relative to the directory of
	 * <code>zipFile</code>.
	 *
	 * @param zipFile the input ZIP file
	 * @param outputPath path to directory where files should be unzipped
	 * @throws IOException if error occurs during reading
	 */
	public static List<String> unzipData(String zipFile,final String outputPath)
		throws IOException
	{
		if(zipFile == null)
			throw new NullPointerException("zipFile");
		
		DefaultUnzipCallback cb = new DefaultUnzipCallback(outputPath);
		unzipData(zipFile,cb);
		return cb.getEntries();
	}
	/**
	 * Callback for processing unzipped entries.
	 *
	 * @author Justin Montgomery
	 * @version $Id: FileUtils.java 2798 2011-04-28 17:16:48Z jmontgomery $
	 */
	public interface UnzipCallback
	{
		/**
		 * Called during the reading of each zip entry in the zip file.
		 *
		 * @param entry the name of the entry
		 * @param data the <code>InputStream</code> for the entry
		 * @throws IOException if an error occurs during the reading of <code>data</code>
		 */
		void onRead(String entry,InputStream data)
			throws IOException;
		
		/**
		 * Determines whether the listed zip entries are as expected.
		 * If they are not as expected, then an <code>IOException</code>
		 * should be thrown.
		 * 
		 * 
		 * @param entries all the entries in the zip file
		 * @throws IOException if the zip archive does not contain all the expected entries
		 */
		void validateEntries(Set<String> entries) throws IOException;
	}

	/**
	 * callback for zipping entries.
	 *
	 * @author Justin Montgomery
	 * @version $Id: FileUtils.java 2798 2011-04-28 17:16:48Z jmontgomery $
	 */
	public interface ZipCallback
	{
		/**
		 * Called during writing of each zip entry.
		 *
		 * @param entry the name of the zip entry
		 * @param out the <code>OutputStream</code> on which to write the zip data
		 * @throws IOException if an error occurs in getting the <code>InputStream</code>
		 *         for <code>entry</code>
		 */
		void onWrite(String entry,OutputStream out)
			throws IOException;
	}
	
	/**
	 * Default implementation of <code>ZipCallback</code> that zips entries from the file system
	 * into the archive.  Subclasses must specify the absolute path to each zip entries by 
	 * implementing {@lin #getFullInputPath(String)}.
	 * 
	 * 
	 * @author Justin Montgomery
	 * @version $Id: FileUtils.java 2798 2011-04-28 17:16:48Z jmontgomery $
	 */
	public abstract static class DefaultZipCallback implements ZipCallback
	{
		private final byte[] buf = new byte[BUF_SIZE];

		public DefaultZipCallback(){}
		public void onWrite(String entry,OutputStream out)
			throws IOException
		{
			
			InputStream in = null;
			in = new BufferedInputStream(new FileInputStream(
					getFullInputPath(entry)),BUF_SIZE);
			try
			{
				int read;
				while((read = in.read(buf)) > 0)
					out.write(buf, 0, read);
			}
			finally
			{
				MiscUtils.closeStream(in);
			}
		}
		
		protected abstract String getFullInputPath(String entry);
	}
	
	/**
	 * Default implementation that unzips to the directory specified by 
	 * <code>outputPath</code>.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: FileUtils.java 2798 2011-04-28 17:16:48Z jmontgomery $
	 */
	public static class DefaultUnzipCallback implements UnzipCallback
	{

		private final String outputPath;
		private final byte [] buf = new byte[BUF_SIZE];
		
		private final List<String> entries = new ArrayList<String>();
		
		public DefaultUnzipCallback(String outputPath)
		{
			this.outputPath = outputPath;
		}
		
		public List<String> getEntries()
		{
			return entries;
		}
		
		public void onRead(String entry, InputStream data)
			throws IOException
		{
			OutputStream out = null;
			try
			{
				File file = new File(outputPath,entry);
				// Ensure that the parent directory of file exists
				// before attempting to write the file by creating
				// the directory if necessary
				File parent = file.getParentFile();
				if(parent != null && !parent.isDirectory())
					parent.mkdirs();
				out = new BufferedOutputStream(
						new FileOutputStream(file),BUF_SIZE);
				int read;
				while((read = data.read(buf)) > 0)
					out.write(buf, 0, read);
				out.flush();
				entries.add(file.toString());
			}
			finally
			{
				MiscUtils.closeStream(out);
			}
		} // onRead

		/* (non-Javadoc)
		 * @see com.gamesalutes.utils.FileUtils.UnzipCallback#validateEntries(java.util.Set)
		 */
		public void validateEntries(Set<String> entries) throws IOException
		{
			// TODO Auto-generated method stub
			
		}

		
	}
	
	/**
	 * Returns the path to the class in the filesystem.  If the class is in a jar file, then this
	 * is the path to the containing jar file; otherwise, it returns the path to the package of the 
	 * containing class.
	 * 
	 * @param clazz the class
	 * @return the directory of the class
	 */
	public static String getPathToClass(Class<?> clazz)
	{
		String classPath = clazz.getName().replace(".", "/") + ".class";
        URL url = clazz.getResource("/" + classPath);
        if(url != null)
        {
        	String path = FileUtils.uriDecodeFile(url.toString());
        	int jarFileIndex = path.indexOf("!/"); 
        	if(jarFileIndex != -1)
        		return path.substring(0,jarFileIndex);
        	return path;
        }
        
        return null;
	}
	/**
	 * Returns the directory of the class in the filesystem.  If the class is in a jar file, then this
	 * is the path to the containing jar file; otherwise, it returns the path to the root package of the 
	 * containing class.
	 * 
	 * @param clazz the class
	 * @return the directory of the class
	 */
	public static String getDirectory(Class<?> clazz)
	{		
        String path = getPathToClass(clazz);
        if(path != null)
        {
        	// if it is a class file, find path to root directory containing the classes
        	if(StringUtils.caseInsensitiveEndsWith(path, ".class"))
        	{
        		String classPath = FileUtils.uriDecodeFile(
        			clazz.getName().replace(".", "/") + ".class");
        		int index = path.indexOf(classPath);
        		if(index != -1)
        		{
        			if(index == 0)
        				return ".";
        			char lastChar = path.charAt(index-1);
        			if(lastChar == '/' || lastChar == '\\')
        				--index;
        			path = path.substring(0,index);
        		}
        		else
        		{
        			throw new IllegalArgumentException("unable to get directory for class=" + clazz.getName());
        		}
        	}
        	// jar file
        	else
        	{
        		int parentIndex = path.lastIndexOf('/');
        		if(parentIndex == -1)
        			parentIndex = path.lastIndexOf('\\');
        		if(parentIndex != -1)
        			path = path.substring(0,parentIndex);
        		else
        			path = ".";
        	}
    		return path;
        }
        
        return null;

	}
	
	/**
	 * Sets the application root directory to be that of <code>dir</code>.
	 * 
	 * @param dir the absolute path to the application directory
	 */
	public static synchronized String setApplicationDirectory(String dir)
	{
		if(appDir == null)
		{
			if(dir == null)
				throw new NullPointerException("dir");
			if(!new File(dir).isAbsolute())
				throw new IllegalArgumentException("dir=" + dir);
			appDir = dir;
		}
		return appDir;
	}
	/**
	 * Sets the application root directory to be that of containing main class.
	 * 
	 * @param mainClazz the main class
	 */
	public static synchronized String setApplicationDirectory(Class<?> mainClazz)
	{
		if(appDir == null)
		{
			String path = getDirectory(mainClazz);
			if(path != null)
			{
				// TODO: remove this hardcoding!
				// if path includes "lib" or "bin" then go up one more
				if(StringUtils.caseInsensitiveEndsWith(path,"lib") ||
				   StringUtils.caseInsensitiveEndsWith(path,"bin"))
				{
					File f = new File(path);
					path = f.getParent();
					if(path == null)
						path = ".";
					else
						path = FileUtils.uriDecodeFile(path);
				}
				
				appDir = path;
			}
		}
		
		return appDir;
	}
	
	/**
	 * Gets the application root directory.
	 * 
	 * @return the application directory
	 */
	public static synchronized String getApplicationDirectory()
	{
		return appDir != null ? appDir : ".";
	}
	
	
	
	////////////////////// File            //////////////////
	public static File newFile(File parent,String child)
	{
		return new File(parent != null ? FileUtils.resolveFile(parent.toString()) : 
							             null,
				        child);
	}
	public static File newFile(String pathname)
	{
		return new File(FileUtils.resolveFile(pathname));
	}
	
	public static File newFile(String parent,String child)
	{
		return new File(parent != null ? FileUtils.resolveFile(parent) : 
			                             null,
			            child);
	} 
	
	public static File newFile(URI uri)
	{
		return new File(uri);
	}
	////////////////////// FileInputStream //////////////////
	public static FileInputStream newFileInputStream(File file)
		throws FileNotFoundException
	{
		return new FileInputStream(FileUtils.resolveFile(file.toString()));
	}
	public static FileInputStream newFileInputStream(FileDescriptor fdObj)

	{
		return new FileInputStream(fdObj);
	}
	public static FileInputStream newFileInputStream(String name)
		throws FileNotFoundException
	{
		return new FileInputStream(FileUtils.resolveFile(name.toString()));
	}

	///////////////////// FileOutputStream ///////////////////
	public static FileOutputStream newFileOutputStream(File file)
		throws FileNotFoundException
	{
		return new FileOutputStream(FileUtils.resolveFile(file.toString()));
	}
	public static FileOutputStream newFileOutputStream(File file,boolean append)
		throws FileNotFoundException

	{
		return new FileOutputStream(FileUtils.resolveFile(file.toString()),append);
	}
	public static FileOutputStream newFileOutputStream(FileDescriptor fdObj)
	{
		return new FileOutputStream(fdObj);
	}
	public static FileOutputStream newFileOutputStream(String name)
		throws FileNotFoundException
	{
		return new FileOutputStream(FileUtils.resolveFile(name));
	}
	public static FileOutputStream newFileOutputStream(String name,boolean append)
		throws FileNotFoundException

	{
		return new FileOutputStream(FileUtils.resolveFile(name),append);

	}

	///////////////// FileReader /////////////////////////
	public static FileReader newFileReader(File file)
		throws FileNotFoundException

	{
		return new FileReader(FileUtils.resolveFile(file.toString()));
	}
	public static FileReader newFileReader(FileDescriptor fd)
	{
		return new FileReader(fd);
	}
	public static FileReader newFileReader(String fileName)
		throws FileNotFoundException

	{
		return new FileReader(FileUtils.resolveFile(fileName));
	}

	//////////////// FileWriter //////////////////////////
	public static FileWriter newFileWriter(File file)
		throws IOException
	{
		return new FileWriter(FileUtils.resolveFile(file.toString()));
	}
	public static FileWriter newFileWriter(File file,boolean append)
		throws IOException

	{
		return new FileWriter(FileUtils.resolveFile(file.toString()),append);
	}
	public static FileWriter newFileWriter(FileDescriptor fd)
	{
		return new FileWriter(fd);
	}
	public static FileWriter newFileWriter(String fileName)
		throws IOException

	{
		return new FileWriter(FileUtils.resolveFile(fileName));
	}
	public static FileWriter newFileWriter(String fileName,boolean append)
		throws IOException
	{
		return new FileWriter(FileUtils.resolveFile(fileName),append);
	}


}
