package com.Candidate_Service.skill.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GetSkillResoponse {
    private String skillName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
