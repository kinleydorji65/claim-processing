package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.WorkflowReasonMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowReasonRepository extends JpaRepository<WorkflowReasonMaster, Long> {

    // 🔹 Find by code
    Optional<WorkflowReasonMaster> findByCode(String code);

    // 🔹 Duplicate check (create)
    boolean existsByCode(String code);

    // 🔹 Duplicate check (update)
    boolean existsByCodeAndIdNot(String code, Long id);

    // 🔹 Active records
    List<WorkflowReasonMaster> findByIsActive(ActivityEnum isActive);
}