package com.claim.claim_processing.application.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClaimSpecialCaseApplicationRepository extends JpaRepository<ClaimSpecialCaseApplication, Long> {

    // -------------------------------
    // FIND BY CLAIM
    // -------------------------------

    /**
     * Find all special case applications for a claim
     */
    List<ClaimSpecialCaseApplication> findByClaimApplicationId(Long claimApplicationId);

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
    List<ClaimSpecialCaseApplication> findByNppfNumber(String nppfNumber);

    /**
     * Find all special case applications by identity number
     */
    List<ClaimSpecialCaseApplication> findByIdentityNumber(String identityNumber);

    /**
     * Find all special case applications by member code
     */
    List<ClaimSpecialCaseApplication> findByMemberCode(String memberCode);

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
    List<ClaimSpecialCaseApplication> findByAgencyCode(String agencyCode);

    // -------------------------------
    // COMPLEX QUERIES
    // -------------------------------


    /**
     * Find all special case applications by date range
     */
    @Query("SELECT s FROM ClaimSpecialCaseApplication s WHERE s.requestDate BETWEEN :startDate AND :endDate")
    List<ClaimSpecialCaseApplication> findByRequestDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get total requested amount by status
     */

 
    /**
     * Find special case applications by claim application ID and case type
     */
    List<ClaimSpecialCaseApplication> findByClaimApplicationIdAndCaseType(
            Long claimApplicationId, String caseType);
}
