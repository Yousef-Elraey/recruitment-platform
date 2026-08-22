package com.Candidate_Service.education.repository;

import com.Candidate_Service.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByCandidateId(Long id);

    Optional<Education> findByCandidateIdAndInstitution(Long id, String institution);

}
