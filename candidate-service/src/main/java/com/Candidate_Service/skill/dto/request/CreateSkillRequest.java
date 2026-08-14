package com.Candidate_Service.skill.dto.request;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.Accessors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateSkillRequest {
    private String skillName;

}
