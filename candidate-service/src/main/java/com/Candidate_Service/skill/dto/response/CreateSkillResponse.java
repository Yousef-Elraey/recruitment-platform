package com.Candidate_Service.skill.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateSkillResponse {
    private Long id;
    private String skillName;
    private String createdBy;
    private LocalDateTime createdAt;
}
