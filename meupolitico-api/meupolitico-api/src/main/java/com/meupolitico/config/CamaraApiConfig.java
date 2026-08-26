package com.meupolitico.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CamaraApiConfig {

    @Value("${camara.api.base-url}")
    private String camaraApiBaseUrl;

    @Bean
    public RestClient camaraRestClient() {
        return RestClient.builder()
                .baseUrl(camaraApiBaseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}