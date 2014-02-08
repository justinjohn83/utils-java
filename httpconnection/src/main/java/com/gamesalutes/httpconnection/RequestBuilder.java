package com.gamesalutes.httpconnection;

import java.util.HashMap;
import java.util.Map;

public class RequestBuilder<T> {

	public RequestBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	private String path = "/";
	
	private final Map<String,String> headers = new HashMap<String,String>();
	private final Map<String,String> queryParameters = new HashMap<String,String>();
	
	private RequestMarshaller marshaller = new NullRequestMarshaller();
	
	
	private ResponseUnmarshaller<T> unmarshaller;
	
	public HttpConnectionRequest<T> build() {
		if(this.unmarshaller == null) {
			throw new IllegalStateException();
		}
		
		return new HttpConnectionRequest<T>(
				this.path,
				this.headers,
				this.queryParameters,
				this.marshaller,
				this.unmarshaller);
	}
	
	public RequestBuilder<T> setPath(String path) {
		this.path = path;
		return this;
	}
	
	public RequestBuilder<T> setMarshaller(RequestMarshaller marshaller) {
		this.marshaller = marshaller;
		return this;
	}
	
	public RequestBuilder<T> setUnmarshaller(ResponseUnmarshaller<T> unmarshaller) {
		this.unmarshaller = unmarshaller;
		return this;
	}
	
	public RequestBuilder<T> addHeader(String key,Object value) {
		this.headers.put(key, value != null ? value.toString() : "");
		return this;
	}
	
	public RequestBuilder<T> addQueryParameter(String key,Object value) {
		this.queryParameters.put(key,value != null ? value.toString() : "");
		return this;
	}

}
