package com.Candidate_Service.candidate.mapper;

import com.Candidate_Service.candidate.dto.request.CreateCandidateRequest;
import com.Candidate_Service.candidate.dto.response.CreateCandidateResponse;
import com.Candidate_Service.candidate.dto.response.GetCandidateResponse;
import com.Candidate_Service.candidate.dto.response.UpdateCandidateResponse;
import com.Candidate_Service.entity.Candidate;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
    List<GetCandidateResponse> toGetCandidateResponses(List<Candidate> candidateList);

    GetCandidateResponse toGetCandidateResponse(Candidate candidate);

    Candidate toCandidate(CreateCandidateRequest candidateRequest);

    CreateCandidateResponse toCreateCandidateResponse(Candidate candidate);

    UpdateCandidateResponse toUpdateCandidateResponse(Candidate candidate);
}
