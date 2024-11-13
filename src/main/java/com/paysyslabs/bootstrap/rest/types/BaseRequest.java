package com.paysyslabs.bootstrap.rest.types;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.enums.WSRequestFormat;
import com.paysyslabs.bootstrap.rest.model.GeneralHTTPRequest;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;
import com.paysyslabs.bootstrap.rest.service.TokenService;

public abstract class BaseRequest {

	protected final Logger LOG = LoggerFactory.getLogger(BaseRequest.class);

	public static final String[] SYSTEM_HEADER_FIELDS = new String[] { TokenService.AUTH_TOKEN_FIELD };

	private void validate(ServiceRequest request, WSEndpointConfig config) throws Exception {
		List<String> expectedFields = config.getExpectedFieldList();

		if (!expectedFields.isEmpty() && (request.getParameters() == null || request.getParameters().isEmpty()))
			throw new Exception("parameters are expected, but not provided");

		for (String field : expectedFields)
			if (!request.getParameters().containsKey(field))
				throw new Exception("required field '" + field + "' not present");
	}

	protected Map<String, String> prepareHeaders(ServiceRequest request, WSEndpointConfig config) throws Exception {
		Map<String, String> preparedHeaders = new HashMap<>();
		Map<String, String> map = config.getRequestHeadersMap();

		for (String headerKey : map.keySet()) {
			String headerValue = map.get(headerKey);

			for (String field : config.getExpectedFieldList()) {

				if (!request.getParameters().containsKey(field))
					throw new Exception("Expected field '" + field + "' not present");

				headerValue = headerValue.replaceAll(String.format("\\{%s\\}", field),
						request.getParameters().get(field));
			}

			for (String field : SYSTEM_HEADER_FIELDS) {
				if (request.getParameters().containsKey(field)) {
					headerValue = headerValue.replaceAll(String.format("\\{%s\\}", field),
							request.getParameters().get(field));
				}
			}

			preparedHeaders.put(headerKey, headerValue);
		}
		for (String k : preparedHeaders.keySet()) {
			LOG.info("HEAD[{}] = {}", k, preparedHeaders.get(k));
		}
		return preparedHeaders;
	}

	protected String prepare(ServiceRequest request, WSEndpointConfig config, boolean encode) throws Exception {
		String data = config.getDataTemplate();
		List<String> fieldList = new ArrayList<>();
		fieldList.addAll(config.getExpectedFieldList());
		fieldList.addAll(config.getVariableFieldList());

		for (String field : fieldList) {

			if (!request.getParameters().containsKey(field))
				throw new Exception("Expected field '" + field + "' not present");

			String replacement = request.getParameters().get(field);

			if (encode)
				replacement = URLEncoder.encode(replacement, "UTF-8");

			data = data.replaceAll(String.format("\\{%s\\}", field), replacement);
		}

		if (data != null)
			LOG.info("DATA[{}] = \n\n{}\n\n", data.length(), data);

		return data;
	}

	public HttpResponse<String> perform(ServiceRequest request, WSEndpointConfig config) throws Exception {
		validate(request, config);
		return initiate(request, config);
	}

	protected abstract HttpResponse<String> initiate(ServiceRequest request, WSEndpointConfig config) throws Exception;

	public GeneralHTTPRequest saf(Map<WSRequestFormat, BaseRequest> supportedRequests, ServiceRequest request,
			WSEndpointConfig config) throws Exception {
		GeneralHTTPRequest httpRequest = safRequest(request, config);

		if (config.getDependents() != null && !config.getDependents().isEmpty()) {
			List<GeneralHTTPRequest> dependentCalls = new ArrayList<>();
			for (WSEndpointConfig conf : config.getDependents()) {

				LOG.info("conf = {}", conf);

				BaseRequest handler = supportedRequests.get(conf.getRequestFormat());

				if (handler == null)
					throw new Exception(String.format("no handler found for format: %s", conf.getRequestFormat()));

				GeneralHTTPRequest req = handler.saf(supportedRequests, request, conf);
				req.setStan(randomStan(12));
				dependentCalls.add(req);
			}
			httpRequest.setPerformOnSuccess(dependentCalls);
		}

		return prepareHttpRequest(httpRequest, request, config);
	}

	protected abstract GeneralHTTPRequest safRequest(ServiceRequest request, WSEndpointConfig config) throws Exception;

	private GeneralHTTPRequest prepareHttpRequest(GeneralHTTPRequest httpRequest, ServiceRequest request,
			WSEndpointConfig config) {
		// add tags, stan
		httpRequest.setStan(request.getRef());
		httpRequest.setType(config.getType());

		Map<String, String> tags = new HashMap<>();

		List<String> fieldList = new ArrayList<String>();
		fieldList.addAll(config.getExpectedFieldList());
		fieldList.addAll(config.getVariableFieldList());
		
		for (String field : fieldList)
			tags.put(field, request.getParameters().get(field));

		httpRequest.setTags(tags);

		return httpRequest;
	}

	private static String randomStan(int length) {
		Random random = new Random();
		char[] digits = new char[length];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return new String(digits);
	}
}
