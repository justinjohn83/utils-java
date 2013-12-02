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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.codec.binary.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Utility methods for encryption and SSL.
 * 
 * @author Justin Montgomery
 * @version $Id: EncryptUtils.java 2611 2011-02-07 21:03:43Z jmontgomery $
 *
 */
public final class EncryptUtils
{
	public static final List<String> STRONG_CIPHER_SUITES;
	private static final String CONF_FILE = "conf.properties";
	private static final String CONF_CIPHER_PREFIX = "EncryptUtils.ciphers";
	private static final String HTTPS_CIPHER_PROP = "https.cipherSuites";
	
	/**
	 * The type of entry to store
	 * @author Justin Montgomery
	 * @version $Id: EncryptUtils.java 2611 2011-02-07 21:03:43Z jmontgomery $
	 */
	public enum StoreType 
	{
		/**
		 * Type is a private key.
		 * 
		 */
		PRIVATE_KEY,
		/**
		 * Type is a certificate.
		 * 
		 */
		CERTIFICATE;
	}
	
	/**
	 * Specifies the protocol of the transport layer security.
	 * 
	 * @author Justin Montgomery
	 * @version $Id: EncryptUtils.java 2611 2011-02-07 21:03:43Z jmontgomery $
	 */
	public enum TransportSecurityProtocol
	{
		/**
		 * TLSv1 protocol.
		 */
		TLS,
		/**
		 * SSLv3 protocol.
		 * 
		 */
		SSL;
	}
	
	private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
	private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
	private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
	private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
	private static final int CHUNK_LEN = 64;
	// TODO: use "\r\n" ? openssl uses "\n"
	private static final String KEY_LINE_TERM = "\n";
	/**
	 * Specifies the <code>PKCS8</code> private key type. 
	 * Encoded key must be in DER format.
	 * 
	 */
	public static final String PKCS8_TYPE = "pkcs8";
	/**
	 * Specifies the <code>PKCS12</code> key store type.
	 * 
	 */
	public static final String PKCS12_TYPE = "pkcs12";
	
	/**
	 * Specifies the <code>JKS</code> key store type.
	 * 
	 */
	public static final String JKS_TYPE = "jks";
	
	/**
	 * Specifies that the certificate type is "X509".
	 * 
	 */
	public static final String CERT_TYPE_X509 = "X509";
	
	
	private static final String KEY_MANAGEMENT_ALG_SUN_X509 = "SunX509";
	
	
	private EncryptUtils() {}
	
	//load the strong cipher suites
	static
	{
		// set bouncy castle provider
//		try
//		{
//		    // some Java system classes may rely on platform provider being first
//			//Security.insertProviderAt(
//			//		new org.bouncycastle.jce.provider.BouncyCastleProvider(),
//			//		1);
//		    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//		}
//		catch(Exception e)
//		{
//			Logger.getLogger(EncryptUtils.class).warn(
//					"Bouncy Castle Provider not available",e);
//		}
		
		SortedMap<String,String> propMap = new TreeMap<String,String>();
		//load the "conf.properties" resource
		Properties prop = new Properties();
		try
		{
			//load the ciphers
			prop.load(EncryptUtils.class.getResourceAsStream(CONF_FILE));
			for(Map.Entry<?,?> E : prop.entrySet())
			{
				String key = (String)E.getKey();
				String value = (String)E.getValue();
				if(key.startsWith(CONF_CIPHER_PREFIX))
					propMap.put(key, value);
			}
			STRONG_CIPHER_SUITES = Collections.unmodifiableList(new ArrayList<String>(propMap.values()));
		}
		catch(IOException e)
		{
			throw new AssertionError(EncryptUtils.class.getName() +
					"could not load ciphers from package resource \"" +
					CONF_FILE + "\"");
		}
		
	} //end static

