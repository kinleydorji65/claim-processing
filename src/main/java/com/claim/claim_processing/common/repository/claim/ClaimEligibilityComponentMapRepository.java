package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimEligibilityComponentMapRepository
        extends JpaRepository<ClaimEligibilityComponentMap, Long> {

    // -------------------------------
    // FIND BY RULE (using relationship)
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByRule_Id(Long ruleId);
    
    List<ClaimEligibilityComponentMap> findByRule_IdAndIsActive(Long ruleId, String isActive);

    // -------------------------------
    // FIND BY CATEGORY MAP (using relationship)
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByClaimEligibilityCategoryMap_Id(Long categoryMapId);
    
    List<ClaimEligibilityComponentMap> findByClaimEligibilityCategoryMap_IdAndIsActive(Long categoryMapId, String isActive);
    
    // -------------------------------
    // FIND BY RULE AND CATEGORY MAP (using relationships)
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByRule_IdAndClaimEligibilityCategoryMap_Id(Long ruleId, Long categoryMapId);
    
    List<ClaimEligibilityComponentMap> findByRule_IdAndClaimEligibilityCategoryMap_IdAndIsActive(
        Long ruleId, Long categoryMapId, ActivityEnum isActive);

    // -------------------------------
    // FIND BY BENEFIT COMPONENT TYPE ID (using relationship)
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByBenefitComponentType_Id(Long benefitComponentTypeId);
    
    List<ClaimEligibilityComponentMap> findByBenefitComponentType_IdAndIsActive(Long benefitComponentTypeId, String isActive);

    // -------------------------------
    // FIND ACTIVE RECORDS ONLY
    // -------------------------------
    List<ClaimEligibilityComponentMap> findByIsActive(String isActive);

    // -------------------------------
    // DUPLICATE CHECK
    // -------------------------------
    boolean existsByRule_IdAndBenefitComponentType_Id(Long ruleId, Long benefitComponentTypeId);
    
    boolean existsByRule_IdAndClaimEligibilityCategoryMap_IdAndBenefitComponentType_Id(
        Long ruleId, Long categoryMapId, Long benefitComponentTypeId);

    // -------------------------------
    // DELETE BY RULE ID
    // -------------------------------
    void deleteByRule_Id(Long ruleId);
    
    // -------------------------------
    // CUSTOM QUERIES
    // -------------------------------
    @Query("SELECT c FROM ClaimEligibilityComponentMap c WHERE c.rule.id = :ruleId AND c.isActive = 'Y'")
    List<ClaimEligibilityComponentMap> findActiveByRuleId(@Param("ruleId") Long ruleId);
    
    @Query("SELECT c FROM ClaimEligibilityComponentMap c WHERE c.rule.id = :ruleId AND c.claimEligibilityCategoryMap.id = :categoryMapId AND c.isActive = 'Y'")
    List<ClaimEligibilityComponentMap> findActiveByRuleIdAndCategoryMapId(
        @Param("ruleId") Long ruleId, 
        @Param("categoryMapId") Long categoryMapId
    );
}