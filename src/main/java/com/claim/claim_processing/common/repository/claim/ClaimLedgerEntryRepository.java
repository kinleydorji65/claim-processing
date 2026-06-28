package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ClaimLedgerEntryRepository extends JpaRepository<ClaimLedgerEntry, Long> {

    List<ClaimLedgerEntry> findByAccountingEventIdOrderBySeqNoAsc(Long accountingEventId);
}