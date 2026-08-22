package com.Candidate_Service.experience.service;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.Experience;
import com.Candidate_Service.experience.dto.request.CreateExperienceRequest;
import com.Candidate_Service.experience.dto.request.UpdateExperienceRequest;
import com.Candidate_Service.experience.dto.response.CreateExperienceResponse;
import com.Candidate_Service.experience.dto.response.GetExperienceResponse;
import com.Candidate_Service.experience.dto.response.UpdateExperienceResponse;
import com.Candidate_Service.experience.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final ExperienceRepository experienceRepository;
    private final CandidateRepository candidateRepository;

    public List<GetExperienceResponse> getAllExperiences() {
        List<Experience> experienceList = experienceRepository.findAll();
        if (experienceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<GetExperienceResponse> getExperienceResponseList = new ArrayList<>();
        for (Experience experience : experienceList) {
            GetExperienceResponse getExperienceResponse = new GetExperienceResponse();
            getExperienceResponse.setId(experience.getId())
                    .setCandidateId(experience.getCandidate().getId())
                    .setCompanyName(experience.getCompanyName())
                    .setJobTitle(experience.getJobTitle())
                    .setStartDate(experience.getStartDate())
                    .setEndDate(experience.getEndDate())
                    .setDescription(experience.getDescription())
                    .setCreatedAt(experience.getCreatedAt())
                    .setCreatedBy(experience.getCreatedBy())
                    .setUpdatedAt(experience.getUpdatedAt())
                    .setUpdatedBy(experience.getUpdatedBy());
            getExperienceResponseList.add(getExperienceResponse);
        }
        return getExperienceResponseList;

    }

    public GetExperienceResponse getExperienceById(Long id) {
        Optional<Experience> experienceOp = experienceRepository.findById(id);
        if (experienceOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.EXPERIENCE_NOT_FOUND.name(),
                    "experience with id (" + id + ") not found");
        }
        Experience experience = experienceOp.get();
        GetExperienceResponse getExperienceResponse = new GetExperienceResponse();
        getExperienceResponse.setId(experience.getId())
                .setCandidateId(experience.getCandidate().getId())
                .setCompanyName(experience.getCompanyName())
                .setJobTitle(experience.getJobTitle())
                .setStartDate(experience.getStartDate())
                .setEndDate(experience.getEndDate())
                .setDescription(experience.getDescription())
                .setCreatedAt(experience.getCreatedAt())
                .setCreatedBy(experience.getCreatedBy())
                .setUpdatedAt(experience.getUpdatedAt())
                .setUpdatedBy(experience.getUpdatedBy());


        return getExperienceResponse;
    }

    public CreateExperienceResponse createExperience(CreateExperienceRequest request) {
        Optional<Candidate> candidateOp = candidateRepository.findById(request.getCandidateId());
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + request.getCandidateId() + ") not found");
        }


        Experience experience = new Experience();
        experience.setCandidate(candidateOp.get())
                .setCompanyName(request.getCompanyName())
                .setJobTitle(request.getJobTitle())
                .setStartDate(request.getStartDate())
                .setEndDate(request.getEndDate())
                .setDescription(request.getDescription());

        experienceRepository.save(experience);

        CreateExperienceResponse response = new CreateExperienceResponse();
        response.setId(experience.getId())
                .setCandidateId(experience.getCandidate().getId())
                .setCompanyName(experience.getCompanyName())
                .setJobTitle(experience.getJobTitle())
                .setStartDate(experience.getStartDate())
                .setEndDate(experience.getEndDate())
                .setDescription(experience.getDescription())
                .setCreatedAt(experience.getCreatedAt())
                .setCreatedBy(experience.getCreatedBy());

        return response;


    }

    public UpdateExperienceResponse updateExperience(Long id, UpdateExperienceRequest request) {

        Optional<Experience> experienceOp = experienceRepository.findById(id);
        if (experienceOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.EXPERIENCE_NOT_FOUND.name(),
                    "experience with id (" + id + ") not found");
        }
        Optional<Candidate> candidateOp = candidateRepository.findById(request.getCandidateId());
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + request.getCandidateId() + ") not found");
        }
        Experience experience = experienceOp.get();
        experience.setCandidate(candidateOp.get())
                .setCompanyName(request.getCompanyName())
                .setJobTitle(request.getJobTitle())
                .setStartDate(request.getStartDate())
                .setEndDate(request.getEndDate())
                .setDescription(request.getDescription());

        experienceRepository.save(experience);

        UpdateExperienceResponse response = new UpdateExperienceResponse();
        response.setId(experience.getId())
                .setCandidateId(experience.getCandidate().getId())
                .setCompanyName(experience.getCompanyName())
                .setJobTitle(experience.getJobTitle())
                .setStartDate(experience.getStartDate())
                .setEndDate(experience.getEndDate())
                .setDescription(experience.getDescription())
                .setUpdatedAt(experience.getUpdatedAt())
                .setUpdatedBy(experience.getUpdatedBy());
        return response;


    }

    public void deleteExperience(Long id) {
        Optional<Experience> experienceOp = experienceRepository.findById(id);
        if (experienceOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.EXPERIENCE_NOT_FOUND.name(),
                    "experience with id (" + id + ") not found");
        }
        experienceRepository.delete(experienceOp.get());
    }

    public List<GetExperienceResponse> getAllExperiencesByCandidateId(Long id) {
        Optional<Candidate> candidateOp = candidateRepository.findById(id);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + id + ") not found");
        }
        List<Experience> experienceList = experienceRepository.findByCandidateId(id);
        if (experienceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<GetExperienceResponse> getExperienceResponseList = new ArrayList<>();
        for (Experience experience : experienceList) {
            GetExperienceResponse getExperienceResponse = new GetExperienceResponse();
            getExperienceResponse.setId(experience.getId())
                    .setCandidateId(experience.getCandidate().getId())
                    .setCompanyName(experience.getCompanyName())
                    .setJobTitle(experience.getJobTitle())
                    .setStartDate(experience.getStartDate())
                    .setEndDate(experience.getEndDate())
                    .setDescription(experience.getDescription())
                    .setCreatedAt(experience.getCreatedAt())
                    .setCreatedBy(experience.getCreatedBy())
                    .setUpdatedAt(experience.getUpdatedAt())
                    .setUpdatedBy(experience.getUpdatedBy());
            getExperienceResponseList.add(getExperienceResponse);
        }
        return getExperienceResponseList;


    }
}
