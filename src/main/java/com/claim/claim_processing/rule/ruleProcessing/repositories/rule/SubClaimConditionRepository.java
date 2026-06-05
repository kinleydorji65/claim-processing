package com.claim.claim_processing.rule.ruleProcessing.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimCondition;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubClaimConditionRepository
        extends JpaRepository<SubClaimCondition, Long> {

    boolean existsByConditionCode(String conditionCode);

    boolean existsByConditionCodeAndIdNot(String conditionCode, Long id);

    Optional<SubClaimCondition> findByConditionCode(String conditionCode);
    List<SubClaimCondition> findBySubClaimMapping_SubClaimCode(String subClaimMappingCode);
}
