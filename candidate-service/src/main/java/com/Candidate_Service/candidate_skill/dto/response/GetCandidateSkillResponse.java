package com.Candidate_Service.candidate_skill.dto.response;

import com.Candidate_Service.entity.BaseEntity;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Skill;
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
public class GetCandidateSkillResponse extends BaseEntity {
    private Long candidateId;
    private Skill skill;
    private Integer yearsOfExperience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}
