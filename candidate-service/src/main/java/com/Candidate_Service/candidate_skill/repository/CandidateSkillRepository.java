package com.Candidate_Service.candidate_skill.repository;

import com.Candidate_Service.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, Long> {

    Optional<CandidateSkill> findByCandidateIdAndSkillId(Long candidateId, Long skillId);

    List<CandidateSkill> findByCandidateId(Long candidateId);
}
