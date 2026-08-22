package com.Candidate_Service.candidate.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class CandidateSearchRequest {
    private String name;
    private Long userId;
    private String phone;
    private String address;
    private String summary;
}
