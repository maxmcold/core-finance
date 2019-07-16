package com.mdv.corefinance.beans;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties("fintech.remote.api.url")
@SuppressWarnings("unused")
public class RestConfiguration {
    private String host;
    private String port;
    private  String url;

    @Profile("dev")
    @Bean
    public String devRestHost(){
        return host;
    }

    @Profile("prod")
    @Bean
    public String prodRestHost(){
        return port;
    }

    @Profile("dev")
    @Bean
    public String devRestUrl(){
        return url;
    }

    @Profile("prod")
    @Bean
    public String prodRestUrl(){
        return url;
    }


}
