package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimApplicationApprovalRepository extends JpaRepository<ClaimApplicationApproval, Long>  {
    Optional<ClaimApplicationApproval> findByClaimApplication_Id(Long claimApplicationId);
    Optional<ClaimApplicationApproval> findByClaimApplication_ApplicationNumber(String applicationNumber);
    List<ClaimApplicationApproval> findByApprovalStatus_StatusId(Long statusId);
    List<ClaimApplicationApproval> findByApprovalStatus_StatusIdNotIn(List<Long> statusIds);
    List<ClaimApplicationApproval> findByApprovalStatus_StatusIdAndClaimedBy(Long statusId, String claimedBy);

    Optional<ClaimApplicationApproval> findTopByClaimApplication_IdOrderByApprovedAtDesc(
            Long claimApplicationId
    );

    boolean existsByClaimApplication_Id(Long claimApplicationId);
    
}
