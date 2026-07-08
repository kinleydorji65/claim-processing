package com.claim.claim_processing.application.repository.claimDetail;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCase;

public interface SpecialCaseRepository extends JpaRepository<ClaimSpecialCase, Long> {
    
}
