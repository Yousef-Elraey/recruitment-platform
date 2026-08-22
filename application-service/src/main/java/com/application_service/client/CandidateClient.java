package com.application_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
    public class CandidateClient {

    private final RestClient restClient;

    public CandidateClient(@Qualifier("candidateRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public CandidateResponseDto getCandidate(Long candidateId) {

        return restClient.get()
                .uri("/api/v1/candidates/{id}", candidateId)
                .retrieve()
                .body(CandidateResponseDto.class);
    }
}