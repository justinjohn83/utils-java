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
package com.gamesalutes.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.gamesalutes.utils.EncryptUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Does an ldap bind or query.
 * 
 * @author Justin Montgomery
 * @version $Id: LdapQuery.java 505 2008-08-05 22:40:59Z jmontgomery $
 */
public final class LdapQuery
{
	
    private static final String INITIAL_CONTEXT_FACTORY =
            Context.INITIAL_CONTEXT_FACTORY;

    private static final String LDAP_VERSION =
            "java.naming.ldap.version";

    private static final String PROVIDER_URL =
            Context.PROVIDER_URL;

    // explicitly specify strong cipher suites if they are available
    private static final String SSL_CIPER_SUITE =
            "java.naming.security.ssl.ciphers";
    private static final String SEARCH_FILTER = "(objectClass=*)";



    private final Hashtable<String,String> mLdapParams;
    private final String mBaseDN;
    private final String mUserNameAttr;
    private final boolean mUseSsl;
    private final String mStrongCiphers;

    private volatile LdapAuthN bindUser;

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());


    public interface AuthorizationCallback
    {
        List<String> getAuthorizationFilters();

        /**
         * Called with the results of the applied <code>filter</code>.
         * If authentication is still successful and
         * further checks should be permitted return <code>true</code>;
         * otherwise, return <code>false</code>.
         *
         * @return <code>true</code> if authentication is still successful
         * and <code>false</code> otherwise
         */
        boolean onAuthorization(String filter,boolean isAuthorized);
    }

    private static class LdapAuthN
    {
        private final String baseDN;
        private final String userNameAttr;
        private final String userName;
        private final String password;

        public LdapAuthN(String baseDN,String userNameAttr,String userName,String password)
        {
            this.baseDN = baseDN;
            this.userNameAttr = userNameAttr;
            this.userName = userName;
            this.password = password;
        }

        public String getBaseDN()
        {
            return baseDN;
        }

        public String getPassword()
        {
            return password;
        }

        public String getUserName()
        {
            return userName;
        }

        public String getUserNameAttr()
        {
            return userNameAttr;
        }

        @Override
        public String toString()
        {
            return new StringBuilder(1024).append("baseDN=").append(baseDN
                    ).append(";userNameAttr=").append(userNameAttr
                    ).append(";userName=").append(userName
                    ).toString();
        }

    }

    /**
     * Constructor.
     *
     * @param contextParams the directory context init parameters
     * @param useSsl <code>true</code> to use ssl and <code>false</code> otherwise
     * @param baseDN the base directory name to use in lookups
     * @param searchAttr the key to use for the user name
     */
    public LdapQuery(
                    Hashtable<? extends Object,? extends Object> contextParams,
                    boolean useSsl,
                    String baseDN,
                    String searchAttr
                    )

    {
        this(
               (String)contextParams.get(INITIAL_CONTEXT_FACTORY),
               (String)contextParams.get(LDAP_VERSION),
               (String)contextParams.get(PROVIDER_URL),
               useSsl,
               baseDN,
               searchAttr);
    }

   /**
     * Constructor.
     *
     * @param initialCtxFactory the <code>JNDI</code> ldap initial context factory
     * @param ldapVersion the version of ldap
     * @param providerUrl the ldap url
     * @param useSsl <code>true</code> to use ssl and <code>false</code> otherwise
     * @param baseDN the base directory name to use in lookups
     * @param searchAttr the key to use for the user name
     */
    public LdapQuery(String initialCtxFactory,String ldapVersion,String providerUrl,
            boolean useSsl,String baseDN,String usernameAttr)
    {

        if(initialCtxFactory == null)
            throw new NullPointerException("initialCtxFactory");
        if(ldapVersion == null)
            throw new NullPointerException("ldapVersion");
        if(providerUrl == null)
            throw new NullPointerException("providerUrl");
        if(baseDN == null)
            throw new NullPointerException("baseDN");
        if(usernameAttr == null)
            throw new NullPointerException("usernameAttr");

        mLdapParams = new Hashtable<String,String>();
        mLdapParams.put(INITIAL_CONTEXT_FACTORY, initialCtxFactory);
        mLdapParams.put(LDAP_VERSION,ldapVersion);
        mLdapParams.put(PROVIDER_URL,providerUrl);

        this.mUseSsl = useSsl || providerUrl.startsWith("ldaps");
        this.mBaseDN = baseDN;
        this.mUserNameAttr = usernameAttr;
        this.mStrongCiphers =
                EncryptUtils.getSupportedStrongCipherSuitesAsStr();


    }

    /**
     * Returns the url to the ldap server.
     *
     * @return the ldap url
     */
    public String getProviderUrl()
    {
        return String.valueOf(this.mLdapParams.get(PROVIDER_URL));
    }

    /**
     * Returns the default base DN.
     *
     * @return the base DN
     */
    public String getBaseDN()
    {
        return this.mBaseDN;
    }

    /**
     * Returns the default attribute name to use as the username when doing lookups.
     *
     * @return the default user name attribute
     */
    public String getUserNameAttribute()
    {
        return this.mUserNameAttr;
    }


    public void dispose()
    {
        this.bindUser = null;
    }


       /**
	 * Checks to see if the specified <code>user</code> is in ldap.
	 *
	 * @param attrName name of attribute to use for the search
        *  @param attrValue the value of <code>attrName</code> to use for the search
        *
	 * @return the found user as a <code>SearchResult</code> or <code>null</code>
	 *          if not found
	 * @throws NamingException if error occurs during lookup
	 */
        	/**
	 * Checks to see if the specified <code>userName</code> is in ldap.
	 *
	 * @param userName the user name
	 * @return the found user as a <code>SearchResult</code> or <code>null</code>
	 *          if not found
	 * @throws NamingException if error occurs during lookup
	 */
	public SearchResult lookup(String userName)
		throws NamingException
	{
            return lookup(this.mUserNameAttr,userName);
        }

        public SearchResult lookup(String userName,AuthorizationCallback cb)
            throws NamingException
        {
            return lookup(this.mUserNameAttr,userName,cb);
        }

        public SearchResult lookup(String userNameAttr,String userNameAttrValue)
                throws NamingException
        {
            return lookup(userNameAttr,userNameAttrValue,null);
        }

        public SearchResult lookup(String userNameAttr,String userNameAttrValue,AuthorizationCallback cb)
                throws NamingException
        {
            return lookup(this.mBaseDN,userNameAttr,userNameAttrValue,cb);
        }


        private InitialDirContext createInitialDirContext()
        {
            return createInitialDirContext(null,null,null,null);
        }

        public SearchResult lookup(String baseDN,String userNameAttr,String userNameAttrValue,AuthorizationCallback cb)
                throws NamingException
        {

                if(userNameAttr == null) throw new NullPointerException("attrName");
                if(userNameAttrValue == null) throw new NullPointerException("attrValue");



                InitialDirContext ldapDirContext = null;
                try
                {
                    LdapAuthN user = bindUser;
                    if(user != null)
                    {
                        ldapDirContext = createInitialDirContext(user);
                    }
                    else
                    {
                        ldapDirContext = createInitialDirContext();
                    }

                    if(ldapDirContext == null)
                    {
                        throw new RuntimeException("Unable to initialize ldap dir context - user:" + (user != null ? user : "anon"));
                    }

                    SearchControls ctrls = new SearchControls();
                    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                    String filter = "(" + userNameAttr + "=" + userNameAttrValue + ")";
                    //apply the name filter
                    NamingEnumeration<SearchResult> answer = ldapDirContext.search(baseDN,filter,ctrls);

                    //user doesn't exist
                    SearchResult sr = null;
                    try
                    {
                        if(!answer.hasMore())
                        {
                            if(logger.isDebugEnabled())
                                logger.debug("Lookup: baseDN: " + baseDN + ";filter=" + filter + " yielded no results");
                            return null;
                        }
                        sr = answer.next();
                        //only single user can match
                        if(answer.hasMore())
                        {
                             if(logger.isDebugEnabled())
                                logger.debug("Lookup: baseDN: " + baseDN + ";filter=" + filter + " yielded multiple results");
                             return null;
                        }
                    }
                    finally
                    {
                        answer.close();
                    }

                    if(checkAuthorization(ldapDirContext,this.makeUserAbsolute(sr),cb))
                        return sr;

//                    if(logger.isDebugEnabled())
//                        logger.debug("Authorization on ldap entry failed for " + this.makeUserAbsolute(sr));
                    return null;
                }
                finally
                {
                    try
                    {
                        if(ldapDirContext != null)
                            ldapDirContext.close();
                    }
                    catch(NamingException e) {}
                }

	}

        private InitialDirContext createInitialDirContext(LdapAuthN user)
        {
            return createInitialDirContext(user.getBaseDN(),user.getUserNameAttr(),
                    user.getUserName(),user.getPassword());
        }
        private InitialDirContext createInitialDirContext(
                String baseDN,String userNameAttr,
                String userName,String password)
        {

            // create the directory context
            Hashtable<Object, Object> ldapParams = new Hashtable<Object, Object>();
            // add all the basic parameters
            ldapParams.putAll(mLdapParams);

            if(userName == null)
            {
                if(mUseSsl)
		{
			ldapParams.put(Context.SECURITY_PROTOCOL, "ssl");
		    //set strong cipher suites if possible
		    if(mStrongCiphers != null)
		    	ldapParams.put(SSL_CIPER_SUITE, mStrongCiphers);
		}

		ldapParams.put(Context.SECURITY_AUTHENTICATION, "none");

                try
                {
                    return new InitialDirContext(ldapParams);
                }
                catch(NamingException e)
                {
                    if(logger.isDebugEnabled())
                        logger.debug("Ldap lookup error",e);
                    return null;
                }
            }
            // do bind
            else
            {
                // make absolute
                if(!MiscUtils.isEmpty(baseDN))
                    userName = userNameAttr + "=" + userName + "," + baseDN;

		if(mUseSsl)
		{
			ldapParams.put(Context.SECURITY_PROTOCOL, "ssl");
		    //set strong cipher suites if possible
		    if(mStrongCiphers != null)
		    	ldapParams.put(SSL_CIPER_SUITE, mStrongCiphers);
		}

		ldapParams.put(Context.SECURITY_AUTHENTICATION, "simple");
                ldapParams.put(Context.SECURITY_PRINCIPAL, userName);
		ldapParams.put(Context.SECURITY_CREDENTIALS, password);

		// authenticates if provided a password; otherwise, does a lookup
                try
                {
                    return new InitialDirContext(ldapParams);
                }
                // authentication failed
                catch(NamingException e)
                {
                   if(logger.isDebugEnabled())
                       logger.debug("Ldap lookup error",e);
                    return null;
                }
            }
        }
	/**
	 * Authenticates the user with <code>userName</code> and <code>password</code>
	 * @param userName the user name
	 * @param password the password
	 * @return the found user as a <code>SearchResult</code>
	 * @throws NamingException if authentication fails
	 */
	public SearchResult authenticate(String userName,String password)
                throws NamingException
        {
            return authenticate(userName,password,null);
        }

        private String makeUserAbsolute(SearchResult sr)
        {
            	//success! Single user found
		//get the full DN of the single user
		//assuming relative DN so append the base
                return sr.getNameInNamespace();
//		if(sr.isRelative())
//			return sr.getName() + "," + this.mBaseDN;
//		else
//			return sr.getName();
        }

       /**
	 * Authenticates the user with <code>userName</code> and <code>password</code>
	 * @param userName the user name
	 * @param password the password
         * @param cb <code>AuthorizationCallback</code>
	 * @return the found user as a <code>SearchResult</code>
	 * @throws NamingException if authentication fails
	 */
        public SearchResult authenticate(String userName,String password,AuthorizationCallback cb)
                throws NamingException
        {
            return authenticate(this.mBaseDN,
                    this.mUserNameAttr,userName,password,cb,false);
        }
	/**
	 * Authenticates the user with <code>userName</code> and <code>password</code>
	 * @param userName the user name
	 * @param password the password
         * @param save save the session
         * @param cb <code>AuthorizationCallback</code>
	 * @return the found user as a <code>SearchResult</code> or <code>null</code> if user does not exist or
         *         authentication fails
	 * @throws NamingException if an error occurs during lookup
	 */
	public SearchResult authenticate(final String baseDN,final String userNameAttr,
                String userName,final String password,
                AuthorizationCallback cb,
                boolean save)
		throws NamingException
	{
		if(userName == null) throw new NullPointerException("userName");
		if(password == null) throw new NullPointerException("password");

                InitialDirContext ldapDirContext = null;

                try
                {
		// first do lookup
		SearchResult sr = lookup(baseDN,userNameAttr,userName,null);
		if(sr == null)
                    return null;

                userName = this.makeUserAbsolute(sr);
                ldapDirContext = createInitialDirContext(null,userNameAttr,
                        userName,password);

                if(ldapDirContext == null)
                    return null;


                    SearchControls ldapSearchControls = new SearchControls();
                    ldapSearchControls.setSearchScope(SearchControls.OBJECT_SCOPE);
                    NamingEnumeration<SearchResult> ldapSearchResultEnum =
                            ldapDirContext.search(userName, SEARCH_FILTER,ldapSearchControls);

                    SearchResult result = null;

                    try
                    {
                        if(ldapSearchResultEnum.hasMore())
                                result = ldapSearchResultEnum.next();
                        else // search failed
                        {
                             if(logger.isDebugEnabled())
                                logger.debug("User: "  + userName + " authenticated, but unable to retrieve search result");
                             return null;
                        }
                    }
                    finally
                    {
                        ldapSearchResultEnum.close();
                    }

                    if(checkAuthorization(ldapDirContext,userName,cb))
                    {
                        if(save)
                            bindUser = new LdapAuthN(null,userNameAttr,userName,password);
                        return result;
                    }

//                    if(logger.isDebugEnabled())
//                        logger.debug("Authorization on ldap entry failed for " + userName);

                    return null;
                }
                finally
                {
                    if(ldapDirContext != null)
                    {
                        try { ldapDirContext.close(); }
                        catch(NamingException ne) {}
                    }
                }
	}
        
        private boolean checkAuthorization(InitialDirContext ctx,
                String username,AuthorizationCallback cb)
           throws NamingException
        {
            // do authorization if callback defined
            if(cb != null)
            {
                List<String> filters = cb.getAuthorizationFilters();
                if(!MiscUtils.isEmpty(filters))
                {
                    SearchControls ctrls = new SearchControls();
                    ctrls.setSearchScope(SearchControls.OBJECT_SCOPE);


                    for(String filter : filters)
                    {
                        NamingEnumeration<SearchResult> answer;

                        answer = ctx.search(username, filter,ctrls);
                        try
                        {
                            if(!cb.onAuthorization(filter, answer.hasMore()))
                            {
                                if(logger.isDebugEnabled())
                                    logger.debug("Authorization for user:" + username + " failed for callback=" + cb + " using filter=" + filter);
                                return false;
                            }
                        }
                        finally
                        {
                            answer.close();
                        }
                    }
                }
            }
            
            return true;
        }



	
	/**
	 * Gets ldap attribute values from the specified <code>SearchResult</code>
	 * and attribute name.
	 * 
	 * @param pResult the <code>SearchResult</code>
	 * @param pName the attribute name
	 * @return the attribute values if a non-null mapping exists for
	 *          <code>pName</code> and <code>null</code> otherwise
	 * @throws NamingException if error occurs reading attributes
	 */
	public static List<String> getLdapAttributes(
			SearchResult pResult, String pName)
		throws NamingException
	{
              return getValues(pResult.getAttributes().get(pName));
	}

        private static List<String> getValues(Attribute attr)
                throws NamingException
        {

	      if (attr == null)
              {
	         return null;
	      }

              List<String> values = new ArrayList<String>();


	      for(int i = 0; i < attr.size(); ++i)
	      {
	    	  Object val = attr.get(i);
	    	  if(val != null)
	    		  values.add(val.toString());
	      }

	      return values;
        }

        private static String getValue(Attribute attr)
                throws NamingException
        {
                List<String> results = getValues(attr);
                return !MiscUtils.isEmpty(results) ? results.get(0) : null;
        }
	
        /**
         * Gets an ldap attribute value from the specified <code>SearchResult</code>
         * and attribute name.
         *
         * @param pResult the <code>SearchResult</code>
         * @param pName the attribute name
         * @return the attribute value if a non-null mapping exists for
         *          <code>pName</code> and <code>null</code> otherwise
         * @throws NamingException if error occurs reading attributes
         */
        public static String getLdapAttribute(
                        SearchResult pResult, String pName)
                throws NamingException
        {
            return getValue(pResult.getAttributes().get(pName));
        }

        public static Map<String,List<String>> getAllLdapAttributes(SearchResult pResult)
                throws NamingException
        {
            Map<String,List<String>> results = new HashMap<String,List<String>>();


            NamingEnumeration<? extends Attribute> attributes = pResult.getAttributes().getAll();
            if(attributes != null)
            {
                while(attributes.hasMore())
                {
                    Attribute attr = attributes.next();
                    
                    String id = attr.getID();
                    if(id != null)
                    {
                        List<String> values = getValues(attr);
                        if(values != null)
                            results.put(id,values);
                    }
                }
            }

            return results;
        }

        public static List<String> getAllLdapAttributeIDs(SearchResult pResult)
                throws NamingException
        {
            NamingEnumeration<String> e = pResult.getAttributes().getIDs();
            List<String> ids = new ArrayList<String>();
            if(e != null)
            {
                while(e.hasMore())
                    ids.add(e.next());
            }

            return ids;
        }

}
