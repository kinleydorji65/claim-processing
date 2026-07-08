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
    List<ClaimSpecialCaseApplication> findByCaseType(String caseType);

    /**
     * Find all special case applications by case type and status
     */
    // -------------------------------
    // FIND BY MEMBER
    // -------------------------------

    /**
     * Find all special case applications by member NPPF number
     */

    /**
     * Find all special case applications by identity number
     */

    /**
     * Find all special case applications by member code
     */
    // -------------------------------
    // FIND BY RESERVE ACCOUNT
    // -------------------------------

    /**
     * Find all special case applications by reserve account
     */
    List<ClaimSpecialCaseApplication> findByReserveAccountId(Long reserveAccountId);

    // -------------------------------
    // FIND BY AGENCY
    // -------------------------------
    /**
     * Find all special case applications by agency code
     */
    // -------------------------------
    // COMPLEX QUERIES
    // -------------------------------

    /**
     * Get total requested amount by status
     */

 
    /**
     * Find special case applications by claim application ID and case type
     */
    List<ClaimSpecialCaseApplication> findByClaimApplicationIdAndCaseType(
            Long claimApplicationId, String caseType);
}
