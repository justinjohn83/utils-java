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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;



/**
 * <code>ProxyRetriever</code> that stores proxy data in a random access file.  All optional
 * methods are implemented.
 * 
 * @author Justin Montgomery
 * @version $Id: FileProxyRetriever.java 1762 2009-11-13 23:34:57Z jmontgomery $
 */
public class FileProxyRetriever<K,V> implements ProxyRetriever<K, V>,Serializable 
{
	private  transient FileChannel fileData;
	private  transient Map<K,V> cache;

	private transient ByteBuffer buf;
	private transient boolean persist;
	private transient MetaData<K> meta;
	
	private static final long serialVersionUID = 1L;
	
	//private FileLock lock;

	
	//private static final String FILE_NAME = "map.bin";
	private static final String META_FILE_NAME = "meta.bin";
	
	
	private static class MetaData<K> implements Serializable
	{
		private static final long serialVersionUID = 1L;
		// stores all the current blocks in the file
		//private List<Block> allBlocks;
		// stores the blocks actually used by the keys
		
		private  File file;
		
		// date of first creation or last clear
		private Date createDate;

		// sorted by max size
		private Map<K,Block<K>> mappedBlocks;
		// cache of free blocks
		// sorted by natural order : offset values
		private SortedSet<Block<K>> freeList;
		
		public MetaData(File f)
		{
			this.file = f;
			//allBlocks = new ArrayList<Block>();
			mappedBlocks = new HashMap<K,Block<K>>();
			freeList = new TreeSet<Block<K>>();

		}
	}
	
	private static class Block<K> implements Comparable<Block<K>>,Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private final int offset;
		private final int length;
		private K key;
		
