package com.paysyslabs.bootstrap.rest.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.entities.WSTokenConfiguration;
import com.paysyslabs.bootstrap.rest.handler.ServiceQueueWorker;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;
import com.paysyslabs.bootstrap.rest.repo.WSTokenConfigurationRepository;

@Service
public class TokenService {
    
    private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);
    
    public static final String AUTH_TOKEN_FIELD = "AUTH_TOKEN";
    
    @Autowired
    private WSTokenConfigurationRepository tokenConfigurationRepository;
    
    public void setupTokenIfRequired(ServiceRequest request, WSEndpointConfig config, ServiceQueueWorker worker) throws Exception {
        if (config.getTokenRequest() != null) {

            LOG.info("Request requires token..");
            
            WSTokenConfiguration configuration = config.getTokenRequest().getTokenConfiguration();
            
            String token = configuration.getCurrentToken();
            
            if (!configuration.isValid()) {
                LOG.info("The existing token is invalid or expired, requesting new token..");

                Map<String, String> tokenRequest = new HashMap<>();

                for (String f : config.getTokenRequest().getExpectedFieldList())
                    if (request.getParameters().containsKey(f))
                        tokenRequest.put(f, request.getParameters().get(f));
                
                ServiceRequest tokenServiceRequest = new ServiceRequest(
                    config.getTokenRequest().getConfig().getType(),
                    config.getTokenRequest().getType(),
                    request.getRef(),
                    tokenRequest
                );
                
                ServiceBasicResponse response = worker.process(tokenServiceRequest, config.getTokenRequest());

                token = response.getData().get(configuration.getTokenField()).toString();
                String expiry = response.getData().get(configuration.getExpiryField()).toString();

                Long exp = null;
                
                switch (configuration.getExpiryType()) {
                    case EPOCH_SECONDS : exp = Long.parseLong(expiry) * 1000; break;
                    case EPOCH_MILLISECONDS : exp = Long.parseLong(expiry); break;
                    case SECONDS : exp = System.currentTimeMillis() + (Long.parseLong(expiry) * 1000); break;
                    case MILLISECONDS : exp = System.currentTimeMillis() + Long.parseLong(expiry); break;
                    default: break;
                }
                
                configuration.setCurrentToken(token);
                configuration.setCurrentExpiryEpoch(exp);
                
                tokenConfigurationRepository.save(configuration);
                
                // restore MDC
                MDC.put("realm", request.getRealm());
                MDC.put("type", request.getType());
                MDC.put("rrn", request.getRef());
            }
            
            request.getParameters().put(AUTH_TOKEN_FIELD, token);
        }
    }

}
