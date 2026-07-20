package com.claim.claim_processing.application.repository.claimDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerEntry;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ClaimLedgerEntryRepository extends JpaRepository<ClaimLedgerEntry, Long> {

    List<ClaimLedgerEntry> findByAccountingEventIdOrderBySeqNoAsc(Long accountingEventId);
}