	/**
	 * Returns <code>List</code> of strings in {@link #STRONG_CIPHER_SUITES} that
	 * are supported by the default ssl socket factory.
	 * 
	 * @return <code>List</code> of supported strong cipher suites
	 */
	public static List<String> getSupportedStrongCipherSuites()
	{
		Set<String> suites = new HashSet<String>(Arrays.asList( 
				HttpsURLConnection.getDefaultSSLSocketFactory().getSupportedCipherSuites()));
		List<String> enabledSuites = new ArrayList<String>();
		
		//get strong suites that are supported by the SSL factory
		for(String s : STRONG_CIPHER_SUITES)
		{
			if(suites.contains(s))
				enabledSuites.add(s);
		}
		return enabledSuites;
		//convert list into comma separated string for use in System.setProperty
	}

	/**
	 * Returns a comma-separated string containing the strong cipher suites that
	 * are supported by the default ssl socket factory.
	 * 
	 * @return comma-separated string containing the supported strong cipher suites or
	 *         <code>null</code> if none are supported
	 */
	public static String getSupportedStrongCipherSuitesAsStr()
	{
		//get the supported strong suites
		Collection<String> suites = getSupportedStrongCipherSuites();
		
		if(suites.isEmpty())
			return null;
		
		//get the deliminated string version 
		return CollectionUtils.convertCollectionIntoDelStr(suites,",");
	}

