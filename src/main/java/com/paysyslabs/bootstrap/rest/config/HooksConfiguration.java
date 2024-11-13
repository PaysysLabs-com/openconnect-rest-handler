package com.paysyslabs.bootstrap.rest.config;

import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import com.paysyslabs.bootstrap.rest.Application;

@Configuration
@ConditionalOnProperty(name = "hooks.file")
public class HooksConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(HooksConfiguration.class);
    
    @Value("${hooks.file}")
    private String hooksJar;
    
    @Bean("customClassLoader")
    public URLClassLoader customClassLoader() throws Exception {
        URL url = ResourceUtils.getURL(hooksJar);
        LOG.info("Hooks Location: {}", url);
        return new URLClassLoader(new URL[] { url }, Application.class.getClassLoader());
    }

}
