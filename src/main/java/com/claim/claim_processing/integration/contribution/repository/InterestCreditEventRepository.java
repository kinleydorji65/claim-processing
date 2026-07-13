package com.claim.claim_processing.integration.contribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.InterestCreditEvent;

import java.util.List;
import java.util.Optional;

/**
 * T-92: Repository for INTEREST_CREDIT_EVENT.
 */
@Repository
public interface InterestCreditEventRepository extends JpaRepository<InterestCreditEvent, Long> {

    /** Lookup by event reference (ICE2025000001). */
    Optional<InterestCreditEvent> findByEventRef(String eventRef);

    /** Lookup by accounting year — uniqueness enforced at DB level. */
    Optional<InterestCreditEvent> findByAccountingYear(String accountingYear);

    /** Lookup by year + status — used in status guards. */
    Optional<InterestCreditEvent> findByAccountingYearAndStatus(
            String accountingYear, String status);

    /**
     * Check whether an active (non-INITIATED) run already exists for the year.
     * Guard in initiate() — prevents duplicate year-end runs.
     */
    @Query("SELECT COUNT(e) > 0 FROM InterestCreditEvent e " +
           "WHERE e.accountingYear = :year " +
           "AND e.status IN ('CALCULATED', 'APPROVED', 'POSTED')")
    boolean existsActiveRunForYear(@Param("year") String accountingYear);

    /** All events ordered newest first — for list endpoint. */
    @Query("SELECT e FROM InterestCreditEvent e ORDER BY e.initiatedAt DESC")
    List<InterestCreditEvent> findAllOrderByInitiatedAtDesc();

    /**
     * Next sequence value for EVENT_REF generation.
     * Uses INTEREST_CREDIT_EVENT_SEQ defined in DDL.
     */
    @Query(value = "SELECT PPFMS_CONTRIBUTION_SERVICE_SCHEMA.INTEREST_CREDIT_EVENT_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
    Long nextSeq();
}

