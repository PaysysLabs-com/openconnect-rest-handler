package com.paysyslabs.bootstrap.rest.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.mashape.unirest.http.Unirest;

@Configuration
public class ServiceCallerConfig {

    @Value("${service.timeout.connect}")
    private Long connectionTimeout;

    @Value("${service.timeout.read}")
    private Long socketTimeout;

    @Value("${service.concurrency.maxtotal}")
    private int maxTotal;

    @Value("${service.concurrency.maxperroute}")
    private int maxPerRoute;

    @PostConstruct
    public void configure() {
        Unirest.setTimeouts(connectionTimeout, socketTimeout);
        Unirest.setConcurrency(maxTotal, maxPerRoute);
    }

}
