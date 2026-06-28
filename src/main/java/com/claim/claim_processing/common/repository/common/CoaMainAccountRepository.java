package com.claim.claim_processing.common.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.common.CoaMainAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoaMainAccountRepository extends JpaRepository<CoaMainAccount, Long> {

    /**
     * Find main account by account code
     */
    Optional<CoaMainAccount> findByAccountCode(String accountCode);

    /**
     * Find main account by account code and active status
     */
    Optional<CoaMainAccount> findByAccountCodeAndIsActive(String accountCode, String isActive);

    /**
     * Find all active main accounts
     */
    List<CoaMainAccount> findByIsActive(String isActive);

    /**
     * Find all main accounts by account type
     */
    List<CoaMainAccount> findByAccountType(String accountType);

    /**
     * Find all main accounts by account type and active status
     */
    List<CoaMainAccount> findByAccountTypeAndIsActive(String accountType, String isActive);

    /**
     * Find all main accounts by normal DRCR
     */
    List<CoaMainAccount> findByNormalDrcr(String normalDrcr);

    /**
     * Check if account code exists
     */
    boolean existsByAccountCode(String accountCode);
}