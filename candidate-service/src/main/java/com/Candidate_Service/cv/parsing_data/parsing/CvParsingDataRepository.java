package com.Candidate_Service.cv.parsing_data.parsing;

import com.Candidate_Service.entity.CvParsingData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CvParsingDataRepository extends JpaRepository<CvParsingData, Long> {
    Optional<CvParsingData> findByCvId(Long cvId);
}