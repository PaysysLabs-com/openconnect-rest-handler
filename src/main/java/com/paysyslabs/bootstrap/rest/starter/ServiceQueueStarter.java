package com.paysyslabs.bootstrap.rest.starter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import com.paysyslabs.queue.QueueManager;

@Configuration
public class ServiceQueueStarter {

    @Autowired
    @Qualifier("serviceQueueManager")
    private QueueManager queueManager;

    @PostConstruct
    public void bootstrap() throws Exception {
        queueManager.createWorkers();
    }

}
