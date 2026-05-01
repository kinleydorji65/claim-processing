package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleTypeRepository extends JpaRepository<RuleTypeMaster, Long> {

    // Find by unique code
    Optional<RuleTypeMaster> findByCode(String code);

    // Duplicate check for create
    boolean existsByCode(String code);

    // Duplicate check for update
    boolean existsByCodeAndIdNot(String code, Long id);

    // Get active records
    List<RuleTypeMaster> findByIsActive(ActivityEnum isActive);

    // Optional sorted active list
    List<RuleTypeMaster> findByIsActiveOrderByDisplayOrderAsc(ActivityEnum isActive);

    // Optional full sorted list
    List<RuleTypeMaster> findAllByOrderByDisplayOrderAsc();
}