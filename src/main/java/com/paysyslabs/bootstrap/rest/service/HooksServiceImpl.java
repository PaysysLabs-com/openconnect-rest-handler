package com.paysyslabs.bootstrap.rest.service;

import java.net.URLClassLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.paysyslabs.bootstrap.rest.hook.AfterParsingParametersHook;
import com.paysyslabs.bootstrap.rest.hook.AfterResponseHook;

@Service
public class HooksServiceImpl implements HooksService {

    private static final String HOOK_BEAN_NAME = "hook-bean";

    @Autowired(required = false)
    @Qualifier("customClassLoader")
    private URLClassLoader customClassLoader;

    @Value("${hooks.after.parsing-parameters:#{null}}")
    private String afterParsingParametersImplClass;

    @Value("${hooks.after.response:#{null}}")
    private String afterResponseImplClass;
    
    @Autowired
    private ApplicationContext context;

    @Override
    public AfterResponseHook getAfterResponseHook() {
        return (AfterResponseHook) safelyLoad(afterResponseImplClass);
    }

    @Override
    public AfterParsingParametersHook getAfterParsingParametersHook() {
        return (AfterParsingParametersHook) safelyLoad(afterParsingParametersImplClass);
    }

    private Object safelyLoad(String clazz) {
        try {
            return context.getBean(HOOK_BEAN_NAME);
        } catch (Exception e) {
            
        }
        
        try {
            Object bean = customClassLoader.loadClass(clazz).newInstance();
            AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
            factory.autowireBean(bean);
            factory.initializeBean(bean, HOOK_BEAN_NAME);
            return bean;
        } catch (Exception e) {
        }
        return null;
    }
}
