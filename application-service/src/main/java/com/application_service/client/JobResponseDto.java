package com.application_service.client;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobResponseDto {
    private Long Id;
    private String title;
    private String description;
    private String companyName;
    private String location;
}
