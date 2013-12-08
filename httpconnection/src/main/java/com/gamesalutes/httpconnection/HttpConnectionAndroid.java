package com.gamesalutes.httpconnection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;
import ch.boye.httpclientandroidlib.client.utils.URIUtils;
import ch.boye.httpclientandroidlib.client.utils.URLEncodedUtils;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import ch.boye.httpclientandroidlib.message.BasicHeader;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.params.CoreConnectionPNames;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.gamesalutes.utils.ByteCountingInputStream;
import com.gamesalutes.utils.ChainedIOException;
import com.gamesalutes.utils.Disposable;
import com.gamesalutes.utils.EncryptUtils;
import com.gamesalutes.utils.EncryptUtils.TransportSecurityProtocol;
import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.WebUtils;

public final class HttpConnectionAndroid implements Disposable,HttpSupport
{
//    public static class Params
//    {
//        public static String protocol;
//        public static String host;
//        public static int port;
//        public static String certFile;
//    }
//    private static final String DATA_PATH = "zport/dmd/Devices";

    private HttpClient httpClient;
    private String protocol;
    private String certFile;
    private String keyFile;
    private int port;
    private String server;
    private String relativePath;
    private String username;
    private String password;
    private Header authenticationHeader;
    
    private AtomicLong totalBytes = new AtomicLong();
    private AtomicInteger openConnections = new AtomicInteger();

    private int timeout;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int DEFAULT_TIMEOUT = 60 * 1000;


    
    private class StreamListener implements ByteCountingInputStream.StreamReadListener {

    	private final HttpEntity entity;
    	private final HttpRequestBase method;
    	private long dt;
    	
    	public StreamListener(HttpEntity entity,HttpRequestBase method) {
    		this.entity = entity;
    		this.method = method;
    	}
		public void onReadComplete(int readCount) {
			
			try {
				totalBytes.addAndGet(readCount);
				if(logger.isDebugEnabled()) {
					logger.debug("Read " + readCount + " bytes from the stream");
				}
				try {
					EntityUtils.consume(entity);
				}
				catch(Exception e) {
					if(logger.isDebugEnabled()) {
						logger.debug("Error consuming entity",e);
					}
				}
				
				// record timing of stream read
				if(logger.isInfoEnabled()) {
					if(dt > 0) {
						dt = System.nanoTime() - dt;
					}
					logger.info("Stream reading complete in " + MiscUtils.formatTime(dt,TimeUnit.NANOSECONDS,TimeUnit.MILLISECONDS,3));
				}
			}
			finally {
        	   try {
        		   method.releaseConnection();
        	   }
        	   catch(Exception e2) {
        		   // ignore
        	   }
	        	int open = openConnections.decrementAndGet();
				if(logger.isInfoEnabled()) {
					logger.info("HttpConnection closed: open connections=" + open);
				}
			}
		}
		public void onReadBegin() {
			if(logger.isInfoEnabled()) {
				dt = System.nanoTime();
			}
		}
    	
    }
    /**
     *
     * Constructor.
     *
     * @path the base url of the connection
     */
    public HttpConnectionAndroid(String path)
    {
        this(path,3,DEFAULT_TIMEOUT);
    }

    /**
     *
     * Constructor.
     *
     * @param path the base url of the connection
     * @param numRetries number of times to retry a request if it fails
     * @param timeout value in milliseconds. Value &lt;= 0 indicates infinite timeout
     */
    public HttpConnectionAndroid(String path,int numRetries,int timeout)
    {
        this(path,numRetries,null,null,timeout);
    }

