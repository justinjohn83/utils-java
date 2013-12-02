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

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.Suite;

/**
 * General Test suite for this package.
 * 
 * @author Justin  Montgomery
 * @version $Id: TestSuite.java 2759 2011-04-13 18:47:23Z jmontgomery $
 */
@RunWith(Suite.class)
@SuiteClasses({FileUtilsTest.class,
			   ByteUtilsTest.class,
	           ClassFinderTest.class,
	           MiscUtilsTest.class,
	           StringUtilsTest.class,
	           CollectionUtilsTest.class,
	           CSVTest.class,
	           CidrTest.class,
	           ReadWriteLockTest.class,
	           LruMapTest.class,
	           SoftHashMapTest.class,
	           LruSoftMapTest.class,
	           DualHashBidiMapTest.class,
	           ProxyMapTest.class,
	           ProxyMapNoLocalTest.class,
	           ArrayMapTest.class,
	           ArraySetTest.class,
	           ConsoleRedirectorTest.class,
	           NetUtilsTest.class,
	           SynchronizedBidiMapTest.class,
	           BidiMapWrapperTest.class,
	           UnmodifiableBidiMapTest.class,
	           ArrayStackTest.class,
	           FileProxyRetrieverTest.class,
	           ByteBufferChannelTest.class,
	           MathUtilsTest.class,
	           HostTokenStoreTest.class,
	           WebUtilsTest.class,
	           NormalizedMapTest.class,
	           ReversableListTest.class,
	           ReversableListNoReverseTest.class,
	           CSVPrinterTest.class,
	           CSVParserTest.class,
	           ReverseFileTest.class,
                   ReaderInputStreamTest.class,
                   WriterOutputStreamTest.class,
                   EmailUtilTest.class
	          })
public class TestSuite {}
