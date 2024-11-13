package com.paysyslabs.bootstrap.rest.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.paysyslabs.bootstrap.rest.generator.GenericStringSequenceGenerator;

@Entity
@Table(name = "stan_generator")
public class STANGenerator {
	@Id
	@GenericGenerator(name = "stan_seq", strategy = "com.paysyslabs.bootstrap.rest.generator.GenericStringSequenceGenerator", parameters = {
			@Parameter(name = GenericStringSequenceGenerator.SEQUENCE_NAME, value = "stan_seq"),
			@Parameter(name = GenericStringSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%06d") })
	@GeneratedValue(generator = "stan_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "code")
	private String code;

	public String getSTAN() {
		return code;
	}
}
