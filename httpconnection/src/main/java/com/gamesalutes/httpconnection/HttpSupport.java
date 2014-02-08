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
    
    <T> T get(HttpConnectionRequest<T> request) throws IOException,HttpBadStatusException;
    <T> T post(HttpConnectionRequest<T> request) throws IOException,HttpBadStatusException;
    <T> T put(HttpConnectionRequest<T> request) throws IOException,HttpBadStatusException;
    <T> T delete(HttpConnectionRequest<T> request) throws IOException,HttpBadStatusException;
    
}
