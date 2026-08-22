package com.application_service.candidate_face.repository;

import com.application_service.entity.CandidateFace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateFaceRepository extends JpaRepository<CandidateFace, Long> {

}
