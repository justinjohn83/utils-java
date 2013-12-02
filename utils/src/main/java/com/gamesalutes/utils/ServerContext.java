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

import static com.gamesalutes.utils.ServerContext.ClientVerificationType.*;
import java.io.*;
import java.util.*;

// FIXME: This does not belong in general utils package - move to a new server utils package
// TODO: specify update manager class name like for database factory?
/**
 * Reads in the server config properties.
 * The default path is automatically resolved against the read property values.
 * The context stream is automatically closed whether or not an <code>IOException</code>
 * occurs.
 * 
 * @author Justin Montgomery
 * @version $Id: ServerContext.java 2655 2011-02-20 17:48:48Z jmontgomery $
 */
public final class ServerContext
{

    /**
     * Type of client verification used by auth and data components.
     * 
     * Client verification is used by the auth and data components to insure that authentication
     * was really done properly.  The auth component typically signs its auth response,
     * and then the data component verifies that signature to make sure the auth component 
     * really did issue that response.
     * 
     * @author Justin Montgomery
     * @version $Id: ServerContext.java 2655 2011-02-20 17:48:48Z jmontgomery $
     */
	public enum ClientVerificationType {NONE,DSA};
	
	private String defaultPathPrefix;
	private String dataConfigXml;
	private String dataConfigXsd;
	private String serverConfig;
	private String serverLogConfig;
	private String authConfig;
	private String authLogConfig;
	private String fileLockPath;
	private Set<String> allowableNullProps;
	private String databaseFactoryClass;
	private String updateManagerClass;
	private String updateConfig;
	private String updateLogConfig;
	private ClientVerificationType clientVerificationType;
	private boolean isServer;
	
	private String dataServerUrl;
	private String authServerUrl;
	private String dataServerCert;
	private String authServerCert;
	
	/**
	 * Property key for the default file prefix path to use if a relative file path is 
	 * specified for a property requiring a file path.
	 * 
	 */
	public static final String DEFAULT_PATH_PROP    = "DEFAULT_PATH_PREFIX";
	
	/**
	 * Property key for the xml configuration data used for the data model.
	 * 
	 */
	public static final String DATA_CONF_XML_PROP   = "DATA_CONFIG_XML_PATH";
	
	/**
	 * Property key for the xml schema used for the xml configuration data for the 
	 * data model.
	 * 
	 */
	public static final String DATA_CONF_XSD_PROP   = "DATA_CONFIG_XSD_PATH";
	
	/**
	 * Property key for the server configuration properties file.
	 * 
	 */
	public static final String SERVER_CONF_PROP     = "SERVER_CONFIG_PATH";
	
	/**
	 * Property key for the server log4j properties file.
	 * 
	 */
	public static final String SERVER_LOG_CONF_PROP = "SERVER_LOG_CONFIG_PATH";
	
	/**
	 * Property key for the auth server configuration properties file.
	 * 
	 */
	public static final String AUTH_CONF_PROP       = "AUTH_CONFIG_PATH";
	
	/**
	 * Property key for the auth server log4j properties file.
	 * 
	 */
	public static final String AUTH_LOG_CONF_PROP   = "AUTH_LOG_CONFIG_PATH";
	
	/**
	 * Property key for specifying the file used for file-locking.  Currently
	 * this is only used for the monolithic client that uses a 
	 * <code>LocalRequestCommunicator</code>.
	 *  
	 */
	public static final String FILE_LOCK_PROP       = "FILE_LOCK_PATH";
	
	/**
	 * Property key for specifiying the fully-qualified name of the
	 * <code>DatabaseFactory</code> implementation class
	 * for the database.
	 * 
	 */
	public static final String DB_FACTORY_CLASS_PROP = "DATABASE_FACTORY_IMPL";
	
	
	/**
	 * Property key for specifying the update server configuration properties file.
	 * 
	 */
	public static final String UPDATE_CONF_PROP = "UPDATE_CONF_PROP";
	
	
	/**
	 * Property key for specifying the update server log configuration properties file.
	 * 
	 */
	public static final String UPDATE_LOG_CONF_PROP = "UPDATE_LOG_CONF_PROP";