	/**
	 * Sets the System property for the enabled cipher suites of <code>HttpsURLConnection</code>
	 * instances to the enabled strong suites.
	 *
	 */
	public static boolean setHttpsEnabledStrongSuites()
	{
		//get the deliminated string version for using System.setProperty
		String suiteStr = getSupportedStrongCipherSuitesAsStr();
		if(suiteStr == null) return false;
		
		try
		{
			System.setProperty(HTTPS_CIPHER_PROP, suiteStr);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Reads a DER-encoded certificate using binary or Base64 encoding from the given file.
	 * 
	 * @param file
	 * @param certType
	 * @return the read certificate
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static java.security.cert.Certificate readCertificate(
			File file,String certType)
		throws CertificateException,IOException
	{
		return readCertificate(new BufferedInputStream(FileUtils.newFileInputStream(file)),certType);
	}
	
	/**
	 * Reads a DER-encoded certificate that uses binary or Base64 encoding from the given input stream.
	 * 
	 * @param in the <code>InputStream</code>
	 * @param certType
	 * @return the read certificate
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static java.security.cert.Certificate readCertificate(
			InputStream in,String certType)
		throws CertificateException,IOException
	{
		// get the raw certificate bytes so that can trim and format cert properly
		byte [] data = null;
		try
		{
			data = ByteUtils.readBytes(in);
			// in automatically closed during call to readBytes even in case of exceptions
			in = null;
		}
		catch(Exception e)
		{
			in = null;
			throw new ChainedIOException("Error getting stream bytes",e);
		}
		CertificateFactory cf = CertificateFactory.getInstance(certType);
		try
		{
			byte [] pemData = formatBase64ToPem(getRawBase64Key(data),BEGIN_CERTIFICATE,END_CERTIFICATE);
			in = new ByteArrayInputStream(pemData);
			return cf.generateCertificate(in);
		}
		catch(Exception e)
		{
			MiscUtils.closeStream(in);
			// maybe it was in binary DER and couldn't be read when it was assumed to be in Base64
			in = new ByteArrayInputStream(data);
			return cf.generateCertificate(in);
		}
		finally
		{
			MiscUtils.closeStream(in);
		}

	}
	


        /**
         * Convenience method for reading a private key from the supported store types :
         *  jks,PKCS12,PKCS8.  This method attempts to guess the format and algorithm to read the key.
         *
         * @param in the key <code>InputStream</code>
         * @param alias the alias for the key or <code>null</code> if this does not apply
         * @param passwd the password for the key or <code>null</code> if no password or does not apply
         *
         */
        public static PrivateKey readPrivateKey(InputStream in,String alias,char [] passwd)
                throws Exception
        {
            // try PKCS8
            if(alias == null && passwd == null)
            {
                try
                {
                    return readPKCS8(in,"RSA");
                }
                catch(Exception e) {}

                try
                {
                    return readPKCS8(in,"DSA");
                }
                catch(Exception e){}
            }

            //try PKCS12
            try
            {
                return readPKCS12PrivateKey(in,alias,passwd);
            }
            catch(Exception e){}

            try
            {
                return readJKSPrivateKey(in,alias,passwd);
            }
            catch(Exception e)
            {
                throw new IOException("Unable to read key stream");
            }
        }
	/**
	 * Reads a private key from a "jks" key store.
	 * 
	 * @param in the key <code>InputStream</code>
	 * @param alias the alias for the key
	 * @param pass the password to retrieve the key
	 * @return the read <code>PrivateKey</code>
	 * @throws Exception if error occurs retrieving the key
	 */
	public static PrivateKey readJKSPrivateKey(InputStream in,String alias,char [] pass)	
		throws Exception
	{
		return readKeyStoreKey(in,"jks",alias,pass);
	}
	
	/**
	 * Reads a private key from a "pkcs12" key store.
	 * 
	 * @param in the key <code>InputStream</code>
	 * @param alias the alias for the key
	 * @param pass the password to retrieve the key
	 * @return the read <code>PrivateKey</code>
	 * @throws Exception if error occurs retrieving the key
	 */
	public static PrivateKey readPKCS12PrivateKey(InputStream in,String alias,char [] pass)	
		throws Exception
	{
		return readKeyStoreKey(in,"pkcs12",alias,pass);
	}
	
	private static PrivateKey readKeyStoreKey(InputStream in,String storeType,
			String alias,char [] pass)
		throws Exception
	{
		try
		{
			KeyStore ks = KeyStore.getInstance(storeType);
			//load the key store
			//TODO: specify other than "null" if want key store integrity check
			//need key store passwd
			ks.load(in,null);
			return (PrivateKey)ks.getKey(alias, pass);
		}
		finally
		{
			MiscUtils.closeStream(in);
		}
	}
	
	/**
	 * Reads a PKCS8 formatted private key from file.
	 * 
	 * @param in the <code>InputStream</code> containing the key
	 * @param alg the key algorithm
	 * @return the read key
	 * @throws Exception if error occurs reading the key
	 */
	public static PrivateKey readPKCS8(InputStream in,String alg)
		throws Exception
	{
		try
		{
			if(alg == null) throw new NullPointerException("alg");
			//alg = alg.toUpperCase();
			//if(!alg.equals("DSA") || !alg.equals("RSA"))
			//	throw new IllegalArgumentException("Illegal key alg=" + alg);
			byte[] encodedKey = ByteUtils.readBytes(in);
			KeyFactory kf = KeyFactory.getInstance(alg);
			try
			{
				return kf.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
			}
			catch(Exception e)
			{
				// maybe key was in PEM so convert to binary
				encodedKey = EncryptUtils.fromPemtoBinary(encodedKey);
				return kf.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
			}
		}
		finally
		{
			MiscUtils.closeStream(in);
		}
	}
	
	/**
	 * Computes an MD5 digest from the contents of the specified <code>InputStream</code>.
	 * 
	 * @param in the <code>InputStream</code>
	 * @return the digest bytes
	 * @throws Exception if error occurs reading the stream or computing the digest
	 */
	public static byte[] computeMD5Digest(InputStream in)
		throws Exception
	{
		byte [] data = ByteUtils.readBytes(in);
		return computeMD5Digest(data);
	}
	
	
	/**
	 * Computes an MD5 digest from the contents of the specified byte array.
	 * 
	 * @param data the data bytes
	 * @return the digest bytes
	 * @throws Exception if error occurs reading the stream or computing the digest
	 */
	public static byte [] computeMD5Digest(byte [] data)
		throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data);
		return md.digest();
	}
	
