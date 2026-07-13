package com.claim.claim_processing.integration.contribution.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;

import java.util.List;

@Repository
public interface ContributionBifurcationDetailRepository
        extends JpaRepository<ContributionBifurcationDetail, Long> {

    List<ContributionBifurcationDetail> findByBifId(Long bifId);
    List<ContributionBifurcationDetail> findByCidAndNppfNumberOrderByCreatedAtAsc(String cid, String nppfNumber);

    @Modifying
    @Query("DELETE FROM ContributionBifurcationDetail d WHERE d.bifId = :bifId")
    void deleteByBifId(@Param("bifId") Long bifId);

    List<ContributionBifurcationDetail> findByBatchId(Long batchId);

    boolean existsByCidAndNppfNumberAndBatchId(String cid, String nppfNumber, Long batchId);

    /**
     * Member-level ledger report (D-029): bifurcation detail joined to posted accounting
     * event on BATCH_ID. Returns [ContributionBifurcationDetail, AccountingEvent] pairs.
     */
    @Query(
        value = """
            SELECT d, e FROM ContributionBifurcationDetail d, AccountingEvent e
            WHERE d.batchId = e.batchId
              AND e.eventType = 'CONTRIBUTION_POSTING'
              AND e.status = 'POSTED'
              AND d.cid = :cid
              AND (:accountingYear IS NULL OR e.accountingYear = :accountingYear)
            ORDER BY e.id DESC
            """,
        countQuery = """
            SELECT COUNT(d) FROM ContributionBifurcationDetail d, AccountingEvent e
            WHERE d.batchId = e.batchId
              AND e.eventType = 'CONTRIBUTION_POSTING'
              AND e.status = 'POSTED'
              AND d.cid = :cid
              AND (:accountingYear IS NULL OR e.accountingYear = :accountingYear)
            """)
    Page<Object[]> findPostedRowsForMember(
            @Param("cid")            String cid,
            @Param("accountingYear") String accountingYear,
            Pageable pageable);

    @Query("""
        SELECT d FROM ContributionBifurcationDetail d, AccountingEvent e
        WHERE d.batchId = e.batchId
          AND e.eventType = 'CONTRIBUTION_POSTING'
          AND e.status = 'POSTED'
          AND d.cid = :cid
          AND (:accountingYear IS NULL OR e.accountingYear = :accountingYear)
    """)
    List<ContributionBifurcationDetail> findAllPostedDetailsForMember(
            @Param("cid")            String cid,
            @Param("accountingYear") String accountingYear);
}

