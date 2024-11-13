package com.paysyslabs.bootstrap.rest.types;

import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.GeneralHTTPRequest;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;

public class POSTBase64QueryStringRequest extends BaseRequest {
	
	private String getFullBase64String(ServiceRequest request, WSEndpointConfig config) throws Exception {
		String postString = prepare(request, config, false);
		LOG.info("Post String ({})", postString);
		postString = Base64.encodeBase64URLSafeString(postString.getBytes());
		
        return postString;
    }
	
	@Override
	protected HttpResponse<String> initiate(ServiceRequest request, WSEndpointConfig config) throws Exception {
		
		String url = String.format("%s%s", config.getConfig().getBaseUrl(), config.getEndpointTemplate());
		LOG.info("URL ({})", url);
		String postString = getFullBase64String(request, config);
		
		return Unirest.post(url).body(postString).asString();
	}

	@Override
	protected GeneralHTTPRequest safRequest(ServiceRequest request, WSEndpointConfig config) throws Exception {
		String url = String.format("%s%s", config.getConfig().getBaseUrl(), config.getEndpointTemplate());
		LOG.info("URL ({})", url);
		String postString = getFullBase64String(request, config);
		List<String> headers = config.getRequestHeadersList();
        
        GeneralHTTPRequest httpRequest = new GeneralHTTPRequest();
        httpRequest.setHeaders(headers);
        httpRequest.setBody(postString);
        httpRequest.setUrl(url);
        httpRequest.setMethod("POST");
        
        return httpRequest;
	}

}
