package com.paysyslabs.bootstrap.rest.hook;

import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;

public interface AfterResponseHook {
	ServiceBasicResponse hook(ServiceRequest request, ServiceBasicResponse response);
}