	/**
	 * Property key for specifying the server update manager.
	 * 
	 */
	public static final String UPDATE_MANAGER_CLASS_PROP = "UPDATE_MANAGER_CLASS";
	
	/**
	 * Property key for specifying the client verification type.
	 * 
	 */
	public static final String CLIENT_VERIFY_TYPE = "CLIENT_VERIFY_TYPE";
	
	
	/**
	 * url for the data server.
	 * 
	 */
	public static final String DATA_SERVER_URL = "DATA_SERVER_URL";
	
	/**
	 * url for the auth server.
	 * 
	 */
	public static final String AUTH_SERVER_URL = "AUTH_SERVER_URL";
	
	/**
	 * Certificate for data server if running over https.
	 * 
	 */
	public static final String DATA_SERVER_CERT = "DATA_SERVER_CERT";
	
	/**
	 * Certificate for auth server if running over https.
	 */
	public static final String AUTH_SERVER_CERT = "AUTH_SERVER_CERT";
	
	/**
	 * Returns the value of the fully-qualified name of the <code>DatabaseFactory</code>
	 * implementation class for the database.
	 * 
	 * @return the <code>DatabaseFactory</code> implementation class name
	 */
	public String getDatabaseFactoryClassName() { return databaseFactoryClass; }
	
	
	/**
	 * Returns the value of the fully-qualified name of the <code>UpdateManager</code>
	 * implementation class .
	 * 
	 * @return the <code>UpdateManager</code> implementation class name
	 */
	public String getUpdateManagerClassName() { return updateManagerClass; }
	/**
	 * Returns the default file prefix path to use if a relative file path is 
	 * specified for a property requiring a file path.
	 * 
	 * @return the default file prefix path
	 */
	public String getDefaultPathPrefix() { return defaultPathPrefix; }
	
	/**
	 * Returns the resolved path to the 
	 * xml configuration data file used for the data model.
	 * 
	 * @return the absolute path to the xml data configuration file
	 */
	public String getDataConfigXML() { return dataConfigXml; }
	
	/**
	 * Returns the resolved path to the
	 * xml schema used for the xml configuration data file for the 
	 * data model.
	 * 
	 * @return the absolute path to the xml schema for the data configuration file
	 * 
	 */
	public String getDataConfigXSD() { return dataConfigXsd; }
	
	/**
	 * Returns the resolved path to the
	 * server configuration properties file.
	 *  
	 * @return the absolute path to the server configuration properties file
	 */
	public String getServerConfig() { return serverConfig; }
	
	/**
	 * Returns the resolved path to the server log4j properties file.
	 * 
	 * @return the absolute path to the server log4j properties file
	 */
	public String getServerLogConfig() { return serverLogConfig; }
	
	/**
	 * Returns the resolved path to the 
	 * auth server configuration properties file.
	 * 
	 * @return the absolute path to the auth server log4j properties file
	 */
	public String getAuthConfig() { return authConfig; }
	
	/**
	 * Returns the resolve path to the auth server log4j properties file.
	 * @return the absolute path to the auth server log4j properties file
	 */
	public String getAuthLogConfig() { return authLogConfig; }
	
	/**
	 * Returns the resolved path to the file used for file-locking.
	 * 
	 * @return the absolute path to the file used for file-locking
	 */
	public String getFileLockPath()  { return fileLockPath; }
	
	/**
	 * Returns the resolved path to the update server config properties file.
	 * 
	 * @return the absolute path to the update server config properties file
	 */
	public String getUpdateConfig() { return updateConfig; }
	
	
	/**
	 * Returns the resolved path to the update server log4j properties file.
	 * 
	 * @return the absolute path to the update server log4j properties file
	 */
	public String getUpdateLogConfig() { return updateLogConfig; }
	
	
	/**
	 * Returns the client verification type used by the system.
	 * 
	 * @return the verification type
	 */
	public ClientVerificationType getClientVerificationType() 
	