    /**
     *
     * Constructor.
     *
     * @param path the base url of the connection
     * @param numRetries number of times to retry a request if it fails
     * @param certFile file containing server certificate or <code>null</code> to accept all certs
     * @param keyFile file containing client key for client key authentication with server or <code>null</code> to not
     *         do this kind of authentication
     * @param timeout value in milliseconds. Value &lt;= 0 indicates infinite timeout
     *
     */
    public HttpConnectionAndroid(String path,int numRetries,String certFile,String keyFile,int timeout)
    {
        URI u;
        try
        {
            u = new URI(path);
        }
        catch(URISyntaxException e)
        {
            throw new IllegalArgumentException("path=" + path);
        }

        this.timeout = timeout;
        this.protocol = u.getScheme();
        this.server = u.getHost();
        if(u.getPort() != -1)
            this.port = u.getPort();
        else if("https".equalsIgnoreCase(u.getScheme()))
            this.port = 443;
        else
            this.port = 80;

        this.certFile = certFile;
        this.keyFile = keyFile;

        //this.relativePath = u.getPath();

        try
        {
            initHttpClient();
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unable to initialize http client",e);
        }
    }

    /**
     * Sets the user name and password to use for HTTP basic auth.  Passing <code>null</code> for
     * both parameters turns off basic auth.
     *
     * @param username the username
     * @param password the password
     */
    public void setCredentials(String username,String password)
    {
        this.username = username;
        this.password = password;

        setBasicAuthn();
    }

