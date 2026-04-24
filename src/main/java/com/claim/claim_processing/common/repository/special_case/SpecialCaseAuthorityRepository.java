package com.claim.claim_processing.common.repository.special_case;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.special_case.SpecialCaseRefundAuthorityMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialCaseAuthorityRepository extends JpaRepository<SpecialCaseRefundAuthorityMaster, Long> {

    // Check duplicate code (used in create)
    boolean existsByCode(String code);

    // Fetch by code (useful for validation / lookup)
    Optional<SpecialCaseRefundAuthorityMaster> findByCode(String code);

    // Active records (for dropdowns / master usage)
    List<SpecialCaseRefundAuthorityMaster> findByIsActive(ActivityEnum isActive);

    // Active + by code (safe lookup)
    Optional<SpecialCaseRefundAuthorityMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);

    // Optional: for sorting UI dropdowns later
    List<SpecialCaseRefundAuthorityMaster> findByIsActiveOrderByNameAsc(ActivityEnum isActive);
}