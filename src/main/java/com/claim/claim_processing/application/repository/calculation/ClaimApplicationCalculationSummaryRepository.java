package com.claim.claim_processing.application.repository.calculation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;

@Repository
public interface ClaimApplicationCalculationSummaryRepository extends JpaRepository<ClaimApplicationCalculationSummary, Long> {
    
}
