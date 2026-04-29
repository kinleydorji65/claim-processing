package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityCategoryMap;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimEligibilityCategoryMapRepository
        extends JpaRepository<ClaimEligibilityCategoryMap, Long> {

    // -------------------------
    // CHECK DUPLICATE
    // -------------------------
    boolean existsByRule_IdAndCategory_CategoryId(String ruleId, String categoryId);

    // -------------------------
    // FIND SINGLE MAPPING
    // -------------------------
    boolean existsByRule_IdAndCategory_CategoryId(Long ruleId, String categoryId);

    // -------------------------
    // FIND BY RULE
    // -------------------------
    List<ClaimEligibilityCategoryMap> findByRule_Id(Long ruleId);

    // -------------------------
    // FIND BY CATEGORY
    // -------------------------
    List<ClaimEligibilityCategoryMap> findByCategory_CategoryId(String categoryId);
    
    List<ClaimEligibilityCategoryMap> findByRule_Id(Long ruleId, ActivityEnum isActive);
}