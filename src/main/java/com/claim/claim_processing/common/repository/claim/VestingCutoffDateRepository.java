package com.claim.claim_processing.common.repository.claim;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.claim.ClaimVestingCutoffMaster;

@Repository
public interface VestingCutoffDateRepository extends JpaRepository<ClaimVestingCutoffMaster, Long> {
    Optional<ClaimVestingCutoffMaster> findByRuleCodeAndIsActive(String ruleCode, String isActive);
}
