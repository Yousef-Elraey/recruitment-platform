package com.Candidate_Service.candidate_skill.dto.response;

import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Skill;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateCandidateSkillResponse {
    private Long id;
    private Long candidateId;
    private Long skillId;
    private Integer yearsOfExperience;
    private LocalDateTime createdAt;
    private String createdBy;
}
