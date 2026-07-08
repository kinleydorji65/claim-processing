package com.claim.claim_processing.integration.contribution.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.MemberInterestRecord;

import java.util.List;
import java.util.Optional;

/**
 * T-88: Repository for MEMBER_INTEREST_RECORD.
 */
@Repository
public interface MemberInterestRecordRepository extends JpaRepository<MemberInterestRecord, Long> {

    /**
     * Unique key lookup — used for OPENING_BALANCE derivation and upsert guard.
     */
    Optional<MemberInterestRecord> findByCidAndNppfNumberAndAccountingYearAndComponentCode(
            String cid, String nppfNumber, String accountingYear, String componentCode);

    /**
     * All records for a member across all years — for member balance history.
     */
    List<MemberInterestRecord> findByCidAndNppfNumberOrderByAccountingYearDesc(
            String cid, String nppfNumber);
    List<MemberInterestRecord> findByNppfNumberOrderByAccountingYearDesc(
            String nppfNumber);

    /**
     * All records for a member in a specific year — for member balance view.
     */
    List<MemberInterestRecord> findByCidAndNppfNumberAndAccountingYear(
            String cid, String nppfNumber, String accountingYear);

    /**
     * All records for an agency in a year — bulk used by calculation engine and reports.
     */
    List<MemberInterestRecord> findByAgencyCodeAndAccountingYear(
            String agencyCode, String accountingYear);

    /**
     * All records for a year in a given status — used by post() to flip CALCULATED→CREDITED.
     */
    List<MemberInterestRecord> findByAccountingYearAndStatus(
            String accountingYear, String status);

    /**
     * All records for a year — used for summary aggregation after calculate().
     */
    List<MemberInterestRecord> findByAccountingYear(String accountingYear);

    /**
     * Total interest for the year — used to populate ICE.TOTAL_INTEREST.
     */
    @Query("SELECT COALESCE(SUM(m.totalInterest), 0) FROM MemberInterestRecord m " +
           "WHERE m.accountingYear = :year")
    java.math.BigDecimal sumTotalInterestByYear(@Param("year") String accountingYear);

    /**
     * Count distinct member+component rows for the year — ICE.TOTAL_MEMBERS.
     */
    @Query("SELECT COUNT(m) FROM MemberInterestRecord m WHERE m.accountingYear = :year")
    long countByAccountingYear(@Param("year") String accountingYear);

    /**
     * Bulk status flip: CALCULATED → CREDITED after posting.
     *
     * KF-023: {@code clearAutomatically = true} is required here. {@code post()} loads every
     * MemberInterestRecord for the year (via {@code findByAccountingYear}) into the persistence
     * context BEFORE calling this bulk update, then {@code MemberBalanceSnapshotServiceImpl
     * .upsertForInterestEvent()} re-queries the same rows moments later in the SAME transaction.
     * A bulk {@code @Modifying} UPDATE bypasses the ORM entirely (writes straight to the DB), so
     * without clearing the L1 cache, that later query returns the SAME stale managed entity
     * instances still showing {@code status=CALCULATED} — the snapshot upsert's CREDITED/ADJUSTED
     * filter then matches nothing, silently upserting zero members.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE MemberInterestRecord m SET m.status = 'CREDITED', m.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE m.accountingYear = :year AND m.status = 'CALCULATED'")
    int markCreditedForYear(@Param("year") String accountingYear);

    /**
     * Aggregate interest by component for a year — used in summary DTO.
     * Returns Object[] rows: [componentCode, SUM(totalInterest)]
     */
    @Query("SELECT m.componentCode, SUM(m.totalInterest) FROM MemberInterestRecord m " +
           "WHERE m.accountingYear = :year " +
           "GROUP BY m.componentCode")
    List<Object[]> sumInterestByComponent(@Param("year") String accountingYear);

    /**
     * Aggregate interest by agency category for a year — used in summary DTO.
     * Returns Object[] rows: [agencyCategoryId, SUM(totalInterest)]
     */
    @Query("SELECT m.agencyCategoryId, SUM(m.totalInterest) FROM MemberInterestRecord m " +
           "WHERE m.accountingYear = :year " +
           "GROUP BY m.agencyCategoryId")
    List<Object[]> sumInterestByCategory(@Param("year") String accountingYear);
}
