package com.Candidate_Service.skill.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateSkillResponse {
    private Long id;
    private String skillName;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
