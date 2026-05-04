package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimLapsedRefundRepository extends JpaRepository<ClaimLapsedRefundMaster, Long> {

    // -------------------------------
    // FIND BY RULE CODE
    // -------------------------------
    Optional<ClaimLapsedRefundMaster> findByRuleCode(String ruleCode);

    // -------------------------------
    // FIND ACTIVE RULES
    // -------------------------------
    List<ClaimLapsedRefundMaster> findByIsActive(ActivityEnum isActive);
    // -------------------------------
    // VALID EFFECTIVE RULES (date range)
    // -------------------------------
    List<ClaimLapsedRefundMaster> findByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    );

    // -------------------------------
    // FIND BY SCHEME TYPE
    // -------------------------------
    List<ClaimLapsedRefundMaster> findBySchemeType_Id(Long schemeTypeId);
    // -------------------------------
    // FIND BY CLAIM CIRCUMSTANCE
    // -------------------------------
    List<ClaimLapsedRefundMaster> findByClaimCircumstance_Id(Long claimCircumstanceId);

    List<ClaimLapsedRefundMaster> findByRuleType_Id(Long ruleTypeId);
}