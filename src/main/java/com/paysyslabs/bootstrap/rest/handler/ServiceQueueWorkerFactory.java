package com.paysyslabs.bootstrap.rest.handler;

import java.util.Map;

import com.paysyslabs.bootstrap.rest.enums.WSRequestFormat;
import com.paysyslabs.bootstrap.rest.enums.WSResponseFormat;
import com.paysyslabs.bootstrap.rest.service.DataService;
import com.paysyslabs.bootstrap.rest.service.HooksService;
import com.paysyslabs.bootstrap.rest.service.TokenService;
import com.paysyslabs.bootstrap.rest.types.BaseRequest;
import com.paysyslabs.bootstrap.rest.types.ResponseFormatter;
import com.paysyslabs.queue.QueueWorker;
import com.paysyslabs.queue.QueueWorkerAbstractFactory;
import com.rabbitmq.client.Channel;

public class ServiceQueueWorkerFactory extends QueueWorkerAbstractFactory {

    private final TokenService tokenService;
    private final HooksService hooksService;
    private final DataService dataService;
    private final String resultQueue;
    private final Map<WSRequestFormat, BaseRequest> supportedRequests;
    private final Map<WSResponseFormat, ResponseFormatter> responseFormatters;
    private final String safQueue;
    private final int prefetchCount;
    private final boolean autoAck;

    public ServiceQueueWorkerFactory(int prefetchCount, boolean autoAck, HooksService hooksService,
            DataService dataService, TokenService tokenService, String resultQueue, String safQueue,
            Map<WSRequestFormat, BaseRequest> supportedRequests,
            Map<WSResponseFormat, ResponseFormatter> responseFormatters) {
        super();

        this.prefetchCount = prefetchCount;
        this.autoAck = autoAck;
        this.hooksService = hooksService;
        this.dataService = dataService;
        this.tokenService = tokenService;
        this.resultQueue = resultQueue;
        this.safQueue = safQueue;
        this.supportedRequests = supportedRequests;
        this.responseFormatters = responseFormatters;
    }

    @Override
    protected QueueWorker createConcreteWorker(String workerName, Channel channel, String queue) throws Exception {
        return new ServiceQueueWorker(workerName, prefetchCount, autoAck, channel, queue, hooksService, dataService,
                tokenService, resultQueue, safQueue, supportedRequests, responseFormatters);
    }

}
