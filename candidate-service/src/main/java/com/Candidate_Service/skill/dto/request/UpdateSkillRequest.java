package com.Candidate_Service.skill.dto.request;

import lombok.*;
import lombok.experimental.Accessors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateSkillRequest {
    private String skillName;
}