	{
		return clientVerificationType;
	}
	
	
	/**
	 * Returns a <code>Collection</code> of property keys allowed to have 
	 * <code>null</code> values in a configuration that only uses the database model
	 * functionality: only uses classes from 
	 * <code>edu.uchicago.nsit.iteco.server.db</code> and not from
	 * the <code>edu.uchicago.nsit.iteco.server</code> package.  These should be 
	 * passed to {@link #ServerContext(InputStream, Collection,boolean)}.
	 * 
	 * @return the <code>Collection</code> of allowable <code>null</code> values for a 
	 *         database model only configuration
	 */
	public static Collection<String> getDBOnlyProps()
	{
		return Arrays.asList(
				SERVER_CONF_PROP,SERVER_LOG_CONF_PROP,AUTH_CONF_PROP,AUTH_LOG_CONF_PROP,
				FILE_LOCK_PROP,UPDATE_CONF_PROP,UPDATE_LOG_CONF_PROP,UPDATE_MANAGER_CLASS_PROP,
				CLIENT_VERIFY_TYPE,
				DATA_SERVER_URL,AUTH_SERVER_URL,
				DATA_SERVER_CERT,AUTH_SERVER_CERT);
	}
	
	
	/**
	 * Returns a <code>Collection</code> of property keys allowed to have <code>null</code>
	 * values in a configuration that only uses the updates server functionality: classes in
	 * <code>edu.uchicago.nsit.iteco.server.update</code> package.
	 * 
	 * @return the <code>Collection</code> of allowable <code>null</code> values for a 
	 * update only configuration.
	 * 
	 */
	public static Collection<String> getUpdateOnlyProps()
	{
		return Arrays.asList(SERVER_CONF_PROP,SERVER_LOG_CONF_PROP,AUTH_CONF_PROP,
				AUTH_LOG_CONF_PROP,FILE_LOCK_PROP,DB_FACTORY_CLASS_PROP,
				DATA_CONF_XML_PROP,DATA_CONF_XSD_PROP,CLIENT_VERIFY_TYPE,
				DATA_SERVER_URL,AUTH_SERVER_URL,
				DATA_SERVER_CERT,AUTH_SERVER_CERT);
	}
	/**
	 * Returns a <code>Collection</code> of property keys allowed to have 
	 * <code>null</code> values in a configuration that only uses the data 
	 * server functionality: classes in <code>edu.uchicago.nsit.iteco.server.auth</code>
	 * are not used.  These should be 
	 * passed to {@link #ServerContext(InputStream, Collection,boolean)}.
	 * 
	 * @return the <code>Collection</code> of allowable <code>null</code> values for a 
	 *         data server only configuration
	 */
	public static Collection<String> getDataOnlyProps()
	{
		return Arrays.asList(AUTH_CONF_PROP,AUTH_LOG_CONF_PROP,FILE_LOCK_PROP,
							UPDATE_CONF_PROP,UPDATE_LOG_CONF_PROP,UPDATE_MANAGER_CLASS_PROP,
							CLIENT_VERIFY_TYPE,
							DATA_SERVER_URL,AUTH_SERVER_URL,
							DATA_SERVER_CERT,AUTH_SERVER_CERT);
	}
	
	/**
	 * Returns a <code>Collection</code> of property keys allowed to have 
	 * <code>null</code> values in a configuration that only uses the authentication
	 * server functionality  only uses classes from 
	 * <code>edu.uchicago.nsit.iteco.server.auth</code> package.  These should be 
	 * passed to {@link #ServerContext(InputStream, Collection,boolean)}.
	 * 
	 * @return the <code>Collection</code> of allowable <code>null</code> values for a 
	 *        authentication server only configuration
	 */
	public static Collection<String> getAuthOnlyProps()
	{
		return Arrays.asList(DATA_CONF_XML_PROP,
					         DATA_CONF_XSD_PROP,
					         SERVER_CONF_PROP,SERVER_LOG_CONF_PROP,FILE_LOCK_PROP,
					         UPDATE_CONF_PROP,UPDATE_LOG_CONF_PROP,UPDATE_MANAGER_CLASS_PROP,
					         CLIENT_VERIFY_TYPE,
					         DATA_SERVER_URL,AUTH_SERVER_URL,
					         DATA_SERVER_CERT,AUTH_SERVER_CERT);
	}
	/**
	 * Constructor.
	 * 
	 * No property values are allowed to be <code>null</code>.
	 * 
	 * @param context <code>InputStream</code> to the context file
	 * @param isServer <code>true</code> if this is a server instance, and 
	 *                 <code>false</code> if this is a local, monolithic instance
	 * @throws IOException if a problem occurs reading <code>context</code> or 
	 *                     if any property value is <code>null</code>
	 */
	public ServerContext(InputStream context,boolean isServer)
		throws IOException
	{
		this(context,Collections.<String>emptySet(),isServer);
	}
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param context <code>InputStream</code> to the context file
	 * @param allowableNullProps <code>Set</code> of properties allowed to be <code>null</code>
	 * @param isServer <code>true</code> if this is a server instance, and 
	 *                 <code>false</code> if this is a local, monolithic instance
	 * @throws IOException if a problem occurs reading <code>context</code> or if any
	 *                     property value other than those with keys in 
	 *                     <code>allowableNullProps</code> is <code>null</code>
	 */
	public ServerContext(InputStream context,Collection<String> allowableNullProps,boolean isServer)
		throws IOException
	{
		if(context == null)
			throw new NullPointerException("context");
		try
		{
			if(allowableNullProps == null)
				throw new NullPointerException("allowableNullProps");
			this.allowableNullProps = new HashSet<String>(allowableNullProps);
			this.isServer = isServer;
			init(context);
		}
		finally
		{
			MiscUtils.closeStream(context);
		}
	}
	
