package com.claim.claim_processing.rule.ruleGateWay.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleCondition;


@Repository
public interface ClaimRuleConditionRepository extends JpaRepository<ClaimRuleCondition, Long> {
    Optional<ClaimRuleCondition> findByRule_Id(Long ruleId);

    Boolean existsByRule_IdAndAccumulation_Id(Long ruleId, Long accumulationId);
}
