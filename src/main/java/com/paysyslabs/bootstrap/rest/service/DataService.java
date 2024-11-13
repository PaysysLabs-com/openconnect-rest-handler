package com.paysyslabs.bootstrap.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.entities.WSResponseDefinition;
import com.paysyslabs.bootstrap.rest.helper.VariableFieldService;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;
import com.paysyslabs.bootstrap.rest.repo.WSEndpointConfigRepository;
import com.paysyslabs.bootstrap.rest.repo.WSResponseDefinitionRepository;

@Service
public class DataService {

	@Autowired
	private WSEndpointConfigRepository endpointConfigRepository;

	@Autowired
	private WSResponseDefinitionRepository responseDefinitionRepository;

	@Autowired
	private VariableFieldService variableFieldService;

	public WSEndpointConfig getEndpointConfiguration(String realm, String type) {
		return endpointConfigRepository.findOneByConfigTypeAndType(realm, type);
	}

	public WSResponseDefinition getResponseDefinition(String realm, String code) {
		return responseDefinitionRepository.findOneByConfigTypeAndMatchCode(realm, code);
	}

	public void getVariableFieldValue(ServiceRequest request, String field) throws Exception {
		variableFieldService.getVariableFieldValue(request, field);
	}
}
