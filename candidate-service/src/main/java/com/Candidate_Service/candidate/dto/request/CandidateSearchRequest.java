package com.Candidate_Service.candidate.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CandidateSearchRequest {
    private Long userId;
    private String phone;
    private String address;
    private String summary;
}
