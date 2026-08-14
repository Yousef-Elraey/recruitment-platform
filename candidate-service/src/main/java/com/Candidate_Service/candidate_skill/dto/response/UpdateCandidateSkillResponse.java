package com.Candidate_Service.candidate_skill.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateCandidateSkillResponse {
    private Long id;
    private Long candidateId;
    private Long skillId;
    private Integer yearsOfExperience;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
