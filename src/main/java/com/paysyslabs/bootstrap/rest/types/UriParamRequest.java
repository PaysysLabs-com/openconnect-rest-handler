package com.paysyslabs.bootstrap.rest.types;

import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.GeneralHTTPRequest;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;

public class UriParamRequest extends BaseRequest {
    private String getFullUrl(ServiceRequest request, WSEndpointConfig config) throws Exception {
        String qs =  prepare(request, config, true);
        String url = String.format("%s%s", config.getConfig().getBaseUrl(), config.getEndpointTemplate());
        String full = qs == null ? url : String.format("%s/%s", url, qs);
        return full;
    }

    @Override
    protected HttpResponse<String> initiate(ServiceRequest request, WSEndpointConfig config) throws Exception {
        String full = getFullUrl(request, config);
        Map<String, String> preparedHeaders = prepareHeaders(request, config);

        LOG.info("{}", full);
        LOG.info("{}", preparedHeaders);
        
        return Unirest.get(full).headers(preparedHeaders).asString();
    }

    @Override
    public GeneralHTTPRequest safRequest(ServiceRequest request, WSEndpointConfig config) throws Exception {
        GeneralHTTPRequest httpRequest = new GeneralHTTPRequest();
        httpRequest.setUrl(getFullUrl(request, config));
        httpRequest.setMethod("GET");
        return httpRequest;
    }
}
