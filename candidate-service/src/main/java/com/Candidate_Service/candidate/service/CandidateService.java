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
import com.Candidate_Service.candidate_skill.dto.request.CreateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.repository.CandidateSkillRepository;
import com.Candidate_Service.candidate_skill.service.CandidateSkillService;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.common.security.AuthenticatedUser;
import com.Candidate_Service.common.security.CurrentUser;
import com.Candidate_Service.cv.parsing_data.dto.ParsedEducation;
import com.Candidate_Service.cv.parsing_data.dto.ParsedExperience;
import com.Candidate_Service.cv.parsing_data.dto.ParsedSkill;
import com.Candidate_Service.cv.parsing_data.parsing.ParsedCvResponse;
import com.Candidate_Service.cv.service.CvService;
import com.Candidate_Service.education.dto.request.CreateEducationRequest;
import com.Candidate_Service.education.repository.EducationRepository;
import com.Candidate_Service.education.service.EducationService;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.CandidateSkill;
import com.Candidate_Service.entity.CandidateStatus;
import com.Candidate_Service.entity.Skill;
import com.Candidate_Service.experience.dto.request.CreateExperienceRequest;
import com.Candidate_Service.experience.repository.ExperienceRepository;
import com.Candidate_Service.experience.service.ExperienceService;
import com.Candidate_Service.skill.repository.SkillRepository;
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
    private final ExperienceService experienceService;
    private final EducationService educationService;
    private final CandidateSkillService candidateSkillService;
    private final CvService cvService;
    private final SkillRepository skillRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;

    public PageResponse<GetCandidateResponse> getAllCandidates(CandidateSearchRequest searchRequest, int page,
                                                               int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Candidate> specification = Specification.unrestricted();

        specification = specification
                .and(CandidateSpecification.hasName(searchRequest.getName()))
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
        List<GetCandidateResponse> candidateResponseList = new ArrayList<>();

        for (Candidate candidate : candidateList) {
            GetCandidateResponse getCandidateResponse = new GetCandidateResponse();
            getCandidateResponse.setId(candidate.getId())
                    .setName(candidate.getName())
                    .setUserId(candidate.getUserId())
                    .setPhone(candidate.getPhone())
                    .setAddress(candidate.getAddress())
                    .setSummary(candidate.getSummary())
                    .setExperiences(experienceService.getAllExperiencesByCandidateId(candidate.getId()))
                    .setCandidateSkills(candidateSkillService.getCandidateSkillByCandidateId(candidate.getId()))
                    .setEducations(educationService.getAllEducationsByCandidateId(candidate.getId()))
                    .setCreatedAt(candidate.getCreatedAt())
                    .setUpdatedAt(candidate.getUpdatedAt())
                    .setCreatedBy(candidate.getCreatedBy())
                    .setUpdatedBy(candidate.getUpdatedBy());
            candidateResponseList.add(getCandidateResponse);
        }
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
        if (candidate.getStatus() == CandidateStatus.DELETED) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_AVAILABLE.name(),
                    "candidate with id (" + id + ") not available");
        }
        return candidateMapper.toGetCandidateResponse(candidate);
    }

    public CreateCandidateResponse createCandidate(CreateCandidateRequest candidateRequest) {
        AuthenticatedUser currentUser = CurrentUser.getCurrentUser();

        Candidate candidate = new Candidate();
        candidate.setId(candidateRequest.getId())
                .setUserId(currentUser.getUserId())
                .setName(candidateRequest.getName())
                .setPhone(candidateRequest.getPhone())
                .setAddress(candidateRequest.getAddress())
                .setSummary(candidateRequest.getSummary())
                .setStatus(CandidateStatus.ACTIVE);

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
        candidate.setId(candidateRequest.getId())
                .setName(candidateRequest.getName())
                .setUserId(candidateRequest.getUserId())
                .setPhone(candidateRequest.getPhone())
                .setAddress(candidateRequest.getAddress())
                .setSummary(candidateRequest.getSummary())
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
        candidate.setStatus(CandidateStatus.DELETED);
        candidateRepository.save(candidate);
    }

    public void approveCandidate(Long id) {
        Optional<Candidate> candidateOp = candidateRepository.findById(id);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + id + ") not found");
        }
        Candidate candidate = candidateOp.get();
        candidate.setStatus(CandidateStatus.APPROVED);
        candidateRepository.save(candidate);
    }

    public void fillCurrentCandidateData() {
        AuthenticatedUser currentUser = CurrentUser.getCurrentUser();

        ParsedCvResponse parsedCvResponse = cvService.loadParsedDataCv();

        if (parsedCvResponse == null) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.NO_PARSED_DATA_FOUND.name(),
                    "no parsed data found for candidate with id :" + currentUser.getUserId());
        }
        List<ParsedSkill> skillList = parsedCvResponse.getSkills();
        for (ParsedSkill parsedSkill : skillList) {
            String parsedSkillName = parsedSkill.getName();

            Optional<Skill> skillOpBefore = skillRepository.findBySkillName(parsedSkillName);

            if (skillOpBefore.isEmpty()) {
                Skill skill = new Skill();
                skill.setSkillName(parsedSkillName);
                skillRepository.save(skill);
            }
            Optional<Skill> skillOpAfter = skillRepository.findBySkillName(parsedSkillName);
           Skill skill = skillOpAfter.get();

            if (candidateSkillRepository.findByCandidateIdAndSkillId(currentUser.getUserId(), skill.getId()).isEmpty()) {
                CreateCandidateSkillRequest candidateSkillRequest = new CreateCandidateSkillRequest();
                candidateSkillRequest.setCandidateId(currentUser.getUserId())
                        .setSkillId(skillOpAfter.get().getId());

                candidateSkillService.createCandidateSkill(candidateSkillRequest);

            }
        }

        List<ParsedEducation> educationList = parsedCvResponse.getEducation();
        for (ParsedEducation parsedEducation : educationList) {
            if (educationRepository.findByCandidateIdAndInstitution(currentUser.getUserId(), parsedEducation.getInstitution()).isEmpty()) {

                CreateEducationRequest createEducationRequest = new CreateEducationRequest();
                createEducationRequest.setCandidateId(currentUser.getUserId())
                        .setInstitution(parsedEducation.getInstitution())
                        .setDegree(parsedEducation.getDegree())
                        .setFieldOfStudy(parsedEducation.getFieldOfStudy())
                        .setStartDate(parsedEducation.getStartDate())
                        .setEndDate(parsedEducation.getEndDate());

                educationService.createEducation(createEducationRequest);
            }
        }

        List<ParsedExperience> experienceList = parsedCvResponse.getExperience();
        for (ParsedExperience parsedExperience : experienceList) {
            if (experienceRepository.findByCandidateIdAndCompanyName(currentUser.getUserId(), parsedExperience.getCompanyName()).isEmpty()) {
                CreateExperienceRequest createExperienceRequest = new CreateExperienceRequest();
                createExperienceRequest.setCandidateId(currentUser.getUserId())
                        .setCompanyName(parsedExperience.getCompanyName())
                        .setJobTitle(parsedExperience.getJobTitle())
                        .setStartDate(parsedExperience.getStartDate())
                        .setEndDate(parsedExperience.getEndDate());

                experienceService.createExperience(createExperienceRequest);
            }
        }




    }
}
