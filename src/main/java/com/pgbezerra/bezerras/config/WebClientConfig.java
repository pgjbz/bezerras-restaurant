package com.pgbezerra.bezerras.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${zipcode.api.baseurl}")
    private String zipcodeBaseUrl;

    @Bean(name = "zipcodeWebClient")
    public WebClient zipcodeWebClient(){
        return WebClient.builder().baseUrl(zipcodeBaseUrl)
                .build();
    }

}
