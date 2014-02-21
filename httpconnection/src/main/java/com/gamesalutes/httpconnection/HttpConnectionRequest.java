package com.gamesalutes.httpconnection;

import java.util.HashMap;
import java.util.Map;

public final class HttpConnectionRequest<S,T> {

	
	private final String path;
	
	private final Map<String,String> headers;
	private final Map<String,String> queryParameters;
	
	private final RequestMarshaller<S> marshaller;
	
	
	private final ResponseUnmarshaller<T> unmarshaller;
	
	private final S request;
	
	HttpConnectionRequest(
			String path,
			Map<String,String> headers,
			Map<String,String> queryParameters,
			RequestMarshaller<S> marshaller,
			ResponseUnmarshaller<T> unmarshaller,
			S request) {
		this.path = path;
		this.headers = copyMap(headers);
		this.queryParameters = copyMap(queryParameters);
		this.marshaller = marshaller;
		this.unmarshaller = unmarshaller;
		this.request = request;
	}
	
	private Map<String,String> copyMap(Map<String,String> m) {
		return m != null ? new HashMap<String,String>(m) : null;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	public RequestMarshaller<S> getMarshaller() {
		return marshaller;
	}

	public ResponseUnmarshaller<T> getUnmarshaller() {
		return unmarshaller;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HttpConnectionRequest [path=");
		builder.append(path);
		builder.append(", headers=");
		builder.append(headers);
		builder.append(", queryParameters=");
		builder.append(queryParameters);
		builder.append(", marshaller=");
		builder.append(marshaller);
		builder.append(", unmarshaller=");
		builder.append(unmarshaller);
		builder.append(", request=");
		builder.append(request);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result
				+ ((queryParameters == null) ? 0 : queryParameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HttpConnectionRequest<S,T> other = (HttpConnectionRequest<S,T>) obj;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (queryParameters == null) {
			if (other.queryParameters != null)
				return false;
		} else if (!queryParameters.equals(other.queryParameters))
			return false;
		return true;
	}

	public S getRequest() {
		return request;
	}
			
}
