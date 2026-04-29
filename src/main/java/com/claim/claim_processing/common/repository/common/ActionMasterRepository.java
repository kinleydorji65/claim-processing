package com.claim.claim_processing.common.repository.common;

import com.claim.claim_processing.common.entities.common.ActionMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionMasterRepository extends JpaRepository<ActionMaster, Long> {

    Optional<ActionMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<ActionMaster> findByIsActive(ActivityEnum isActive);
}