package com.Candidate_Service.experience.repository;

import com.Candidate_Service.entity.Education;
import com.Candidate_Service.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByCandidateId(Long candidateId);

    Optional<Experience> findByCandidateIdAndCompanyName(Long id, String companyName);


}