    /**
     * Sets the proxy to use for the client requests.
     *
     * @param proxy the proxy url
     */
    public void setProxy(String proxy)
    {
        HttpHost host = null;

        if(proxy != null)
        {
            URI u;
            try
            {
                u = new URI(proxy);
            }
            catch(URISyntaxException e)
            {
                throw new IllegalArgumentException("proxy=" + proxy);
            }

            String h = u.getHost();
            int port = u.getPort();
            String scheme = u.getScheme();

            host = new HttpHost(h,port,scheme);
        }

        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, host);

    }

    /**
     * Executes an HTTP GET on the target path.
     *
     * @param path the path appended to the base host url
     * @param headers HTTP headers to set
     * @param queryParams query parameters to encode into <code>path</code>
     *
     * @return the <code>HttpResponse</code>
     */
    public HttpResponse get(String path,Map<String,String> headers,Map<String,String> queryParams) throws IOException
    {

        URI uri = createUri(path,queryParams);
        HttpGet request = new HttpGet(uri);
        setHeaders(request,headers);

        return getResponse(uri,request);
    }

    /**
     * Executes an HTTP Post on the target path.
     *
     * @param path the path appended to the base host url
     * @param headers HTTP headers to set
     * @param queryParams query parameters to encode into <code>path</code>
     * @param formParams the parameters to submit in the body of the post
     *
     * @return the <code>HttpResponse</code>
     */
    public HttpResponse post(String path,Map<String,String> headers,Map<String,String> queryParams,Map<String,String> formParams) throws IOException
    {
        URI uri = createUri(path,queryParams);
        HttpPost request = new HttpPost(uri);
        setHeaders(request,headers);
        setFormParams(request,formParams);

        return getResponse(uri,request);
    }

    private HttpResponse getResponse(URI uri,HttpRequestBase method) throws IOException
    {
	        ch.boye.httpclientandroidlib.HttpResponse response;
	//        try
	//        {
	        	HttpHost httpHost = new HttpHost(uri.getHost(),uri.getPort(),uri.getScheme());
	        	
	        	long dt = 0;
	        	if(logger.isInfoEnabled()) {
	        		dt = System.nanoTime();
	        	}
	        
	        int open = -1;
	        try {
	            response = httpClient.execute(httpHost,method);
	            
				open = openConnections.incrementAndGet();

	            if(logger.isInfoEnabled()) {
	            	dt = System.nanoTime() - dt;
	            	logger.info("uri=" + uri + " responded in " + MiscUtils.formatTime(dt, TimeUnit.NANOSECONDS, TimeUnit.MILLISECONDS, 3));
	            	
					logger.info("HttpConnection opened: open connections=" + open);
	            }
	//        }
	//        catch(Exception e)
	//        {
	//            logger.warn("Unable to execute query: " + uri,e);
	//            return null;
	//        }
	
		        StatusLine sl = response.getStatusLine();
		        int code = sl.getStatusCode();
		        String status = sl.getReasonPhrase();
		
		        InputStream messageStream = null;
		
		        try
		        {
		        	messageStream = getResponseStream(method,response);
		        }
		        catch(Exception e)
		        {
		           if(open > 0) {
		        	   open = openConnections.decrementAndGet();
		           }
		           logger.warn("Unable to retrive content (" + code + " : " + status + ") : " + uri,e);
	        	   logger.warn("Unable to connect to uri=" + uri,e);
	        	   try {
	        		   method.releaseConnection();
	        	   }
	        	   catch(Exception e2) {
	        		   // ignore
	        	   }
		        }
		        
		        return new HttpResponse(code,status,messageStream);

	        }
	        catch(Exception e) {
		           if(open > 0) {
		        	   open = openConnections.decrementAndGet();
		           }
	        	   logger.warn("Unable to connect to uri=" + uri,e);
	        	   try {
	        		   method.releaseConnection();
	        	   }
	        	   catch(Exception e2) {
	        		   // ignore
	        	   }
	        	   
	        	   throw new ChainedIOException("Unable to connect: uri=" + uri,e);
	        }
	        
    }
    
    private InputStream getResponseStream(HttpRequestBase method,ch.boye.httpclientandroidlib.HttpResponse response) throws IOException {
    	// TODO: use DecompressingHttpClient instead of DefaultHttpClient - but will obscure number of bytes read
    	InputStream messageStream = null;
        HttpEntity entity = response.getEntity();
        if(entity != null) {
        	
        	// buffer the raw input
        	InputStream rawStream = 
        			new BufferedInputStream(
        					new ByteCountingInputStream(entity.getContent(),new StreamListener(entity,method)),4096);
        	
        	Header header = response.getFirstHeader("Content-Encoding");
        	String value = header != null ? header.getValue() : null;
        	
        	if("gzip".equalsIgnoreCase(value)) {
        		if(logger.isDebugEnabled()) {
        			logger.debug("Reading response stream from gzip compression");
        		}
        		messageStream = new GZIPInputStream(rawStream,4096);
        	}
        	else if("deflate".equalsIgnoreCase(value)) {
        		if(logger.isDebugEnabled()) {
        			logger.debug("Reading response stream from deflate compression");
        		}
        		messageStream = new InflaterInputStream(rawStream, new Inflater(true),4096);
        	}
        	else {
        		
        		if(logger.isDebugEnabled()) {
        			logger.debug("Reading uncompressed response stream");
        		}
            	messageStream = rawStream;

        	}
        			
        }
        
        return messageStream;
        
    }

    private static final Pattern HOST_PATTERN = Pattern.compile(
    		"(\\.edu)|(\\.com)|(\\.net)");
    
    public URI createUri(String path,Map<String,String> queryParams)
    {
    	if(MiscUtils.isEmpty(path))
    	{
    		throw new IllegalArgumentException("path=" + path);
    	}
    	
    	if(path.startsWith("http")) {
    		try {
    			return new URI(path);
    		}
    		catch(URISyntaxException e) {
    			throw new RuntimeException(e);
    		}
    	}
    	Matcher m = HOST_PATTERN.matcher(path);
    	if(m.find())
    	{
    		int index = m.end();
    		if(index < path.length() - 1)
    		{
    			path = path.substring(index);
    		}
    	}
    	//path regex
    	int slashIndex = path.indexOf('/');
//    	if(slashIndex != -1 && slashIndex < path.length() - 1)
//    	{
//    		path = path.substring(slashIndex+1);
//    	}
//    	else
    	if(slashIndex == -1)
    	{
    		path = "/" + path;
    	}

        // encode the query params
        String query = encodeParams(queryParams);
        try
        {
            return URIUtils.createURI(this.protocol,this.server,this.port,path,query,null);
        }
        catch(URISyntaxException e)
        {
            throw new IllegalArgumentException("path=" + path);
        }
    }



    private String encodeParams(Map<String,String> queryParams)
    {
        if(MiscUtils.isEmpty(queryParams)) return null;

        List<NameValuePair> qParams = new ArrayList<NameValuePair>(queryParams.size());

        for(Map.Entry<String,String> E : queryParams.entrySet())
        {
            qParams.add(new BasicNameValuePair(E.getKey(),E.getValue()));
        }


        //        try
//        {
//                return WebUtils.setQueryParameters(new URI(rel), queryParams).toString();
//        }
//        catch(URISyntaxException e)
//        {
//            throw new IllegalArgumentException(rel);
//        }
        
        return URLEncodedUtils.format(qParams, "UTF-8");
     }


    private void setHeaders(HttpRequestBase method,Map<String,String> headers)
    {
        // set the authentication header if it is present
        if(authenticationHeader != null)
            method.setHeader(authenticationHeader);
        
        if(!MiscUtils.isEmpty(headers))
        {
            for(Map.Entry<String,String> E : headers.entrySet() )
            {
                method.setHeader(E.getKey(),E.getValue());
            }
        }
        // support gzip and defalte compression schemes
        method.setHeader("Accept-Encoding","gzip, deflate");
    }

    private void setFormParams(HttpPost method,Map<String,String> formParams)
    {

        if(MiscUtils.isEmpty(formParams))
            return;


        List<NameValuePair> nvps = new ArrayList<NameValuePair>(formParams.size());

        for(Map.Entry<String,String> E : formParams.entrySet())
        {
            nvps.add(new BasicNameValuePair(E.getKey(),E.getValue()));
        }

        try
        {
            method.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
        }
        catch( UnsupportedEncodingException e)
        {
            throw new AssertionError(e);
        }
    }

    private void setBasicAuthn()
    {
        // TODO: this method doesn't work!
//        if(username == null) return;
//
//        httpClient.getCredentialsProvider().setCredentials(
//            AuthScope.ANY,
//            new UsernamePasswordCredentials(username,password));

        // do it manually
        // authenticate
        // set the login credentials
        if(username == null)
            authenticationHeader = null;
        else
        {
            StringBuilder buf = new StringBuilder(128);
            buf.append(username).append(":").append(password);

            String creds = WebUtils.base64Encode(buf.toString());

            MiscUtils.clearStringBuilder(buf);
            buf.append("Basic ").append(creds);

            authenticationHeader = new BasicHeader("Authorization",buf.toString());
        }
    }

    private void initHttpClient() throws Exception
    {
            //this.connCb = new HttpConnectionCallback();
    //             SSLSocketFactory socketFactory = new SSLSocketFactory(EncrpytUtils.c);
    //        Scheme sch = new Scheme("https", 443, socketFactory);
    //        httpclient.getConnectionManager().getSchemeRegistry().register(sch);

            // use a custom protocol socket factory that uses the specified server certificate
            // if protocol is "https" and the cert is not null

    	    // multi-threaded
    	    PoolingClientConnectionManager connManager = new PoolingClientConnectionManager();
    	    connManager.setDefaultMaxPerRoute(20);
    	    connManager.setMaxTotal(20);
    	    
            httpClient = //new DecompressingHttpClient(
            		new DefaultHttpClient(connManager);
            
//            if(logger.isDebugEnabled()) {
//            	httpClient.log.enableDebug(true);
//            }

            if(this.protocol.equalsIgnoreCase("https"))
            {
                
                     X509Certificate[] certs = null;
                     PrivateKey key = null;

                     if(this.certFile != null)
                     {
                         certs = new X509Certificate [] {
                             (X509Certificate)EncryptUtils.readCertificate(new File(certFile), EncryptUtils.CERT_TYPE_X509)
                         };
                     }
                     if(this.keyFile != null)
                     {
                         key = EncryptUtils.readPrivateKey(new BufferedInputStream(new FileInputStream(this.keyFile)),null,null);
                     }
                     SSLContext sslContext = EncryptUtils.createSSLContext(TransportSecurityProtocol.SSL, key,certs);
                     SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                     Scheme sch = new Scheme("https",port,socketFactory);
                     httpClient.getConnectionManager().getSchemeRegistry().register(sch);

           }

            if(this.timeout < 0) this.timeout = 0;

            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
    }

    /**
     * Disposes of any resources.
     *
     */
    public void dispose()
    {
        httpClient.getConnectionManager().shutdown();
    }
    
    public long getTotalBytes() {
    	return totalBytes.get();
    }
    public void resetTotalBytes() {
    	totalBytes.set(0);
    }

}