	private void init(InputStream context)
		throws IOException
	{
		String value;
		Properties props = new Properties();
		props.load(context);
		
		// read in the properties
		
		// default path
		value = FileUtils.getConfigProperty(
				props, DEFAULT_PATH_PROP, !allowableNullProps.contains(DEFAULT_PATH_PROP));
		// don't prepend paths with "."
		if(".".equals(value)) value = "";
		this.defaultPathPrefix = value;
		
		// data config xml
		value = FileUtils.getConfigProperty(
				props, DATA_CONF_XML_PROP, 
				!allowableNullProps.contains(DATA_CONF_XML_PROP));
		if(value != null)
		{
			this.dataConfigXml = FileUtils.resolveFile(
					value,defaultPathPrefix);
		}

		// data config xsd
		value = FileUtils.getConfigProperty(
				props, DATA_CONF_XSD_PROP, 
				!allowableNullProps.contains(DATA_CONF_XSD_PROP));
		if(value != null)
		{
			this.dataConfigXsd = FileUtils.resolveFile(
					value, defaultPathPrefix);
		}
		
		// server conf
		value = FileUtils.getConfigProperty(
				props, SERVER_CONF_PROP, 
				!allowableNullProps.contains(SERVER_CONF_PROP));
		if(value != null)
		{
			this.serverConfig = FileUtils.resolveFile(
					value, defaultPathPrefix);
		}
		
		// server log conf
		value = FileUtils.getConfigProperty(
				props, SERVER_LOG_CONF_PROP, 
				!allowableNullProps.contains(SERVER_LOG_CONF_PROP));
		if(value != null)
		{
			this.serverLogConfig = 
				FileUtils.resolveFile(
						value, defaultPathPrefix);
		}
		
		// auth conf
		value = FileUtils.getConfigProperty(
				props, AUTH_CONF_PROP,
				!allowableNullProps.contains( AUTH_CONF_PROP));
		if(value != null)
		{
			this.authConfig = 
				FileUtils.resolveFile(
						value, defaultPathPrefix);
		}
		
		// auth log conf
		value = FileUtils.getConfigProperty(
				props, AUTH_LOG_CONF_PROP,
				!allowableNullProps.contains( AUTH_LOG_CONF_PROP));
		if(value != null)
		{
			this.authLogConfig = 
				FileUtils.resolveFile(
						value, defaultPathPrefix);
		}
		
		// file lock path
		value = FileUtils.getConfigProperty(
				props, FILE_LOCK_PROP,
				!allowableNullProps.contains( FILE_LOCK_PROP));
		if(value != null)
		{
			this.fileLockPath = 
				FileUtils.resolveFile(
						value, defaultPathPrefix);
		}
		
		// datatabase factory class name
		value = FileUtils.getConfigProperty(props, DB_FACTORY_CLASS_PROP,
				!allowableNullProps.contains( DB_FACTORY_CLASS_PROP));
		this.databaseFactoryClass = value;
		
		
		// update server conf
		value = FileUtils.getConfigProperty(
				props, UPDATE_CONF_PROP,
				!allowableNullProps.contains(UPDATE_CONF_PROP));
		if(value != null)
		{
			this.updateConfig = 
				FileUtils.resolveFile(value, defaultPathPrefix);
		}
		
		// update server log conf
		value = FileUtils.getConfigProperty(
				props, UPDATE_LOG_CONF_PROP,
				!allowableNullProps.contains(UPDATE_LOG_CONF_PROP));
		if(value != null)
		{
			this.updateLogConfig = 
				FileUtils.resolveFile(value, defaultPathPrefix);
		}
		
		// update manager class name
		value = FileUtils.getConfigProperty(
				props, UPDATE_MANAGER_CLASS_PROP,
				!allowableNullProps.contains(UPDATE_MANAGER_CLASS_PROP));
		this.updateManagerClass = value;
		
		// client verification type
		value = FileUtils.getConfigProperty(
				props, CLIENT_VERIFY_TYPE,
				!allowableNullProps.contains(CLIENT_VERIFY_TYPE));
		if(value != null)
		{
			value = value.toUpperCase().trim();
			this.clientVerificationType = ClientVerificationType.valueOf(value);
		}
		// if type is null then default how things were done before:
		// server instances used DSA, while local instances used none
		if(this.clientVerificationType == null)
			this.clientVerificationType = isServer ? DSA : NONE;
		
		// data server url
		value = FileUtils.getConfigProperty(
				props, DATA_SERVER_URL,
				!allowableNullProps.contains(DATA_SERVER_URL));
		this.dataServerUrl = value;
			
		// auth server url
		value = FileUtils.getConfigProperty(
				props, AUTH_SERVER_URL,
				!allowableNullProps.contains(AUTH_SERVER_URL));
		this.authServerUrl = value;
		
		// data server cert
		value = FileUtils.getConfigProperty(
				props,DATA_SERVER_CERT,
				!allowableNullProps.contains(DATA_SERVER_CERT));
		if(value != null)
		{
			this.dataServerCert = 
				FileUtils.resolveFile(value, defaultPathPrefix);
		}
		
		// auth server cert
		value = FileUtils.getConfigProperty(
				props,AUTH_SERVER_CERT,
				!allowableNullProps.contains(AUTH_SERVER_CERT));
		if(value != null)
		{
			this.authServerCert = 
				FileUtils.resolveFile(value, defaultPathPrefix);
		}
		
		
	} // end init
	/**
	 * Relativizes the output log files specified in <code>logProps</code>
	 * to this context's default path prefix if a relative path is given.
	 * 
	 * @param logProps the log <code>Properties</code>
	 */
	public void relativizeLogOutputFiles(Properties logProps)
	{
		for(Map.Entry<Object, Object> E : logProps.entrySet())
		{
			Object o = E.getKey();
			if(o instanceof String)
			{
				String key = (String)o;
				String value = (String)E.getValue();
				if(!MiscUtils.isEmpty(key) && !MiscUtils.isEmpty(value))
				{
					// replace all log4j file entries
					if(key.startsWith("log4j.appender.") &&
				       key.endsWith(".File"))
					{
						value = FileUtils.resolveFile(value,this.defaultPathPrefix);
						logProps.setProperty(key, value);
					}
				}
			}
		} // end for
	}
	
	/**
	 * Returns the url string of the data server.
	 * 
	 * @return the data server url
	 */
	public String getDataServerURL()
	{
		return this.dataServerUrl;
	}
	
	/**
	 * Returns the url string of the auth server.
	 * 
	 * @return the auth server url
	 */
	public String getAuthServerURL()
	{
		return this.authServerUrl;
	}
	
	/**
	 * Returns the certificate of the data server.
	 * 
	 * @return the data server certificate
	 */
	public String getDataServerCertificate()
	{
		return this.dataServerCert;
	}
	
	/**
	 * Returns the certificate of the auth server.
	 * 
	 * @return the auth server certificate
	 */
	public String getAuthServerCertificate()
	{
		return this.authServerCert;
	}
	
} // end ServerContext