	/**
	 * Converts PEM-encoded key data to raw binary format.  It is expected that 
	 * the first and last lines have <code>---BEGIN [xxx]---</code> and
	 * <code>---END [xxx]---</code>, respectively.
	 * 
	 * <code>data</code> array is not modified as a result of calling this 
	 * operation.
	 * 
	 * @param data the PEM-encoded key data
	 * @return the raw binary encoded key data
	 */
	public static byte[] fromPemtoBinary(byte [] data)
	{
		// first read in the raw pem data and trim the first and last lines
		if(data == null)
			throw new NullPointerException("data");
		
		data = getRawBase64Key(data);
		// now base64 decode the resultant raw Base64 data
		return Base64.decodeBase64(data);
	}
	
	/**
	 * Returns a raw Base64 key with no line breaks and no <code>---BEGIN [xxx]---</code> or
	 * <code>---END [xxx]---</code> lines that appear in the PEM encoding.
	 * <code>data</code> array is not modified as a result of calling this 
	 * operation.
	 * 
	 * @param data the input base 64 data using a PEM-encoding
	 * @return the stripped base 64 data
	 */
	private static byte [] getRawBase64Key(byte [] data)
	{
		// first read in the raw pem data and trim the first and last lines
		if(data == null)
			throw new NullPointerException("data");
		
		String str = null;
		try
		{
			str = new String(data,"UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new AssertionError("UTF-8 encoding not supported");
		}
		String [] lineSplits = str.split(MiscUtils.LINE_BREAK_REGEX);
		// replace the first line: "has a -" character in it
		int firstReplaceIndex = -1;
		for(int i = 0; i < lineSplits.length; ++i)
		{
			if(lineSplits[i].indexOf('-') != -1)
			{
				str = str.replace(lineSplits[i], "");
				firstReplaceIndex = i;
				break;
			}
		}
		// replace the last line: if we couldn't find the "first line" then we've searched the 
		// whole string, so don't search again!
		if(firstReplaceIndex != -1)
		{
			for(int i = lineSplits.length - 1; i > firstReplaceIndex; --i)
			{
				if(lineSplits[i].indexOf('-') != -1)
				{
					str = str.replace(lineSplits[i], "");
					break;
				}
			}
		}
		// remove all the rest of the spaces and line breaks
		str = str.replaceAll("\\s", "");
		try
		{
			return str.getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new AssertionError("UTF-8 encoding not supported");
		}
	}
	
	/**
	 * Converts the raw binary encoded bytes of the specified
	 * {@link StoreType} key data to <code>PEM</code>  encoded bytes.
	 * <code>data</code> array is not modified as a result of calling this 
	 * operation.
	 * 
	 * @param data the raw binary key data
	 * @param type the <code>StoreType</code> of the data
	 * @return the <code>PEM</code> key data
	 */
	public static byte [] fromBinaryToPem(byte [] data,StoreType type)
	{
		if(data == null)
			throw new NullPointerException("data");
		if(type == null)
			throw new NullPointerException("type");
		switch(type)
		{
		case PRIVATE_KEY:
			return fromBinaryToPem(data,BEGIN_PRIVATE_KEY,END_PRIVATE_KEY);
		case CERTIFICATE:
			return fromBinaryToPem(data,BEGIN_CERTIFICATE,END_CERTIFICATE);
		default: throw new IllegalArgumentException("type=" + type);
		}
	}


       /**
	 * Creates an <code>SSLContext</code> that accepts all server certificates.
	 *
	 * @param protocol the {@link TransportSecurityProtocol} to use for the context
	 * @return the created <code>SSLContext</code>
	 * @throws Exception if error occurs during the process of creating the context
	 */
	public static SSLContext createSSLContext(TransportSecurityProtocol protocol)
                throws Exception
        {
            return createSSLContext(protocol,(java.security.cert.X509Certificate[])null);
        }


        /**
	 * Creates an <code>SSLContext</code> that uses the specified trusted certificates.
	 *
	 * @param protocol the {@link TransportSecurityProtocol} to use for the context
	 * @param trustedCerts certificates to import into the <code>SSLContext</code> or <code>null</code>
         *         to accept all issuers
	 * @return the created <code>SSLContext</code>
	 * @throws Exception if error occurs during the process of creating the context
	 */
	public static SSLContext createSSLContext(TransportSecurityProtocol protocol,
			java.security.cert.X509Certificate... trustedCerts)
		throws Exception
	{
            return createSSLContext(protocol,null,trustedCerts);
        }
	/**
	 * Creates an <code>SSLContext</code> that uses the specified trusted certificates.
	 * 
	 * @param protocol the {@link TransportSecurityProtocol} to use for the context
	 * @param trustedCerts certificates to import into the <code>SSLContext</code> or <code>null</code>
         *         to accept all issuers
         * @param privateKey the client key to authenticate the client with the server
	 * @return the created <code>SSLContext</code>
	 * @throws Exception if error occurs during the process of creating the context
	 */
	public static SSLContext createSSLContext(TransportSecurityProtocol protocol,PrivateKey privateKey,
			java.security.cert.X509Certificate... trustedCerts)
		throws Exception
	{
		if(trustedCerts != null && trustedCerts.length == 0)
			throw new IllegalArgumentException("trustedCerts is empty");
		
                X509TrustManager defaultManager = null;
                KeyManager [] keyManagers = null;
                KeyStore keyStore = null;

                if(privateKey != null || trustedCerts != null)
                {
                    // create a new key store instance that will install the certificates
                    // and/or the private keys
                    keyStore = KeyStore.getInstance(JKS_TYPE);
                    keyStore.load(null, null);
                }

                // import the certs
                if(trustedCerts != null)
                {
                    // set up the key manager for the certificates
                    javax.net.ssl.TrustManagerFactory trustFact =
                            javax.net.ssl.TrustManagerFactory.getInstance(KEY_MANAGEMENT_ALG_SUN_X509);

                    // install the certificates in the key store and give them a unique alias
                    int imported = 0;
                    for(java.security.cert.X509Certificate cert : trustedCerts)
                    {
                            if(cert != null)
                                    keyStore.setCertificateEntry("cert" + ++imported, cert);
                    }
                    if(imported == 0)
                            throw new IllegalArgumentException("no non-null certs in trustedCerts");
                    // add the certs to the trust factory
                    trustFact.init(keyStore);
                    
                    // get a default trust manager
                    TrustManager [] tms = trustFact.getTrustManagers();
                    if(tms != null && tms.length >= 1)
                    defaultManager = (X509TrustManager)tms[0];
                }

                // import the private key
                if(privateKey != null)
                {
                    keyStore.setKeyEntry("client", privateKey, null, null);
                    KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(privateKey.getAlgorithm());

                    kmfactory.init(keyStore, null);
                    keyManagers = kmfactory.getKeyManagers();
                }
		//create the SSL context based on these parameters
		SSLContext sslContext = SSLContext.getInstance(protocol.toString());

		
		// use a CertX509TrustManager since default one will still fail validation for 
		// self-signed certs
		sslContext.init(
				keyManagers,
				new TrustManager[]
				{
					trustedCerts != null ? new CertX509TrustManager(defaultManager,trustedCerts) : new CertX509TrustManager()
				}
				, null);
		
		return sslContext;
		
	}
	private static byte [] fromBinaryToPem(byte [] data,String begin,String end) 
	{
		// first base64 encode the data with no chunking
		data = Base64.encodeBase64(data, false);
		return formatBase64ToPem(data,begin,end);
		
	}
	
	private static byte [] formatBase64ToPem(byte [] data,String begin,String end)
	{
		// manually chunk the output
		StringBuilder out = new StringBuilder((int)((data.length + begin.length() + end.length())*1.2));
		
		// write header
		out.append(begin).append("\n");
		int lineCount = 0;
		for(int i = 0; i < data.length; ++i)
		{
			out.append((char)data[i]);
			if(++lineCount == CHUNK_LEN)
			{
				out.append(KEY_LINE_TERM);
				lineCount = 0;
			}
		}
		// write out a line terminator for last non-full line
		if(lineCount > 0)
			out.append(KEY_LINE_TERM);
		
		//write footer
		out.append(end).append("\n");
		
		try
		{
			return out.toString().getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException e) 
		{
			throw new AssertionError("UTF-8 encoding not supported");
		}
	}
	
}
