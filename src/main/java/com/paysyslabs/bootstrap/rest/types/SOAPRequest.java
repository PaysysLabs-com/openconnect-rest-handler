package com.paysyslabs.bootstrap.rest.types;

import java.util.List;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.GeneralHTTPRequest;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;

public class SOAPRequest extends BaseRequest {
	@Override
	protected HttpResponse<String> initiate(ServiceRequest request, WSEndpointConfig config) throws Exception {
		String data = prepare(request, config, false);
		Map<String, String> headers = prepareHeaders(request, config);

		String url = config.getConfig().getBaseUrl();

		if (config.getEndpointTemplate() != null)
			url += config.getEndpointTemplate();

		return Unirest.post(url).headers(headers).body(data).asString();
	}

	@Override
	public GeneralHTTPRequest safRequest(ServiceRequest request, WSEndpointConfig config) throws Exception {
		String data = prepare(request, config, false);
		String url = String.format("%s%s", config.getConfig().getBaseUrl(), config.getEndpointTemplate());
		List<String> headers = config.getRequestHeadersList();
		
		GeneralHTTPRequest httpRequest = new GeneralHTTPRequest();
		httpRequest.setHeaders(headers);
		httpRequest.setBody(data);
		httpRequest.setUrl(url);
		httpRequest.setMethod("POST");

		return httpRequest;
	}

}