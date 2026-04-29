package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.DeductionReferenceTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionReferenceTypeRepository extends JpaRepository<DeductionReferenceTypeMaster, Long> {

    // -------------------------------------------------
    // FIND BY CODE
    // -------------------------------------------------
    Optional<DeductionReferenceTypeMaster> findByCode(String code);

    // -------------------------------------------------
    // CHECK CODE EXISTS
    // -------------------------------------------------
    boolean existsByCode(String code);

    // -------------------------------------------------
    // GET ACTIVE ONLY
    // -------------------------------------------------
    List<DeductionReferenceTypeMaster> findAllByIsActive(ActivityEnum isActive);

    // -------------------------------------------------
    // OPTIONAL: ACTIVE BY CODE
    // -------------------------------------------------
    Optional<DeductionReferenceTypeMaster> findByCodeAndIsActive(
            String code,
            ActivityEnum isActive
    );

    boolean existsByCodeAndIdNot(String code, Long id);
}