package com.claim.claim_processing.application.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimSpecialCaseApplicationRepository extends JpaRepository<ClaimSpecialCaseApplication, Long> {

    // -------------------------------
    // FIND BY CLAIM
    // -------------------------------

    /**
     * Find all special case applications for a claim
     */
    Optional<ClaimSpecialCaseApplication> findByClaimApplicationId(Long claimApplicationId);

    /**
     * Check if claim has any special case application
     */
    boolean existsByClaimApplicationId(Long claimApplicationId);
}
