package com.dominiccobo.fyp.stackexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class StackExchangeDocResolverApp {
    public static void main(String[] args) {
        SpringApplication.run(StackExchangeDocResolverApp.class, args);
    }
}
