package com.claim.claim_processing.application.repository.claimDetail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationSummary;

@Repository
public interface ClaimCalculationSummaryRepository extends JpaRepository<ClaimCalculationSummary, Long> {

    Optional<ClaimCalculationSummary> findByClaimDetail_Id(Long claimDetailId);
}
