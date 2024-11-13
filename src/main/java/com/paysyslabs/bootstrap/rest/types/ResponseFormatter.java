package com.paysyslabs.bootstrap.rest.types;

import com.mashape.unirest.http.HttpResponse;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;

public interface ResponseFormatter {
    ServiceBasicResponse format(HttpResponse<String> response, WSEndpointConfig config) throws Exception;
}
