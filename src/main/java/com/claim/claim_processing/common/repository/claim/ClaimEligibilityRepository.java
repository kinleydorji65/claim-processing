package com.claim.claim_processing.common.repository.claim;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimEligibilityRepository extends JpaRepository<ClaimEligibilityMaster, Long> {

    Optional<ClaimEligibilityMaster> findByRuleCode(String ruleCode);

    boolean existsByRuleCode(String ruleCode);

    List<ClaimEligibilityMaster> findByIsActiveOrderByRuleNameAsc(ActivityEnum isActive);
    List<ClaimEligibilityMaster> findByIsActive(ActivityEnum isActive);
    List<ClaimEligibilityMaster> findByClaimCircumstance_Id(Long claimCircumstanceId);
    List<ClaimEligibilityMaster> findBySchemeType_Id(Long schemeTypeId);
    List<ClaimEligibilityMaster> findByRuleType_Id(Long ruleTypeId);

}

