package com.paysyslabs.bootstrap.rest.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paysyslabs.bootstrap.rest.entities.WSResponseDefinition;

@Repository
public interface WSResponseDefinitionRepository extends CrudRepository<WSResponseDefinition, Long> {
    public WSResponseDefinition findOneByConfigTypeAndMatchCode(String realm, String code);
}
