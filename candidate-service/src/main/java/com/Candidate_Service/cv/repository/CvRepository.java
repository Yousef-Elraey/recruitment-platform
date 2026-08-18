package com.Candidate_Service.cv.repository;

import com.Candidate_Service.entity.Cv;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CvRepository extends JpaRepository<Cv, Long> {
}
