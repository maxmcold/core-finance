package com.mdv.corefinance;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class CoreFinanceApplication extends SpringBootServletInitializer{
    private static final Logger logger = LoggerFactory.getLogger(CoreFinanceApplication.class);

    @Override
    protected SpringApplicationBuilder configure (SpringApplicationBuilder builder){
        return builder.sources(CoreFinanceApplication.class);
    }
    public static void main(String[] args) {

        SpringApplication.run(CoreFinanceApplication.class, args);

    }





}