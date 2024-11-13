package com.paysyslabs.bootstrap.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paysyslabs.queue.QueueConfig;
import com.paysyslabs.queue.QueueConfigBuilder;

import net.jodah.lyra.config.RecoveryPolicies;
import net.jodah.lyra.config.RetryPolicies;
import net.jodah.lyra.util.Duration;

@Configuration
public class QueueInitializationConfiguration {

    @Value("${queue.config.host}")
    private String host;

    @Value("${queue.config.port}")
    private Integer port;

    @Value("${queue.config.username}")
    private String username;
    
    @Value("${queue.config.password}")
    private String password;

    @Value("${queue.config.heartbeat}")
    private Integer heartBeat;

    @Value("${queue.config.retry.interval}")
    private Integer retryInterval;
    
    @Bean
    public QueueConfig queueConfig() {
        return new QueueConfigBuilder()
                    .withHost(host)
                    .withPort(port)
                    .withUsername(username)
                    .withPassword(password)
                    .withRequestedHeartbeat(heartBeat)
                    .withRecoveryPolicy(RecoveryPolicies.recoverAlways())
                    .withRetryPolicy(RetryPolicies.retryAlways().withInterval(Duration.seconds(retryInterval)))
                    .build();
    }
}
