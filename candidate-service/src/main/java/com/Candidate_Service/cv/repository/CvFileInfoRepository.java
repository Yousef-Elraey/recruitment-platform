package com.Candidate_Service.cv.repository;

import com.Candidate_Service.entity.CvFileInfo;
import com.Candidate_Service.entity.CvStatus;
import com.Candidate_Service.entity.CvVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CvFileInfoRepository extends JpaRepository<CvFileInfo, Long> {
    List<CvFileInfo> findAllByCandidateId(Long candidateId);

    Optional<CvFileInfo> findByCvId(Long cvId);

    Optional<CvFileInfo> findByCandidateIdAndVersion(Long candidateId, CvVersion cvVersion);

    Page<CvFileInfo> findByStatus(CvStatus status, Pageable pageable);

    @Query("""
    SELECT cvfi
    FROM CvFileInfo cvfi
    WHERE cvfi.status IN :statuses
      AND cvfi.retryCounter < :maxRetries
    ORDER BY cvfi.uploadedAt ASC
    """)
    Page<CvFileInfo> findCvsForParsing(
            @Param("statuses") Collection<CvStatus> statuses,
            @Param("maxRetries") int maxRetries,
            Pageable pageable
    );
}