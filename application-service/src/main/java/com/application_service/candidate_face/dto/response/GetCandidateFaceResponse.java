package com.application_service.candidate_face.dto.response;

import com.application_service.entity.Application;
import com.application_service.entity.Face;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Setter
@Getter
@Accessors(chain = true)
public class GetCandidateFaceResponse {
    private Long id;
    private Long applicationId;
    private String faceCode;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
