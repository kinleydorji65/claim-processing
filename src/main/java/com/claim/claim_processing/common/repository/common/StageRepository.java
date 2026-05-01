package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<StageMaster, Long> {

    // Find by unique code
    Optional<StageMaster> findByCode(String code);

    // Duplicate check for create
    boolean existsByCode(String code);

    // Duplicate check for update
    boolean existsByCodeAndIdNot(String code, Long id);

    // Active records
    List<StageMaster> findByIsActive(ActivityEnum isActive);
}