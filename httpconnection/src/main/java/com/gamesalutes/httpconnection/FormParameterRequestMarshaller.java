package com.gamesalutes.httpconnection;

import java.util.HashMap;
import java.util.Map;

import com.gamesalutes.utils.MiscUtils;

/**
 * Request marshaller that has set of key value pairs that are URL-form encoded into the
 * payload of the request.
 * 
 * @author jmontgomery
 *
 */
public abstract class FormParameterRequestMarshaller implements RequestMarshaller<Map<String,String>> {

	protected final Map<String,String> formParameters = new HashMap<String,String>();
	
	public FormParameterRequestMarshaller() {
	}
	
	public FormParameterRequestMarshaller addAll(Map<String,String> parameters) {
		if(parameters == null) {
			throw new NullPointerException("parameters");
		}
		this.formParameters.putAll(parameters);
		
		return this;
	}
	public FormParameterRequestMarshaller clear() {
		this.formParameters.clear();
		
		return this;
	}
	
	public FormParameterRequestMarshaller add(String key,String value) {
		if(MiscUtils.isEmpty(key)) {
			throw new IllegalArgumentException("key=" + key);
		}
		this.formParameters.put(key, value);
		
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FormParameterRequestMarshaller [formParameters=");
		builder.append(formParameters);
		builder.append("]");
		return builder.toString();
	}
	

	public String getContentType() {
		return "text/plain";
	}
	
	

}
