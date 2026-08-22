package com.application_service.application.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AssignRecruiterIdRequest {
    private Long recruiterId;
    private Long applicationId;
}
