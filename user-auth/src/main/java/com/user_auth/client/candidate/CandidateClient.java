package com.user_auth.client.candidate;

import com.user_auth.client.dto.CandidateCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CandidateClient {

    @Qualifier("candidateRestClient")
    private final RestClient restClient;

    public void createCandidate(CandidateCreateRequestDto request) {

        restClient.post()
                .uri("/api/v1/candidates")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}