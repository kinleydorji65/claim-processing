package com.claim.claim_processing.common.repository.unclaimed;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedInterestFreezeRuleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestFreezeRuleRepository
        extends JpaRepository<UnclaimedInterestFreezeRuleMaster, Long> {

    Optional<UnclaimedInterestFreezeRuleMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<UnclaimedInterestFreezeRuleMaster> findByIsActive(ActivityEnum isActive);
}