package com.paysyslabs.bootstrap.rest;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        SpringApplication.run(Application.class, args);
        latch.await();
    }

}
