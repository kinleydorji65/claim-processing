package com.claim.claim_processing.application.repository.calculation;

import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimApplicationCalculationSummaryRepository
        extends JpaRepository<ClaimApplicationCalculationSummary, Long> {

    Optional<ClaimApplicationCalculationSummary> findByClaimApplication_Id(Long claimApplicationId);
}
