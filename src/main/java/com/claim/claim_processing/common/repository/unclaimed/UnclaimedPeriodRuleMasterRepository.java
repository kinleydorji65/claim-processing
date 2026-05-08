package com.claim.claim_processing.common.repository.unclaimed;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedPeriodRuleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnclaimedPeriodRuleMasterRepository
        extends JpaRepository<UnclaimedPeriodRuleMaster, Long> {

    Optional<UnclaimedPeriodRuleMaster> findByRuleName(String ruleName);

    boolean existsByRuleName(String ruleName);

    List<UnclaimedPeriodRuleMaster> findByIsActive(ActivityEnum isActive);

    Optional<UnclaimedPeriodRuleMaster> findByPeriodValue(Integer periodValue);
}