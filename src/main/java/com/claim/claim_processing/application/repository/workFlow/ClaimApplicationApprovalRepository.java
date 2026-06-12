package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;

import java.util.Optional;

@Repository
public interface ClaimApplicationApprovalRepository extends JpaRepository<ClaimApplicationApproval, Long>  {
    Optional<ClaimApplicationApproval> findByClaimApplication_Id(Long claimApplicationId);

    Optional<ClaimApplicationApproval> findTopByClaimApplication_IdOrderByApprovedAtDesc(
            Long claimApplicationId
    );

    boolean existsByClaimApplication_Id(Long claimApplicationId);
    
}
