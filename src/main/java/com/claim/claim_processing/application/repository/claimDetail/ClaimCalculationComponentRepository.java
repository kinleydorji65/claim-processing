package com.claim.claim_processing.application.repository.claimDetail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationComponent;

@Repository
public interface ClaimCalculationComponentRepository extends JpaRepository<ClaimCalculationComponent, Long> {
    List<ClaimCalculationComponent> findByRuleEvaluation_Id(Long ruleEvaluationId);
}
