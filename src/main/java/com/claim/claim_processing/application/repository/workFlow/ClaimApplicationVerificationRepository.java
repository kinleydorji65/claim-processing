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
    List<ClaimApplicationVerification> findByStatus_StatusId(Long statusId);
    List<ClaimApplicationVerification> findByClaimedByAndStatus_StatusId(String claimedBy, Long statusId);
    List<ClaimApplicationVerification> findByStatus_StatusIdNotIn(List<Long> statusIds);
    List<ClaimApplicationVerification> findByClaimedBy(String claimedBy);

    boolean existsByClaimApplication_Id(Long claimApplicationId);
    
}
