package com.Candidate_Service.candidate_skill.dto.request;

import com.Candidate_Service.entity.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateCandidateSkillRequest {
    @NotNull(message = "candidate id is required")
    private Long candidateId;
    @NotNull(message = "skill id is required")
    private Long skillId;
    @NotNull(message = "years of experience is required")
    private Integer yearsOfExperience;
}
