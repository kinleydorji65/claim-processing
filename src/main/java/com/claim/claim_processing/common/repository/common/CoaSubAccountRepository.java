package com.claim.claim_processing.common.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.common.CoaSubAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoaSubAccountRepository extends JpaRepository<CoaSubAccount, Long> {

    /**
     * Find sub account by sub account code
     */
    Optional<CoaSubAccount> findBySubAccountCode(String subAccountCode);

    /**
     * Find sub account by sub account code and active status
     */
    Optional<CoaSubAccount> findBySubAccountCodeAndIsActive(String subAccountCode, String isActive);

    /**
     * Find all sub accounts for a main account
     */
    List<CoaSubAccount> findByMainAccountId(Long mainAccountId);

    /**
     * Find all active sub accounts for a main account
     */
    List<CoaSubAccount> findByMainAccountIdAndIsActive(Long mainAccountId, String isActive);

    /**
     * Find all sub accounts by balance nature
     */
    List<CoaSubAccount> findByBalanceNature(String balanceNature);

    /**
     * Find all active sub accounts by balance nature
     */
    List<CoaSubAccount> findByBalanceNatureAndIsActive(String balanceNature, String isActive);

    /**
     * Check if sub account code exists
     */
    boolean existsBySubAccountCode(String subAccountCode);

    /**
     * Find all sub accounts by main account ID and balance nature
     */
    List<CoaSubAccount> findByMainAccountIdAndBalanceNature(
        Long mainAccountId, String balanceNature
    );
}
