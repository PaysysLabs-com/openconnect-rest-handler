package com.paysyslabs.bootstrap.rest.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;

@Repository
public interface WSEndpointConfigRepository extends CrudRepository<WSEndpointConfig, Long> {
    public WSEndpointConfig findOneByConfigTypeAndType(String realm, String type);
}
