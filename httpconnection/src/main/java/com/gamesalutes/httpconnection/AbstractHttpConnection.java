package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.Disposable;
import com.gamesalutes.utils.MiscUtils;

public abstract class AbstractHttpConnection implements Disposable,HttpSupport {

	public AbstractHttpConnection() {
		// TODO Auto-generated constructor stub
	}
	
    protected final Map<String,String> defaultHeaders = new HashMap<String,String>();
    protected final ReadWriteLock sharedModificationLock = new ReentrantReadWriteLock();
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final <T> T unmarshallResponse(HttpResponse response,ResponseUnmarshaller<T> unmarshaller) throws IOException {
		if(unmarshaller == null) {
			throw new IllegalArgumentException("No unmarshaller configured");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Unmarshalling entity response using: " + unmarshaller);
		}
		try {
			return unmarshaller.unmarshall(response.getInputStream());
		}
		finally {
			MiscUtils.closeStream(response.getInputStream());
		}
	}
	
	protected final void handleExceptions(HttpResponse response,ResponseUnmarshaller<?> errorUnmarshaller) throws IOException,HttpBadStatusException {
		if(response == null) {
			throw new IOException("No Response");
		}
		
		final int code = response.getCode();
		
		if(logger.isDebugEnabled()) {
			logger.debug("Response code=" + code);
		}
		
		if(code >= 200 && code < 300) {
			return;
		}
		
		HttpBadStatusException ex = null;
		
		if(code == 400) {
			ex = new BadRequestException(response.getStatus());
		}
		else if(code == 403) {
			ex = new NotAuthorizedException(response.getStatus());
		}
		else if(code == 405) {
			ex = new MethodNotSupportedException(response.getStatus());
		}
		else if(code == 409) {
			ex = new ConflictException(response.getStatus());
		}
		else if(code == 404) {
			ex = new NotFoundException(response.getStatus());
		}
		else if(code >= 500 && code < 600) {
			ex = new ServerHttpException(code,response.getStatus());
		}
		// generic error
		else {
			ex = new HttpBadStatusException(code,response.getStatus());
		}
		
		// attempt to deserialize error response if unmarshaller set
		try {
			if(errorUnmarshaller != null) {
				try {
					Object errorResponse = errorUnmarshaller.unmarshall(response.getInputStream());
					ex.setExceptionResponse(errorResponse);
				}
				catch(Throwable t) {
					logger.warn("Unable to unmarshall error response",t);
				}
			}
		}
		finally {
			MiscUtils.closeStream(response.getInputStream());
		}
		
		if(ex != null) {
			throw ex;
		}
	}
	
    public HttpSupport addGlobalHeader(String key,String value) {
    	if(key == null) {
    		throw new NullPointerException("key");
    	}
    	Lock lock = this.sharedModificationLock.writeLock();
    	lock.lock();
    	try {
    		this.defaultHeaders.put(key, value);
    	}
    	finally {
    		lock.unlock();
    	}
    	
    	return this;
    }
    
    public HttpSupport removeGlobalHeader(String key) {
    	if(key == null) {
    		throw new NullPointerException("key");
    	}
    	Lock lock = this.sharedModificationLock.writeLock();
    	lock.lock();
    	try {
    		this.defaultHeaders.remove(key);
    	}
    	finally {
    		lock.unlock();
    	}
    	
    	return this;
    }
    
    public HttpSupport setGlobalHeaders(String...keyvalues) {
    	if(keyvalues == null) {
    		throw new NullPointerException("keyvalues");
    	}
    	if(keyvalues.length % 2 != 0) {
    		throw new IllegalArgumentException("keyvalues=" + Arrays.toString(keyvalues));
    	}
    	
    	Map<String,String> m = new HashMap<String,String>();
    	for(int i = 0; i < keyvalues.length; i+=2) {
    		m.put(keyvalues[i], keyvalues[i+1]);
    	}
    	
    	return setGlobalHeaders(m);
    }
    public HttpSupport setGlobalHeaders(Map<String,String> headers) {
    	Lock lock = this.sharedModificationLock.writeLock();
    	lock.lock();
    	try {
    		this.defaultHeaders.clear();
    		if(!MiscUtils.isEmpty(headers)) {
    			this.defaultHeaders.putAll(headers);
    		}
    	}
    	finally {
    		lock.unlock();
    	}
    	
    	return this;
    }

}
