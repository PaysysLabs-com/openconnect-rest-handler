package com.paysyslabs.bootstrap.rest.types;

import org.apache.commons.codec.binary.Base64;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.GeneralHTTPRequest;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;

public class Base64QueryStringRequest extends BaseRequest {

    private String getFullUrl(ServiceRequest request, WSEndpointConfig config) throws Exception {
        String qs =  prepare(request, config, false);
        String endpoint = config.getEndpointTemplate().replaceAll("\\{b64\\}", Base64.encodeBase64URLSafeString(qs.getBytes()));
        String url = String.format("%s%s", config.getConfig().getBaseUrl(), endpoint);
        return url;
    }
    
    @Override
    protected HttpResponse<String> initiate(ServiceRequest request, WSEndpointConfig config) throws Exception {
        String url = getFullUrl(request, config);
        
        LOG.info("{}", url);
        
        return Unirest.get(url).asString();
    }

    @Override
    public GeneralHTTPRequest safRequest(ServiceRequest request, WSEndpointConfig config) throws Exception {
        GeneralHTTPRequest httpRequest = new GeneralHTTPRequest();
        httpRequest.setUrl(getFullUrl(request, config));
        httpRequest.setMethod("GET");
        return httpRequest;
    }

}
