package com.Candidate_Service.candidate_skill.dto.request;

import com.Candidate_Service.entity.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateCandidateSkillRequest {
    @NotNull(message = "candidate id is required")
    private Long candidateId;
    @NotNull(message = "skill id is required")
    private Long skillId;
    @NotNull(message = "years of experience is required")
    private Integer yearsOfExperience;
}
