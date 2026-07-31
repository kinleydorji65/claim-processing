package com.claim.claim_processing.integration.contribution.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.ContributionBifurcationDetail;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContributionBifurcationDetailRepository
    extends JpaRepository<ContributionBifurcationDetail, Long> {

  List<ContributionBifurcationDetail> findByBifId(Long bifId);

  List<ContributionBifurcationDetail> findByCidAndNppfNumberOrderByCreatedAtAsc(String cid, String nppfNumber);

  List<ContributionBifurcationDetail> findByNppfNumberOrderByCreatedAtAsc(String nppfNumber);

  List<ContributionBifurcationDetail> findByCidAndNppfNumber(String cid, String nppfNumber);

  List<ContributionBifurcationDetail> findByIdIn(List<Long> ids);

  @Modifying
  @Query("DELETE FROM ContributionBifurcationDetail d WHERE d.bifId = :bifId")
  void deleteByBifId(@Param("bifId") Long bifId);

  List<ContributionBifurcationDetail> findByBatchId(Long batchId);

  boolean existsByCidAndNppfNumberAndBatchId(String cid, String nppfNumber, Long batchId);

  // NEW: Get contributions between two dates
  @Query("SELECT d FROM ContributionBifurcationDetail d " +
      "WHERE d.cid = :cid " +
      "AND d.nppfNumber = :nppfNumber " +
      "AND d.createdAt BETWEEN :startDate AND :endDate " +
      "ORDER BY d.createdAt ASC")
  List<ContributionBifurcationDetail> findByCidAndNppfNumberAndCreatedAtBetween(
      @Param("cid") String cid,
      @Param("nppfNumber") String nppfNumber,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  // NEW: Get contributions up to a specific date
  @Query("SELECT d FROM ContributionBifurcationDetail d " +
      "WHERE d.cid = :cid " +
      "AND d.nppfNumber = :nppfNumber " +
      "AND d.createdAt <= :asOfDateTime " +
      "ORDER BY d.createdAt ASC")
  List<ContributionBifurcationDetail> findContributionsUpToDate(
      @Param("cid") String cid,
      @Param("nppfNumber") String nppfNumber,
      @Param("asOfDateTime") LocalDateTime asOfDateTime);

  /**
   * Member-level ledger report (D-029): bifurcation detail joined to posted
   * accounting
   * event on BATCH_ID. Returns [ContributionBifurcationDetail, AccountingEvent]
   * pairs.
   */
  @Query(value = """
      SELECT d, e FROM ContributionBifurcationDetail d, AccountingEvent e
      WHERE d.batchId = e.batchId
        AND e.eventType = 'CONTRIBUTION_POSTING'
        AND e.status = 'POSTED'
        AND d.cid = :cid
        AND (:accountingYear IS NULL OR e.accountingYear = :accountingYear)
      ORDER BY e.id DESC
      """, countQuery = """
      SELECT COUNT(d) FROM ContributionBifurcationDetail d, AccountingEvent e
      WHERE d.batchId = e.batchId
        AND e.eventType = 'CONTRIBUTION_POSTING'
        AND e.status = 'POSTED'
        AND d.cid = :cid
        AND (:accountingYear IS NULL OR e.accountingYear = :accountingYear)
      """)
  Page<Object[]> findPostedRowsForMember(
      @Param("cid") String cid,
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
      @Param("cid") String cid,
      @Param("accountingYear") String accountingYear);

  @Query("""
          SELECT d FROM ContributionBifurcationDetail d
          WHERE d.postingStatus = 'POSTED'
            AND d.nppfNumber = :nppfNumber
            AND YEAR(d.createdAt) = :year
      """)
  List<ContributionBifurcationDetail> findAllPostedDetailsForMemberForYear(
      @Param("nppfNumber") String nppfNumber,
      @Param("year") int year);

  @Query("""
          SELECT d FROM ContributionBifurcationDetail d
          WHERE d.postingStatus = 'POSTED'
            AND d.nppfNumber = :nppfNumber
            AND YEAR(d.createdAt) = :year
          ORDER BY d.createdAt ASC
      """)
  List<ContributionBifurcationDetail> findAllDetailsForMemberForYear(
      @Param("nppfNumber") String nppfNumber,
      @Param("year") int year);
}