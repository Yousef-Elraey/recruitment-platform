package com.user_auth.common.config;

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


}