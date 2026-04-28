package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimLapsedRefundCategoryMapRepository
        extends JpaRepository<ClaimLapsedRefundCategoryMap, Long> {

    // -------------------------------
    // FIND BY RULE ID
    // -------------------------------
    List<ClaimLapsedRefundCategoryMap> findByRule_Id(Long ruleId);

    // -------------------------------
    // FIND BY CATEGORY ID
    // -------------------------------
    List<ClaimLapsedRefundCategoryMap> findByCategory_CategoryId(String categoryId);

    // -------------------------------
    // FIND BY RULE ID + CATEGORY ID
    // -------------------------------
    Optional<ClaimLapsedRefundCategoryMap> findByRule_IdAndCategory_CategoryId(
            Long ruleId,
            String categoryId
    );

    // -------------------------------
    // EXISTS CHECK
    // -------------------------------
    boolean existsByRule_IdAndCategory_CategoryId(
            Long ruleId,
            String categoryId
    );

    // -------------------------------
    // DELETE BY RULE ID
    // -------------------------------
    void deleteByRule_Id(Long ruleId);

    // -------------------------------
    // DELETE BY CATEGORY ID
    // -------------------------------
    void deleteByCategory_CategoryId(String categoryId);
}