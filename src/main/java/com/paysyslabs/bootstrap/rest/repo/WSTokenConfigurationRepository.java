package com.paysyslabs.bootstrap.rest.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paysyslabs.bootstrap.rest.entities.WSTokenConfiguration;

@Repository
public interface WSTokenConfigurationRepository extends CrudRepository<WSTokenConfiguration, Long> {
}
