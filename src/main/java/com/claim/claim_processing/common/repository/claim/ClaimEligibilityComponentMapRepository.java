package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimEligibilityComponentMapRepository
        extends JpaRepository<ClaimEligibilityComponentMap, Long> {

    // -------------------------------
    // FIND BY RULE ID
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByRuleId(Long ruleId);

    // -------------------------------
    // FIND BY BENEFIT COMPONENT TYPE ID
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByBenefitComponentTypeId(Long benefitComponentTypeId);

    // -------------------------------
    // FIND ACTIVE RECORDS ONLY
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByIsActive(String isActive);

    // -------------------------------
    // CHECK EXISTENCE (DUPLICATE PREVENTION)
    // -------------------------------
    boolean existsByRuleIdAndBenefitComponentTypeId(Long ruleId, Long benefitComponentTypeId);
}