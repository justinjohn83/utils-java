package com.gamesalutes.httpconnection;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.gamesalutes.utils.MiscUtils;

public final class HttpFormParameterRequestMarshaller extends
		FormParameterRequestMarshaller {

	public void marshall(OutputStream out) throws IOException {
        if(MiscUtils.isEmpty(this.formParameters))
            return;


        List<NameValuePair> nvps = new ArrayList<NameValuePair>(this.formParameters.size());

        for(Map.Entry<String,String> E : this.formParameters.entrySet())
        {
            nvps.add(new BasicNameValuePair(E.getKey(),E.getValue()));
        }

        UrlEncodedFormEntity entity = null;
        try
        {
            entity = new UrlEncodedFormEntity(nvps,HTTP.UTF_8);
        }
        catch( UnsupportedEncodingException e)
        {
            throw new AssertionError(e);
        }
        
        entity.writeTo(out);
	}

}
