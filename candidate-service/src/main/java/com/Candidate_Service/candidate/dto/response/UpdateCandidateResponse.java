package com.Candidate_Service.candidate.dto.response;

import com.Candidate_Service.entity.CandidateEducation;
import com.Candidate_Service.entity.CandidateExperience;
import com.Candidate_Service.entity.CandidateSkill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateCandidateResponse {
    private Long Id;
    private Long userId;
    private String phone;
    private String address;
    private String summary;
    private List<CandidateExperience> experiences;
    private List<CandidateSkill> skills;
    private List<CandidateEducation> educations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
