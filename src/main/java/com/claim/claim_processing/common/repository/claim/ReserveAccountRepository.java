package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ReserveAccountMaster;
import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveAccountRepository extends JpaRepository<ReserveAccountMaster, Long> {

    // -------------------------------
    // FIND BY CODE
    // -------------------------------
    Optional<ReserveAccountMaster> findByReserveAccountCode(String reserveAccountCode);

    // -------------------------------
    // FIND BY ACCOUNT TYPE (FK)
    // -------------------------------
    List<ReserveAccountMaster> findByAccountType(AccountTypeMaster accountType);

    // -------------------------------
    // FIND BY ACCOUNT TYPE ID (more practical for service layer)
    // -------------------------------
    List<ReserveAccountMaster> findByAccountType_Id(Long accountTypeId);

    // -------------------------------
    // FIND BY SCHEME TYPE (FK)
    // -------------------------------
    List<ReserveAccountMaster> findBySchemeType(SchemeMaster schemeType);

    // -------------------------------
    // FIND BY SCHEME TYPE ID (preferred)
    // -------------------------------
    List<ReserveAccountMaster> findBySchemeType_Id(Long schemeTypeId);

    // -------------------------------
    // FIND ACTIVE ONLY
    // -------------------------------
    List<ReserveAccountMaster> findByIsActive(String isActive);
}