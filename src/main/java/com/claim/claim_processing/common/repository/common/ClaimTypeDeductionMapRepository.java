package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.ClaimTypeDeductionMap;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimTypeDeductionMapRepository extends JpaRepository<ClaimTypeDeductionMap, Long> {

    Optional<ClaimTypeDeductionMap> findByClaimType_IdAndDeductionType_Id(Long claimTypeId, Long deductionTypeId);

    boolean existsByClaimType_IdAndDeductionType_Id(Long claimTypeId, Long deductionTypeId);

    List<ClaimTypeDeductionMap> findByClaimType_Id(Long claimTypeId);

    List<ClaimTypeDeductionMap> findByDeductionType_Id(Long deductionTypeId);

    List<ClaimTypeDeductionMap> findByIsActive(ActivityEnum isActive);

}