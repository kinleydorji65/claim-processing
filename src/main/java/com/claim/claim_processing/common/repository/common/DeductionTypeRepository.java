package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.DeductionTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionTypeRepository extends JpaRepository<DeductionTypeMaster, Long> {


    Optional<DeductionTypeMaster> findByCode(String code);

    List<DeductionTypeMaster> findByIsActive(ActivityEnum isActive);

    boolean existsByCode(String code);

    Optional<DeductionTypeMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);
}