		private Block(int offset,int length)
		{
			this.offset = offset;
			this.length = length;
		}
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(!(o instanceof FileProxyRetriever.Block)) return false;
			return offset == ((Block)o).offset;
		}
		
		@Override
		public int hashCode()
		{
			//return MiscUtils.hashLong(offset);
			return offset;
		}
		
		public int compareTo(Block b)
		{
			if(offset < b.offset) return -1;
			else if(offset > b.offset) return 1;
			return 0;
		}
		@Override
		public String toString()
		{
			return new StringBuilder("[offset=").append(offset).append(
					";length=").append(length).append(";key=").append(key
				).append("]").toString();
		}
	}
	
	private class BlockSizeComparator implements Comparator<Block>
	{
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Block o1, Block o2) 
		{
			int len1 = o1.length;
			int len2 = o2.length;
			if(len1 < len2) return -1;
			else if(len1 > len2) return 1;
			return 0;
		}
		
	}
	
	 private void writeObject(java.io.ObjectOutputStream out)
	 	throws IOException
     {
		 out.defaultWriteObject(); 
		 out.writeObject(meta.file);
     }
	 
	 private void readObject(java.io.ObjectInputStream in)
     	throws IOException, ClassNotFoundException
     {
		 //read in default state
		 in.defaultReadObject();
		 File f = (File)in.readObject();
		 
		 // reconstruct meta data
		 init(f);
     }
	/**
	 * Constructor.
	 * 
	 * @param tmpDir directory to create resources for this object
	 * @param prefix the prefix name to give to the temporary file(s)
	 */
	public FileProxyRetriever(File tmpDir,String prefix) throws IOException
	{
		this(tmpDir,prefix,false);
	}
	
	private void init(File f) throws IOException
	{
		init(f.getParentFile(),FileUtils.removeFileExt(f.getName()),true);
	}
	private void init(File tmpDir,String prefix,boolean persist) throws IOException
	{
		if(tmpDir == null)
			throw new NullPointerException("tmpDir");
		if(prefix == null)
			throw new NullPointerException("prefix");
		tmpDir.mkdirs();
		if(!tmpDir.isDirectory())
			throw new IllegalArgumentException("tmpDir=" + tmpDir + " not a dir");
		
		this.persist = persist;
		


		meta = createMetaData(tmpDir,prefix,persist);
		fileData = createDataFileChannel(this.meta.file,persist);
		// 128K initial buffer
		buf = ByteBuffer.allocate(1024 * 128);
		cache = new HashMap<K,V>();

	}
	
	private MetaData<K> createMetaData(File dir,String prefix,boolean persist) throws IOException
	{
		if(persist)
		{
			return this.loadMetadata(new File(dir,prefix + ".bin"));
		}
		else
		{
			return new MetaData<K>(File.createTempFile(prefix, ".bin", dir));
			//file.deleteOnExit();
		}
	}
	
	private FileChannel createDataFileChannel(File f,boolean persist) throws IOException
	{
		String mode;
		if(persist)
		{
			mode = "rws";
		}
		else
		{
			mode = "rw";
		}
	    return new RandomAccessFile(meta.file,mode).getChannel();

	}
	/**
	 * Constructor.
	 * 
	 * @param tmpDir directory to create resources for this object
	 * @param prefix the prefix name to give to the temporary file(s)
	 */
	public FileProxyRetriever(File tmpDir,String prefix,boolean persist)
		throws IOException
	{
		init(tmpDir,prefix,persist);
	}
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.ProxyRetriever#lookup(java.lang.Object)
	 */
	public synchronized Pair<Boolean, V> lookup(K key) 
	{
		boolean contains = false;
		V value = null;
		
		if(meta.mappedBlocks.containsKey(key))
		{
			contains = true;
			if(cache.containsKey(key))	
				value = cache.get(key);
			else // read from the file
			{
				Block<K> b = meta.mappedBlocks.get(key);
				try
				{
					// seek to the requested position
					fileData.position(b.offset);
					buf = ByteUtils.growBuffer(buf,b.length);
					// make sure that there is sufficient data in file
					long rem = fileData.size() - fileData.position();
					if(rem < b.length)
					{
						throw new IllegalStateException(
								"Insufficient data in file pos=" + 
								fileData.position() + ";size=" + fileData.size() +
								";block=" + b);
					}
					// set buffer limit to size of block
					buf.limit(b.length);
					buf.rewind();
					fileData.read(buf);
					buf.flip();
					value = readObject();
					
				}
				catch(Exception e)
				{
					throw new RuntimeException("Unable to lookup key=" + key + ";block=" + b,e);
				}
			}
		
			//System.out.println("Time=" + new java.util.Date() + ";Loaded Block=" + b + ";session=" + value);
		}
		
		return Pair.makePair(contains, value);
	}
	
	private V readObject()
		throws Exception
	{
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(Channels.newInputStream(new ByteBufferChannel(buf)));
			return (V)in.readObject();
		}
		finally
		{
			MiscUtils.closeStream(in);
		}
		
	}
	
	private void writeObject(V object)
		throws IOException
	{
		ObjectOutputStream out = null;
		try
		{
			ByteBufferChannel bbc = new ByteBufferChannel(buf);
			out = new ObjectOutputStream(Channels.newOutputStream(
					bbc));
			out.writeObject(object);
			out.flush();
			// get a reference to buffer in ByteBufferChannel in case a new buffer needed to
			// be allocated to accomodate the storage requirements
			buf = bbc.getByteBuffer();
		}
		finally
		{
			MiscUtils.closeStream(out);
		}
	}
	
	
	public synchronized void clear()
	{
		//allBlocks.clear();
		meta.mappedBlocks.clear();
		meta.freeList.clear();
		cache.clear();
		buf.clear();
		
		try
		{
			if(!meta.file.exists()) 
			{
				throw new IOException("Channel closed");
			}
			fileData.position(0);
			fileData.truncate(0);
		}
		catch(IOException e)
		{
			// maybe file stream was closed or file is not found : let's try to reinitialize
			MiscUtils.closeStream(fileData);
			try
			{
				fileData = this.createDataFileChannel(meta.file, this.persist);
				fileData.position(0);
				fileData.truncate(0);
			}
			catch(Exception e2)
			{
				throw new RuntimeException(e2);
			}
			
		}

		if(persist)
		{
			this.storeMetadata(true);
		}
		
	}
	
	public synchronized void put(K key,V value)
	{
		// only necessary to store if not in cache
		if(cache.containsKey(key))
			cache.put(key, value);
		else
			storeData(key,value);
	}
	

	public synchronized void remove(Object key)
	{
		removeData(key,-1,true);
		// also remove from cache if an entry was cached
		cache.remove(key);
		
		if(persist)
		{
			this.storeMetadata(false);
		}
	}
	
    public synchronized void update(K key,V value)
    {
    	put(key,value);
    }
    
    private void storeData(K key,V value)
    {
    	Block<K> storage = null;
    	try
    	{
    		buf.clear();
    		writeObject(value);
    		buf.flip();
    		boolean fromFreeList = false;
    		final int len = buf.limit();
    		// free previous key if it exists
    		removeData(key,len,false);
	    	for(Block<K> b : meta.freeList)
	    	{
	    		if(b.length >= len)
	    		{
	    			storage = b;
	    			fromFreeList = true;
	    			break;
	    		}
	    	}
	    	
	    	// allocate a new block
	    	if(storage == null)
	    	{
	    		int offset = (int)fileData.size();
	    		storage = new Block<K>(offset,len);
	    	}
	    	// write the data
	    	fileData.position(storage.offset);
	    	fileData.write(buf);
	    	// remove from free list
	    	if(fromFreeList)
	    		meta.freeList.remove(storage);
	    	storage.key = key;
	    	meta.mappedBlocks.put(key, storage);
	    	//compact(true);
	    	//System.out.println("Time=" + new java.util.Date() + ";Stored Block=" + storage + ";session=" + value);
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException("error putting [key=" + key + ";value=" + value + ";block=" + storage,e);
    	}
    	if(persist)
    	{
    		storeMetadata(false);
    	}
    }
    
    
    private MetaData<K> loadMetadata(File dataFile)
    {
    	ObjectInputStream in = null;
    	try
    	{
    		File f = new File(dataFile.getParentFile(),META_FILE_NAME);
    		if(!f.exists())
    		{
    			return new MetaData(dataFile);
    		}
    		
    		in = new ObjectInputStream(new BufferedInputStream(
    				new FileInputStream(f)));
    		return (MetaData<K>)in.readObject();
    		
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException("Error serializing metadata");
    	}
    	finally
    	{
    		MiscUtils.closeStream(in);
    	}
    }
    private void storeMetadata(boolean updateCreateDate)
    {
    	ObjectOutputStream out = null;
    	try
    	{
    		if(meta.createDate == null || updateCreateDate) {
    			meta.createDate = new Date();
    		}
    		File f = new File(this.meta.file.getParentFile(),META_FILE_NAME);
    		
    		out = new ObjectOutputStream(new BufferedOutputStream(
    				new FileOutputStream(f)));
    		out.writeObject(meta);
    		out.flush();
    		
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException("Error serializing metadata");
    	}
    	finally
    	{
    		MiscUtils.closeStream(out);
    	}
    }
    
    private Block<K> free(Object key)
    {
    	if(meta.mappedBlocks.containsKey(key))
    	{
			Block<K> storage = meta.mappedBlocks.remove(key);
			storage.key = null;
			meta.freeList.add(storage);
			return storage;
    	}
    	return null;
    }
    // truncate indicates whether file should be truncated if free space exists sequentially
    // at the end of the file
    // returns true if data was removed
    private boolean removeData(Object key,int newSize,boolean truncate)
    {
    	if(meta.mappedBlocks.containsKey(key))
    	{
	    	Block<K> storage = null;
	    	try
	    	{
	    		storage = free(key);
	    		if(storage != null)
	    			compact(newSize,truncate);
	    	}
	    	catch(Exception e)
	    	{
	    		throw new RuntimeException("error removing [key=" + key + ";block=" + storage,e);
	    	}
	    	return true;
    	}
    	return false;
    }
    
    private int getMaxBlockSize()
    {
    	if(!meta.mappedBlocks.isEmpty())
    	{
    		return Collections.max(meta.mappedBlocks.values(),
    				new BlockSizeComparator()).length;
    	}
    	else
    		return 0;
    }
    private void compact(int newSize,boolean truncate)
    {
    	try
    	{
    		// collate free entries
    		List<Block<K>> newEntries = new ArrayList<Block<K>>();
    		List<Block<K>> toRemove = new ArrayList<Block<K>>();
    		List<Block<K>> consec = new ArrayList<Block<K>>();
    		List<Block<K>> lastNewEntries = new ArrayList<Block<K>>();
    		
    		long end = 0;
    		// find the current largest block in mappedBlocks
    		int maxLength = getMaxBlockSize();
    		if(newSize > maxLength)
    			maxLength = newSize;
    		
    		for(Iterator<Block<K>> it = meta.freeList.iterator(); it.hasNext();)
    		{
    			Block f = it.next();
    			boolean hasNext = it.hasNext();
    			if(!hasNext && end == f.offset)
    				consec.add(f);
    			// reached end of consecutive blocks
    			// or iteration sequence has finished
    			if(end != f.offset || !hasNext)
    			{
    				//System.out.println("end consec=[end=" + end + ";block="+f+";consec=" + consec);
    				// collect current consecutive entries
    				if(consec.size() > 1)
    				{
    					int len = 0;
    					int offset = consec.get(0).offset;
    					for(Block b : consec)
    						len += b.length;
    					lastNewEntries.clear();
    					// formulate new blocks
    					// just combine into one large block
    					if(maxLength <= 0)
    						lastNewEntries.add(new Block(offset,len));
    					else
    					{
    						// split the blocks into maxLength chunks
    						int chunks = len / maxLength;
    						for(int i = 0; i < chunks; ++i)
    						{
    							lastNewEntries.add(new Block(offset,maxLength));
    							offset += maxLength;
    						}
    						// leftover chunk?
    						if(len % maxLength != 0)
    							lastNewEntries.add(new Block(offset,len - (chunks * maxLength)));
    					}
    					toRemove.addAll(consec);
    					newEntries.addAll(lastNewEntries);
    				}
    				consec.clear();
    					
    			}
    			
    			if(hasNext)
	    			consec.add(f);
	    		end = f.offset + f.length;
    		}
    		
    		
    		//System.out.println("FreeList=" + freeList + ";used=" + mappedBlocks.values() + ";size=" + fileData.size());
    		
    		// compaction occurred
    		if(!newEntries.isEmpty())
    		{
    			meta.freeList.removeAll(toRemove);
    			meta.freeList.addAll(newEntries);
    		}
    		
    		// see if we can discard the last block in the free list
    		if(truncate && !lastNewEntries.isEmpty())
    		{
    			Block last = lastNewEntries.get(lastNewEntries.size()-1);
    			if(last.offset + last.length >= fileData.size())
    			{
    				// truncate from first of last block of new entries
    				int offset = lastNewEntries.get(0).offset;
    				fileData.truncate(offset);
    				// remove this last block of new entries from the free list
    				meta.freeList.removeAll(lastNewEntries);
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException("Compaction error;freeList=" + meta.freeList,e);
    	}
    }
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.Disposable#dispose()
	 */
	public synchronized void dispose() 
	{
		MiscUtils.closeStream(fileData);
		if(!persist)
		{
			meta.file.delete();
		}
	}
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.ProxyRetriever#addCacheEntry(java.lang.Object, java.lang.Object)
	 */
	public synchronized void addCacheEntry(K key, V value) 
	{
		if(!meta.mappedBlocks.containsKey(key))
			throw new IllegalArgumentException("key=" + key);
		cache.put(key, value);
		// update the entry on disk
		// no need since lookup will retrieve it from the cache
		// storeData(key,value);
	}
	/* (non-Javadoc)
	 * @see com.gamesalutes.utils.ProxyRetriever#removeCacheEntry(java.lang.Object)
	 */
	public synchronized void removeCacheEntry(K key)
	{
		if(!cache.containsKey(key))
			throw new IllegalArgumentException("key=" + key);
		V value = cache.remove(key);
		// update the entry on disk
		storeData(key,value);
	}

	public Set<K> getKeySet() {
		
		// Merge the keys
		Set<K> keys = CollectionUtils.createHashSet(cache.size() + meta.mappedBlocks.size(), CollectionUtils.LOAD_FACTOR);
		keys.addAll(cache.keySet());
		keys.addAll(meta.mappedBlocks.keySet());
		
		return keys;
		
	}
	
	public Date getCreateDate() {
		if(meta != null) {
			Date d = meta.createDate;
			if(d != null) {
				return (Date)d.clone();
			}
		}
		return null;
	}

}
