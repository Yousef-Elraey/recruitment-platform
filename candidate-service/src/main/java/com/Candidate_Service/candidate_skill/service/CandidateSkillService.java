package com.Candidate_Service.candidate_skill.service;

import com.Candidate_Service.candidate.repository.CandidateRepository;
import com.Candidate_Service.candidate_skill.dto.request.CreateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.request.UpdateCandidateSkillRequest;
import com.Candidate_Service.candidate_skill.dto.response.CreateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.GetCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.dto.response.UpdateCandidateSkillResponse;
import com.Candidate_Service.candidate_skill.mapper.CandidateSkillMapper;
import com.Candidate_Service.common.exceprion.ErrorCode;
import com.Candidate_Service.common.exceprion.RecruitmentBusinessException;
import com.Candidate_Service.entity.Candidate;
import com.Candidate_Service.entity.CandidateSkill;
import com.Candidate_Service.candidate_skill.repository.CandidateSkillRepository;
import com.Candidate_Service.entity.Skill;
import com.Candidate_Service.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateSkillService {
    private final CandidateSkillRepository candidateSkillRepository;
    private final CandidateSkillMapper candidateSkillMapper;
    private final CandidateRepository candidateRepository;
    private final SkillRepository skillRepository;

    public List<GetCandidateSkillResponse> getAllCandidateSkills() {
        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findAll();
        if (candidateSkillList.isEmpty()) {
            return new ArrayList<>();
        }

        List<GetCandidateSkillResponse> getCandidateSkillResponseList = new ArrayList<>();

        for (CandidateSkill candidateSkill : candidateSkillList) {
            GetCandidateSkillResponse getCandidateSkillResponse = new GetCandidateSkillResponse();
            getCandidateSkillResponse.setCandidateId(candidateSkill.getCandidate().getId())
                    .setSkill(candidateSkill.getSkill())
                    .setYearsOfExperience(candidateSkill.getYearsOfExperience())
                    .setCreatedAt(candidateSkill.getCreatedAt())
                    .setUpdatedAt(candidateSkill.getUpdatedAt())
                    .setId(candidateSkill.getId())
                    .setCreatedBy(candidateSkill.getCreatedBy())
                    .setUpdatedBy(candidateSkill.getUpdatedBy());
            getCandidateSkillResponseList.add(getCandidateSkillResponse);
        }

        return getCandidateSkillResponseList;
    }

    public CreateCandidateSkillResponse createCandidateSkill(CreateCandidateSkillRequest createCandidateSkillRequest) {

        Optional<Candidate> candidateOp = candidateRepository.findById(createCandidateSkillRequest.getCandidateId());
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + createCandidateSkillRequest.getCandidateId() + ") not found");
        }

        Optional<Skill> skillOp = skillRepository.findById(createCandidateSkillRequest.getSkillId());
        if (skillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.SKILL_NOT_FOUND.name(),
                    "skill with id (" + createCandidateSkillRequest.getSkillId() + ") not found");
        }

        Candidate candidate = candidateOp.get();
        Skill skill = skillOp.get();
        if (candidateSkillRepository.findByCandidateIdAndSkillId(candidate.getId(), skill.getId()).isPresent()) {
            throw new RecruitmentBusinessException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS.name(),
                    "this candidate_skill record is already exist");
        }


        CandidateSkill candidateSkill = new CandidateSkill();
        candidateSkill.setCandidate(candidate)
                .setSkill(skill)
                .setYearsOfExperience(createCandidateSkillRequest.getYearsOfExperience());

        candidateSkillRepository.save(candidateSkill);
        CreateCandidateSkillResponse response = new CreateCandidateSkillResponse();
        response.setId(candidateSkill.getId())
                .setCandidateId(candidateSkill.getCandidate().getId())
                .setSkillId(candidateSkill.getSkill().getId())
                .setYearsOfExperience(candidateSkill.getYearsOfExperience())
                .setCreatedAt(candidateSkill.getCreatedAt())
                .setCreatedBy(candidateSkill.getCreatedBy());

        return response;
    }


    public GetCandidateSkillResponse getCandidateSkillById(Long id) {
        Optional<CandidateSkill> candidateSkillOp = candidateSkillRepository.findById(id);
        if (candidateSkillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_SKILL_NOT_FOUND.name(),
                    "candidate skill with id (" + id + ") not found");
        }
        CandidateSkill candidateSkill = candidateSkillOp.get();
        GetCandidateSkillResponse response = new GetCandidateSkillResponse();
        response.setCandidateId(candidateSkill.getCandidate().getId())
                .setSkill(candidateSkill.getSkill())
                .setYearsOfExperience(candidateSkill.getYearsOfExperience())
                .setId(candidateSkill.getId())
                .setCreatedBy(candidateSkill.getCreatedBy())
                .setCreatedAt(candidateSkill.getCreatedAt())
                .setUpdatedBy(candidateSkill.getUpdatedBy())
                .setUpdatedAt(candidateSkill.getUpdatedAt());
        return response;


    }

    public UpdateCandidateSkillResponse updateCandidateSkill(Long id, UpdateCandidateSkillRequest requset) {
        Optional<Candidate> candidateOp = candidateRepository.findById(requset.getCandidateId());
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + requset.getCandidateId() + ") not found");
        }

        Optional<Skill> skillOp = skillRepository.findById(requset.getSkillId());
        if (skillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.SKILL_NOT_FOUND.name(),
                    "skill with id (" + requset.getSkillId() + ") not found");
        }

        Optional<CandidateSkill> candidateSkillOp = candidateSkillRepository.findById(id);
        if (candidateSkillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_SKILL_NOT_FOUND.name(),
                    "candidate skill with id (" + id + ") not found");
        }

        CandidateSkill candidateSkill = candidateSkillOp.get();
        candidateSkill.setCandidate(candidateOp.get())
                .setSkill(skillOp.get())
                .setYearsOfExperience(requset.getYearsOfExperience());
        candidateSkillRepository.save(candidateSkill);

        UpdateCandidateSkillResponse response = new UpdateCandidateSkillResponse();
        response.setId(candidateSkill.getId())
                .setCandidateId(candidateSkill.getCandidate().getId())
                .setSkillId(candidateSkill.getSkill().getId())
                .setYearsOfExperience(candidateSkill.getYearsOfExperience())
                .setUpdatedAt(candidateSkill.getUpdatedAt())
                .setUpdatedBy(candidateSkill.getUpdatedBy());
        return response;

    }

    public void deleteCandidateSkill(Long id) {
        Optional<CandidateSkill> candidateSkillOp = candidateSkillRepository.findById(id);
        if (candidateSkillOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_SKILL_NOT_FOUND.name(),
                    "candidate skill with id (" + id + ") not found");
        }
        candidateSkillRepository.delete(candidateSkillOp.get());
    }

    public List<GetCandidateSkillResponse> getCandidateSkillByCandidateId(Long id) {
        Optional<Candidate> candidateOp = candidateRepository.findById(id);
        if (candidateOp.isEmpty()) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, ErrorCode.CANDIDATE_NOT_FOUND.name(),
                    "candidate with id (" + id + ") not found");
        }
        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findByCandidateId(id);
        if (candidateSkillList.isEmpty()) {
            return new ArrayList<>();
        }
        List<GetCandidateSkillResponse> getCandidateSkillResponseList = new ArrayList<>();

        for (CandidateSkill candidateSkill : candidateSkillList) {
            GetCandidateSkillResponse getCandidateSkillResponse = new GetCandidateSkillResponse();
            getCandidateSkillResponse
                    .setCandidateId(candidateSkill.getCandidate().getId())
                    .setSkill(candidateSkill.getSkill())
                    .setYearsOfExperience(candidateSkill.getYearsOfExperience())
                    .setCreatedAt(candidateSkill.getCreatedAt())
                    .setUpdatedAt(candidateSkill.getUpdatedAt())
                    .setId(candidateSkill.getId())
                    .setCreatedBy(candidateSkill.getCreatedBy())
                    .setUpdatedBy(candidateSkill.getUpdatedBy());
            getCandidateSkillResponseList.add(getCandidateSkillResponse);
        }

        return getCandidateSkillResponseList;
    }
}
