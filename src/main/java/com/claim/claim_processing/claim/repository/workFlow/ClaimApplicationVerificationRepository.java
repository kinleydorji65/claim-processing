package com.claim.claim_processing.claim.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.claim.entity.workFlow.ClaimApplicationVerification;

@Repository
public interface ClaimApplicationVerificationRepository extends JpaRepository<ClaimApplicationVerification, Long> {
    
}
