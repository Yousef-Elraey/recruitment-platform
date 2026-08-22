package com.application_service.client;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CandidateResponseDto {
    private Long Id;
    private Long userId;
    private String phone;
    private String address;
    private String summary;
}
