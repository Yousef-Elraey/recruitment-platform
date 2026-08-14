package com.Candidate_Service.skill.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SkillSearchRequest {
    private String skillName;
}
