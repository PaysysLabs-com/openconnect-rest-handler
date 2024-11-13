package com.paysyslabs.bootstrap.rest.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paysyslabs.bootstrap.rest.entities.STANGenerator;

public interface STANGeneratorRepository extends JpaRepository<STANGenerator, String> {

}
