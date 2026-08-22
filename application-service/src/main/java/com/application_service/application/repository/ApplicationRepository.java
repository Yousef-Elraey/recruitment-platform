package com.application_service.application.repository;

import com.application_service.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByCandidateIdAndJobId(Long candidateId, Long jobId);
}
