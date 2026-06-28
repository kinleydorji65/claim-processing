package com.claim.claim_processing.application.repository.calculation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;

@Repository
public interface ClaimApplicationCalculationComponentRepository extends JpaRepository<ClaimApplicationCalculationComponent, Long> {
    List<ClaimApplicationCalculationComponent> findByRuleEvaluation_Id(Long ruleEvaluationId);
}
