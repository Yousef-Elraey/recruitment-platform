package com.application_service.application.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AddFeedbackScoreRequest {
    private Long applicationId;
    private String feedback;
    private Float score;
}
