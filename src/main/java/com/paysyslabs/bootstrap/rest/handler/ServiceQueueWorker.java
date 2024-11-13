package com.paysyslabs.bootstrap.rest.handler;

import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.entities.WSResponseDefinition;
import com.paysyslabs.bootstrap.rest.enums.WSRequestFormat;
import com.paysyslabs.bootstrap.rest.enums.WSResponseFormat;
import com.paysyslabs.bootstrap.rest.helper.GsonHelper;
import com.paysyslabs.bootstrap.rest.helper.ResponseMaker;
import com.paysyslabs.bootstrap.rest.hook.AfterParsingParametersHook;
import com.paysyslabs.bootstrap.rest.hook.AfterResponseHook;
import com.paysyslabs.bootstrap.rest.model.GeneralHTTPRequest;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;
import com.paysyslabs.bootstrap.rest.service.DataService;
import com.paysyslabs.bootstrap.rest.service.HooksService;
import com.paysyslabs.bootstrap.rest.service.TokenService;
import com.paysyslabs.bootstrap.rest.types.BaseRequest;
import com.paysyslabs.bootstrap.rest.types.ResponseFormatter;
import com.paysyslabs.queue.QueueWorker;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

public class ServiceQueueWorker extends QueueWorker {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceQueueWorker.class);

	@SuppressWarnings("unused")
	private final HooksService hooksService;

	private final TokenService tokenService;
	private final DataService dataService;
	private final String resultQueue;
	private final Map<WSRequestFormat, BaseRequest> supportedRequests;
	private final Map<WSResponseFormat, ResponseFormatter> responseFormatters;
	private final String safQueue;

	private final AfterParsingParametersHook afterParsingHook;
	private final AfterResponseHook afterResponseHook;

	public ServiceQueueWorker(String workerName, int prefetchCount, boolean autoAck, Channel channel, String queue,
			HooksService hooksService, DataService dataService, TokenService tokenService, String resultQueue,
			String safQueue, Map<WSRequestFormat, BaseRequest> supportedRequests,
			Map<WSResponseFormat, ResponseFormatter> responseFormatters) throws Exception {
		super(workerName, prefetchCount, autoAck, channel, queue);

		this.hooksService = hooksService;
		this.dataService = dataService;
		this.tokenService = tokenService;
		this.resultQueue = resultQueue;
		this.safQueue = safQueue;
		this.supportedRequests = supportedRequests;
		this.responseFormatters = responseFormatters;
		this.afterParsingHook = hooksService.getAfterParsingParametersHook();
		this.afterResponseHook = hooksService.getAfterResponseHook();
	}

	public ServiceBasicResponse process(ServiceRequest request, WSEndpointConfig config) throws Exception {
		LOG.info("Before parsing '{}'", request);

		if (afterParsingHook != null) {
			request = afterParsingHook.hook(request);
		} else {
			LOG.info("afterParsingHook == null");
		}

		LOG.info("After parsing '{}'", request);

		BaseRequest handler = supportedRequests.get(config.getRequestFormat());

		if (handler == null)
			throw new Exception(String.format("no handler found for format: %s", config.getRequestFormat()));

		LOG.info("Base: {}", config.getConfig().getBaseUrl());
		LOG.info("Endpoint: {}", config.getEndpointTemplate());

		if (config.getGuaranteed() && config.getGuaranteed().booleanValue()) {

			GeneralHTTPRequest safRequest = handler.saf(supportedRequests, request, config);
			String json = GsonHelper.GSON.toJson(safRequest);

			LOG.info("forwarding to SAF: {}", json);

			respond(safQueue, null, json.getBytes("UTF-8"));

			return new ServiceBasicResponse("00", "SUCCESS");

		} else {

			LOG.info("not a SAF request, processing ourselves");
			ResponseFormatter formatter = responseFormatters.get(config.getResponseFormat());

			if (formatter == null)
				throw new Exception(String.format("no formatter found for format: %s", config.getResponseFormat()));

			// setup auth token if it is required
			tokenService.setupTokenIfRequired(request, config, this);

			HttpResponse<String> response = handler.perform(request, config);

			LOG.info("Response: {}", response);

			ServiceBasicResponse result = formatter.format(response, config);

			if (afterResponseHook != null)
				result = afterResponseHook.hook(request, result);

			LOG.info("Result: {}", result);

			return result;
		}

	}

	@Override
	public void handle(long tag, BasicProperties properties, byte[] body) throws Exception {
		String requestXML = new String(body);

		LOG.info("REQUEST {}", requestXML);
		String reversalType = null;
		ServiceRequest request = null;
		try {
			request = ServiceRequest.fromXML(requestXML);

			MDC.put("realm", request.getRealm());
			MDC.put("type", request.getType());
			MDC.put("rrn", request.getRef());

			WSEndpointConfig config = dataService.getEndpointConfiguration(request.getRealm(), request.getType());

			if (config == null)
				throw new Exception(String.format("failure finding endpoint configuration for realm: %s endpoint: %s",
						request.getRealm(), request.getType()));

			reversalType = config.getReversalType();
			request.parseParameters(requestXML, config.getExpectedFieldList());

			for (String field : config.getVariableFieldList()) {
				if (!request.getParameters().containsKey(field)) {
					dataService.getVariableFieldValue(request, field);
				}
			}

			ServiceBasicResponse formatted = process(request, config);

			LOG.info("CODE {}", formatted.getCode());
			LOG.info("BODY {}", formatted.getBody());

			WSResponseDefinition definition = dataService.getResponseDefinition(request.getRealm(),
					formatted.getCode());

			String xml = ResponseMaker.make(request.getRef(), request.getType(), formatted, definition);

			LOG.info("XML\n\n{}\n", xml);

			respond(resultQueue, properties, xml.getBytes("UTF-8"));

		} catch (Exception e) {

			String code = "500";
			String desc = e.getMessage();

			if (e instanceof UnirestException) {
				UnirestException xe = (UnirestException) e;

				if (xe.getCause() instanceof ConnectTimeoutException) {
					code = "501";
					desc = "Connect Timeout Exception";
				} else if (xe.getCause() instanceof SocketTimeoutException) {
					code = "502";
					desc = "Socket Timeout Exception";
				}

				if (code.equals("502") && reversalType != null) {
					try {
//						ServiceRequest request = ServiceRequest.fromXML(requestXML);
						WSEndpointConfig config = dataService.getEndpointConfiguration(request.getRealm(), reversalType);

						if (config == null)
							throw new Exception(
									String.format("failure finding endpoint configuration for realm: %s endpoint: %s",
											request.getRealm(), request.getType()));

						request.parseParameters(requestXML, config.getExpectedFieldList());

						for (String field : config.getVariableFieldList()) {
							if (!request.getParameters().containsKey(field)) {
								dataService.getVariableFieldValue(request, field);
							}
						}

						ServiceBasicResponse formatted = process(request, config);
						LOG.info("REVERSAL CODE {}", formatted.getCode());
						LOG.info("REVERSAL BODY {}", formatted.getBody());
					} catch (Exception ex) {
						LOG.error("Error occured while reversal: {}", ex.getMessage());
					}
				}
			} else if (e instanceof NullPointerException) {
				code = "503";
				desc = "Null Pointer Exception";
			}

			LOG.error("EXCEPTION TYPE: {}", e.getClass());

			LOG.error("ERROR", e);

			String xml = ResponseMaker.make(MDC.get("rrn"), code, desc);

			LOG.info("XML\n\n{}\n", xml);

			respond(resultQueue, properties, xml.getBytes("UTF-8"));

			throw e;

		} finally {
			acknowledge(tag);
		}
	}

}
