package com.Candidate_Service.candidate.dto.response;

import com.Candidate_Service.candidate_skill.dto.response.GetCandidateSkillResponse;
import com.Candidate_Service.education.dto.response.GetEducationResponse;
import com.Candidate_Service.entity.Education;
import com.Candidate_Service.entity.Experience;
import com.Candidate_Service.entity.CandidateSkill;
import com.Candidate_Service.experience.dto.response.GetExperienceResponse;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GetCandidateResponse {
    private Long Id;
    private String name;
    private Long userId;
    private String phone;
    private String address;
    private String summary;
    private List<GetExperienceResponse> experiences;
    private List<GetCandidateSkillResponse> candidateSkills;
    private List<GetEducationResponse> educations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
