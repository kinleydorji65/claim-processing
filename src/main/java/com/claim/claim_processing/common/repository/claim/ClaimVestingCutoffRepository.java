package com.claim.claim_processing.common.repository.claim;

import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.claim.ClaimVestingCutoffMaster;

@Repository
public interface ClaimVestingCutoffRepository extends JpaRepository<ClaimVestingCutoffMaster, Long> {
    List<ClaimVestingCutoffMaster> findByIsActive(ActivityEnum isActive);

    boolean existsByRuleCode(String ruleCode);
}
