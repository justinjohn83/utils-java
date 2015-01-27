package com.gamesalutes.httpconnection;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import com.gamesalutes.utils.ByteCountingInputStream;
import com.gamesalutes.utils.ChainedIOException;
import com.gamesalutes.utils.EncryptUtils;
import com.gamesalutes.utils.EncryptUtils.TransportSecurityProtocol;
import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.WebUtils;

public final class HttpConnection extends AbstractHttpConnection
{
//    public static class Params
//    {
//        public static String protocol;
//        public static String host;
//        public static int port;
//        public static String certFile;
//    }
//    private static final String DATA_PATH = "zport/dmd/Devices";

    private CloseableHttpClient httpClient;
    private String protocol;
    private String certFile;
    private String keyFile;
    private int port;
    private String server;
    private String relativePath;
    private String username;
    private String password;
    private int numRetries;
    private AtomicLong totalBytes = new AtomicLong();
    private AtomicInteger openConnections = new AtomicInteger();

    private int timeout;

    public static final int DEFAULT_TIMEOUT = 60 * 1000;


    
    private class StreamListener implements ByteCountingInputStream.StreamReadListener {

    	private final Closeable resource;
    	private long dt;
    	
    	public StreamListener(Closeable resource) {
    		this.resource = resource;
    	}
		public void onReadComplete(int readCount) {
			
			try {
				totalBytes.addAndGet(readCount);
				if(logger.isDebugEnabled()) {
					logger.debug("Read " + readCount + " bytes from the stream");
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
        		   resource.close();
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
     * Constructor.
     * 
     * No base path set.  All uris must be absolute.
     * 
     */
    public HttpConnection() {
    	this(null,3,DEFAULT_TIMEOUT);
    }
    /**
     *
     * Constructor.
     *
     * @path the base url of the connection
     */
    public HttpConnection(String path)
    {
        this(path,3,DEFAULT_TIMEOUT);
    }

    
    /**
     * Constructor.
     * 
     * No base path set.  All uris must be absolute.
     * 
     * @param numRetries
     * @param timeout
     */
    public HttpConnection(int numRetries,int timeout) {
    	this(null,numRetries,null,null,timeout);
    }
    /**
     *
     * Constructor.
     *
     * @param path the base url of the connection
     * @param numRetries number of times to retry a request if it fails
     * @param timeout value in milliseconds. Value &lt;= 0 indicates infinite timeout
     */
    public HttpConnection(String path,int numRetries,int timeout)
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
    public HttpConnection(String path,int numRetries,String certFile,String keyFile,int timeout)
    {
        if(path != null) {
            URI u;
	        try
	        {
	            u = new URI(path);
	        }
	        catch(URISyntaxException e)
	        {
	            throw new IllegalArgumentException("path=" + path,e);
	        }
	
	        this.protocol = u.getScheme();
	        this.server = u.getHost();
	        if(u.getPort() != -1)
	            this.port = u.getPort();
	        else if("https".equalsIgnoreCase(u.getScheme()))
	            this.port = 443;
	        else
	            this.port = 80;
        }
        
        this.timeout = timeout;

        this.certFile = certFile;
        this.keyFile = keyFile;
        this.numRetries = numRetries;

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
     * 
     * @deprecated
     */
    @Deprecated
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
                throw new IllegalArgumentException("proxy=" + proxy,e);
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

    private HttpResponse getResponse(URI uri,HttpUriRequest method) throws IOException
    {
	        CloseableHttpResponse response = null;
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
		        	messageStream = getResponseStream(response);
		        }
		        catch(Exception e)
		        {
		           if(open > 0) {
		        	   open = openConnections.decrementAndGet();
		           }
		           logger.warn("Unable to retreive content (" + code + " : " + status + ") : " + uri,e);
	        	   logger.warn("Unable to connect to uri=" + uri,e);
	        	   try {
	        		   response.close();
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
	        		   if(response != null) {
	        			   response.close();
	        		   }
	        	   }
	        	   catch(Exception e2) {
	        		   // ignore
	        	   }
	        	   
	        	   throw new ChainedIOException("Unable to connect: uri=" + uri,e);
	        }
	        
    }
    
    private InputStream getResponseStream(CloseableHttpResponse response) throws IOException {
    	// TODO: use DecompressingHttpClient instead of DefaultHttpClient - but will obscure number of bytes read
    	InputStream messageStream = null;
        HttpEntity entity = response.getEntity();
        if(entity != null) {
        	
        	// buffer the raw input
        	InputStream rawStream = 
        			new BufferedInputStream(
        					new ByteCountingInputStream(entity.getContent(),new StreamListener(response)),4096);
        	
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
    		"((localhost)|(\\.edu)|(\\.com)|(\\.net))(:\\d+)?");
    
    public URI createUri(String path,Map<String,String> queryParams)
    {
    	if(MiscUtils.isEmpty(path))
    	{
    		throw new IllegalArgumentException("path=" + path);
    	}
    	URI currentUri = null;
    	if(path.startsWith("http")) {
    		currentUri = WebUtils.createUri(path);
    	}
    	// no base url specified so if uri is not absolute we cannot determine the url
    	else if(this.server == null) {
    		throw new IllegalStateException("Uri not absolute when no base path set: path=" + path + ";queryParams=" + queryParams );
    	}
    	
    	if(currentUri == null) {
	    	Matcher m = HOST_PATTERN.matcher(path);
	    	if(m.find())
	    	{
	    		int index = m.end();
	    		if(index < path.length() - 1)
	    		{
	    			path = path.substring(index);
	    		}
	    		else {
	    			path = "";
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
	    	
	    	// strip off raw query
	    	int queryIndex = path.indexOf('?');
	    	String query;
	    	if(queryIndex != -1 && queryIndex < path.length() - 1) {
	    		query = path.substring(queryIndex + 1);
	    		path = path.substring(0,queryIndex);
	    	}
	    	else {
	    		query = null;
	    	}
	    	try {
	    		currentUri = new URI(this.protocol,null,this.server,this.port,path,query,null);
	    	}
	    	catch(URISyntaxException e)
	        {
	            throw new IllegalArgumentException("path=" + path,e);
	        }
	    	
	    	Map<String,String> existingQueryParameters = WebUtils.getQueryParameters(currentUri);

	    	// check if query parameters already exist and merge them
	    	if(!MiscUtils.isEmpty(queryParams)) {
	    		if(!MiscUtils.isEmpty(existingQueryParameters)) {
	    			Map<String,String> newQueryParameters = new LinkedHashMap<String,String>(existingQueryParameters);
	    			newQueryParameters.putAll(queryParams);
	    			queryParams = newQueryParameters;
	    		}
	    	}
	    	else if(!MiscUtils.isEmpty(existingQueryParameters)) {
	    		queryParams = existingQueryParameters;
	    	}
    	}

        // encode the query params
        String query = encodeParams(queryParams);
        try
        {
        	return new URI(
            		currentUri.getScheme(),
            		null,
            		currentUri.getHost(),
            		currentUri.getPort(),
            		currentUri.getPath(),
            		query,
            		null);
        }
        catch(URISyntaxException e)
        {
            throw new IllegalArgumentException("path=" + path,e);
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


    private void setHeaders(HttpUriRequest method,Map<String,String> headers)
    {
    	Map<String,String> allHeaders = new HashMap<String,String>();
    	
        // set the default headers
    	allHeaders.putAll(this.getDefaultHeaders(method.getURI().toString()));
    	
    	// add custom headers
    	if(!MiscUtils.isEmpty(headers)) {
    		allHeaders.putAll(headers);
    	}
        
        if(!MiscUtils.isEmpty(allHeaders))
        {
            for(Map.Entry<String,String> E : allHeaders.entrySet() )
            {
                method.setHeader(E.getKey(),E.getValue());
            }
        }
        // support gzip and defalte compression schemes
        // TODO: What if added encoding above?
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
    	final String headerName = "Authorization";
    	
        if(username == null)
            this.removeGlobalHeader(headerName);
        else
        {
            StringBuilder buf = new StringBuilder(128);
            buf.append(username).append(":").append(password);

            String creds = WebUtils.base64Encode(buf.toString());

            MiscUtils.clearStringBuilder(buf);
            buf.append("Basic ").append(creds);

            this.addGlobalHeader(headerName, buf.toString());
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
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
			connManager.setDefaultMaxPerRoute(20);
	    	connManager.setMaxTotal(20);
	    
	    	HttpClientBuilder builder = 
        		HttpClientBuilder.create()
        		// allow automatic redirects
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setConnectionManager(connManager);
            
//            if(logger.isDebugEnabled()) {
//            	httpClient.log.enableDebug(true);
//            }

            if("https".equalsIgnoreCase(this.protocol))
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
                     builder.setSSLSocketFactory(new SSLConnectionSocketFactory(
                    		 sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
                     
     
                     builder.setSchemePortResolver(new DefaultSchemePortResolver() {

						@Override
						public int resolve(HttpHost host)
								throws UnsupportedSchemeException {
							if("https".equalsIgnoreCase(host.getSchemeName()) &&
									server.equalsIgnoreCase(host.getHostName())) {
								return port;
							}
									
							return super.resolve(host);
						}
                    	 
                     });
//                     Scheme sch = new Scheme("https",port,socketFactory);
//                     httpClient.getConnectionManager().getSchemeRegistry().register(sch);

           }

            if(this.timeout < 0) this.timeout = 0;

            // TODO: Should be setting proxy here too! and not in separate method
            // This will cause an interface change in this class
            //builder.setProxy(proxy)
            RequestConfig requestConfig = RequestConfig.custom()
            		.setSocketTimeout(timeout)
            		.setConnectionRequestTimeout(timeout)
            		.setConnectTimeout(timeout)
            		.setRedirectsEnabled(true)
            		.setRelativeRedirectsAllowed(true)
            		.build();
            builder.setDefaultRequestConfig(requestConfig);
            
            builder.setDefaultSocketConfig(
            		SocketConfig.custom().setSoTimeout(timeout).build());
            
            // TODO: Implement auto retry
            // TODO: Utilize caching features
            
//            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
//            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
            
            httpClient = builder.build();
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
    
    public <S,T> T get(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException {
		URI uri = createUri(request.getPath(),request.getQueryParameters());
        HttpGet http = new HttpGet(uri);
        
        return action(uri,http,request);
        
	}
	public <S,T> T post(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException {
		URI uri = createUri(request.getPath(),request.getQueryParameters());
		HttpPost http = new HttpPost(uri);
        
        return action(uri,http,request);
	}
	public <S,T> T put(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException {
		URI uri = createUri(request.getPath(),request.getQueryParameters());
		HttpPut http = new HttpPut(uri);
        
        return action(uri,http,request);
	}
	public <S,T> T delete(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException {
		URI uri = createUri(request.getPath(),request.getQueryParameters());
		HttpDelete http = new HttpDelete(uri);
        
        return action(uri,http,request);
	}
	

	
	private <S,T> T action(URI uri,HttpUriRequest method,HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException {
        setHeaders(method,request.getHeaders());
        // marshall request
        if(method instanceof HttpEntityEnclosingRequestBase) {
        	marshallRequest((HttpEntityEnclosingRequestBase)method,request.getMarshaller(),request.getRequest());
        }
        
        HttpResponse response = getResponse(uri,method);
        handleExceptions(uri,response,request.getErrorUnmarshaller());
        
        // unmarshaller
        return unmarshallResponse(response,request.getUnmarshaller());
        
	}
	
	private <S> void  marshallRequest(HttpEntityEnclosingRequestBase method,final RequestMarshaller<S> marshaller,final S request) {
		if(marshaller == null) {
			throw new IllegalArgumentException("No marshaller configured for entity enclosing request");
		}
		EntityTemplate template = new EntityTemplate(new ContentProducer() {

			public void writeTo(OutputStream out) throws IOException {
				if(logger.isDebugEnabled()) {
					logger.debug("Marshalling entity content: " + marshaller);
				}
				marshaller.marshall(request,out);
			}
			
		});
		template.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,marshaller.getContentType()));
		method.setEntity(template);
				
	}

}