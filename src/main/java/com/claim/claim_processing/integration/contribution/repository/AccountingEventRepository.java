package com.claim.claim_processing.integration.contribution.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.AccountingEvent;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountingEventRepository extends JpaRepository<AccountingEvent, Long> {

    Optional<AccountingEvent> findByEventRef(String eventRef);

    // Guard G4 — one active (non-REVERSED) event per (BATCH_ID, EVENT_TYPE)
    Optional<AccountingEvent> findFirstByBatchIdAndEventTypeAndStatusNot(
            Long batchId, String eventType, String status);

    // Guard G3 — year-close state = existence of a YEAR_END_CLOSE event for the year
    boolean existsByEventTypeAndAccountingYearAndStatusNot(
            String eventType, String accountingYear, String status);

    Optional<AccountingEvent> findFirstByEventTypeAndAccountingYearAndStatusNot(
            String eventType, String accountingYear, String status);

    List<AccountingEvent> findByAccountingYearAndStatusNot(String accountingYear, String status);

    // Event browser — all filters optional
    @Query("""
        SELECT e FROM AccountingEvent e
        WHERE (:batchId    IS NULL OR e.batchId    = :batchId)
          AND (:eventType  IS NULL OR e.eventType  = :eventType)
          AND (:agencyCode IS NULL OR e.agencyCode = :agencyCode)
          AND (:monthName  IS NULL OR UPPER(e.monthName) = UPPER(:monthName))
          AND (:year       IS NULL OR e.year       = :year)
        ORDER BY e.id DESC
    """)
    Page<AccountingEvent> searchEvents(
            @Param("batchId")    Long batchId,
            @Param("eventType")  String eventType,
            @Param("agencyCode") String agencyCode,
            @Param("monthName")  String monthName,
            @Param("year")       String year,
            Pageable pageable);

    // Report basis — active (non-REVERSED) events, all filters optional
    @Query("""
        SELECT e FROM AccountingEvent e
        WHERE e.status <> 'REVERSED'
          AND (:eventType      IS NULL OR e.eventType          = :eventType)
          AND (:accountingYear IS NULL OR e.accountingYear     = :accountingYear)
          AND (:schemeCategory IS NULL OR e.agencyCategoryId = :schemeCategory)
          AND (:agencyCode     IS NULL OR e.agencyCode         = :agencyCode)
          AND (:monthName      IS NULL OR UPPER(e.monthName)   = UPPER(:monthName))
          AND (:year           IS NULL OR e.year               = :year)
        ORDER BY e.id DESC
    """)
    List<AccountingEvent> findActiveForReports(
            @Param("eventType")      String eventType,
            @Param("accountingYear") String accountingYear,
            @Param("schemeCategory") String schemeCategory,
            @Param("agencyCode")     String agencyCode,
            @Param("monthName")      String monthName,
            @Param("year")           String year);

    // D-036 doc number sequence — NGN{calYear}{6-digit seq}
    @Query(value = "SELECT PPFMS_CONTRIBUTION_SERVICE_SCHEMA.LEDGER_DOC_NO_SEQ.NEXTVAL FROM DUAL",
            nativeQuery = true)
    Long nextDocNoSequence();

    /**
     * T-99: All POSTED events of a given type in an accounting year.
     * Used by InterestCalculationServiceImpl.calculate() to discover
     * which batches contributed in the year.
     */
    List<AccountingEvent> findByAccountingYearAndEventTypeAndStatus(
            String accountingYear, String eventType, String status);

    // ── Workstream R: posting query support ─────────────────────────────────

    /** All events for a batch, ordered (original posting + reversal chain). */
    List<AccountingEvent> findByBatchIdOrderByIdAsc(Long batchId);

    /** REVERSAL events that point back to a given original event id. */
    List<AccountingEvent> findByReversalOfEventId(Long reversalOfEventId);

    /**
     * Posting list (GET /posting) — one row per CONTRIBUTION_POSTING event
     * (status POSTED or REVERSED). All filters optional. Date range is on POSTED_AT.
     */
    @Query("""
        SELECT e FROM AccountingEvent e
        WHERE e.eventType = 'CONTRIBUTION_POSTING'
          AND (:status     IS NULL OR e.status     = :status)
          AND (:agencyCode IS NULL OR e.agencyCode = :agencyCode)
          AND (:monthName  IS NULL OR UPPER(e.monthName) = UPPER(:monthName))
          AND (:year       IS NULL OR e.year       = :year)
          AND (:fromDate   IS NULL OR e.postedAt  >= :fromDate)
          AND (:toDate     IS NULL OR e.postedAt  <= :toDate)
        ORDER BY e.id DESC
    """)
    Page<AccountingEvent> searchPostings(
            @Param("status")     String status,
            @Param("agencyCode") String agencyCode,
            @Param("monthName")  String monthName,
            @Param("year")       String year,
            @Param("fromDate")   java.time.LocalDateTime fromDate,
            @Param("toDate")     java.time.LocalDateTime toDate,
            Pageable pageable);
}

