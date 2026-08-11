package com.claim.claim_processing.integration.contribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationHeader;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContributionBifurcationHeaderRepository
        extends JpaRepository<ContributionBifurcationHeader, Long> {

    Optional<ContributionBifurcationHeader> findByBatchId(Long batchId);

    /**
     * Direct JPQL delete by PK — avoids Hibernate entity lifecycle and
     * StaleObjectStateException when the row was already deleted by a
     * concurrent or previous scheduler run.
     */
    @Modifying
    @Query("DELETE FROM ContributionBifurcationHeader h WHERE h.bifId = :bifId")
    void deleteByBifId(@Param("bifId") Long bifId);

    boolean existsByBatchId(Long batchId);

    List<ContributionBifurcationHeader> findByStatus(String status);

    List<ContributionBifurcationHeader> findByStatusIn(List<String> statuses);

    /**
     * Workstream R — postable candidates (GET /posting/candidates).
     * Bifurcation headers with status BIFURCATED or BIFURCATION_WARNING whose
     * batch has NO active (non-REVERSED) CONTRIBUTION_POSTING accounting event.
     * Reversed-then-rebifurcated batches qualify again, since their prior posting
     * event is REVERSED.
     */
    @Query("""
        SELECT h FROM ContributionBifurcationHeader h
        WHERE h.status IN ('BIFURCATED', 'BIFURCATION_WARNING')
          AND NOT EXISTS (
              SELECT 1 FROM AccountingEvent e
              WHERE e.batchId   = h.batchId
                AND e.eventType = 'CONTRIBUTION_POSTING'
                AND e.status   <> 'REVERSED'
          )
        ORDER BY h.batchId DESC
    """)
    List<ContributionBifurcationHeader> findPostableCandidates();
}

