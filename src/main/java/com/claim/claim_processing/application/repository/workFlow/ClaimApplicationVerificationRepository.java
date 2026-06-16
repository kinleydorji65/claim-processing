package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimApplicationVerificationRepository extends JpaRepository<ClaimApplicationVerification, Long> {
    Optional<ClaimApplicationVerification> findByClaimApplication_Id(Long claimApplicationId);
    Optional<ClaimApplicationVerification> findByClaimApplication_ApplicationNumber(String applicationNumber);
    List<ClaimApplicationVerification> findByClaimedByAndStatus_StatusId(String claimedBy, Long statusId);

    boolean existsByClaimApplication_Id(Long claimApplicationId);
    
}
