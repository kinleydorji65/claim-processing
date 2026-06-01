package com.claim.claim_processing.rule.ruleGateWay.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.rule.SubClaimCondition;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubClaimConditionRepository
        extends JpaRepository<SubClaimCondition, Long> {

    boolean existsByConditionCodeIgnoreCase(String conditionCode);

    boolean existsByConditionCodeIgnoreCaseAndIdNot(String conditionCode, Long id);

    Optional<SubClaimCondition> findByConditionCodeIgnoreCase(String conditionCode);
    List<SubClaimCondition> findBySubClaimMapping_SubClaimCode(String subClaimMappingCode);
}
