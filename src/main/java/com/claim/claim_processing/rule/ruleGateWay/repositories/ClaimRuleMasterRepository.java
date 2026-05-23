package com.claim.claim_processing.rule.ruleGateWay.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleMaster;

public interface ClaimRuleMasterRepository extends JpaRepository<ClaimRuleMaster, Long> {
    List<ClaimRuleMaster> findByRuleTypeId(Long ruleTypeId);
}
