package com.Candidate_Service.candidate.service;

import com.Candidate_Service.candidate.dto.request.CandidateSearchRequest;
import com.Candidate_Service.candidate.dto.request.CreateCandidateRequest;
import com.Candidate_Service.candidate.dto.request.UpdateCandidateRequest;
import com.Candidate_Service.candidate.dto.response.CreateCandidateResponse;
import com.Candidate_Service.candidate.dto.response.GetCandidateResponse;
import com.Candidate_Service.candidate.dto.response.PageResponse;
import com.Candidate_Service.candidate.dto.response.UpdateCandidateResponse;
import com.Candidate_Service.candidate.mapper.CandidateMapper;
import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.candidate.specification.CandidateSpecification;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;

    public PageResponse<GetCandidateResponse> getAllCandidates(CandidateSearchRequest searchRequest, int page,
                                                               int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Candidate> specification = Specification.unrestricted();

        specification = specification
                .and(CandidateSpecification.hasUserId(searchRequest.getUserId()))
                .and(CandidateSpecification.hasAddress(searchRequest.getAddress()))
                .and(CandidateSpecification.hasPhone(searchRequest.getPhone()))
                .and(CandidateSpecification.hasSummary(searchRequest.getSummary()));


        Page<Candidate> candidatePage = candidateRepository.findAll(specification, pageable);
        if (candidatePage.isEmpty()) {
            return PageResponse.<GetCandidateResponse>builder()
                    .data(new ArrayList<>())
                    .page(candidatePage.getNumber())
                    .size(candidatePage.getSize())
                    .totalElements(candidatePage.getTotalElements())
                    .totalPages(candidatePage.getTotalPages())
                    .first(candidatePage.isFirst())
                    .last(candidatePage.isLast())
                    .build();
        }
        List<Candidate> candidateList = candidatePage.getContent();
        List<GetCandidateResponse> candidateResponseList = candidateMapper.toGetCandidateResponses(candidateList);
        return PageResponse.<GetCandidateResponse>builder()
                .data(candidateResponseList)
                .first(candidatePage.isFirst())
                .last(candidatePage.isLast())
                .page(candidatePage.getNumber())
                .size(candidatePage.getSize())
                .totalElements(candidatePage.getTotalElements())
                .totalPages(candidatePage.getTotalPages())
                .build();
    }

    public GetCandidateResponse getCandidateById(Long id) {
        Optional<Candidate> candidateOp = candidateRepository.findById(id);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name()
                    , "candidate with id (" + id + ") not found");
        }
        Candidate candidate = candidateOp.get();

        return candidateMapper.toGetCandidateResponse(candidate);
    }

    public CreateCandidateResponse createCandidate(CreateCandidateRequest candidateRequest) {
        Candidate candidate = candidateMapper.toCandidate(candidateRequest);
        candidateRepository.save(candidate);
        return candidateMapper.toCreateCandidateResponse(candidate);

    }

    public UpdateCandidateResponse updateCandidateDate(UpdateCandidateRequest candidateRequest,
                                                       Long id) {
        Optional<Candidate> candidateOp = candidateRepository.findById(id);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + id + ") not found");
        }
        Candidate candidate = candidateOp.get();
        candidate.setUserId(candidateRequest.getUserId())
                .setPhone(candidateRequest.getPhone())
                .setAddress(candidateRequest.getAddress())
                .setSummary(candidateRequest.getSummary())
                .setExperiences(null) // temporary
                .setSkills(null)
                .setEducations(null)
                .setStatus(candidateRequest.getStatus());
        candidateRepository.save(candidate);

        return candidateMapper.toUpdateCandidateResponse(candidate);
    }

    public void deleteCandidate(Long id) {
        Optional<Candidate> candidateOp = candidateRepository.findById(id);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + id + ") not found");
        }
    Candidate candidate = candidateOp.get();
        candidate.setStatus(Status.DELETED);
        candidateRepository.save(candidate);
    }
}
