package com.application_service.application.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AddFeedbackScoreResponse {
    private String feedback;
    private Float score;
}
