package com.claim.claim_processing.application.repository.calculation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;

@Repository
public interface ClaimApplicationCalculationComponentRepository extends JpaRepository<ClaimApplicationCalculationComponent, Long> {
    List<ClaimApplicationCalculationComponent> findByRuleEvaluation_Id(Long ruleEvaluationId);

    // All calculation components for a claim, across every rule evaluation under its calculation
    // summary — used to forward the contribution-component breakdown to pension-service on approval.
    List<ClaimApplicationCalculationComponent> findByRuleEvaluation_CalculationSummary_ClaimApplication_Id(
            Long claimApplicationId);
}
