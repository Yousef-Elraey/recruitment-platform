package com.Candidate_Service.education.service;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.education.dto.request.CreateEducationRequest;
import com.Candidate_Service.education.dto.request.UpdateEducationRequest;
import com.Candidate_Service.education.dto.response.CreateEducationResponse;
import com.Candidate_Service.education.dto.response.GetEducationResponse;
import com.Candidate_Service.education.dto.response.UpdateEducationResponse;
import com.Candidate_Service.education.maper.EducationMapper;
import com.Candidate_Service.education.repository.EducationRepository;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Education;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository educationRepository;
    private final CandidateRepository candidateRepository;
    private final EducationMapper educationMapper;

    public List<GetEducationResponse> getAllEducations() {
        List<Education> educationList = educationRepository.findAll();
        if (educationList.isEmpty()) {
            return new ArrayList<>();
        }
        List<GetEducationResponse> getEducationResponseList = new ArrayList<>();
        for (Education education : educationList) {
            GetEducationResponse getEducationResponse = new GetEducationResponse();
            getEducationResponse.setCandidateId(education.getCandidate().getId())
                    .setInstitution(education.getInstitution())
                    .setDegree(education.getDegree())
                    .setFieldOfStudy(education.getFieldOfStudy())
                    .setStartDate(education.getStartDate())
                    .setEndDate(education.getEndDate())
                    .setCurrent(education.getCurrent())
                    .setDescription(education.getDescription());
            getEducationResponseList.add(getEducationResponse);
        }
        return getEducationResponseList;
    }

    public CreateEducationResponse createEducation(CreateEducationRequest request) {
        Optional<Candidate> candidateOp = candidateRepository.findById(request.getCandidateId());
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name()
                    , "candidate with id (" + request.getCandidateId() + ") not found");
        }
        Education education = educationMapper.toEducation(request);
        education.setCandidate(candidateOp.get())
                .setInstitution(request.getInstitution())
                .setDegree(request.getDegree())
                .setFieldOfStudy(request.getFieldOfStudy())
                .setStartDate(request.getStartDate())
                .setEndDate(request.getEndDate())
                .setCurrent(request.getCurrent())
                .setDescription(request.getDescription());
        if (request.getCurrent()) {
            education.setEndDate(LocalDate.now());
        }
        educationRepository.save(education);
        CreateEducationResponse response = new CreateEducationResponse();
        response.setCandidateId(education.getCandidate().getId())
                .setInstitution(education.getInstitution())
                .setDegree(education.getDegree())
                .setFieldOfStudy(education.getFieldOfStudy())
                .setStartDate(education.getStartDate())
                .setEndDate(education.getEndDate())
                .setCurrent(education.getCurrent())
                .setDescription(education.getDescription());

        return response;
    }


    public GetEducationResponse getEducationById(Long id) {
        Optional<Education> educationOp = educationRepository.findById(id);
        if (educationOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.EDUCATION_NOT_FOUND.name(),
                    "education with id (" + id + ") not found");
        }
        Education education = educationOp.get();
        GetEducationResponse educationResponse = new GetEducationResponse();
        educationResponse.setCandidateId(education.getCandidate().getId())
                .setInstitution(education.getInstitution())
                .setDegree(education.getDegree())
                .setFieldOfStudy(education.getFieldOfStudy())
                .setStartDate(education.getStartDate())
                .setEndDate(education.getEndDate())
                .setCurrent(education.getCurrent())
                .setDescription(education.getDescription());
        return educationResponse;
    }

    public UpdateEducationResponse updateEducation(Long id, UpdateEducationRequest request) {
        Optional<Candidate> candidateOp = candidateRepository.findById(request.getCandidateId());
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name()
                    , "candidate with id (" + request.getCandidateId() + ") not found");
        }
        Optional<Education> educationOp = educationRepository.findById(id);
        if (educationOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.EDUCATION_NOT_FOUND.name(),
                    "education with id (" + id + ") not found");
        }
        Education education = educationOp.get();
        education.setCandidate(candidateOp.get())
                .setInstitution(request.getInstitution())
                .setDegree(request.getDegree())
                .setFieldOfStudy(request.getFieldOfStudy())
                .setStartDate(request.getStartDate())
                .setEndDate(request.getEndDate())
                .setCurrent(request.getCurrent())
                .setDescription(request.getDescription());

        if (request.getCurrent()) {
            education.setEndDate(LocalDate.now());
        }
        educationRepository.save(education);

        UpdateEducationResponse response = new UpdateEducationResponse();
        response.setCandidateId(education.getCandidate().getId())
                .setInstitution(education.getInstitution())
                .setDegree(education.getDegree())
                .setFieldOfStudy(education.getFieldOfStudy())
                .setStartDate(education.getStartDate())
                .setEndDate(education.getEndDate())
                .setCurrent(education.getCurrent())
                .setDescription(education.getDescription())
                .setUpdatedAt(education.getUpdatedAt())
                .setUpdatedBy(education.getUpdatedBy());
        return response;
    }

    public void deleteEducation(Long id) {
        Optional<Education> educationOp = educationRepository.findById(id);
        if (educationOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.EDUCATION_NOT_FOUND.name(),
                    "education with id (" + id + ") not found");
        }
        educationRepository.delete(educationOp.get());
    }

    public List<GetEducationResponse> getAllEducationsByCandidateId(Long id) {
        Optional<Candidate> candidateOp = candidateRepository.findById(id);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name()
                    , "candidate with id (" + id + ") not found");
        }
        List<Education> educationList = educationRepository.findByCandidateId(id);
        if (educationList.isEmpty()) {
            return new ArrayList<>();
        }
        List<GetEducationResponse> getEducationResponseList = new ArrayList<>();
        for (Education education : educationList) {
            GetEducationResponse getEducationResponse = new GetEducationResponse();
            getEducationResponse.setCandidateId(education.getCandidate().getId())
                    .setInstitution(education.getInstitution())
                    .setDegree(education.getDegree())
                    .setFieldOfStudy(education.getFieldOfStudy())
                    .setStartDate(education.getStartDate())
                    .setEndDate(education.getEndDate())
                    .setCurrent(education.getCurrent())
                    .setDescription(education.getDescription())
                    .setCreatedAt(education.getCreatedAt())
                    .setUpdatedAt(education.getUpdatedAt())
                    .setCreatedBy(education.getCreatedBy())
                    .setUpdatedBy(education.getUpdatedBy());
            getEducationResponseList.add(getEducationResponse);
        }
        return getEducationResponseList;
    }
}
