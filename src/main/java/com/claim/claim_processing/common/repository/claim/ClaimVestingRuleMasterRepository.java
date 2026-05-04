package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.*;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimVestingRuleMasterRepository
        extends JpaRepository<ClaimVestingRuleMaster, Long> {

    Optional<ClaimVestingRuleMaster> findByRuleCode(String ruleCode);

    List<ClaimVestingRuleMaster> findByCategory(AgencyCategory category);

    List<ClaimVestingRuleMaster> findByCutoff(ClaimVestingCutoffMaster cutoff);

    List<ClaimVestingRuleMaster> findByRefund(VestingRefundType refund);

    List<ClaimVestingRuleMaster> findByRuleType(RuleTypeMaster ruleType);

    List<ClaimVestingRuleMaster> findByIsActive(ActivityEnum isActive);
}