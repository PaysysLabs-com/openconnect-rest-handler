package com.paysyslabs.bootstrap.rest.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.options.Options;
import com.paysyslabs.bootstrap.rest.enums.WSRequestFormat;
import com.paysyslabs.bootstrap.rest.enums.WSResponseFormat;
import com.paysyslabs.bootstrap.rest.handler.ServiceQueueWorkerFactory;
import com.paysyslabs.bootstrap.rest.service.DataService;
import com.paysyslabs.bootstrap.rest.service.HooksService;
import com.paysyslabs.bootstrap.rest.service.TokenService;
import com.paysyslabs.bootstrap.rest.types.Base64QueryStringRequest;
import com.paysyslabs.bootstrap.rest.types.BaseRequest;
import com.paysyslabs.bootstrap.rest.types.JSONResponseFormatter;
import com.paysyslabs.bootstrap.rest.types.NBPXMLResponseFormatter;
import com.paysyslabs.bootstrap.rest.types.POSTBase64QueryStringRequest;
import com.paysyslabs.bootstrap.rest.types.QueryStringRequest;
import com.paysyslabs.bootstrap.rest.types.ResponseFormatter;
import com.paysyslabs.bootstrap.rest.types.SOAPRequest;
import com.paysyslabs.bootstrap.rest.types.XMLResponseFormatter;
import com.paysyslabs.queue.QueueManager;
import com.rabbitmq.client.Connection;

@Configuration
public class ServiceRequestInboundConfiguration {

	public static final Map<WSRequestFormat, BaseRequest> SUPPORTED_REQUESTS = new HashMap<>();
	public static final Map<WSResponseFormat, ResponseFormatter> RESPONSE_FORMATTERS = new HashMap<>();

	static {
		SUPPORTED_REQUESTS.put(WSRequestFormat.QUERY_STRING, new QueryStringRequest());
		SUPPORTED_REQUESTS.put(WSRequestFormat.B64_QUERY_STRING, new Base64QueryStringRequest());
		SUPPORTED_REQUESTS.put(WSRequestFormat.SOAP, new SOAPRequest());
		SUPPORTED_REQUESTS.put(WSRequestFormat.B64_POST_QUERY_STRING, new POSTBase64QueryStringRequest());

		RESPONSE_FORMATTERS.put(WSResponseFormat.XML, new XMLResponseFormatter());
		RESPONSE_FORMATTERS.put(WSResponseFormat.NBP_XML, new NBPXMLResponseFormatter());
		RESPONSE_FORMATTERS.put(WSResponseFormat.JSON, new JSONResponseFormatter());
	}

	@Value("${service.client.unsafe:false}")
	private boolean unsafe;

	@Value("${service.queue.request}")
	private String requestQueue;

	@Value("${service.queue.result}")
	private String resultQueue;

	@Value("${service.queue.workers}")
	private Integer workers;

	@Value("${service.timeout.connect}")
	private Long connectionTimeout;

	@Value("${service.timeout.read}")
	private Long readTimeout;

	@Value("${service.queue.saf}")
	private String safQueue;

	@Value("${queue.config.prefetch.count}")
	private int prefetchCount;

	@Value("${queue.config.auto.ack}")
	private boolean autoAck;

	@Autowired
	private Connection queueConnection;

	@Autowired
	private DataService dataService;

	@Autowired
	private HooksService hooksService;

	@Autowired
	private TokenService tokenService;

	@Bean(name = "serviceQueueManager")
	public QueueManager queueManager() throws Exception {
		return new QueueManager("service-queue-worker", workers, requestQueue, queueConnection,
				new ServiceQueueWorkerFactory(prefetchCount, autoAck, hooksService, dataService, tokenService,
						resultQueue, safQueue, SUPPORTED_REQUESTS, RESPONSE_FORMATTERS),
				false);
	}

	@PostConstruct
	public void configure() throws Exception {
		Unirest.setTimeouts(connectionTimeout, readTimeout);
		
		if (unsafe)
			Unirest.setHttpClient(unsafeHttpClient());

		Options.refresh();
	}

	public static HttpClient unsafeHttpClient() throws Exception {

		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy() {
			@Override
			public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				return true;
			}
		}).build();

		return HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier())
				.build();
	}
}
