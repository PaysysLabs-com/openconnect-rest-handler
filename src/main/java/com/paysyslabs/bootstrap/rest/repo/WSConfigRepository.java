package com.paysyslabs.bootstrap.rest.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paysyslabs.bootstrap.rest.entities.WSConfig;

@Repository
public interface WSConfigRepository extends CrudRepository<WSConfig, Long> {

}
