package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundComponentMap;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimLapsedRefundComponentMapRepository
        extends JpaRepository<ClaimLapsedRefundComponentMap, Long> {

    // -------------------------------
    // FIND BY RULE ID
    // -------------------------------
    List<ClaimLapsedRefundComponentMap> findByRule_Id(Long ruleId);

    // -------------------------------
    // FIND BY BENEFIT COMPONENT TYPE ID
    // -------------------------------
    List<ClaimLapsedRefundComponentMap> findByBenefitComponentType_Id(Long benefitComponentTypeId);

    // -------------------------------
    // FIND BY ACTIVE STATUS
    // -------------------------------
    List<ClaimLapsedRefundComponentMap> findByIsActive(ActivityEnum isActive);

    // -------------------------------
    // FIND BY RULE + COMPONENT TYPE (FOR VALIDATION / DUPLICATE CHECK)
    // -------------------------------
    Optional<ClaimLapsedRefundComponentMap> findByRule_IdAndBenefitComponentType_Id(
            Long ruleId,
            Long benefitComponentTypeId
    );

    // -------------------------------
    // EXISTS CHECK (VERY IMPORTANT FOR UNIQUE CONSTRAINT HANDLING)
    // -------------------------------
    boolean existsByRule_IdAndBenefitComponentType_Id(
            Long ruleId,
            Long benefitComponentTypeId
    );

    // -------------------------------
    // FIND ALL BY RULE + ACTIVE ONLY
    // -------------------------------
    List<ClaimLapsedRefundComponentMap> findByRule_IdAndIsActive(
            Long ruleId,
            ActivityEnum isActive
    );
}