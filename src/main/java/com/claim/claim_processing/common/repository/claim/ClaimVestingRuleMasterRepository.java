package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimVestingRuleMasterRepository extends JpaRepository<ClaimVestingRuleMaster, Long> {

    Optional<ClaimVestingRuleMaster> findByRuleCode(String ruleCode);

    List<ClaimVestingRuleMaster> findByCategory_CategoryId(String categoryId);

    List<ClaimVestingRuleMaster> findByIsActive(ActivityEnum isActive);

    List<ClaimVestingRuleMaster> findByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
            LocalDate from,
            LocalDate to
    );
}