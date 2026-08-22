package com.application_service.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient candidateRestClient(JwtForwardingInterceptor interceptor,
                                          @Value("${candidate-service.url}") String candidateServiceUrl) {

        return RestClient.builder()
                .requestInterceptor(interceptor)
                .baseUrl(candidateServiceUrl)
                .build();
    }

    @Bean
    public RestClient jobRestClient(JwtForwardingInterceptor interceptor,
                                    @Value("${job-service.url}") String jobServiceUrl) {

        return RestClient.builder()
                .requestInterceptor(interceptor)
                .baseUrl(jobServiceUrl)
                .build();
    }

    @Bean
    public RestClient userRestClient(JwtForwardingInterceptor interceptor,
                                    @Value("${user_auth-service.url}") String userServiceUrl) {

        return RestClient.builder()
                .requestInterceptor(interceptor)
                .baseUrl(userServiceUrl)
                .build();
    }
}