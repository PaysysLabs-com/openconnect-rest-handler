package com.paysyslabs.bootstrap.rest;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.mashape.unirest.http.Unirest;
import com.paysyslabs.bootstrap.rest.config.ServiceRequestInboundConfiguration;
import com.paysyslabs.bootstrap.rest.service.DataService;
import com.paysyslabs.bootstrap.rest.service.HooksService;

@Ignore
@RunWith(SpringRunner.class)
@EnableJpaRepositories(basePackages = { "com.paysyslabs.bootstrap.rest" })
@EntityScan(basePackages = { "com.paysyslabs.bootstrap.rest" })
@DataJpaTest(showSql = true)
@ContextConfiguration(classes = { DataService.class, HooksService.class })
public class UnsafeTest {

    private static final Logger LOG = LoggerFactory.getLogger(UnsafeTest.class);


    @Test
    public void test() throws Exception {
        Unirest.setHttpClient(ServiceRequestInboundConfiguration.unsafeHttpClient());
        String body = Unirest.get("https://52.166.135.2/").asString().getBody();
        LOG.info("{}", body);
    }

}
