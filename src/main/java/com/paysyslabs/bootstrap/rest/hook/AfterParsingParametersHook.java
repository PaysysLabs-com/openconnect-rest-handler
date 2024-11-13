package com.paysyslabs.bootstrap.rest.hook;

import com.paysyslabs.bootstrap.rest.model.ServiceRequest;

public interface AfterParsingParametersHook {
    ServiceRequest hook(ServiceRequest request);
}
