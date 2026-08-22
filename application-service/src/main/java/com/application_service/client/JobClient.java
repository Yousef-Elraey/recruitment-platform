package com.application_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
    public class JobClient {

    private final RestClient restClient;

    public JobClient(@Qualifier("jobRestClient")RestClient restClient) {
        this.restClient = restClient;
    }

    public JobResponseDto getJob(Long jobId) {

        return restClient.get()
                .uri("/api/v1/jobs/{id}", jobId)
                .retrieve()
                .body(JobResponseDto.class);
    }
}