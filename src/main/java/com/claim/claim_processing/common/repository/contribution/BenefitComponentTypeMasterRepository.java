package com.claim.claim_processing.common.repository.contribution;


import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BenefitComponentTypeMasterRepository
        extends JpaRepository<BenefitComponentTypeMaster, Long> {

    /**
     * Find by unique code
     */
    Optional<BenefitComponentTypeMaster> findByCode(String code);

    /**
     * Check duplicate code
     */
    boolean existsByCode(String code);

    /**
     * Find active records
     */
    List<BenefitComponentTypeMaster> findByIsActive(ActivityEnum isActive);

    /**
     * Find by code and active status
     */
    Optional<BenefitComponentTypeMaster> findByCodeAndIsActive(
            String code,
            ActivityEnum isActive
    );

    /**
     * Search by name ignoring case
     */
    List<BenefitComponentTypeMaster> findByNameContainingIgnoreCase(String name);
}