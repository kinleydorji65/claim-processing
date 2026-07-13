package com.claim.claim_processing.common.repository.others;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.others.AccountingInterestMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountingInterestMasterRepository extends JpaRepository<AccountingInterestMaster, Long> {
    Optional<AccountingInterestMaster> findByFinancialYear(String financialYear);
    List<AccountingInterestMaster> findByIsActive(String isActive);
}
