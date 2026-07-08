package com.claim.claim_processing.integration.contribution.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.integration.contribution.entity.MemberBalanceSnapshot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberBalanceSnapshotRepository extends JpaRepository<MemberBalanceSnapshot, Long> {

    Optional<MemberBalanceSnapshot> findByCidAndNppfNumberAndAccountingYear(
            String cid, String nppfNumber, String accountingYear);

    List<MemberBalanceSnapshot> findByNppfNumberOrderByAccountingYearDesc(String nppfNumber);
    List<MemberBalanceSnapshot> findByCidAndNppfNumberOrderByAccountingYearDesc(String cid, String nppfNumber);

    // Lifetime totals — single-row projection across all years
    @Query("""
            SELECT
              SUM(s.pfEc),           SUM(s.pfMc),      SUM(s.pensionEc),
              SUM(s.gc),             SUM(s.vc),
              SUM(s.totalContributions),
              SUM(s.interestEc),     SUM(s.interestMc), SUM(s.interestPension),
              SUM(s.interestGc),     SUM(s.interestVc),
              SUM(s.totalInterest),  SUM(s.totalBalance)
            FROM MemberBalanceSnapshot s
            WHERE s.cid = :cid AND s.nppfNumber = :nppfNumber
            """)
    Object[] lifetimeTotals(@Param("cid") String cid, @Param("nppfNumber") String nppfNumber);

    // Lifetime contributions only (no interest)
    @Query("""
            SELECT SUM(s.totalContributions)
            FROM MemberBalanceSnapshot s
            WHERE s.cid = :cid AND s.nppfNumber = :nppfNumber
            """)
    BigDecimal lifetimeContributions(@Param("cid") String cid, @Param("nppfNumber") String nppfNumber);
}

