package com.Candidate_Service.candidate_skill.mapper;

import com.Candidate_Service.candidate_skill.dto.response.CreateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.GetCandidateSkillResponse;
import com.Candidate_Service.entity.CandidateSkill;
import com.Candidate_Service.candidate_skill.dto.request.CreateCandidateSkillRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateSkillMapper {
    public CandidateSkill toSkill(CreateCandidateSkillRequest createCandidateSkillRequest);

    public CreateCandidateSkillResponse toCreateSkillResponse(CandidateSkill candidateSkill);

    List<GetCandidateSkillResponse> toGetCandidateSkillResponses(List<CandidateSkill> candidateSkillList);
}
