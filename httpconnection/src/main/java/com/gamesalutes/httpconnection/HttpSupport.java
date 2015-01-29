package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.util.Map;

public interface HttpSupport {

    /**
     * Executes an HTTP GET on the target path.
     *
     * @param path the path appended to the base host url
     * @param headers HTTP headers to set
     * @param queryParams query parameters to encode into <code>path</code>
     * 
     * @deprecated 
     *
     * @return the <code>HttpResponse</code>
     */
	@Deprecated
    HttpResponse get(String path,Map<String,String> headers,Map<String,String> queryParams) throws IOException;
    
    /**
     * Executes an HTTP Post on the target path.
     *
     * @param path the path appended to the base host url
     * @param headers HTTP headers to set
     * @param queryParams query parameters to encode into <code>path</code>
     * @param formParams the parameters to submit in the body of the post
     * 
     * @deprecated 
     *
     * @return the <code>HttpResponse</code>
     */
	@Deprecated
    HttpResponse post(String path,Map<String,String> headers,Map<String,String> queryParams,Map<String,String> formParams) throws IOException;
    
    <S,T> T get(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException;
    <S,T> T post(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException;
    <S,T> T put(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException;
    <S,T> T delete(HttpConnectionRequest<S,T> request) throws IOException,HttpBadStatusException;
    
    /**
     * Adds a global header key/value pair for all urls.
     * 
     * @param key the header key
     * @param value the header value
     * @return <code>this</code>
     */
    HttpSupport addGlobalHeader(String key,String value);
    
    /**
     * Adds a global header key/value pair for urls that match the specified regex.
     * 
     * @param key the header key
     * @param value the header value
     * @param urlRegex the regular expression pattern to add the global header
     * @return <code>this</code>
     */
    HttpSupport addGlobalHeader(String key,String value,String urlRegex);
    
    /**
     * Removes the specified global header key for all urls.
     * 
     * @param key the header key
     * @return <code>this</code>
     */
    HttpSupport removeGlobalHeader(String key);
    
    /**
     * Removes the specified global header key for urls that match the given regex.
     * 
     * @param key the header key
     * @param urlRegex the regular expression to remove for urls
     * @return <code>this</code>
     */
    HttpSupport removeGlobalHeader(String key,String urlRegex);
    
    HttpSupport setGlobalHeaders(Map<String,String> keyValuePairs);
    
    HttpSupport setGlobalHeaders(String...keyValuePairs);
    
    void addBadStatusInterceptor(HttpBadStatusInterceptor i);
    
    boolean removeBadStatusInterceptor(HttpBadStatusInterceptor i);
    
    /**
     * Sets the proxy to use for the client requests.
     *
     * @param proxy the proxy url
     * 
     * @throws IllegalStateException if web methods have already been invoked
     */
    void setProxy(String proxy);
    
    
}
