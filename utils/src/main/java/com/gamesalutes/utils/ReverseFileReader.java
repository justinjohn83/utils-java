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
/* Copyright 2008 - 2010 University of Chicago
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
/*******************************************************************
 * Author:		Ryan D. Emerle
 * Author:      Justin Montgomery (buffering and fix for reading blank lines)
 * Date:			10.12.2004
 * Desc:			Reverse file reader.  Reads a file from the end to the
 *						beginning
 *
 * Known Issues:
 *						Does not support unicode!
 *******************************************************************/
 
package com.gamesalutes.utils;
import java.io.*;
 
public class ReverseFileReader implements Closeable
{	
		private RandomAccessFile randomfile;	
		private long position;
		private int bufPos;
		private boolean first = true;
		private byte [] buffer = new byte[8192];
		private StringBuilder finalLine = new StringBuilder(1024);
		
		public ReverseFileReader (String filename) throws IOException 
		{		
			// Open up a random access file
			this.randomfile=new RandomAccessFile(filename,"r");
			// Set our seek position to the end of the file
			this.position=this.randomfile.length();
			this.bufPos = -1;
			
			// Seek to the end of the file
			this.randomfile.seek(this.position);
			//Move our pointer to the first valid position at the end of the file.
			String thisLine=this.randomfile.readLine();
			while(thisLine == null ) {
				this.position--;
				this.randomfile.seek(this.position);
				thisLine=this.randomfile.readLine();
				this.randomfile.seek(this.position);
			}
		}	
		
		private boolean fill()
			throws IOException
		{
			if(this.position < 0) return false;
			
			if(this.position < buffer.length)
			{
				this.randomfile.seek(0);
				bufPos = (int)(first ? this.position : this.position - 1);
				this.randomfile.readFully(buffer,0,bufPos+1);
				this.position = -1;
				first = false;
			}
			else
			{
				this.bufPos = buffer.length - 1;
				this.position -= first ? buffer.length - 1 : buffer.length;
				first = false;
				this.randomfile.seek(this.position);
				this.randomfile.readFully(buffer);
			}
			// reverse the byte array
//			for(int i = 0, end = bufPos / 2; i < end; ++i)
//			{
//				byte temp = buffer[i];
//				buffer[i] = buffer[bufPos - i];
//				buffer[bufPos-i] = temp;
//			}
			
			return true;
		}
		// Read one line from the current position towards the beginning
		public String readLine() throws IOException 
		{		
			int thisCode;
			char thisChar;
			
			// Seek to the current position
			if(this.bufPos < 0 && !fill())
				return null;
			MiscUtils.clearStringBuilder(finalLine);
			
			for(;;)
			{
				// Seek to the current position
				if(this.bufPos < 0 && !fill())
						break;
			
				// Read the data at this position
				thisCode= this.buffer[bufPos--];
				thisChar=(char)thisCode;
				
				// If this is a line break or carrige return, stop looking
				if ((thisCode == 13 || thisCode == 10))
				{
					// look for line feed after carriage return
					// doing it backwards because reading in reverse
					if(thisCode == 10)
					{
						if(this.bufPos >= 0 || fill())
						{
							int nextCode=this.buffer[bufPos];
							if(nextCode == 13)
								bufPos--;
						}
					}
					break;
				} 
				else 
				{
					// This is a valid character append to the string
					finalLine.append(thisChar);
				}
			}
			// return the line
			// reverse the line
			finalLine.reverse();
			return finalLine.toString();
		}

		/* (non-Javadoc)
		 * @see java.io.Closeable#close()
		 */
		public void close() throws IOException
		{
			this.randomfile.close();
		}	
}
