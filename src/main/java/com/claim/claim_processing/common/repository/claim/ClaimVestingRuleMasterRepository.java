package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimVestingRuleMasterRepository
        extends JpaRepository<ClaimVestingRuleMaster, Long> {

    // -----------------------------
    // FIND BY CODE
    // -----------------------------
    Optional<ClaimVestingRuleMaster> findByRuleCode(String ruleCode);

    // -----------------------------
    // CHECK DUPLICATE CODE
    // -----------------------------
    boolean existsByRuleCode(String ruleCode);

    // -----------------------------
    // FIND ALL ACTIVE
    // -----------------------------
    List<ClaimVestingRuleMaster> findByIsActive(ActivityEnum isActive);

    // -----------------------------
    // FIND BY AGENCY CATEGORY
    // -----------------------------
    List<ClaimVestingRuleMaster> findByCategory_CategoryId(String categoryId);

    // -----------------------------
    // FIND BY REFUND PROCESS
    // -----------------------------
    List<ClaimVestingRuleMaster> findByRefundType_Id(Long refundId);

    // -----------------------------
    // FIND BY RULE TYPE
    // -----------------------------
    List<ClaimVestingRuleMaster> findByRuleType_Id(Long ruleTypeId);
}