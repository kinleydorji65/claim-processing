package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimTypeMasterRepository extends JpaRepository<ClaimTypeMaster, Long> {

    // 🔹 Used for switch-case / rule engine
    Optional<ClaimTypeMaster> findByCode(String code);

    // 🔹 Active records only
    List<ClaimTypeMaster> findByIsActive(ActivityEnum isActive);

    // 🔹 Category-based filtering (useful for rule grouping/UI filters)
    List<ClaimTypeMaster> findByCategoryCodeAndIsActive(String categoryCode, ActivityEnum isActive);

    // 🔹 Validation before insert
    boolean existsByCode(String code);
}