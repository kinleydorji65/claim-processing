package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;

import java.util.List;

@Repository
public interface ClaimApplicationVerificationRepository extends JpaRepository<ClaimApplicationVerification, Long> {
    List<ClaimApplicationVerification> findAllByClaimApplication_Id(Long claimApplicationId);

    List<ClaimApplicationVerification> findAllByClaimApplication_IdOrderByVerificationLevelAsc(
            Long claimApplicationId
    );

    boolean existsByClaimApplication_Id(Long claimApplicationId);
    
}
