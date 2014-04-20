package com.gamesalutes.httpconnection;

import java.util.HashMap;
import java.util.Map;

public class RequestBuilder<S,T> {

	public RequestBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	private String path = "/";
	
	private final Map<String,String> headers = new HashMap<String,String>();
	private final Map<String,String> queryParameters = new HashMap<String,String>();
	
	@SuppressWarnings("rawtypes")
	private RequestMarshaller marshaller = new NullRequestMarshaller();
	
	
	private ResponseUnmarshaller<T> unmarshaller;
	
	@SuppressWarnings("rawtypes")
	private ResponseUnmarshaller errorUnmarshaller = new NullResponseMarshaller();
	
	private S requestObject;
	
	@SuppressWarnings("unchecked")
	public HttpConnectionRequest<S,T> build() {
		if(this.unmarshaller == null) {
			throw new IllegalStateException();
		}
		
		return new HttpConnectionRequest<S,T>(
				this.path,
				this.headers,
				this.queryParameters,
				this.marshaller,
				this.unmarshaller,
				this.errorUnmarshaller,
				this.requestObject);
	}
	
	public RequestBuilder<S,T> setPath(String path) {
		this.path = path;
		return this;
	}
	
	public RequestBuilder<S,T> setMarshaller(RequestMarshaller<S> marshaller) {
		this.marshaller = marshaller;
		return this;
	}
	
	public RequestBuilder<S,T> setUnmarshaller(ResponseUnmarshaller<T> unmarshaller) {
		this.unmarshaller = unmarshaller;
		return this;
	}
	/**
	 * Sets unmarshaller to use in case of a non-success http status code.
	 * 
	 * @param unmarshaller the <code>ResponseUnmarshaller</code>
	 * @return <code>this</code> for chaining
	 */
	public RequestBuilder<S,T> setHttpErrorUnmarshaller(ResponseUnmarshaller<?> unmarshaller) {
		this.errorUnmarshaller = unmarshaller;
		return this;
	}
	
	
	public RequestBuilder<S,T> addHeader(String key,Object value,boolean addEmptyIfNull) {
		if(value != null || addEmptyIfNull) {
			this.headers.put(key, value != null ? value.toString() : "");
		}
		return this;
	}
	
	public RequestBuilder<S,T> addQueryParameter(String key,Object value,boolean addEmptyIfNull) {
		if(value != null || addEmptyIfNull) {
			this.queryParameters.put(key,value != null ? value.toString() : "");
		}
		return this;
	}
	
	public RequestBuilder<S,T> addHeader(String key,Object value) {
		return addHeader(key,value,true);
	}
	
	public RequestBuilder<S,T> addQueryParameter(String key,Object value) {
		return addQueryParameter(key,value,true);
	}

	public RequestBuilder<S,T> setRequest(S requestObject) {
		this.requestObject = requestObject;
		return this;
	}

}
